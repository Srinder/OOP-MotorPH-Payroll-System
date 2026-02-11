package service;

import model.Employee;
import repository.SSSRepository;

public class SalaryService implements ISalaryService{
    
    private final SSSRepository sssRepository = new SSSRepository();

    // MASTER METHOD: This is what our GUI will call for the final net pay.
    @Override
    public double calculateSemiMonthlyNet(Employee emp, double hours, double overtimeHours) {
        double semiMonthlyGross = calculateGrossIncome(emp, hours, overtimeHours);
        
        // Convert to monthly to find the correct SSS/PhilHealth/PagIbig bracket
        double monthlyEquivalent = semiMonthlyGross * 2; 
        
        double sssSemi = calculateSSS(monthlyEquivalent) / 2;
        double philHealthSemi = calculatePhilHealth(monthlyEquivalent) / 2;
        double pagIbigSemi = calculatePagIbig(monthlyEquivalent) / 2;
        
        double totalDeductions = sssSemi + philHealthSemi + pagIbigSemi;
        double taxableIncome = semiMonthlyGross - totalDeductions;
        
        double tax = calculateWithholdingTax(taxableIncome);
        
        // Add half of monthly allowances
        double semiMonthlyAllowances = emp.getTotalAllowances() / 2;
        
        return taxableIncome - tax + semiMonthlyAllowances;
    }

    //Calculates Gross Income based on hourly rate.
     @Override
    public double calculateGrossIncome(Employee employee, double workedHours, double overtimeHours) {
        double regularPay = workedHours * employee.getHourlyRate();
        double overtimePay = overtimeHours * employee.getHourlyRate();
        return regularPay + overtimePay;
    }

    // Added for Payslip.java 
    @Override
    public double calculateSSS(double monthlyGross) {
        return sssRepository.getPremiumByIncome(monthlyGross);
    }

    //Added for Payslip.java (Line 84)
    @Override
    public double calculateWithholdingTax(double taxableIncome) {
        return calculateSemiMonthlyWithholdingTax(taxableIncome);
    }
    
    @Override
    public double calculatePhilHealth(double monthlyGross) {
        double totalPremium;
        if (monthlyGross <= 10000) totalPremium = 300;
        else if (monthlyGross >= 60000) totalPremium = 1800;
        else totalPremium = monthlyGross * 0.03;
        
        return totalPremium; 
    }
    
    @Override
    public double calculatePagIbig(double monthlyGross) {
        double premium = (monthlyGross <= 1500) ? (monthlyGross * 0.01) : (monthlyGross * 0.02);
        return Math.min(premium, 100);
    }
    
    @Override
    public double calculateSemiMonthlyWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 10417) return 0;
        if (taxableIncome <= 16666) return (taxableIncome - 10417) * 0.20;
        if (taxableIncome <= 33332) return 1250 + (taxableIncome - 16667) * 0.25;
        if (taxableIncome <= 83332) return 5416.67 + (taxableIncome - 33333) * 0.30;
        if (taxableIncome <= 333332) return 20416.67 + (taxableIncome - 83333) * 0.32;
        return 100416.67 + (taxableIncome - 333333) * 0.35;
    }
}