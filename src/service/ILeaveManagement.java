/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

/**
 *
 * @author singh
 */


import java.util.List;
import model.LeaveRequest;

public interface ILeaveManagement {
    boolean applyLeave(int empID, String leaveType, String startDate, String endDate, String reason);
    double getRemainingLeaveCredits(int empId, String leaveType);
    void approveLeave(int leaveId);
    double calculateLeaveDays(String start, String end);
    public boolean updateLeaveStatus(int empId, String startDate, String newStatus);
    
    // ADD THIS LINE TO THE INTERFACE
    List<LeaveRequest> getEmployeeLeaveHistory(int empId);
}

