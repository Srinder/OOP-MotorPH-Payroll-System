/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package model;

public class AdminStaff extends Employee {
    
    //default constructor
    public AdminStaff(int employeeNumber, String lastName, String firstName, String email) {
        super(employeeNumber, lastName, firstName, email);
    }
    
    // Used by the EmployeeRepository to load full payroll details
    public AdminStaff(int employeeNumber, String lastName, String firstName, String birthday, 
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
        // Finance might have a different logic for net pay or adjustments
        return grossIncome - totalDeductions + getTotalAllowances();
    }
}