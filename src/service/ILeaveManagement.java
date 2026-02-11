/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

/**
 *
 * @author singh
 */
public interface ILeaveManagement {
    boolean applyLeave(int empID, String leaveType, String startDate, String endDate);
    double getRemainingLeaveCredits(int empId);
    void approveLeave(int leaveId);
    
    double calculateLeaveDays(String start, String end);
    
}
