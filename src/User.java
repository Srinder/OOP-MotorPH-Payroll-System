public class User {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;

    private static User loggedInUser;

    public User(String employeeId, String firstName, String lastName,
                String username, String password, String role) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Session tracking
    public static void setLoggedInUser(User u) {
        loggedInUser = u;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getRole()        { return role; }
}
