/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author singh
 */
package service;

import repository.LeaveRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import model.LeaveRequest;

public class LeaveManagement implements ILeaveManagement {
    private final LeaveRepository repo = new LeaveRepository();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final double ANNUAL_LEAVE_ALLOWANCE = 15.0; 

    
public double getTotalRemainingCredits(int empId) {
        // Sum up the remaining balances for all three types
    double vacation = getRemainingLeaveCredits(empId, "Vacation");
    double sick = getRemainingLeaveCredits(empId, "Sick");
    double emergency = getRemainingLeaveCredits(empId, "Emergency");
    
    return vacation + sick + emergency;
}

    @Override
    public boolean updateLeaveStatus(int empId, String startDate, String newStatus) {
    // This calls your repository to find the row and change the column
    return repo.updateStatus(empId, startDate, newStatus);
}

    @Override
    public void approveLeave(int leaveId) {
    // Leave this empty for now so the code compiles
    
}
        
    @Override
    public List<model.LeaveRequest> getEmployeeLeaveHistory(int empId) {
    List<model.LeaveRequest> allRequests = repo.getAllRequests();
    List<model.LeaveRequest> filteredRequests = new java.util.ArrayList<>();

    for (model.LeaveRequest req : allRequests) {
        if (req.getEmployeeId() == empId) {
            filteredRequests.add(req);
        }
    }
        return filteredRequests;
}
    
    
    @Override
    public boolean applyLeave(int empID, String leaveType, String startDate, String endDate, String reason) {
    // 1. Calculate requested days
    double requestedDays = calculateLeaveDays(startDate, endDate);
    
    // 2. Check remaining credits
    double remaining = getRemainingLeaveCredits(empID, leaveType);
    
    if (requestedDays <= remaining) {
        // 3. Create a new Model object
        model.LeaveRequest newReq = new model.LeaveRequest(
            empID, leaveType, startDate, endDate, reason, "Pending"
        );
        
        // 4. Ask Repository to save
        return repo.saveRequest(newReq);
    }
    
    return false; // Insufficient credits
}
    


    @Override
    public double getRemainingLeaveCredits(int empId, String type) {
    double initialBalance = 0;
    if (type.equalsIgnoreCase("Vacation")) {
        initialBalance = 20.0; 
    } else if (type.equalsIgnoreCase("Sick")) {
        initialBalance = 10.0; 
    } else if (type.equalsIgnoreCase("Emergency")) {
        initialBalance = 10.0; 
    }

    
    List<model.LeaveRequest> allRequests = repo.getAllRequests(); 
    
    double used = 0;
    for (model.LeaveRequest req : allRequests) {
        
        if (req.getEmployeeId() == empId && req.getLeaveType().equalsIgnoreCase(type)) {
            
            
            if (req.getStatus().equalsIgnoreCase("Approved")) {
                used += calculateLeaveDays(req.getStartDate(), req.getEndDate());
            }
        }
    }
    return initialBalance - used;
}

    @Override
    public double calculateLeaveDays(String start, String end) {
    try {
        
        LocalDate sDate = LocalDate.parse(start, formatter);
        LocalDate eDate = LocalDate.parse(end, formatter);

      
        long days = ChronoUnit.DAYS.between(sDate, eDate) + 1;

        
        return (days < 0) ? 0 : (double) days;
    } catch (Exception e) {
        System.err.println("Error parsing dates: " + e.getMessage());
        return 0;
    }
}
}