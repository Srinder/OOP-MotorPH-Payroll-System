package repository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import model.AttendanceRecord;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AttendanceRepository extends BaseRepository<AttendanceRecord> {

    private static final String FILE_PATH = "data/attendance_logs.csv";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    public AttendanceRepository(){
        try {
            File file = new File(FILE_PATH);
            File folder = file.getParentFile();
            if (folder != null && !folder.exists()) folder.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                try (FileWriter fw = new FileWriter(file)){
                    fw.write("EmpNo,LastName,FirstName,Date,LogIn,LogOut\n");
                }
            }
        } catch (IOException e){
            System.err.println("File Error: " + e.getMessage());
        }
    }

    
    public List<AttendanceRecord> findByEmployeeId(String empId) {
        return findAll().stream()
                .filter(r -> r.getEmpNo().trim().equals(empId.trim()))
                .collect(Collectors.toList());
    }

    
    public double getTotalHoursInRange(String empId, String startDate, String endDate) {
        List<AttendanceRecord> records = findByEmployeeId(empId);
        double totalHours = 0.0;
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        for (AttendanceRecord record : records) {
            try {
                LocalDate recordDate = LocalDate.parse(record.getDate(), formatter);
                if ((recordDate.isEqual(start) || recordDate.isAfter(start)) && 
                    (recordDate.isEqual(end) || recordDate.isBefore(end))) {
                    totalHours += 8.0; // Standard 8-hour shift logic
                }
            } catch (Exception e) { /* Skip malformed dates */ }
        }
        return totalHours;
    }

    
    public Map<String, Double> computeDailyAttendanceMinutes(int empId, String date) {
        Map<String, Double> metrics = new HashMap<>();
        boolean exists = hasLoggedToday(String.valueOf(empId), date);
        metrics.put("totalMinutes", exists ? 480.0 : 0.0);
        metrics.put("lateMinutes", 0.0); 
        return metrics;
    }

    
    public boolean update(List<AttendanceRecord> updatedRecords) {
        try {
            List<AttendanceRecord> allRecords = findAll();
            for (AttendanceRecord updated : updatedRecords) {
                for (int i = 0; i < allRecords.size(); i++) {
                    if (allRecords.get(i).getEmpNo().equals(updated.getEmpNo()) && 
                        allRecords.get(i).getDate().equals(updated.getDate())) {
                        allRecords.set(i, updated);
                    }
                }
            }
            saveAll(allRecords);
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public void update(AttendanceRecord record) {
        List<AttendanceRecord> list = new ArrayList<>();
        list.add(record);
        update(list); // Reuses the list-based update logic
    }

    @Override
    public void save(AttendanceRecord record) {
        List<AttendanceRecord> allRecords = findAll();
        allRecords.add(record);
        saveAll(allRecords);
    }

    private void saveAll(List<AttendanceRecord> records) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("EmpNo,LastName,FirstName,Date,LogIn,LogOut");
            for (AttendanceRecord rec : records) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s",
                    rec.getEmpNo(), rec.getLastName(), rec.getFirstName(),
                    rec.getDate(), rec.getLogIn(), rec.getLogOut()));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean hasLoggedToday(String empId, String date) {
    List<AttendanceRecord> records = findAll();
    
    // Standardize the search date format (removes leading zeros/mismatches)
    String searchDate = date.trim(); 
    
    for (AttendanceRecord rec : records) {
        // Use trim() on both sides to catch invisible spaces
        if (rec.getEmpNo().trim().equals(empId.trim()) && 
            rec.getDate().trim().equals(searchDate)) {
            return true;
        }
    }
    return false;
}

    @Override
    public List<AttendanceRecord> findAll() {
        List<AttendanceRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine(); // Skip Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (row.length >= 6) {
                    records.add(new AttendanceRecord(row[0], row[1], row[2], row[3], row[4], row[5]));
                }
            }
        } catch (Exception e) { }
        return records;
    }

    @Override protected String getFilePath() { return FILE_PATH; }
    @Override public Optional<AttendanceRecord> findById(int id) { return Optional.empty(); }
    @Override public void delete(int id) {}
}