import sys

file_path = r'E:\Apache Netbeans\NetbeansProjects\MotorPHms2\src\view\MainMenu.java'

# Read the entire file
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Define the old code (exactly as it appears, including all whitespace)
old_code = """    //Default constructor (optional for testing or fallback)
    public MainMenu() {
        initComponents();
        // Fetch the globally stored user session
        this.currentUser = model.User.getLoggedInUser(); 
        updateWelcomeMessage();
        applyRolePermissions();
      
    }"""

# Define the new code
new_code = """    //Default constructor (optional for testing or fallback)
    public MainMenu() {
        this(model.User.getLoggedInUser(), 
             new AttendanceService(), 
             new EmployeeManagementService(), 
             new SalaryService());
    }"""

# Replace
if old_code in content:
    content = content.replace(old_code, new_code)
    print("✓ First constructor replaced successfully")
else:
    print("✗ First constructor pattern not found")
    print("\nSearching for 'public MainMenu() {' lines...")
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if 'public MainMenu()' in line:
            print(f"\nFound at line {i+1}:")
            for j in range(max(0, i-2), min(len(lines), i+15)):
                print(f"{j+1}: {repr(lines[j])}")
    sys.exit(1)

# Write the file back
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("\n✓ File updated successfully!")

# Verify
with open(file_path, 'r', encoding='utf-8') as f:
    verify_content = f.read()

# Check if first constructor is correct
if 'this(model.User.getLoggedInUser(),' in verify_content:
    print("✓ Verification: New first constructor is in place")
else:
    print("✗ Verification failed")
    sys.exit(1)

# Check if we have all three constructors
if verify_content.count('public MainMenu(') == 3:
    print("✓ Verification: All three constructors are present")
else:
    count = verify_content.count('public MainMenu(')
    print(f"✗ Verification: Expected 3 constructors, found {count}")
    sys.exit(1)

print("\n✓ All checks passed!")
