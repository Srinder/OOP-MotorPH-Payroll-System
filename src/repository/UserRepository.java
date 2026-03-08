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
import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String LOGIN_FILE = "data/employee_logins.csv";

    public Optional<String[]> findLoginRowByUsername(String username) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header

            while ((row = reader.readNext()) != null) {
                if (row.length >= 7 && username != null) {
                    String storedUsername = row[5].trim();
                    if (storedUsername.equals(username.trim())) {
                        return Optional.of(Arrays.copyOf(row, row.length));
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public String getSecurityQuestion(String empId) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                if (row.length >= 8 && row[0].trim().equals(empId.trim())) {
                    return row[7].trim();
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error getting security question: " + e.getMessage());
        }
        return null;
    }

    public String getSecurityAnswer(String empId) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                // Ensure row has enough columns for the security answer (index 8)
                if (row.length >= 9 && row[0].trim().equals(empId.trim())) {
                    return row[8].trim();
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error verifying answer: " + e.getMessage());
        }
        return null;
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

    public boolean createLoginCredentials(int empNum,
                                          String lastName,
                                          String firstName,
                                          String position,
                                          String supervisor,
                                          String username,
                                          String password,
                                          String securityQuestion,
                                          String securityAnswer,
                                          String accessLevel,
                                          String email) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(LOGIN_FILE, true))) {
            String[] loginRow = {
                    String.valueOf(empNum),
                    lastName != null ? lastName : "",
                    firstName != null ? firstName : "",
                    position != null ? position : "NA",
                    supervisor != null ? supervisor : "NA",
                    username != null ? username : "",
                    password != null ? password : "",
                    securityQuestion != null ? securityQuestion : "",
                    securityAnswer != null ? securityAnswer : "",
                    accessLevel != null ? accessLevel : "EMPLOYEE",
                    email != null ? email : ""
            };
            writer.writeNext(loginRow, false);
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error creating login credentials: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete user from employee_logins.csv by employee number
     */
    public boolean deleteByEmployeeNumber(int empNum) {
        List<String[]> allData = new ArrayList<>();
        boolean found = false;

        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                // Keep the header row
                if (row.length > 0 && !row[0].trim().matches("\\d+")) {
                    allData.add(row); // Keep header
                } else if (row.length > 0 && row[0].trim().equals(String.valueOf(empNum))) {
                    // Skip this row (delete it)
                    found = true;
                } else {
                    // Keep all other rows
                    allData.add(row);
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading login file for deletion: " + e.getMessage());
            return false;
        }

        if (found) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(LOGIN_FILE))) {
                writer.writeAll(allData);
                return true;
            } catch (IOException e) {
                System.err.println("Error writing login file after deletion: " + e.getMessage());
            }
        }
        return false;
    }
}
