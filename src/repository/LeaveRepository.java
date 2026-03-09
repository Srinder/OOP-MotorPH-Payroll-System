/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author singh
 */

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LeaveRepository {
    private final String FILE_PATH = "data/leave_requests.csv";
    private static final java.util.Set<String> KNOWN_STATUSES =
            new java.util.HashSet<>(java.util.Arrays.asList("PENDING", "APPROVED", "REJECTED", "CANCELLED"));

    public boolean saveRequest(model.LeaveRequest request) {
    
    try (com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new java.io.FileWriter(FILE_PATH, true))) {
        
       
        String[] record = {
            String.valueOf(request.getEmployeeId()),
            request.getLeaveType(),
            request.getStartDate(),
            request.getEndDate(),
            request.getReason(),
            request.getStatus()
        };
        
        writer.writeNext(record);
        return true;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public boolean updateStatus(int empId, String startDate, String newStatus) {
    // 1. Get the current list of all leave requests from the CSV
    List<model.LeaveRequest> allRequests = getAllRequests(); 
    boolean recordFound = false;

    for (model.LeaveRequest req : allRequests) {
        // 2. Locate the specific record using ID and Start Date
        if (req.getEmployeeId() == empId && req.getStartDate().equals(startDate)) {
            req.setStatus(newStatus); // Update the status (Approved/Rejected)
            recordFound = true;
            break;
        }
    }

    if (recordFound) {
        // 3. Overwrite the CSV with the updated list
        return saveAllRequests(allRequests); 
    }
    return false;
}

    public boolean updateStatusByDetails(int empId, String leaveType, String startDate, String endDate, String reason, String newStatus) {
        List<model.LeaveRequest> allRequests = getAllRequests();
        boolean recordFound = false;

        for (model.LeaveRequest req : allRequests) {
            if (req.getEmployeeId() == empId
                    && req.getLeaveType().equalsIgnoreCase(leaveType)
                    && req.getStartDate().equals(startDate)
                    && req.getEndDate().equals(endDate)
                    && req.getReason().equals(reason)) {
                req.setStatus(newStatus);
                recordFound = true;
                break;
            }
        }

        if (recordFound) {
            return saveAllRequests(allRequests);
        }
        return false;
    }
    
private boolean saveAllRequests(List<model.LeaveRequest> requests) {
    
    String filePath = "data/leave_requests.csv"; 
    
    try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filePath))) {
        for (model.LeaveRequest req : requests) {
            writer.println(req.getEmployeeId() + "," +
                           req.getLeaveType() + "," +
                           req.getStartDate() + "," +
                           req.getEndDate() + "," +
                           req.getReason() + "," +
                           req.getStatus());
        }
        return true;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return false;
    }
}

public java.util.List<model.LeaveRequest> getAllRequests() {
    java.util.List<model.LeaveRequest> list = new java.util.ArrayList<>();
    try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new java.io.FileReader(FILE_PATH))) {
        String[] line;
        while ((line = reader.readNext()) != null) {
            if (line.length >= 6) {
                String first = line[0] == null ? "" : line[0].trim();
                // Skip header if present.
                if (!first.matches("\\d+")) {
                    continue;
                }

                String col5 = line[4] == null ? "" : line[4].trim();
                String col6 = line[5] == null ? "" : line[5].trim();

                String reason;
                String status;
                
                if (KNOWN_STATUSES.contains(col5.toUpperCase())) {
                    status = col5;
                    reason = col6;
                } else {
                    reason = col5;
                    status = col6;
                }

                list.add(new model.LeaveRequest(
                    Integer.parseInt(first),   // ID
                    line[1].trim(),            // Type
                    line[2].trim(),            // Start
                    line[3].trim(),            // End
                    reason,                    // Reason
                    status                     // Status
                ));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list; 
}
}
