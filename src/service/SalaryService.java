package service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import model.AttendanceRecord;
import model.Employee;
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
        if (isProbationaryStatus(emp)) {
            overtimeHours = 0.0;
        }
        double semiMonthlyGross = calculateGrossIncome(emp, hours, overtimeHours);
        
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
        double sssPremium = 0.0;
        for (String[] row : sssRepository.readContributionRows()) {
            String minRaw = row[0] == null ? "" : row[0].toLowerCase();
            String maxRaw = row[2] == null ? "" : row[2].toLowerCase();
            double minBracket = parseSssValue(row[0]);
            double premium = parseSssValue(row[3]);

            if (minRaw.contains("below")) {
                if (monthlyGross < minBracket) {
                    sssPremium = premium;
                    break;
                }
            } else if (maxRaw.contains("over")) {
                if (monthlyGross > minBracket) {
                    sssPremium = premium;
                    break;
                }
            } else {
                double maxBracket = parseSssValue(row[2]);
                if (monthlyGross >= minBracket && monthlyGross <= maxBracket) {
                    sssPremium = premium;
                    break;
                }
            }
        }
        return sssPremium;
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
        double contribution = compensationBase * rate;
        return Math.min(contribution, 100.0);
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
        boolean probationaryStatus = isProbationaryStatus(emp);
        long regularMinutes = 0L;
        long overtimeMinutes = 0L;
        long lateMinutes = 0L;
        long undertimeMinutes = 0L;
        long absentDays = 0L;
        long workingDaysInCutoff = 0L;

        List<AttendanceRecord> records = attendanceRepository.findByEmployeeId(empId);
        Set<LocalDate> presentDays = new HashSet<>();
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

                presentDays.add(recordDate);

                LocalTime regularStart = logIn.isAfter(SHIFT_START) ? logIn : SHIFT_START;
                LocalTime regularEnd = logOut.isBefore(SHIFT_END) ? logOut : SHIFT_END;
                if (regularEnd.isAfter(regularStart)) {
                    regularMinutes += Duration.between(regularStart, regularEnd).toMinutes();
                }

                if (!probationaryStatus && logIn.isBefore(SHIFT_START)) {
                    LocalTime morningOtEnd = logOut.isBefore(SHIFT_START) ? logOut : SHIFT_START;
                    if (morningOtEnd.isAfter(logIn)) {
                        overtimeMinutes += Duration.between(logIn, morningOtEnd).toMinutes();
                    }
                }
                if (!probationaryStatus && logOut.isAfter(SHIFT_END)) {
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

        for (LocalDate day = startDate; !day.isAfter(endDate); day = day.plusDays(1)) {
            DayOfWeek dow = day.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                continue;
            }
            workingDaysInCutoff++;
            if (!presentDays.contains(day)) {
                absentDays++;
            }
        }

        if (probationaryStatus) {
            overtimeMinutes = 0L;
        }
        double overtimeHours = overtimeMinutes / 60.0;
        double semiMonthlyBasicSalary = workingDaysInCutoff * 8 * emp.getHourlyRate();
        double overtimeAdjustment = overtimeHours * emp.getHourlyRate();
        double lateDeduction = (lateMinutes / 60.0) * emp.getHourlyRate();
        double undertimeDeduction = (undertimeMinutes / 60.0) * emp.getHourlyRate();
        double absentDeduction = absentDays * 8 * emp.getHourlyRate();
        double lateAdjustment = -lateDeduction;
        double undertimeAdjustment = -undertimeDeduction;
        double absentAdjustment = -absentDeduction;
        double gross = semiMonthlyBasicSalary + overtimeAdjustment;

        double sss = calculateSSS(gross);
        // Per cutoff, PhilHealth is based on the displayed basic salary (semi-monthly basic salary).
        double philhealth = calculatePhilHealth(semiMonthlyBasicSalary);
        // Per cutoff, PAG-IBIG is based on the displayed basic salary (semi-monthly basic salary).
        double pagibig = calculatePagIbig(semiMonthlyBasicSalary);

        double statutoryDeductions = sss + philhealth + pagibig;
        double taxableIncome = gross - statutoryDeductions;
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeductionsPositive = statutoryDeductions + tax + lateDeduction + undertimeDeduction + absentDeduction;
        double totalDeductions = -totalDeductionsPositive;
        double netPay = gross + (emp.getTotalAllowances() / 2) + totalDeductions;

        boolean hasAttendance = workingDaysInCutoff > 0;
        return new PayslipData(
                "",
                hasAttendance,
                regularMinutes,
                overtimeMinutes,
                lateMinutes,
                undertimeMinutes,
                absentDays,
                overtimeAdjustment,
                lateAdjustment,
                undertimeAdjustment,
                absentAdjustment,
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

    private boolean isProbationaryStatus(Employee employee) {
        if (employee == null || employee.getStatus() == null) {
            return false;
        }
        return "Probationary".equalsIgnoreCase(employee.getStatus().trim());
    }

    @Override
    public List<LocalDate> getAvailableCutoffDates(String empId) {
        Set<LocalDate> cutoffDates = new TreeSet<>();
        List<AttendanceRecord> records = attendanceRepository.findByEmployeeId(empId);

        for (AttendanceRecord record : records) {
            try {
                LocalDate recordDate = LocalDate.parse(record.getDate(), CSV_DATE_FORMAT);
                if (record.getLogIn() == null || record.getLogOut() == null
                        || record.getLogIn().trim().isEmpty() || record.getLogOut().trim().isEmpty()) {
                    continue;
                }
                LocalTime logIn = LocalTime.parse(record.getLogIn().trim(), TIME_FORMAT);
                LocalTime logOut = LocalTime.parse(record.getLogOut().trim(), TIME_FORMAT);
                if (!logOut.isAfter(logIn)) {
                    continue;
                }

                int day = recordDate.getDayOfMonth();

                if (day <= 5) {
                    cutoffDates.add(recordDate.withDayOfMonth(15));
                } else if (day <= 20) {
                    cutoffDates.add(recordDate.withDayOfMonth(Math.min(30, recordDate.lengthOfMonth())));
                } else {
                    LocalDate nextMonth = recordDate.plusMonths(1);
                    cutoffDates.add(nextMonth.withDayOfMonth(15));
                }
            } catch (DateTimeParseException ignored) {
                // Ignore malformed date rows.
            }
        }

        return new ArrayList<>(cutoffDates);
    }

    @Override
    public LocalDate[] getCutoffPeriod(LocalDate cutoffDate) {
        if (cutoffDate.getDayOfMonth() <= 15) {
            LocalDate previousMonth = cutoffDate.minusMonths(1);
            return new LocalDate[]{previousMonth.withDayOfMonth(21), cutoffDate.withDayOfMonth(5)};
        }
        return new LocalDate[]{cutoffDate.withDayOfMonth(6), cutoffDate.withDayOfMonth(20)};
    }

    @Override
    public boolean exportPayslipPdf(BufferedImage image, String outputPath) {
        if (image == null || outputPath == null || outputPath.trim().isEmpty()) {
            return false;
        }
        try {
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(image, null);
            float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float pageHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            pdfImage.scaleToFit(pageWidth, pageHeight);
            pdfImage.setAlignment(Element.ALIGN_CENTER);
            document.add(pdfImage);

            document.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private double parseSssValue(String value) {
        if (value == null) {
            return 0.0;
        }
        String cleaned = value.replaceAll("[^\\d.]", "");
        if (cleaned.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
}
