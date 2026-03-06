//Interface defining the contract for Salary and Payroll calculations.

/**
 *
 * @author singh
 */

package service;

import model.Employee;
import model.PayslipData;
import java.time.LocalDate;
import java.util.List;

public interface ISalaryService {
    
    double calculateSemiMonthlyNet(Employee emp, double hours, double overtimeHours);
    double calculateGrossIncome(Employee employee, double workedHours, double overtimeHours);
    double calculateSSS(double monthlyGross);
    double calculateWithholdingTax(double taxableIncome);
    double calculatePhilHealth(double monthlyGross);
    double calculatePagIbig(double monthlyGross);
    double calculateSemiMonthlyWithholdingTax(double taxableIncome);
    PayslipData computePayslipData(Employee emp, String empId, LocalDate startDate, LocalDate endDate);
    List<LocalDate> getAvailableCutoffDates(String empId);
    LocalDate[] getCutoffPeriod(LocalDate cutoffDate);
}
