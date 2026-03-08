//Interface defining the contract for Attendance operations.


/**
 *
 * @author singh
 */

package service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.AttendanceRecord;
import model.Employee;



public interface IAttendanceService {
    Map<String, Double> computeDailyAttendanceMinutes(int empId, String dateStr);
    double calculateLateMinutes(AttendanceRecord record);
    double calculateOvertimeMinutes(AttendanceRecord record);
    double calculateMonthlyNetHours(String empId, String targetMonthYear);
    void recordTimeIn(Employee employee);
    void recordTimeOut(Employee employee);
    List<Employee> getSupervisedEmployees(Employee supervisor);
    String getEmployeeDisplayName(String empNo);
    List<Employee> searchEmployees(String query);
    List<AttendanceRecord> getAttendanceRecords(String targetEmpNo, LocalDate startDate, LocalDate endDate);
    boolean updateAttendanceRecords(String targetEmpNo, List<AttendanceRecord> updatedRecords);
}
