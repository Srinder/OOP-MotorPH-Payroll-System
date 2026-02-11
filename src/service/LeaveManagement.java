
package service;

/**
 *
 * @author singh
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LeaveManagement implements ILeaveManagement {
    
    

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final double ANNUAL_LEAVE_ALLOWANCE = 15.0;

    @Override
    public boolean applyLeave(int empId, String leaveType, String startDate, String endDate) {
        double requestedDays = calculateLeaveDays(startDate, endDate);
        double currentCredits = getRemainingLeaveCredits(empId);

        if (requestedDays <= currentCredits) {
            //println for the meantime, we need to change it to UI later
            System.out.println("Leave applied successfully for " + requestedDays + " days.");
            return true;
        }
        return false;
    }

    @Override
    public double getRemainingLeaveCredits(int empId) { 
        // eventually fetch "used leaves" from a Repository
        return ANNUAL_LEAVE_ALLOWANCE; 
    }

    @Override
    public double calculateLeaveDays(String start, String end) {
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
        
        
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    @Override
    public void approveLeave(int leaveId) {
        
    }
}
