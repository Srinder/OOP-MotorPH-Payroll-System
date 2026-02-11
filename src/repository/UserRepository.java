/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author singh
 */

import java.io.BufferedReader;
import java.io.FileReader;
import model.AdminStaff;
import model.Employee;
import model.FinanceStaff;
import model.HRStaff;
import model.ITStaff;
import model.RegularEmployee;
import model.User;

public class UserRepository {
    private final String path = "data/employee_logins.csv";
    
    public Employee authenticate(String enteredUser, String enteredPass) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                if (data.length >= 11) {
                    String empNum = data[0];
                    String lastName = data[1];
                    String firstName = data[2];
                    String username = data[5];
                    String password = data[6];
                    String access = data[9].trim().toUpperCase();
                    String email = data[10];
                    if (username.equals(enteredUser) && password.equals(enteredPass)) {
                        int id = Integer.parseInt(empNum);
                        
                        // POLYMORPHISM: Return the specific subclass based on role
                        switch (access) {
                            case "ADMIN": return new AdminStaff(id, lastName, firstName, email);
                            case "HR": return new HRStaff(id, lastName, firstName, email);
                            case "FINANCE": return new FinanceStaff(id, lastName, firstName, email);
                            case "IT": return new ITStaff(id, lastName, firstName, email);
                            default: return new RegularEmployee(id, lastName, firstName, email);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
        }
        return null; // Login failed
    }
}
    