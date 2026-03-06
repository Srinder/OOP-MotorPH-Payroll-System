/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author singh
 */

package model;

public class PayslipData {
    private String period;
    private double gross;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double tax;
    private double allowances;
    private double net;
    private double semiMonthlyBasicSalary;
    private double totalDeductions;
    private boolean hasAttendance;
    private long regularMinutes;
    private long overtimeMinutes;
    private long lateMinutes;
    private long undertimeMinutes;
    private double overtimeAdjustment;
    private double lateAdjustment;
    private double undertimeAdjustment;

    public PayslipData(String period, double gross, double sss, double philhealth, 
                       double pagibig, double tax, double allowances, double net) {
        this.period = period;
        this.gross = gross;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.tax = tax;
        this.allowances = allowances;
        this.net = net;
        this.semiMonthlyBasicSalary = 0.0;
        this.totalDeductions = sss + philhealth + pagibig + tax;
        this.hasAttendance = false;
        this.regularMinutes = 0L;
        this.overtimeMinutes = 0L;
        this.lateMinutes = 0L;
        this.undertimeMinutes = 0L;
        this.overtimeAdjustment = 0.0;
        this.lateAdjustment = 0.0;
        this.undertimeAdjustment = 0.0;
    }

    public PayslipData(
            String period,
            boolean hasAttendance,
            long regularMinutes,
            long overtimeMinutes,
            long lateMinutes,
            long undertimeMinutes,
            double overtimeAdjustment,
            double lateAdjustment,
            double undertimeAdjustment,
            double gross,
            double sss,
            double philhealth,
            double pagibig,
            double tax,
            double allowances,
            double semiMonthlyBasicSalary,
            double totalDeductions,
            double net) {
        this.period = period;
        this.hasAttendance = hasAttendance;
        this.regularMinutes = regularMinutes;
        this.overtimeMinutes = overtimeMinutes;
        this.lateMinutes = lateMinutes;
        this.undertimeMinutes = undertimeMinutes;
        this.overtimeAdjustment = overtimeAdjustment;
        this.lateAdjustment = lateAdjustment;
        this.undertimeAdjustment = undertimeAdjustment;
        this.gross = gross;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.tax = tax;
        this.allowances = allowances;
        this.semiMonthlyBasicSalary = semiMonthlyBasicSalary;
        this.totalDeductions = totalDeductions;
        this.net = net;
    }
    
    public String getPeriod() { return period; }

    public double getSss() {
        return sss;
    }

    public double getPhilhealth() {
        return philhealth;
    }

    public double getPagibig() {
        return pagibig;
    }

    public double getTax() {
        return tax;
    }

    // Getters
    public double getAllowances() {
        return allowances;
    }

    public double getNet() {
        return net;
    }
    public double getGross() { return gross; }
    public double getTotalDeductions() { return totalDeductions; }
    public boolean hasAttendance() { return hasAttendance; }
    public long getRegularMinutes() { return regularMinutes; }
    public long getOvertimeMinutes() { return overtimeMinutes; }
    public long getLateMinutes() { return lateMinutes; }
    public long getUndertimeMinutes() { return undertimeMinutes; }
    public double getOvertimeAdjustment() { return overtimeAdjustment; }
    public double getLateAdjustment() { return lateAdjustment; }
    public double getUndertimeAdjustment() { return undertimeAdjustment; }
    public double getSemiMonthlyBasicSalary() { return semiMonthlyBasicSalary; }
   
}
