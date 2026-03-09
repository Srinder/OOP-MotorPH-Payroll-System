/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author singh
 */
package service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.AttendanceRecord;
import model.Employee;
import repository.AttendanceRepository;
import repository.EmployeeRepository;

public class AttendanceService implements IAttendanceService{
    
    private final AttendanceRepository attendanceRepo = new AttendanceRepository();
    private final EmployeeRepository employeeRepo = new EmployeeRepository();
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final List<DateTimeFormatter> acceptedTimeFormats = Arrays.asList(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("hh:mm a")
    );

    // MotorPH Business Rules
    private final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private final LocalTime GRACE_PERIOD = SHIFT_START.plusMinutes(10); // 8:10 AM
    private final LocalTime SHIFT_END = LocalTime.of(17, 0);
    
    @Override
    public Map<String, Double> computeDailyAttendanceMinutes(int empId, String dateStr) {
        // 1. Get the raw record from the repository
        List<AttendanceRecord> records = attendanceRepo.findByEmployeeId(String.valueOf(empId));

        // 2. Find the specific record for this date
        AttendanceRecord dailyRecord = records.stream()
                .filter(r -> r.getDate().equals(dateStr))
                .findFirst()
                .orElse(null);
        if (dailyRecord == null) return Map.of("Late", 0.0, "Overtime", 0.0, "Undertime", 0.0);

        // 3. Use SERVICE rules to compute the values
        double late = calculateLateMinutes(dailyRecord);
        double overtime = calculateOvertimeMinutes(dailyRecord);
        double undertime = calculateUndertimeMinutes(dailyRecord);

        return Map.of(
            "Late", late,
            "Overtime", overtime,
            "Undertime", undertime
        );
}

    //Calculates late minutes for a specific record.
    //Rule: If arrival is after 8:10 AM, late is calculated from 8:00 AM.
     @Override
    public double calculateLateMinutes(AttendanceRecord record) {
        LocalTime logIn = parseTime(record.getLogIn());
        if (logIn == null) {
            return 0.0;
        }
        
        if (logIn.isAfter(GRACE_PERIOD)) {
            // Grace period means lateness starts counting after 8:10 AM.
            return Duration.between(GRACE_PERIOD, logIn).toMinutes();
        }
        return 0.0;
    }

    // Calculates overtime minutes for a specific record.
    // Rule: Any time worked after 5:00 PM, but only if 30 mins or more.
    @Override
    public double calculateOvertimeMinutes(AttendanceRecord record) {
        LocalTime logIn = parseTime(record.getLogIn());
        LocalTime logOut = parseTime(record.getLogOut());

        double overtimeMinutes = 0.0;

        // Any login before 8:00 AM is overtime.
        if (logIn != null && logIn.isBefore(SHIFT_START)) {
            overtimeMinutes += Duration.between(logIn, SHIFT_START).toMinutes();
        }

        // Any logout after 5:00 PM is overtime.
        if (logOut != null && logOut.isAfter(SHIFT_END)) {
            overtimeMinutes += Duration.between(SHIFT_END, logOut).toMinutes();
        }

        return overtimeMinutes;
}

    private double calculateUndertimeMinutes(AttendanceRecord record) {
        LocalTime logOut = parseTime(record.getLogOut());
        if (logOut == null) {
            return 0.0;
        }
        return logOut.isBefore(SHIFT_END)
                ? Duration.between(logOut, SHIFT_END).toMinutes()
                : 0.0;
    }

    private LocalTime parseTime(String timeValue) {
        if (timeValue == null) {
            return null;
        }
        String value = timeValue.trim();
        if (value.isEmpty() || "N/A".equalsIgnoreCase(value)) {
            return null;
        }

        // Normalize common user-entered variants like "8:11am" or "8:11 a.m."
        String normalized = value.toUpperCase()
                .replace(".", "")
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.endsWith("AM") || normalized.endsWith("PM")) {
            normalized = normalized.replace("AM", " AM").replace("PM", " PM").replaceAll("\\s+", " ").trim();
        }

        for (DateTimeFormatter formatter : acceptedTimeFormats) {
            try {
                return LocalTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }
        return null;
    }
        
    //Calculates total hours worked in a month for Payroll.
     //Subtracts 1 hour for lunch if worked more than 4 hours.
    @Override
    public double calculateMonthlyNetHours(String empId, String targetMonthYear) {
        List<AttendanceRecord> records = attendanceRepo.findByEmployeeId(empId);
        double totalHours = 0.0;

        for (AttendanceRecord record : records) {
            LocalDate logDate = LocalDate.parse(record.getDate(), dateFmt);
            String monthYear = logDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

            if (monthYear.equalsIgnoreCase(targetMonthYear) && logDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
              
                if (record.getLogOut() == null || record.getLogOut().isEmpty()) {
                    System.err.println("MISSING LOG-OUT: Emp " + empId + " on " + record.getDate());
                    continue; 
                }

                LocalTime logIn = parseTime(record.getLogIn());
                LocalTime logOut = parseTime(record.getLogOut());
                if (logIn == null || logOut == null) {
                    continue;
                }

                double hours = Duration.between(logIn, logOut).toMinutes() / 60.0;
                
                // Subtract 1 hour for lunch if they worked a full shift
                double dailyNet = (hours > 4) ? hours - 1 : hours;
                totalHours += dailyNet;
            }
        }
        // Round to 2 decimal places for precision
        return Math.round(totalHours * 100.0) / 100.0;
    }

    @Override
    public void recordTimeIn(Employee employee) {
        String empId = String.valueOf(employee.getEmployeeNumber());
        String today = LocalDate.now().format(dateFmt);
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        AttendanceRecord record = new AttendanceRecord(
                empId,
                employee.getLastName(),
                employee.getFirstName(),
                today,
                now,
                "N/A");
        attendanceRepo.save(record);
    }

    @Override
    public void recordTimeOut(Employee employee) {
        String empId = String.valueOf(employee.getEmployeeNumber());
        String today = LocalDate.now().format(dateFmt);
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        AttendanceRecord record = new AttendanceRecord(
                empId,
                employee.getLastName(),
                employee.getFirstName(),
                today,
                "N/A",
                now);
        List<AttendanceRecord> oneRowUpdate = new ArrayList<>();
        oneRowUpdate.add(record);
        updateAttendanceRecords(empId, oneRowUpdate);
    }

    @Override
    public List<Employee> getSupervisedEmployees(Employee supervisor) {
        List<Employee> supervised = new ArrayList<>();
        if (supervisor == null) {
            return supervised;
        }

        String supervisorName = supervisor.getLastName().trim() + "  " + supervisor.getFirstName().trim();
        for (Employee employee : employeeRepo.findAll()) {
            if (employee.getSupervisor().trim().equalsIgnoreCase(supervisorName)) {
                supervised.add(employee);
            }
        }
        return supervised;
    }

    @Override
    public String getEmployeeDisplayName(String empNo) {
        try {
            return employeeRepo.findById(Integer.parseInt(empNo))
                    .map(e -> e.getFirstName() + " " + e.getLastName())
                    .orElse("Unknown Employee");
        } catch (Exception e) {
            return "Unknown Employee";
        }
    }

    @Override
    public List<Employee> searchEmployees(String query) {
        List<Employee> matches = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return matches;
        }

        String normalizedQuery = query.toLowerCase().trim();
        for (Employee employee : employeeRepo.findAll()) {
            String fullName = (employee.getLastName() + ", " + employee.getFirstName()).toLowerCase();
            String id = String.valueOf(employee.getEmployeeNumber());
            if (id.equals(query.trim()) || fullName.contains(normalizedQuery)) {
                matches.add(employee);
            }
        }
        return matches;
    }

    @Override
    public List<AttendanceRecord> getAttendanceRecords(String targetEmpNo, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> filtered = new ArrayList<>();
        if (targetEmpNo == null || startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return filtered;
        }

        for (AttendanceRecord record : attendanceRepo.findByEmployeeId(targetEmpNo.trim())) {
            try {
                LocalDate recordDate = LocalDate.parse(record.getDate().trim(), dateFmt);
                if (!recordDate.isBefore(startDate) && !recordDate.isAfter(endDate)) {
                    filtered.add(record);
                }
            } catch (DateTimeParseException ignored) {
                // Skip malformed date rows.
            }
        }
        return filtered;
    }

    @Override
    public List<Object[]> buildAttendanceTableRows(String targetEmpNo, LocalDate startDate, LocalDate endDate) {
        List<Object[]> rows = new ArrayList<>();
        List<AttendanceRecord> records = getAttendanceRecords(targetEmpNo, startDate, endDate);
        int targetEmpId;
        try {
            targetEmpId = Integer.parseInt(targetEmpNo.trim());
        } catch (Exception ex) {
            targetEmpId = -1;
        }

        for (AttendanceRecord record : records) {
            Map<String, Double> metrics = targetEmpId > 0
                    ? computeDailyAttendanceMinutes(targetEmpId, record.getDate())
                    : Map.of("Late", 0.0, "Overtime", 0.0, "Undertime", 0.0);
            rows.add(new Object[]{
                    record.getDate(),
                    record.getLogIn(),
                    record.getLogOut(),
                    metrics.get("Late"),
                    metrics.get("Overtime"),
                    metrics.get("Undertime")
            });
        }
        return rows;
    }

    @Override
    public List<AttendanceRecord> mapRowsToAttendanceRecords(String targetEmpNo, List<Object[]> rows) {
        List<AttendanceRecord> mapped = new ArrayList<>();
        if (targetEmpNo == null || rows == null) {
            return mapped;
        }
        String empNo = targetEmpNo.trim();
        for (Object[] row : rows) {
            if (row == null || row.length < 3) {
                continue;
            }
            String date = row[0] == null ? "" : String.valueOf(row[0]).trim();
            if (date.isEmpty()) {
                continue;
            }
            String logIn = row[1] == null ? "" : String.valueOf(row[1]).trim();
            String logOut = row[2] == null ? "" : String.valueOf(row[2]).trim();
            mapped.add(new AttendanceRecord(empNo, "", "", date, logIn, logOut));
        }
        return mapped;
    }

    @Override
    public boolean updateAttendanceRecords(String targetEmpNo, List<AttendanceRecord> updatedRecords) {
        if (targetEmpNo == null || updatedRecords == null || updatedRecords.isEmpty()) {
            return false;
        }

        String normalizedEmpNo = targetEmpNo.trim();
        Map<String, AttendanceRecord> existingByDate = new HashMap<>();
        for (AttendanceRecord existing : attendanceRepo.findByEmployeeId(normalizedEmpNo)) {
            if (existing != null && existing.getDate() != null) {
                String key = existing.getDate().trim();
                AttendanceRecord current = existingByDate.get(key);
                if (current == null) {
                    existingByDate.put(key, new AttendanceRecord(
                            normalizedEmpNo,
                            existing.getLastName(),
                            existing.getFirstName(),
                            key,
                            existing.getLogIn(),
                            existing.getLogOut()));
                } else {
                    // Merge split records for the same date (keep non-empty/non-N/A values).
                    if (isMissingTime(current.getLogIn()) && !isMissingTime(existing.getLogIn())) {
                        current.setLogIn(existing.getLogIn());
                    }
                    if (isMissingTime(current.getLogOut()) && !isMissingTime(existing.getLogOut())) {
                        current.setLogOut(existing.getLogOut());
                    }
                }
            }
        }

        Employee employee = null;
        try {
            employee = employeeRepo.findById(Integer.parseInt(normalizedEmpNo)).orElse(null);
        } catch (NumberFormatException ignored) {
            // Leave employee null and preserve row-level names.
        }

        String firstName = employee != null ? employee.getFirstName() : "";
        String lastName = employee != null ? employee.getLastName() : "";
        List<AttendanceRecord> normalized = new ArrayList<>();

        for (AttendanceRecord row : updatedRecords) {
            if (row == null) {
                continue;
            }
            String date = row.getDate() == null ? "" : row.getDate().trim();
            if (date.isEmpty()) {
                continue;
            }
            AttendanceRecord existing = existingByDate.get(date);
            String mergedLogIn = row.getLogIn();
            if (isMissingTime(mergedLogIn) && existing != null) {
                mergedLogIn = existing.getLogIn();
            }
            String mergedLogOut = row.getLogOut();
            if (isMissingTime(mergedLogOut) && existing != null) {
                mergedLogOut = existing.getLogOut();
            }
            normalized.add(new AttendanceRecord(
                    normalizedEmpNo,
                    lastName,
                    firstName,
                    date,
                    mergedLogIn == null ? "" : mergedLogIn.trim(),
                    mergedLogOut == null ? "" : mergedLogOut.trim()
            ));
        }

        return !normalized.isEmpty() && attendanceRepo.update(normalized);
    }

    private boolean isMissingTime(String value) {
        return value == null || value.trim().isEmpty() || "N/A".equalsIgnoreCase(value.trim());
    }

}
