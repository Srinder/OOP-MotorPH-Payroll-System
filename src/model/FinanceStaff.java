//Finance Department Class
/**
 *
 * @author singh
 */
package model;

public class FinanceStaff extends Employee {
    
    //default constructor
    public FinanceStaff(int employeeNumber, String lastName, String firstName, String email) {
        super(employeeNumber, lastName, firstName, email);
    }
    
    // Used by the EmployeeRepository to load full payroll details
    public FinanceStaff(int employeeNumber, String lastName, String firstName, String birthday, 
                        String phoneNumber, String address, String status, String position, 
                        String supervisor, String sssNumber, String philHealthNumber, 
                        String tinNumber, String pagIbigNumber, double basicSalary, 
                        double riceSubsidy, double phoneAllowance, double clothingAllowance, 
                        double grossSemiMonthlyRate, double hourlyRate, double withholdingTax) {
        
        super(employeeNumber, lastName, firstName, birthday, phoneNumber, address, status, 
              position, supervisor, sssNumber, philHealthNumber, tinNumber, pagIbigNumber, 
              basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
              grossSemiMonthlyRate, hourlyRate, withholdingTax);
    }
    
    // Implementing the "Contract" from ICalculatable
    @Override
    public double calculateGrossIncome(double hoursWorked, double overtimeHours) {
        return (getHourlyRate() * hoursWorked) + (getHourlyRate() * overtimeHours);
    }

    @Override
    public double calculateNetPay(double grossIncome, double totalDeductions) {
        
        return grossIncome - totalDeductions + getTotalAllowances();
    }
}
