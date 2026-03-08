package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.LeaveRequest;
import repository.LeaveRepository;

public class LeaveManagement implements ILeaveManagement {
    private final LeaveRepository repo = new LeaveRepository();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("M/d/yy");

    @Override
    public boolean updateLeaveStatus(int empId, String startDate, String newStatus) {
        return repo.updateStatus(empId, startDate, newStatus);
    }

    @Override
    public boolean updateLeaveStatusByDetails(int empId, String leaveType, String startDate, String endDate, String reason, String newStatus) {
        return repo.updateStatusByDetails(empId, leaveType, startDate, endDate, reason, newStatus);
    }

    @Override
    public List<LeaveRequest> getEmployeeLeaveHistory(int empId) {
        List<LeaveRequest> allRequests = repo.getAllRequests();
        List<LeaveRequest> filteredRequests = new ArrayList<>();
        for (LeaveRequest req : allRequests) {
            if (req.getEmployeeId() == empId) {
                filteredRequests.add(req);
            }
        }
        return filteredRequests;
    }

    @Override
    public List<LeaveRequest> getEmployeeLeaveHistoryFiltered(int empId, String statusFilter) {
        List<LeaveRequest> history = getEmployeeLeaveHistory(empId);
        if (statusFilter == null || statusFilter.trim().isEmpty() || "All".equalsIgnoreCase(statusFilter)) {
            return history;
        }
        List<LeaveRequest> filtered = new ArrayList<>();
        for (LeaveRequest request : history) {
            if (statusFilter.equalsIgnoreCase(request.getStatus())) {
                filtered.add(request);
            }
        }
        return filtered;
    }

    @Override
    public boolean applyLeave(int empID, String leaveType, String startDate, String endDate, String reason) {
        double requestedDays = calculateLeaveDays(startDate, endDate);
        double remaining = getRemainingLeaveCredits(empID, leaveType);

        if (requestedDays <= remaining) {
            LeaveRequest newReq = new LeaveRequest(empID, leaveType, startDate, endDate, reason, "Pending");
            return repo.saveRequest(newReq);
        }
        return false;
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

        List<LeaveRequest> allRequests = repo.getAllRequests();
        double used = 0;
        for (LeaveRequest req : allRequests) {
            if (req.getEmployeeId() == empId && req.getLeaveType().equalsIgnoreCase(type)
                    && req.getStatus().equalsIgnoreCase("Approved")) {
                used += calculateLeaveDays(req.getStartDate(), req.getEndDate());
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
            return 0;
        }
    }

    @Override
    public int getLeaveDaysInt(String start, String end) {
        return (int) calculateLeaveDays(start, end);
    }

    @Override
    public int getLeaveDaysInt(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        String startText = normalizeDateForStorage(start);
        String endText = normalizeDateForStorage(end);
        return getLeaveDaysInt(startText, endText);
    }

    @Override
    public String normalizeDateForStorage(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(dateText, formatter);
            return date.format(formatter);
        } catch (Exception ignored) {
        }
        try {
            LocalDate date = LocalDate.parse(dateText, displayFormatter);
            return date.format(formatter);
        } catch (Exception ignored) {
        }
        return dateText.trim();
    }

    @Override
    public String normalizeDateForStorage(Date dateValue) {
        if (dateValue == null) {
            return "";
        }
        LocalDate date = dateValue.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
        return date.format(formatter);
    }

    @Override
    public String formatDateForDisplay(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(rawDate, formatter);
            return date.format(displayFormatter);
        } catch (Exception ex) {
            return rawDate;
        }
    }

    @Override
    public int processStatusUpdates(List<LeaveRequest> selectedRequests, String targetStatus) {
        if (selectedRequests == null || selectedRequests.isEmpty()) {
            return 0;
        }
        String normalizedTarget = targetStatus == null ? "" : targetStatus.trim();
        if (!"Approved".equalsIgnoreCase(normalizedTarget) && !"Rejected".equalsIgnoreCase(normalizedTarget)) {
            return 0;
        }

        int updated = 0;
        for (LeaveRequest request : selectedRequests) {
            if (request == null) {
                continue;
            }
            int empId = request.getEmployeeId();
            String leaveType = request.getLeaveType() == null ? "" : request.getLeaveType().trim();
            String startDate = normalizeDateForStorage(request.getStartDate());
            String endDate = normalizeDateForStorage(request.getEndDate());
            String currentStatus = request.getStatus() == null ? "" : request.getStatus().trim();
            String reason = request.getReason() == null ? "" : request.getReason().trim();

            if (!"PENDING".equalsIgnoreCase(currentStatus)) {
                continue;
            }
            if (updateLeaveStatusByDetails(empId, leaveType, startDate, endDate, reason, normalizedTarget)) {
                updated++;
            }
        }
        return updated;
    }

    @Override
    public int cancelOwnPendingLeave(int currentEmployeeId, int requestEmployeeId, String requestStartDate, String requestStatus) {
        if (requestEmployeeId != currentEmployeeId) {
            return CANCEL_NOT_OWNER;
        }
        if (requestStatus == null || !"Pending".equalsIgnoreCase(requestStatus.trim())) {
            return CANCEL_NOT_PENDING;
        }
        String startDate = normalizeDateForStorage(requestStartDate);
        boolean updated = updateLeaveStatus(requestEmployeeId, startDate, "Cancelled");
        return updated ? CANCEL_SUCCESS : CANCEL_FAILED;
    }

    @Override
    public int[] getLeaveCreditsSummary(int empId) {
        int vacation = (int) getRemainingLeaveCredits(empId, "Vacation");
        int sick = (int) getRemainingLeaveCredits(empId, "Sick");
        int emergency = (int) getRemainingLeaveCredits(empId, "Emergency");
        int total = vacation + sick + emergency;
        return new int[]{vacation, sick, emergency, total};
    }
}
