package model;

/**
 * Represents a single attendance entry for an employee.
 * This model is designed to match the MotorPH Attendance Record CSV structure.
 */
public class AttendanceRecord {
    // Private fields to ensure Encapsulation
    private String empNo;
    private String lastName;
    private String firstName;
    private String date;
    private String logIn;
    private String logOut;

    
    public AttendanceRecord(String empNo, String lastName, String firstName,
                            String date, String logIn, String logOut) {
        this.empNo = empNo;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
    }


    public String getEmpNo() {
        return empNo;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDate() {
        return date;
    }

    public String getLogIn() {
        return logIn;
    }

    public String getLogOut() {
        return logOut;
    }

    // --- SETTERS (Used when updating logs, e.g., clocking out) ---

    public void setLogIn(String logIn) {
        this.logIn = logIn;
    }

    public void setLogOut(String logOut) {
        this.logOut = logOut;
    }

    // Helper method to get the full name easily
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getTimeIn(){
        return logIn;
    }
    
    public String getTimeOut(){
        return logOut;
    }
        
        
    }