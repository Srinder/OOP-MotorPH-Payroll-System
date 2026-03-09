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
import java.util.Date;
import model.LeaveRequest;

public interface ILeaveManagement {
    int CANCEL_SUCCESS = 0;
    int CANCEL_NOT_OWNER = 1;
    int CANCEL_NOT_PENDING = 2;
    int CANCEL_FAILED = 3;

    boolean applyLeave(int empID, String leaveType, String startDate, String endDate, String reason);
    double getRemainingLeaveCredits(int empId, String leaveType);
    double calculateLeaveDays(String start, String end);
    boolean updateLeaveStatus(int empId, String startDate, String newStatus);
    boolean updateLeaveStatusByDetails(int empId, String leaveType, String startDate, String endDate, String reason, String newStatus);
    List<LeaveRequest> getEmployeeLeaveHistory(int empId);
    List<LeaveRequest> getEmployeeLeaveHistoryFiltered(int empId, String statusFilter);
    String normalizeDateForStorage(String dateText);
    String normalizeDateForStorage(Date dateValue);
    String formatDateForDisplay(String rawDate);
    int processStatusUpdates(List<LeaveRequest> selectedRequests, String targetStatus);
    int cancelOwnPendingLeave(int currentEmployeeId, int requestEmployeeId, String requestStartDate, String requestStatus);
    int getLeaveDaysInt(String start, String end);
    int getLeaveDaysInt(Date start, Date end);
    int[] getLeaveCreditsSummary(int empId);
    List<LeaveRequest> mapRowsToLeaveRequests(List<Object[]> rows, boolean statusOnlyMode);
}

