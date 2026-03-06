package repository;

import model.AttendanceRecord;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AttendanceRepository {
    private static final String FILE_PATH = "data/attendance_record.csv";

    // 1. SAVE: Simply appends a new line for Time-In
    public void save(AttendanceRecord record) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            out.println(record.getEmpNo() + "," + record.getLastName() + "," + record.getFirstName() + "," + 
                        record.getDate() + "," + record.getLogIn() + "," + record.getLogOut());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 2. SINGLE UPDATE: Needed for Time-Out button
    public void update(AttendanceRecord record) {
        List<AttendanceRecord> list = new ArrayList<>();
        list.add(record);
        update(list);
    }

    // 3. BULK UPDATE: Needed for your "Save" button in the Attendance Window
    public boolean update(List<AttendanceRecord> updatedRecords) {
        List<AttendanceRecord> allRecords = findAll();
        boolean foundAny = false;

        for (AttendanceRecord updated : updatedRecords) {
            for (int i = 0; i < allRecords.size(); i++) {
                AttendanceRecord existing = allRecords.get(i);

                if (existing.getEmpNo().trim().equals(updated.getEmpNo().trim()) && 
                    existing.getDate().trim().equals(updated.getDate().trim())) {

                    // CRITICAL: If the update has no Time In, keep the existing one
                    if (updated.getLogIn() == null || updated.getLogIn().isEmpty() || updated.getLogIn().equals("N/A")) {
                        updated.setLogIn(existing.getLogIn());
                    }

                    allRecords.set(i, updated);
                    foundAny = true;
                }
            }
        }
        if (foundAny) { saveAll(allRecords); return true; }
        return false;
}

    // 4. SAVE ALL: Rewrites the CSV (Used by update)
    private void saveAll(List<AttendanceRecord> records) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("EmpNo,LastName,FirstName,Date,LogIn,LogOut"); // Header
            for (AttendanceRecord rec : records) {
                writer.println(rec.getEmpNo() + "," + rec.getLastName() + "," + rec.getFirstName() + "," + 
                               rec.getDate() + "," + rec.getLogIn() + "," + rec.getLogOut());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // 5. FIND ALL: Reads CSV into a List
    public List<AttendanceRecord> findAll() {
        List<AttendanceRecord> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] r = line.split(",");
                if (r.length >= 6) {
                    list.add(new AttendanceRecord(r[0].trim(), r[1].trim(), r[2].trim(), 
                                                 r[3].trim(), r[4].trim(), r[5].trim()));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 6. HELPER METHODS: For calculations and UI
    public List<AttendanceRecord> findByEmployeeId(String empId) {
        List<AttendanceRecord> filtered = new ArrayList<>();
        for (AttendanceRecord r : findAll()) {
            if (r.getEmpNo().equals(empId)) filtered.add(r);
        }
        return filtered;
    }

    public Map<String, Double> computeDailyAttendanceMinutes(int empId, String date) {
        Map<String, Double> map = new HashMap<>();
        // These keys MUST match what you use in Attendance.java (lines 261-263)
        map.put("Late", 0.0);
        map.put("Overtime", 0.0);
        map.put("Undertime", 0.0);
        return map;
    }

    public double getTotalHoursInRange(String empId, String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(start, formatter);
            endDate = LocalDate.parse(end, formatter);
        } catch (DateTimeParseException ex) {
            return 0.0;
        }

        if (endDate.isBefore(startDate)) {
            return 0.0;
        }

        int workingDays = 0;
        for (AttendanceRecord record : findByEmployeeId(empId)) {
            try {
                LocalDate recordDate = LocalDate.parse(record.getDate(), formatter);
                if (!recordDate.isBefore(startDate) && !recordDate.isAfter(endDate)) {
                    workingDays++;
                }
            } catch (DateTimeParseException ignored) {
                // Skip rows with invalid dates.
            }
        }
        return workingDays * 8.0;
    }
}
