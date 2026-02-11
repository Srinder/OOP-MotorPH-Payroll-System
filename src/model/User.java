package model;

import java.io.BufferedReader;
import java.io.FileReader;
import model.AdminStaff;
import model.Employee;
import model.FinanceStaff;
import model.HRStaff;
import model.ITStaff;
import model.RegularEmployee;

//Represents the User entity for login credentials.
public class User {
    private String employeeId;
    private String username;
    private String password;
    private String role;

    //Static field to track who is currently using the app
    private static Employee loggedInUser;

    public User(String employeeId,String username, String password, String role) {
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }



    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    

    public static void setLoggedInUser(Employee emp) { 
        loggedInUser = emp; }
    
    public static Employee getLoggedInUser() { 
        return loggedInUser; }
}