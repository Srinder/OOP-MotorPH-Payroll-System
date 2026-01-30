package model;

import java.io.BufferedReader;
import java.io.FileReader;
import model.AdminStaff;
import model.Employee;
import model.FinanceStaff;
import model.HRStaff;
import model.ITStaff;
import model.RegularEmployee;

public class User {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;
    private String email;

    private static Employee loggedInUser;

    public User(String employeeId, String firstName, String lastName,
                String username, String password, String role, String email) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public static Employee authenticate(String enteredUser, String enteredPass) {
        String path = "data/employee_logins.csv"; 
       
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
               
                if (data.length >= 11) {
                    String EmpNum = data[0];
                    String LastName = data[1];
                    String FirstName = data[2];
                    // Columns 3, 4, 7, 8 are parsed but not used for the constructor
                    String Username = data[5];
                    String Password = data[6];
                    String Access = data[9].trim().toUpperCase(); 
                    String emailAddress = data[10];
                    

                    if (Username.equals(enteredUser) && Password.equals(enteredPass)) {
                        int empIdInt = Integer.parseInt(EmpNum); // Fixed: changed 'id' to 'EmpNum'
                        
                        // POLYMORPHISM: Fixed: changed 'roleFromCSV' to 'Access'
                        // and changed 'lname/fname' to match your variables above
                        switch (Access) {
                            case "ADMIN":
                                return new AdminStaff(empIdInt, LastName, FirstName, emailAddress); 
                            case "HR":
                                return new HRStaff(empIdInt, LastName, FirstName, emailAddress);
                            case "FINANCE": 
                                return new FinanceStaff(empIdInt, LastName, FirstName, emailAddress);
                            case "IT":
                                return new ITStaff(empIdInt, LastName, FirstName, emailAddress);
                            default:
                                return new RegularEmployee(empIdInt, LastName, FirstName, emailAddress);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    // --- Getters and Setters ---
    public String getEmail()      { return email; }
    public String getEmployeeId() { return employeeId; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }
    public String getRole()       { return role; }

    public static void setLoggedInUser(Employee emp) { 
        loggedInUser = emp; }
    
    public static Employee getLoggedInUser() { 
        return loggedInUser; }
}