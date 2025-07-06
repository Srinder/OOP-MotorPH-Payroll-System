public class AttendanceRecord {
    public String empNo;
    public String lastName;
    public String firstName;
    public String date;
    public String logIn;
    public String logOut;

    public AttendanceRecord(String empNo, String lastName, String firstName,
                            String date, String logIn, String logOut) {
        this.empNo = empNo;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
    }
}
