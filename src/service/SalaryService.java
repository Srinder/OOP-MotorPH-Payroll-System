package service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import model.AttendanceRecord;
import model.Employee;
import model.ICalculatable;
import model.PayslipData;
import repository.AttendanceRepository;
import repository.SSSRepository;

public class SalaryService implements ISalaryService {
    
    private final SSSRepository sssRepository = new SSSRepository();
    private final AttendanceRepository attendanceRepository = new AttendanceRepository();
    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    private static final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private static final LocalTime GRACE_END = LocalTime.of(8, 10);
    private static final LocalTime SHIFT_END = LocalTime.of(17, 0);

    // MASTER METHOD: This is what our GUI will call for the final net pay.
    @Override
    public double calculateSemiMonthlyNet(Employee emp, double hours, double overtimeHours) {
        double semiMonthlyGross = calculateGrossIncome(emp, hours, overtimeHours);
        
        // Convert to monthly to find the correct SSS/PhilHealth/PagIbig bracket
        double monthlyEquivalent = semiMonthlyGross * 2; 
        
        double sssSemi = calculateSSS(semiMonthlyGross);
        double philHealthSemi = calculatePhilHealth(semiMonthlyGross);
        double pagIbigSemi = calculatePagIbig(semiMonthlyGross);
        
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

    
    @Override
    public double calculateSSS(double monthlyGross) {
        return sssRepository.getPremiumByIncome(monthlyGross);
    }

    
    @Override
    public double calculateWithholdingTax(double taxableIncome) {
        // Payslip is per cutoff; tax table is monthly. Convert to monthly,
        // apply monthly table, then convert back to cutoff share.
        double monthlyTaxableIncome = taxableIncome * 2;
        double monthlyTax = calculateSemiMonthlyWithholdingTax(monthlyTaxableIncome);
        return monthlyTax / 2;
    }
    
    @Override
    public double calculatePhilHealth(double compensationBase) {
        double totalPremium = compensationBase * 0.03;
        if (totalPremium < 300) totalPremium = 300;
        if (totalPremium > 1800) totalPremium = 1800;

        // Employee share (50%)
        return totalPremium * 0.5;
    }
    
    @Override
    public double calculatePagIbig(double compensationBase) {
        // Employee share based on compensation received:
        // 1% for 1,000-1,500 and 2% for above 1,500.
        double rate = (compensationBase > 1500) ? 0.02 : 0.01;
        return compensationBase * rate;
    }
    
    @Override
    public double calculateSemiMonthlyWithholdingTax(double taxableIncome) {
        // Monthly withholding tax table (after deductions).
        if (taxableIncome <= 20833) return 0;
        if (taxableIncome <= 33333) return (taxableIncome - 20833) * 0.20;
        if (taxableIncome <= 66667) return 2500 + (taxableIncome - 33333) * 0.25;
        if (taxableIncome <= 166667) return 10833 + (taxableIncome - 66667) * 0.30;
        if (taxableIncome <= 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    @Override
    public PayslipData computePayslipData(Employee emp, String empId, LocalDate startDate, LocalDate endDate) {
        long regularMinutes = 0L;
        long overtimeMinutes = 0L;
        long lateMinutes = 0L;
        long undertimeMinutes = 0L;

        List<AttendanceRecord> records = attendanceRepository.findByEmployeeId(empId);
        for (AttendanceRecord record : records) {
            try {
                LocalDate recordDate = LocalDate.parse(record.getDate(), CSV_DATE_FORMAT);
                if (recordDate.isBefore(startDate) || recordDate.isAfter(endDate)) {
                    continue;
                }

                if (record.getLogIn() == null || record.getLogOut() == null
                        || record.getLogIn().trim().isEmpty() || record.getLogOut().trim().isEmpty()) {
                    continue;
                }

                LocalTime logIn = LocalTime.parse(record.getLogIn().trim(), TIME_FORMAT);
                LocalTime logOut = LocalTime.parse(record.getLogOut().trim(), TIME_FORMAT);
                if (!logOut.isAfter(logIn)) {
                    continue;
                }

                LocalTime regularStart = logIn.isAfter(SHIFT_START) ? logIn : SHIFT_START;
                LocalTime regularEnd = logOut.isBefore(SHIFT_END) ? logOut : SHIFT_END;
                if (regularEnd.isAfter(regularStart)) {
                    regularMinutes += Duration.between(regularStart, regularEnd).toMinutes();
                }

                if (logIn.isBefore(SHIFT_START)) {
                    LocalTime morningOtEnd = logOut.isBefore(SHIFT_START) ? logOut : SHIFT_START;
                    if (morningOtEnd.isAfter(logIn)) {
                        overtimeMinutes += Duration.between(logIn, morningOtEnd).toMinutes();
                    }
                }
                if (logOut.isAfter(SHIFT_END)) {
                    LocalTime eveningOtStart = logIn.isAfter(SHIFT_END) ? logIn : SHIFT_END;
                    if (logOut.isAfter(eveningOtStart)) {
                        overtimeMinutes += Duration.between(eveningOtStart, logOut).toMinutes();
                    }
                }

                if (logIn.isAfter(GRACE_END)) {
                    LocalTime lateEnd = logIn.isBefore(SHIFT_END) ? logIn : SHIFT_END;
                    if (lateEnd.isAfter(SHIFT_START)) {
                        lateMinutes += Duration.between(SHIFT_START, lateEnd).toMinutes();
                    }
                }

                if (logOut.isBefore(SHIFT_END)) {
                    undertimeMinutes += Duration.between(logOut, SHIFT_END).toMinutes();
                }
            } catch (DateTimeParseException ignored) {
                // Skip malformed rows.
            }
        }

        double overtimeHours = overtimeMinutes / 60.0;
        double semiMonthlyBasicSalary = emp.getBasicSalary() / 2;
        double overtimeAdjustment = overtimeHours * emp.getHourlyRate();
        double lateDeduction = (lateMinutes / 60.0) * emp.getHourlyRate();
        double undertimeDeduction = (undertimeMinutes / 60.0) * emp.getHourlyRate();
        double lateAdjustment = -lateDeduction;
        double undertimeAdjustment = -undertimeDeduction;
        double gross = semiMonthlyBasicSalary + overtimeAdjustment;
        double monthlyEquivalent = gross * 2;

        double sss = calculateSSS(gross);
        // Per cutoff, PhilHealth is based on the displayed basic salary (semi-monthly basic salary).
        double philhealth = calculatePhilHealth(semiMonthlyBasicSalary);
        // Per cutoff, PAG-IBIG is based on the displayed basic salary (semi-monthly basic salary).
        double pagibig = calculatePagIbig(semiMonthlyBasicSalary);

        double statutoryDeductions = sss + philhealth + pagibig;
        double taxableIncome = gross - statutoryDeductions;
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeductionsPositive = statutoryDeductions + tax + lateDeduction + undertimeDeduction;
        double totalDeductions = -totalDeductionsPositive;
        double netPay = gross + (emp.getTotalAllowances() / 2) + totalDeductions;

        boolean hasAttendance = (regularMinutes + overtimeMinutes) > 0;
        return new PayslipData(
                "",
                hasAttendance,
                regularMinutes,
                overtimeMinutes,
                lateMinutes,
                undertimeMinutes,
                overtimeAdjustment,
                lateAdjustment,
                undertimeAdjustment,
                gross,
                sss,
                philhealth,
                pagibig,
                tax,
                emp.getTotalAllowances() / 2,
                semiMonthlyBasicSalary,
                totalDeductions,
                netPay);
    }

    @Override
    public List<LocalDate> getAvailableCutoffDates(String empId) {
        Map<YearMonth, boolean[]> availabilityByMonth = new TreeMap<>();
        List<AttendanceRecord> records = attendanceRepository.findByEmployeeId(empId);

        for (AttendanceRecord record : records) {
            try {
                LocalDate recordDate = LocalDate.parse(record.getDate(), CSV_DATE_FORMAT);
                boolean[] availability = availabilityByMonth.computeIfAbsent(YearMonth.from(recordDate), k -> new boolean[2]);
                if (recordDate.getDayOfMonth() <= 15) {
                    availability[0] = true;
                } else {
                    availability[1] = true;
                }
            } catch (DateTimeParseException ignored) {
                // Ignore malformed date rows.
            }
        }

        List<LocalDate> cutoffDates = new ArrayList<>();
        for (Map.Entry<YearMonth, boolean[]> entry : availabilityByMonth.entrySet()) {
            YearMonth month = entry.getKey();
            boolean[] availability = entry.getValue();

            if (availability[0]) {
                cutoffDates.add(month.atDay(15));
            }
            if (availability[1]) {
                cutoffDates.add(month.atDay(Math.min(30, month.lengthOfMonth())));
            }
        }
        return cutoffDates;
    }

    @Override
    public LocalDate[] getCutoffPeriod(LocalDate cutoffDate) {
        if (cutoffDate.getDayOfMonth() <= 15) {
            LocalDate previousMonth = cutoffDate.minusMonths(1);
            return new LocalDate[]{previousMonth.withDayOfMonth(21), cutoffDate.withDayOfMonth(5)};
        }
        return new LocalDate[]{cutoffDate.withDayOfMonth(6), cutoffDate.withDayOfMonth(20)};
    }
}
