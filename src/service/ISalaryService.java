//Interface defining the contract for Salary and Payroll calculations.

/**
 *
 * @author singh
 */

package service;

import model.Employee;

public interface ISalaryService {
    
    double calculateSemiMonthlyNet(Employee emp, double hours, double otHours);
    double calculateGrossIncome(Employee employee, double workedHours, double overtimeHours);
    double calculateSSS(double monthlyGross);
    double calculateWithholdingTax(double taxableIncome);
    double calculatePhilHealth(double monthlyGross);
    double calculatePagIbig(double monthlyGross);
    double calculateSemiMonthlyWithholdingTax(double taxableIncome);
}
