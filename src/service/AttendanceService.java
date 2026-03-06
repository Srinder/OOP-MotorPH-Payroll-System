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
import java.util.List;
import java.util.Map;
import model.AttendanceRecord;
import repository.AttendanceRepository;

public class AttendanceService implements IAttendanceService{
    
    private final AttendanceRepository attendanceRepo = new AttendanceRepository();
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

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

        // Simple Undertime logic: If they leave before 5:00 PM
        LocalTime logOut = LocalTime.parse(dailyRecord.getLogOut(), timeFmt);
        double undertime = logOut.isBefore(SHIFT_END) ? 
                           java.time.Duration.between(logOut, SHIFT_END).toMinutes() : 0.0;

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
        LocalTime logIn = LocalTime.parse(record.getLogIn(), timeFmt);
        
        if (logIn.isAfter(GRACE_PERIOD)) {
            return Duration.between(SHIFT_START, logIn).toMinutes();
        }
        return 0.0;
    }

    // Calculates overtime minutes for a specific record.
    // Rule: Any time worked after 5:00 PM, but only if 30 mins or more.
    @Override
    public double calculateOvertimeMinutes(AttendanceRecord record) {
        if (record.getLogOut() == null || record.getLogOut().isEmpty()) return 0.0;
        
        LocalTime logOut = LocalTime.parse(record.getLogOut(), timeFmt);
        if (logOut.isAfter(SHIFT_END)) {
            long totalOvertimeMinutes = Duration.between(SHIFT_END, logOut).toMinutes();
            
        //Apply 30-minute threshold
        if (totalOvertimeMinutes >= 30) {
            return (double) totalOvertimeMinutes;
        }
    }
        return 0.0; // Returns 0 if overtime is less than 30 minutes
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

                LocalTime logIn = LocalTime.parse(record.getLogIn(), timeFmt);
                LocalTime logOut = LocalTime.parse(record.getLogOut(), timeFmt);

                double hours = Duration.between(logIn, logOut).toMinutes() / 60.0;
                
                // Subtract 1 hour for lunch if they worked a full shift
                double dailyNet = (hours > 4) ? hours - 1 : hours;
                totalHours += dailyNet;
            }
        }
        // Round to 2 decimal places for precision
        return Math.round(totalHours * 100.0) / 100.0;
    }
}