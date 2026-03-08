//The Repository acts as the Data Access Layer.
//It handles the "CRUD" operations (Create, Read, Update, Delete) for Employees.

package repository;

import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class EmployeeRepository extends BaseRepository<Employee> {

    private static final String FILE_PATH = "data/employee_info.csv";

    @Override
    protected String getFilePath() {
        return FILE_PATH;
    }
    
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
    
    @Override
    public Optional<Employee> findById(int id) {
        return findAll().stream()
                .filter(e -> e.getEmployeeNumber() == id)
                .findFirst();
    }
    
    public Optional<Employee> findByIdWithRole(int id, String accessRole) {
        try (CSVReader reader = new CSVReader(new FileReader(getFilePath()))) {
            reader.readNext(); // Skip header
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length >= 1 && Integer.parseInt(row[0].trim()) == id) {
                    Employee emp = mapRowToEmployeeWithRole(row, accessRole);
                    return Optional.of(emp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding employee by ID with role: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public void save(Employee employee) {
        try (FileWriter fw = new FileWriter(getFilePath(), true);
             CSVWriter writer = new CSVWriter(fw)) {
            writer.writeNext(formatEmployeeData(employee));
        } catch (IOException e) {
            // Throwing the exception allows the GUI/Service layer to handle the error display
            throw new RuntimeException("Could not save employee to CSV. File may be locked.", e);
        }
    }

    public void save(Employee employee, String accessLevel) {
        try (FileWriter fw = new FileWriter(getFilePath(), true);
             CSVWriter writer = new CSVWriter(fw)) {
            writer.writeNext(formatEmployeeData(employee, accessLevel));
        } catch (IOException e) {
            // Throwing the exception allows the GUI/Service layer to handle the error display
            throw new RuntimeException("Could not save employee to CSV. File may be locked.", e);
        }
    }
    
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
    
    @Override
    public void delete(int id) {
        List<Employee> employees = findAll();
        employees.removeIf(e -> e.getEmployeeNumber() == id);
        saveAll(employees);
    }

    private void saveAll(List<Employee> employees) {
        try (FileWriter fw = new FileWriter(getFilePath());
             CSVWriter writer = new CSVWriter(fw)) {
            writer.writeNext(getHeader());
            for (Employee emp : employees) {
                writer.writeNext(formatEmployeeData(emp));
            }
        } catch (IOException e) {
            throw new RuntimeException("Critical Error: Failed to rewrite CSV database.", e);
        }
    }

    private Employee mapRowToEmployee(String[] data) {
        String role = (data.length > 20) ? data[20].trim().toUpperCase() : "REGULAR";
        return buildEmployeeFromRow(data, role);
    }

    private Employee mapRowToEmployeeWithRole(String[] data, String accessRole) {
        String role = (accessRole == null || accessRole.trim().isEmpty())
                ? ((data.length > 20) ? data[20].trim().toUpperCase() : "REGULAR")
                : accessRole.trim().toUpperCase();
        return buildEmployeeFromRow(data, role);
    }

    private Employee buildEmployeeFromRow(String[] data, String role) {
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

        return switch (role) {
            case "ADMIN" -> new AdminStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "HR" -> new HRStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "FINANCE" -> new FinanceStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "IT" -> new ITStaff(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            case "PROBATIONARY" -> new ProbationaryEmployee(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
            default -> new RegularEmployee(id, lastName, firstName, birthday, phone, address, status, position, supervisor, sss, philhealth, tin, pagibig, basic, rice, phoneAllowance, clothing, semiMonthly, hourly, tax);
        };
    }

    private String[] formatEmployeeData(Employee e) {
        return formatEmployeeData(e, e.getRole());
    }

    private String[] formatEmployeeData(Employee e, String accessLevel) {
        return new String[]{
            String.valueOf(e.getEmployeeNumber()), e.getLastName(), e.getFirstName(),
            e.getPhoneNumber(), e.getStatus(), e.getPosition(), e.getSupervisor(),
            e.getAddress(), e.getSssNumber(), e.getPhilHealthNumber(), e.getTinNumber(),
            e.getPagIbigNumber(), String.valueOf(e.getBasicSalary()),
            String.valueOf(e.getRiceSubsidy()), String.valueOf(e.getPhoneAllowance()),
            String.valueOf(e.getClothingAllowance()), String.valueOf(e.getGrossSemiMonthlyRate()),
            String.valueOf(e.getHourlyRate()), String.valueOf(e.getWithholdingTax()),
            e.getBirthday(),
            accessLevel
        };
    }

    private String[] getHeader() {
        return new String[]{
            "EmpNum","LastName","FirstName","PhoneNumber","Status","Position",
            "Supervisor","Address","SSS","PHILHEALTH","TIN","PAGIBIG",
            "Basic Salary","Rice Subsidy","Phone Allowance","Clothing Allowance",
            "Gross Semi-monthly Rate","Hourly Rate","Withholding Tax","Birthday",
            "AccessRole" 
        };
    }

    private double parseDouble(String value) {
        try {
            return (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("N/A")) ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
