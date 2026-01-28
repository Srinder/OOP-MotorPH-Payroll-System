/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author singh
 */
package service;

import model.AttendanceRecord;
import repository.AttendanceRepository;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceService {
    private final AttendanceRepository attendanceRepo = new AttendanceRepository();
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // MotorPH Business Rules
    private final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private final LocalTime GRACE_PERIOD = SHIFT_START.plusMinutes(10); // 8:10 AM
    private final LocalTime SHIFT_END = LocalTime.of(17, 0);

    /**
     * Calculates late minutes for a specific record.
     * Rule: If arrival is after 8:10 AM, late is calculated from 8:00 AM.
     */
    public double calculateLateMinutes(AttendanceRecord record) {
        LocalTime logIn = LocalTime.parse(record.getLogIn(), timeFmt);
        
        if (logIn.isAfter(GRACE_PERIOD)) {
            return Duration.between(SHIFT_START, logIn).toMinutes();
        }
        return 0.0;
    }

    /**
     * Calculates overtime minutes for a specific record.
     * Rule: Any time worked after 5:00 PM.
     */
    public double calculateOvertimeMinutes(AttendanceRecord record) {
        if (record.getLogOut() == null || record.getLogOut().isEmpty()) return 0.0;
        
        LocalTime logOut = LocalTime.parse(record.getLogOut(), timeFmt);
        if (logOut.isAfter(SHIFT_END)) {
            return Duration.between(SHIFT_END, logOut).toMinutes();
        }
        return 0.0;
    }

    /**
     * Calculates total hours worked in a month for Payroll.
     * Subtracts 1 hour for lunch if worked more than 4 hours.
     */
    public double calculateMonthlyNetHours(String empId, String targetMonthYear) {
        List<AttendanceRecord> records = attendanceRepo.findByEmployeeId(empId);
        double totalHours = 0.0;

        for (AttendanceRecord record : records) {
            // Filter by month and exclude Sundays
            LocalDate logDate = LocalDate.parse(record.getDate(), dateFmt);
            String monthYear = logDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

            if (monthYear.equalsIgnoreCase(targetMonthYear) && logDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                if (record.getLogOut().isEmpty()) continue;

                LocalTime logIn = LocalTime.parse(record.getLogIn(), timeFmt);
                LocalTime logOut = LocalTime.parse(record.getLogOut(), timeFmt);

                double hours = Duration.between(logIn, logOut).toMinutes() / 60.0;
                
                // Subtract 1 hour for lunch (standard break)
                totalHours += (hours > 4) ? hours - 1 : hours;
            }
        }
        return Math.round(totalHours * 100.0) / 100.0;
    }
}