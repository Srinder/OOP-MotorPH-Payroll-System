//Interface defining the contract for Employee Management operations.

package service;

import model.Employee;
import java.util.List;
import java.util.Optional;

public interface IEmployeeManagementService {
    
    List<Employee> getAllEmployees();
    
    Optional<Employee> getEmployeeById(int employeeId);
    
    int getNextEmployeeNumber();

    String generateDefaultUsername(String firstName, String lastName, int empNum);

    String getDefaultPassword();

    String getDefaultSecurityQuestion();

    String getDefaultEmail();

    boolean registerEmployeeWithLogin(int empNum,
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
                                      String securityAnswer);

    boolean createEmployeeWithLogin(Employee employee,
                                    String accessLevel,
                                    String username,
                                    String password,
                                    String securityQuestion,
                                    String securityAnswer,
                                    String email);
    
    boolean updateEmployee(Employee employee);

    boolean updateEmployeeFromForm(Employee employee,
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
                                   String riceSubsidyText);
    
    boolean deleteEmployeeAndLogin(int employeeId);
    
    boolean validateEmployeeData(Employee employee);
    
    List<Employee> searchEmployees(String searchTerm);
}
