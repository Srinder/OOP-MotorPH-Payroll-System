import java.time.*;
import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.io.File;

public class AttendanceFileHandler {
    private static final String FILE_PATH = "src/data/Attendance Record.csv";
    private static final double STANDARD_WORK_HOURS = 40.0; // Weekly full-time hours.
    private static final double STANDARD_DAILY_HOURS = 8.0;  // Standard hours per workday.
    
    public static boolean hasLoggedToday(String empId, String today) {
    try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
        String[] row;
        reader.readNext(); // Skip header
        while ((row = reader.readNext()) != null) {
            if (row.length < 6) continue;
            if (row[0].trim().equals(empId) && row[3].trim().equals(today)) {
                return true;
            }
        }
    } catch (Exception e) {
    }
    return false;
}

    public static void logTimeIn(User user) {
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    String timeIn = LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm"));

    if (hasLoggedToday(user.getEmployeeId(), today)) {
        JOptionPane.showMessageDialog(null, "You already clocked in today.");
        return;
    }

    try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, true))) {
        String[] newRow = {
            user.getEmployeeId(),
            user.getLastName(),
            user.getFirstName(),
            today,
            timeIn,
            "" 
        };

        writer.writeNext(newRow);
        JOptionPane.showMessageDialog(null, "Time In recorded at " + timeIn);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Failed to log Time In: " + e.getMessage());
    }
}

    public static void logTimeOut(User user) {
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    String timeOut = LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm"));

    try {
        List<String[]> allRows = new ArrayList<>();
        boolean updated = false;

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            boolean isHeader = true;

            while ((row = reader.readNext()) != null) {
                if (isHeader) {
                    allRows.add(row);
                    isHeader = false;
                    continue;
                }

                if (!updated &&
                    row.length >= 6 &&
                    row[0].trim().equals(user.getEmployeeId().trim()) &&
                    row[3].trim().equals(today)) {

                    if (row[4].trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                            "You haven’t clocked in yet today. Please Time In first.",
                            "Missing Time In",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (!row[5].trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                            "You already clocked out today.",
                            "Already Timed Out",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    row[5] = timeOut;
                    updated = true;
                }

                allRows.add(row);
            }
        }

        if (!updated) {
            JOptionPane.showMessageDialog(null,
                "No valid Time In found for today. Please Time In first.",
                "Time Out Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeAll(allRows);
        }

        JOptionPane.showMessageDialog(null, "Time Out recorded at " + timeOut);
    } catch (IOException | CsvValidationException e) {
        JOptionPane.showMessageDialog(null, "Failed to log Time Out: " + e.getMessage());
    }
}

    public static double[] computeMonthlyHoursAndOT(int empNo, String targetMonthYear) {
        double totalWorkedHours = 0.0;
        double overtimeHours = 0.0;

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            reader.readNext(); // Skip CSV header.

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");

            LocalTime officeStart = LocalTime.of(8, 0);
            LocalTime officeEnd = LocalTime.of(17, 0);

            while ((row = reader.readNext()) != null) {
                if (row.length < 6) continue; // Skip incomplete records.
                int recordEmpNo = Integer.parseInt(row[0].trim());
                if (recordEmpNo != empNo) continue;

                LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);
                if (!logDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")).equalsIgnoreCase(targetMonthYear.trim())) continue;
                if (logDate.getDayOfWeek() == DayOfWeek.SUNDAY) continue; // Exclude Sundays.

                LocalTime logIn = LocalTime.parse(row[4].trim(), timeFmt);
                LocalTime logOut = LocalTime.parse(row[5].trim(), timeFmt);

                if (logOut.isAfter(logIn)) {
                    double workedHours = Duration.between(logIn, logOut).toMinutes() / 60.0;
                    totalWorkedHours += workedHours;
                }

                if (logIn.isBefore(officeStart)) {
                    overtimeHours += Duration.between(logIn, officeStart).toMinutes() / 60.0;
                }
                if (logOut.isAfter(officeEnd)) {
                    overtimeHours += Duration.between(officeEnd, logOut).toMinutes() / 60.0;
                }
            }
        } catch (Exception e) {
        }

        return new double[] {
            Math.round(totalWorkedHours * 100.0) / 100.0,
            Math.round(overtimeHours * 100.0) / 100.0
        };
    }

    public static int computeAbsentDays(int empNo, String targetMonthYear) {
        double totalWorkedHours = computeMonthlyHoursAndOT(empNo, targetMonthYear)[0];

        if (totalWorkedHours >= STANDARD_WORK_HOURS) {
            return 0;
        }

        double missingHours = STANDARD_WORK_HOURS - totalWorkedHours;
        return (int) Math.ceil(missingHours / STANDARD_DAILY_HOURS);
    }

    public static double computeLateMinutes(int empNo, String targetMonthYear) {
        double totalLateMinutes = 0.0;

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            reader.readNext(); // Skip CSV header.

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");

            LocalTime officeStart = LocalTime.of(8, 0);
            LocalTime graceCutoff = officeStart.plusMinutes(10);
            LocalTime officeEnd = LocalTime.of(17, 0);

            while ((row = reader.readNext()) != null) {
                if (row.length < 6) continue;
                int recordEmpNo = Integer.parseInt(row[0].trim());
                if (recordEmpNo != empNo) continue;

                LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);
                if (!logDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")).equalsIgnoreCase(targetMonthYear.trim())) continue;
                if (logDate.getDayOfWeek() == DayOfWeek.SUNDAY) continue; // Exclude Sundays.

                LocalTime logIn = LocalTime.parse(row[4].trim(), timeFmt);
                LocalTime logOut = LocalTime.parse(row[5].trim(), timeFmt);

                if (logIn.isAfter(graceCutoff)) {
                    totalLateMinutes += Duration.between(graceCutoff, logIn).toMinutes();
                }

                if (logOut.isBefore(officeEnd)) {
                    totalLateMinutes += Duration.between(logOut, officeEnd).toMinutes();
                }
            }
        } catch (Exception e) {
        }

        return totalLateMinutes;
    }
    
    public static Map<String, Double> computeDailyAttendanceMinutes(int empNo, String logDateStr) {
    double lateMinutes = 0.0;
    double overtimeMinutes = 0.0;
    double undertimeMinutes = 0.0;

    try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
        String[] row;
        reader.readNext(); // Skip CSV header.

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");

        LocalTime shiftStart = LocalTime.of(8, 0);
        LocalTime graceCutoff = shiftStart.plusMinutes(10); // ✅ 10-minute grace period
        LocalTime shiftEnd = LocalTime.of(17, 0);

        LocalDate targetDate = LocalDate.parse(logDateStr, dateFmt);

        while ((row = reader.readNext()) != null) {
            if (row.length < 6) continue;
            int recordEmpNo = Integer.parseInt(row[0].trim());
            if (recordEmpNo != empNo) continue;

            LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);
            if (!logDate.isEqual(targetDate)) continue; // ✅ Match row's exact date

            LocalTime logIn = LocalTime.parse(row[4].trim(), timeFmt);
            LocalTime logOut = LocalTime.parse(row[5].trim(), timeFmt);

            // ✅ Compute late minutes **only if arrival is AFTER 8:10 AM**
            if (logIn.isAfter(graceCutoff)) {
                lateMinutes = Duration.between(shiftStart, logIn).toMinutes(); // ✅ Count full minutes from 8:00 AM
            }

            // ✅ Compute overtime minutes only if Time OUT is after 5:00 PM
            if (logOut.isAfter(shiftEnd)) {
                overtimeMinutes = Duration.between(shiftEnd, logOut).toMinutes();
            }

            // ✅ Compute undertime minutes only if Time OUT is **before** 5:00 PM
            if (logOut.isBefore(shiftEnd)) {
                undertimeMinutes = Duration.between(logOut, shiftEnd).toMinutes();
            }

            break; // ✅ Stop after finding the correct date's entry
        }
    } catch (Exception e) {
    }

    Map<String, Double> result = new HashMap<>();
    result.put("Late", lateMinutes);
    result.put("Overtime", overtimeMinutes);
    result.put("Undertime", undertimeMinutes);
    return result;
}

    public static List<String[]> getAttendanceRecords(String empNo, LocalDate startDate, LocalDate endDate) {
    List<String[]> records = new ArrayList<>();

    try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
        String[] row;
        reader.readNext(); // Skip CSV header.

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        while ((row = reader.readNext()) != null) {
            if (row.length < 6) continue;
            if (!row[0].trim().equals(empNo)) continue; // Filter by employee number

            LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);

            // Ensure the date falls within the selected range
            if (!logDate.isBefore(startDate) && !logDate.isAfter(endDate)) {
                records.add(new String[]{row[3], row[4], row[5]}); // Date, Time In, Time Out
            }
        }
    } catch (Exception e) {
    }

    return records;
}
public static List<AttendanceRecord> readAllAttendanceRecords() throws IOException, CsvValidationException {
        List<AttendanceRecord> records = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] line;
            boolean skipHeader = true;

            while ((line = reader.readNext()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                records.add(new AttendanceRecord(
                    line[0], // Employee #
                    line[1], // Last Name
                    line[2], // First Name
                    line[3], // Date
                    line[4], // Log In
                    line[5]  // Log Out
                ));
            }
        }

        return records;
    }

    // Update records by matching empNo + date
   public static boolean updateAttendanceRecords(List<AttendanceRecord> updatedRecords)
        throws IOException, CsvValidationException {

    List<AttendanceRecord> allRecords = readAllAttendanceRecords();
    int updateCount = 0;

    for (AttendanceRecord updated : updatedRecords) {
        for (AttendanceRecord existing : allRecords) {
            if (existing.empNo.equals(updated.empNo) && existing.date.equals(updated.date)) {

                boolean changed = false;

                if (!existing.logIn.equals(updated.logIn)) {
                    existing.logIn = updated.logIn;
                    changed = true;
                }

                if (!existing.logOut.equals(updated.logOut)) {
                    existing.logOut = updated.logOut;
                    changed = true;
                }

                if (changed) {
                    updateCount++;
                }

                break;
            }
        }
    }

    File file = new File(FILE_PATH);
 

    try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
        writer.writeNext(new String[] {
            "Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out"
        });

        for (AttendanceRecord r : allRecords) {
            writer.writeNext(new String[] {
                r.empNo, r.lastName, r.firstName, r.date, r.logIn, r.logOut
            });
        }
    }

    return true;
}  
   

}
