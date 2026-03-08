package repository;

import model.AttendanceRecord;
import java.io.*;
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

    public List<AttendanceRecord> findByEmployeeId(String empId) {
        List<AttendanceRecord> filtered = new ArrayList<>();
        for (AttendanceRecord r : findAll()) {
            if (r.getEmpNo().equals(empId)) filtered.add(r);
        }
        return filtered;
    }
}
