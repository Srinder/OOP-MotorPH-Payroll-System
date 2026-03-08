//Implementation of Employee Management operations.
//Handles employee CRUD operations, validation, and business rules.

package service;

import repository.EmployeeRepository;
import repository.UserRepository;
import model.Employee;
import model.RegularEmployee;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeManagementService implements IEmployeeManagementService {
    
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final UserRepository userRepository = new UserRepository();
    
    // Validation constants
    private static final String PHONE_PATTERN = "^[0-9\\-\\s()]+$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    @Override
    public Optional<Employee> getEmployeeById(int employeeId) {
        return employeeRepository.findById(employeeId);
    }
    
    @Override
    public int getNextEmployeeNumber() {
        List<Employee> employees = employeeRepository.findAll();
        
        if (employees.isEmpty()) {
            return 10001;
        }
        
        return employees.stream()
            .mapToInt(Employee::getEmployeeNumber)
            .max()
            .orElse(10000) + 1;
    }
    
    @Override
    public String generateDefaultUsername(String firstName, String lastName, int empNum) {
        if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {
            return (firstName.charAt(0) + lastName).toLowerCase().replaceAll("\\s+", "");
        }
        return "emp" + empNum;
    }

    @Override
    public String getDefaultPassword() {
        return "Password123";
    }

    @Override
    public String getDefaultSecurityQuestion() {
        return "What is your favorite food?";
    }

    @Override
    public String getDefaultEmail() {
        return "Abcdefg001@motorph.com";
    }

    @Override
    public boolean registerEmployeeWithLogin(int empNum,
                                             String lastName,
                                             String firstName,
                                             String phoneNumber,
                                             String status,
                                             String position,
                                             String sssNumber,
                                             String philHealthNumber,
                                             String tinNumber,
                                             String pagIbigNumber,
                                             String accessLevel,
                                             String securityAnswer) {
        String normalizedLastName = safeOrEmpty(lastName);
        String normalizedFirstName = safeOrEmpty(firstName);
        String normalizedPhone = safeOrEmpty(phoneNumber);
        String normalizedStatus = safeOrDefault(status, "NA");
        String normalizedPosition = safeOrDefault(position, "NA");
        String normalizedSss = safeOrDefault(sssNumber, "NA");
        String normalizedPhilHealth = safeOrDefault(philHealthNumber, "NA");
        String normalizedTin = safeOrDefault(tinNumber, "NA");
        String normalizedPagIbig = safeOrDefault(pagIbigNumber, "NA");
        String normalizedAccess = safeOrDefault(accessLevel, "EMPLOYEE");
        String normalizedAnswer = safeOrEmpty(securityAnswer);

        if (normalizedLastName.isEmpty() || normalizedFirstName.isEmpty() || normalizedPhone.isEmpty() || normalizedAnswer.isEmpty()) {
            return false;
        }

        Employee employee = new RegularEmployee(
                empNum,
                normalizedLastName,
                normalizedFirstName,
                "NA",
                normalizedPhone,
                "NA",
                normalizedStatus,
                normalizedPosition,
                "NA",
                normalizedSss,
                normalizedPhilHealth,
                normalizedTin,
                normalizedPagIbig,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0
        );

        String username = generateDefaultUsername(normalizedFirstName, normalizedLastName, empNum);
        return createEmployeeWithLogin(
                employee,
                normalizedAccess,
                username,
                getDefaultPassword(),
                getDefaultSecurityQuestion(),
                normalizedAnswer,
                getDefaultEmail()
        );
    }

    @Override
    public boolean createEmployeeWithLogin(Employee employee,
                                           String accessLevel,
                                           String username,
                                           String password,
                                           String securityQuestion,
                                           String securityAnswer,
                                           String email) {
        if (!validateEmployeeData(employee)) {
            return false;
        }
        try {
            employeeRepository.save(employee, accessLevel);
            boolean loginSaved = userRepository.createLoginCredentials(
                    employee.getEmployeeNumber(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    employee.getPosition(),
                    employee.getSupervisor(),
                    username,
                    password,
                    securityQuestion,
                    securityAnswer,
                    accessLevel,
                    email
            );
            if (!loginSaved) {
                employeeRepository.delete(employee.getEmployeeNumber());
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean updateEmployee(Employee employee) {
        if (!validateEmployeeData(employee)) {
            return false;
        }
        
        try {
            employeeRepository.update(employee);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean updateEmployeeFromForm(Employee employee,
                                          String lastName,
                                          String position,
                                          String phoneNumber,
                                          String status,
                                          String supervisor,
                                          String address,
                                          String birthday,
                                          String sssNumber,
                                          String philHealthNumber,
                                          String tinNumber,
                                          String pagIbigNumber,
                                          String basicSalaryText,
                                          String hourlyRateText,
                                          String phoneAllowanceText,
                                          String clothingAllowanceText,
                                          String riceSubsidyText) {
        if (employee == null) {
            return false;
        }

        employee.setLastName(resolveText(lastName, employee.getLastName()));
        employee.setPosition(resolveText(position, employee.getPosition()));
        employee.setPhoneNumber(resolveText(phoneNumber, employee.getPhoneNumber()));
        employee.setStatus(resolveText(status, employee.getStatus()));
        employee.setSupervisor(resolveText(supervisor, employee.getSupervisor()));
        employee.setAddress(resolveText(address, employee.getAddress()).replaceAll("^\"|\"$", ""));
        employee.setBirthday(resolveText(birthday, employee.getBirthday()));
        employee.setSssNumber(resolveText(sssNumber, employee.getSssNumber()));
        employee.setPhilHealthNumber(resolveText(philHealthNumber, employee.getPhilHealthNumber()));
        employee.setTinNumber(resolveText(tinNumber, employee.getTinNumber()));
        employee.setPagIbigNumber(resolveText(pagIbigNumber, employee.getPagIbigNumber()));

        employee.setBasicSalary(parseDoubleOrCurrent(basicSalaryText, employee.getBasicSalary()));
        employee.setHourlyRate(parseDoubleOrCurrent(hourlyRateText, employee.getHourlyRate()));
        employee.setPhoneAllowance(parseDoubleOrCurrent(phoneAllowanceText, employee.getPhoneAllowance()));
        employee.setClothingAllowance(parseDoubleOrCurrent(clothingAllowanceText, employee.getClothingAllowance()));
        employee.setRiceSubsidy(parseDoubleOrCurrent(riceSubsidyText, employee.getRiceSubsidy()));

        return updateEmployee(employee);
    }

    @Override
    public boolean deleteEmployeeAndLogin(int employeeId) {
        try {
            employeeRepository.delete(employeeId);
            userRepository.deleteByEmployeeNumber(employeeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean validateEmployeeData(Employee employee) {
        if (employee == null) return false;
        
        // Check required fields
        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            return false;
        }
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            return false;
        }
        if (employee.getPhoneNumber() == null || employee.getPhoneNumber().trim().isEmpty()) {
            return false;
        }
        if (employee.getStatus() == null || employee.getStatus().trim().isEmpty()) {
            return false;
        }
        if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
            return false;
        }
        
        // Validate phone number format
        if (!isValidPhoneNumber(employee.getPhoneNumber())) {
            return false;
        }
        
        // Validate email if present
        if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
            if (!employee.getEmail().matches(EMAIL_PATTERN)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isValidPhoneNumber(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || !trimmed.matches(PHONE_PATTERN)) {
            return false;
        }
        String digitsOnly = trimmed.replaceAll("\\D", "");
        return digitsOnly.length() >= 9 && digitsOnly.length() <= 11;
    }

    private String safeOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeOrDefault(String value, String defaultValue) {
        String normalized = safeOrEmpty(value);
        return normalized.isEmpty() ? defaultValue : normalized;
    }

    private String resolveText(String candidate, String currentValue) {
        String normalized = safeOrEmpty(candidate);
        return normalized.isEmpty() ? currentValue : normalized;
    }

    private double parseDoubleOrCurrent(String value, double currentValue) {
        try {
            String cleanedValue = safeOrEmpty(value).replace("â‚±", "").replace(",", "");
            return cleanedValue.isEmpty() ? currentValue : Double.parseDouble(cleanedValue);
        } catch (NumberFormatException e) {
            return currentValue;
        }
    }
    
    @Override
    public List<Employee> searchEmployees(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEmployees();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        
        return employeeRepository.findAll().stream()
            .filter(emp -> 
                String.valueOf(emp.getEmployeeNumber()).contains(lowerSearchTerm) ||
                emp.getLastName().toLowerCase().contains(lowerSearchTerm) ||
                emp.getFirstName().toLowerCase().contains(lowerSearchTerm) ||
                emp.getPhoneNumber().contains(lowerSearchTerm)
            )
            .collect(Collectors.toList());
    }
    
}
