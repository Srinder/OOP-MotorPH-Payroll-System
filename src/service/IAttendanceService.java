//Interface defining the contract for Attendance operations.


/**
 *
 * @author singh
 */

package service;

import java.util.List;
import java.util.Map;
import model.AttendanceRecord;



public interface IAttendanceService {
    Map<String, Double> computeDailyAttendanceMinutes(int empId, String dateStr);
    double calculateLateMinutes(AttendanceRecord record);
    double calculateOvertimeMinutes(AttendanceRecord record);
    double calculateMonthlyNetHours(String empId, String targetMonthYear);
}
