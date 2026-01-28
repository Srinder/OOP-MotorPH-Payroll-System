package repository;

import model.User;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.util.*;
import model.Employee;

public class UserDatabase {
    private static final String LOGIN_FILE = "data/employee_logins.csv";

    // ✅ Authenticate user and capture role
    public static Employee authenticate(String username, String password) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header

            while ((row = reader.readNext()) != null) {
                if (row.length >= 10) {
                    String storedUsername = row[5].trim();
                    String storedPassword = row[6].trim();

                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    // 1. Get the Employee ID from the login file
                    int empId = Integer.parseInt(row[0].trim());

                    // 2. Use the Repository to get the FULL Polymorphic object
                    // This returns an Admin, HRStaff, etc., based on the CSV role
                    EmployeeRepository repo = new EmployeeRepository();
                    return repo.findById(empId).orElse(null);
                }
            }
            }
        } catch (IOException | CsvValidationException e) {
        System.err.println("Login error: " + e.getMessage());
    }
    return null; // Login failed
}

    // ✅ Existing: Security Question
    public static String getSecurityQuestion(String empId) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                if (row.length >= 9 && row[0].trim().equals(empId)) {
                    return row[7].trim();
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading security question: " + e.getMessage());
        }
        return null;
    }

    // ✅ Existing: Verify Security Answer
    public static boolean verifySecurityAnswer(String empId, String inputAnswer) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                if (row.length >= 9 && row[0].trim().equals(empId)) {
                    return row[8].trim().equalsIgnoreCase(inputAnswer.trim());
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error verifying answer: " + e.getMessage());
        }
        return false;
    }

    // ✅ Existing: Update Password
    public static boolean updatePassword(String empId, String newPassword) {
        List<String[]> updatedRows = new ArrayList<>();
        boolean success = false;

        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] header = reader.readNext();
            if (header != null) updatedRows.add(header);

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length >= 9 && row[0].trim().equals(empId)) {
                    row[6] = newPassword;
                    success = true;
                }
                updatedRows.add(row);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading for password update: " + e.getMessage());
            return false;
        }

        if (success) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_FILE))) {
                for (String[] row : updatedRows) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error writing updated CSV: " + e.getMessage());
                return false;
            }
        }

        return success;
    }
}
