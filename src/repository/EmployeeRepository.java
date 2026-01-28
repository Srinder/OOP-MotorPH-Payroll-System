//The Repository acts as the Data Access Layer.
//It handles the "CRUD" operations (Create, Read, Update, Delete) for Employees.

package repository;

import model.Employee;
import model.AdminStaff;
import model.HRStaff;
import model.FinanceStaff;
import model.ITStaff;
import model.RegularEmployee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javax.swing.JOptionPane;



public class EmployeeRepository extends BaseRepository<Employee> {

    private static final String FILE_PATH = "data/employee_info.csv";

    //ABSTRACTION: Implements the abstract method from BaseRepository.
    //This tells the parent class exactly which file this repository manages.
    @Override
    protected String getFilePath() {
    java.io.File file = new java.io.File("data/employee_info.csv");
    System.out.println("DEBUG: Looking for file at: " + file.getAbsolutePath());
    System.out.println("DEBUG: Does it exist? " + file.exists());
    return file.getPath();
}
    
    //READ OPERATION: Loads all data from the CSV.
    //It transforms raw text rows into a List of Employee objects.
    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(getFilePath()))) {
            reader.readNext(); // Skip header
            String[] row;
        while ((row = reader.readNext()) != null) {
            if (row.length >= 19) { 
                employees.add(mapRowToEmployee(row));
            }
        }
    } catch (Exception e) {
        System.err.println("Error reading CSV: " + e.getMessage());
    }
    return employees;
}
    
    //SEARCH OPERATION: Uses Java Streams to find a specific employee by ID.
    //Returns an Optional to handle cases where the ID might not exist safely.
    @Override
    public Optional<Employee> findById(int id) {
        return findAll().stream()
                .filter(e -> e.getEmployeeNumber() == id)
                .findFirst();
    }
    
    //CREATE OPERATION: Appends a new employee record to the end of the CSV file.
    @Override
    public void save(Employee employee) {
        try (FileWriter fw = new FileWriter(getFilePath(), true);
         CSVWriter writer = new CSVWriter(fw)) {
        
        writer.writeNext(formatEmployeeData(employee));
        System.out.println("Successfully added employee to CSV.");
        
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "File is currently locked. Please close Excel and try again.");
        e.printStackTrace();
    }
}
    
    //UPDATE OPERATION: Replaces an existing employee's data.
    //It loads all records, swaps the updated one, and rewrites the whole file.
    @Override
    public void update(Employee updatedEmployee) {
        List<Employee> employees = findAll();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeNumber() == updatedEmployee.getEmployeeNumber()) {
                employees.set(i, updatedEmployee);
                break;
            }
        }
        saveAll(employees);
    }
    
    
    //DELETE OPERATION: Removes an employee by ID using a filter.
    @Override
    public void delete(int id) {
        List<Employee> employees = findAll();
        employees.removeIf(e -> e.getEmployeeNumber() == id);
        saveAll(employees);
    }

    //Helper to rewrite the entire CSV file.
    private void saveAll(List<Employee> employees) {
        try (FileWriter fw = new FileWriter(getFilePath());
         CSVWriter writer = new CSVWriter(fw)) {
        
        writer.writeNext(getHeader());
        for (Employee emp : employees) {
            writer.writeNext(formatEmployeeData(emp));
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error saving: " + e.getMessage());
    }
}
    
    //---------Private Helper Methods-------------
    
    //FILE MANAGEMENT: Overwrites the CSV file with the current List of employees.
    //POLYMORPHISM: This method reads the text from the CSV and builds the
    //correct Java Subclass. We use the 21st column (Index 20) for the Role.

    private Employee mapRowToEmployee(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        String lastName = data[1].trim();
        String firstName = data[2].trim();
        String phone = data[3].trim();
        String status = data[4].trim();
        String position = data[5].trim();
        String supervisor = data[6].trim();
        String address = data[7].trim();
        String sss = data[8].trim();
        String philhealth = data[9].trim();
        String tin = data[10].trim();
        String pagibig = data[11].trim();
        double basic = parseDouble(data[12]);
        double rice = parseDouble(data[13]);
        double phoneAllowance = parseDouble(data[14]);
        double clothing = parseDouble(data[15]);
        double semiMonthly = parseDouble(data[16]);
        double hourly = parseDouble(data[17]);
        double tax = parseDouble(data[18]);
        String birthday = data[19].trim();
        
        // SAFE CHECK for Role (Index 20)
        String role = "REGULAR"; 
        if (data.length > 20) {
            role = data[20].trim().toUpperCase();
        }
    
        // Instantiate the correct concrete class based on the role column
        return switch (role) {
            case "ADMIN" -> new AdminStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "HR" -> new HRStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "Finance" -> new FinanceStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "IT" -> new ITStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            default  -> new RegularEmployee(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
        };
    }

        //ENCAPSULATION: Turns the Object back into a String array for the CSV.
    private String[] formatEmployeeData(Employee e) {
        // Determine role string for the 21st column
        String role = "EMPLOYEE";
        if (e instanceof AdminStaff) {
            role = "ADMIN";
        } else if (e instanceof HRStaff) {
            role = "HR";
        } else if (e instanceof FinanceStaff) {
            role = "FINANCE";
        } else if (e instanceof ITStaff) {
            role = "IT";
        }

        return new String[]{
            String.valueOf(e.getEmployeeNumber()), e.getLastName(), e.getFirstName(),
            e.getPhoneNumber(), e.getStatus(), e.getPosition(), e.getSupervisor(),
            e.getAddress(), e.getSssNumber(), e.getPhilHealthNumber(), e.getTinNumber(),
            e.getPagIbigNumber(), String.valueOf(e.getBasicSalary()),
            String.valueOf(e.getRiceSubsidy()), String.valueOf(e.getPhoneAllowance()),
            String.valueOf(e.getClothingAllowance()), String.valueOf(e.getGrossSemiMonthlyRate()),
            String.valueOf(e.getHourlyRate()), String.valueOf(e.getWithholdingTax()),
            e.getBirthday(), role
        };
    }
        // The header includes "AccessRole"
    private String[] getHeader() {
    return new String[]{
        "EmpNum","LastName","FirstName","PhoneNumber","Status","Position",
        "Supervisor","Address","SSS","PHILHEALTH","TIN","PAGIBIG",
        "Basic Salary","Rice Subsidy","Phone Allowance","Clothing Allowance",
        "Gross Semi-monthly Rate","Hourly Rate","Withholding Tax","Birthday",
        "AccessRole" 
    };
}
        // Prevents crashes by handling non-numeric data gracefully.
    private double parseDouble(String value) {
        try {
            return (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("N/A")) ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void deleteEmployee(int empNum) {
        // This calls existing delete(int id) method which handles the CSV rewriting
        this.delete(empNum);
    }

    public void deleteEmployeeLogin(int empNum) {
        System.out.println("Login credentials for " + empNum + " removed.");
    }
}