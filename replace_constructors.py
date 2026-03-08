#!/usr/bin/env python3
"""Replace MainMenu constructors"""

file_path = r'E:\Apache Netbeans\NetbeansProjects\MotorPHms2\src\view\MainMenu.java'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_code = '''    //Default constructor (optional for testing or fallback)
    public MainMenu() {
        initComponents();
        // Fetch the globally stored user session
        this.currentUser = model.User.getLoggedInUser(); 
        updateWelcomeMessage();
        applyRolePermissions();
      
    }

    //Constructor with user info passed from Login

    public MainMenu(Employee employee) {
        this.currentUser = employee;
        initComponents();
        initModules();
        updateWelcomeMessage();
        applyRolePermissions();
    }'''

new_code = '''    //Default constructor (optional for testing or fallback)
    public MainMenu() {
        this(model.User.getLoggedInUser(), 
             new AttendanceService(), 
             new EmployeeManagementService(), 
             new SalaryService());
    }

    //Constructor with user info passed from Login

    public MainMenu(Employee employee) {
        this(employee, 
             new AttendanceService(), 
             new EmployeeManagementService(), 
             new SalaryService());
    }
    
    //Constructor with dependency injection
    public MainMenu(Employee employee, IAttendanceService attendanceService, 
                    IEmployeeManagementService employeeService, ISalaryService salaryService) {
        this.currentUser = employee;
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.salaryService = salaryService;
        initComponents();
        initModules();
        updateWelcomeMessage();
        applyRolePermissions();
    }'''

if old_code in content:
    content = content.replace(old_code, new_code)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("SUCCESS: Constructors replaced!")
else:
    print("ERROR: Old code pattern not found")
    # Debug: show lines around the target
    lines = content.split('\n')
    for i in range(34, min(54, len(lines))):
        print(f"Line {i+1}: {repr(lines[i])}")
