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
import com.opencsv.exceptions.CsvValidationException;
import model.Employee;
import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String LOGIN_FILE = "data/employee_logins.csv";
    // We create a reference to EmployeeRepository to share its logic
    private final EmployeeRepository employeeRepo = new EmployeeRepository();

    public Employee authenticate(String username, String password) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header

            while ((row = reader.readNext()) != null) {
                if (row.length >= 7) {
                    String storedUsername = row[5].trim();
                    String storedPassword = row[6].trim();

                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        int empId = Integer.parseInt(row[0].trim());
                        // Success: Call the EmployeeRepository to get the specific role (Admin, HR, etc.)
                        return employeeRepo.findById(empId).orElse(null);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }

    // FIX: Changed to use employeeRepo.findById to fix the "cannot find symbol" error
    public String getSecurityQuestion(String empId) {
        try {
            int id = Integer.parseInt(empId.trim());
            return employeeRepo.findById(id)
                .map(emp -> emp.getSecurityQuestion()) // Assumes getSecurityQuestion exists in Employee model
                .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean verifySecurityAnswer(String empId, String inputAnswer) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                // Ensure row has enough columns for the security answer (index 8)
                if (row.length >= 9 && row[0].trim().equals(empId.trim())) {
                    return row[8].trim().equalsIgnoreCase(inputAnswer.trim());
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error verifying answer: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePassword(String empId, String newPassword) {
        List<String[]> allData = new ArrayList<>();
        boolean found = false;

        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row[0].trim().equals(empId.trim())) {
                    row[6] = newPassword; 
                    found = true;
                }
                allData.add(row);
            }
        } catch (IOException | CsvValidationException e) {
            return false;
        }

        if (found) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(LOGIN_FILE))) {
                writer.writeAll(allData);
                return true;
            } catch (IOException e) {
                System.err.println("Error writing password update: " + e.getMessage());
            }
        }
        return false;
    }
}