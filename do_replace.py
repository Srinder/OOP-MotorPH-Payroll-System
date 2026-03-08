#!/usr/bin/env python3
"""Replace MainMenu constructors - with debugging"""

file_path = r'E:\Apache Netbeans\NetbeansProjects\MotorPHms2\src\view\MainMenu.java'

# Read the file
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

print("=== CURRENT LINES 35-53 ===")
for i in range(34, min(53, len(lines))):
    print(f"Line {i+1}: {repr(lines[i])}")

# Now let's extract exactly lines 35-53 (indices 34-52)
old_lines = lines[34:53]
old_code = ''.join(old_lines)

print("\n=== OLD CODE TO REPLACE ===")
print(repr(old_code))

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
    }
'''

# Replace lines 34-52 (inclusive) with new code
new_lines = lines[:34] + [new_code] + lines[53:]

# Write back
with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("\n=== REPLACEMENT DONE ===")
print("File has been updated!")

# Verify the replacement
with open(file_path, 'r', encoding='utf-8') as f:
    new_content = f.readlines()

print("\n=== VERIFICATION: LINES 35-80 ===")
for i in range(34, min(80, len(new_content))):
    print(f"Line {i+1}: {new_content[i]}", end='')
