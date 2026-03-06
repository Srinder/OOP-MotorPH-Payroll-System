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

    public void saveRequest(String[] data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, true))) {
            writer.writeNext(data); // Writes 6 columns: ID, Type, Start, End, Reason, Status
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
    
    private boolean saveAllRequests(List<model.LeaveRequest> requests) {
    
    String filePath = "data/leave_requests.csv"; 
    
    try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filePath))) {
        for (model.LeaveRequest req : requests) {
            writer.println(req.getEmployeeId() + "," +
                           req.getLeaveType() + "," +
                           req.getStartDate() + "," +
                           req.getEndDate() + "," +
                           req.getStatus() + "," +
                           req.getReason());
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
        reader.readNext(); // Skip header
        String[] line;
        while ((line = reader.readNext()) != null) {
            if (line.length >= 6) {
                // Convert raw CSV line into a Model object
                list.add(new model.LeaveRequest(
                    Integer.parseInt(line[0]), // ID
                    line[1],                   // Type
                    line[2],                   // Start
                    line[3],                   // End
                    line[4],                   // Reason
                    line[5]                    // Status
                ));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list; 
}
}
