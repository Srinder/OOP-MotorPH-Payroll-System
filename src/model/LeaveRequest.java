/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author singh
 */
public class LeaveRequest {
    
    private int leaveId;
    private String empNo;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String status; // e.g., "Pending", "Approved", "Rejected"

    // Constructor
    public LeaveRequest(int leaveId, String empNo, String leaveType, String startDate, String endDate, String status) {
        this.leaveId = leaveId;
        this.empNo = empNo;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
    
    //getters

    public int getLeaveId() {
        return leaveId;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
    
    // Setters
        public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
    