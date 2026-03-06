//Why do we do this?
//The ICalculatable interface ensures that no matter what kind of employee we have, we can always call calculateGrossIncome() on them.
/**
 *
 * @author singh
 */

package model;

//Iterface for salary-related calculations

public interface ICalculatable {
    double calculateGrossIncome(double hoursWorked, double overtimeHours);
    double calculateNetPay(double grossIncome, double totalDeductions);
    
}
