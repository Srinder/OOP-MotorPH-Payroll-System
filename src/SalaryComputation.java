import com.opencsv.CSVReader; // Used for reading CSV files.
import java.io.FileReader;    // Used for reading character files.

/**
 * The `SalaryComputation` class provides static methods for calculating various
 * components of an employee's salary, including gross income, overtime pay,
 * government contributions (SSS, PhilHealth, Pag-IBIG), withholding tax, and net pay.
 */
public class SalaryComputation {

    /**
     * Computes the overtime pay based on overtime hours and hourly rate.
     * @param overtimeHours The total number of overtime hours worked.
     * @param hourlyRate The employee's hourly rate.
     * @return The calculated overtime pay.
     */
    public static double computeOvertimePay(double overtimeHours, double hourlyRate) {
        // Overtime pay is directly proportional to overtime hours and hourly rate.
        return overtimeHours * hourlyRate;
    }

    /**
     * Computes the total gross income.
     * @param workedHours The total number of regular hours worked.
     * @param overtimePay The calculated overtime pay.
     * @param hourlyRate The employee's hourly rate.
     * @return The total gross income (regular pay + overtime pay).
     */
    public static double computeGrossIncome(double workedHours, double overtimePay, double hourlyRate) {
        // Gross income is the sum of regular pay (worked hours * hourly rate) and overtime pay.
        return (workedHours * hourlyRate) + overtimePay;
    }

    /**
     * Computes the applicable SSS (Social Security System) premium based on the gross income.
     * This method reads the SSS contribution table from a CSV file.
     * Assumes the SSS contribution table file is located at: `src/data/SSS Contribution.csv`.
     * The CSV file is expected to have columns for minimum salary bracket, maximum salary bracket, and premium amount.
     * @param grossIncome The employee's gross income for the period.
     * @return The calculated SSS premium. Returns 0.0 if there's an error reading the file or no matching bracket is found.
     */
    public static double getSSSPremium(double grossIncome) {
        double sss = 0.0; // Initialize SSS premium.

        // Use try-with-resources for automatic closing of the CSVReader.
        try (CSVReader reader = new CSVReader(new FileReader("src/data/SSS Contribution.csv"))) {
            String[] row;
            reader.readNext(); // Skip the header row of the CSV file.

            // Iterate through each row in the SSS contribution table.
            while ((row = reader.readNext()) != null) {
                if (row.length < 4) continue; // Skip rows that do not have enough columns.

                // Extract and clean the premium amount. Remove non-digit and non-decimal characters.
                String premiumRaw = row[3].replaceAll("[^\\d.]", "");
                // Parse the premium, default to 0.0 if empty.
                double premium = premiumRaw.isEmpty() ? 0.0 : Double.parseDouble(premiumRaw);

                // Extract and clean the minimum and maximum salary bracket values.
                String minRaw = row[0].replaceAll("[^\\d.]", "");
                String maxRaw = row[2].replaceAll("[^a-zA-Z0-9.]", "").toLowerCase(); // Clean and convert to lowercase for comparison.

                // Logic to determine the SSS premium based on salary brackets.
                if (row[0].toLowerCase().contains("below")) {
                    // Handle "Below X" type of bracket.
                    if (!minRaw.isEmpty() && grossIncome < Double.parseDouble(minRaw)) {
                        sss = premium; // Assign premium if gross income is below the minimum.
                        break;         // Exit loop once a match is found.
                    }
                } else if (maxRaw.equals("over") || maxRaw.contains("over")) {
                    // Handle "Over X" type of bracket (e.g., "30000 and Over").
                    if (!minRaw.isEmpty() && grossIncome > Double.parseDouble(minRaw)) {
                        sss = premium; // Assign premium if gross income is over the minimum.
                        break;         // Exit loop once a match is found.
                    }
                } else {
                    // Handle standard range-based brackets (e.g., "X to Y").
                    if (!minRaw.isEmpty() && !row[2].isEmpty()) {
                        double min = Double.parseDouble(minRaw);
                        double max = Double.parseDouble(row[2].replaceAll("[^\\d.]", "")); // Clean and parse max value.
                        if (grossIncome >= min && grossIncome <= max) {
                            sss = premium; // Assign premium if gross income falls within the range.
                            break;         // Exit loop once a match is found.
                        }
                    }
                }
            }

        } catch (Exception e) {
            // Log any errors that occur during file reading or parsing.
            System.err.println("⚠️ Error reading SSS Contribution.csv: " + e.getMessage());
        }

        return sss; // Return the determined SSS premium.
    }

    /**
     * Computes the applicable PhilHealth premium based on the gross income.
     * The computation follows specific tiers and a percentage rate.
     * The premium is divided by 2 as typically it's split between employee and employer,
     * and this method presumably returns the employee's share.
     * @param grossIncome The employee's gross income for the period.
     * @return The calculated PhilHealth premium (employee's share).
     */
    public static double getPhilHealthPremium(double grossIncome) {
        double rate = 0.03;       // Default PhilHealth contribution rate (3%).
        double totalPremium;      // Variable to hold the total premium before splitting.

        // PhilHealth contribution tiers.
        if (grossIncome <= 10000) {
            totalPremium = 300; // Fixed premium for income below or equal to 10,000.
        } else if (grossIncome >= 60000) {
            totalPremium = 1800; // Fixed maximum premium for income equal to or above 60,000.
        } else {
            totalPremium = grossIncome * rate; // Percentage-based premium for income within the range.
        }

        // Return half of the total premium, assuming it's the employee's share.
        return totalPremium / 2;
    }

    /**
     * Computes the applicable Pag-IBIG (Home Development Mutual Fund) premium based on the gross income.
     * The contribution rate depends on the income bracket.
     * @param grossIncome The employee's gross income for the period.
     * @return The calculated Pag-IBIG premium.
     */
    public static double getPagIbigPremium(double grossIncome) {
        if (grossIncome <= 1500) {
            return grossIncome * 0.01; // 1% contribution for income up to 1,500.
        } else {
            return grossIncome * 0.02; // 2% contribution for income above 1,500.
        }
    }

    /**
     * Computes the withholding tax based on the taxable income using a progressive tax table.
     * This table reflects common Philippine withholding tax brackets (TRAIN Law).
     * @param taxableIncome The employee's taxable income after deductions (e.g., government contributions).
     * @return The calculated withholding tax.
     */
    public static double computeWithholdingTax(double taxableIncome) {
        double tax = 0.0; // Initialize tax.

        // Progressive tax rate brackets.
        if (taxableIncome <= 20833) {
            tax = 0.0; // No tax for income up to 20,833.
        } else if (taxableIncome <= 33332) {
            tax = (taxableIncome - 20833) * 0.20; // 20% of the excess over 20,833.
        } else if (taxableIncome <= 66667) {
            tax = 2500 + (taxableIncome - 33333) * 0.25; // Fixed amount + 25% of the excess over 33,333.
        } else if (taxableIncome <= 166667) {
            tax = 10833 + (taxableIncome - 66667) * 0.30; // Fixed amount + 30% of the excess over 66,667.
        } else if (taxableIncome <= 666667) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32; // Fixed amount + 32% of the excess over 166,667.
        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35; // Fixed amount + 35% of the excess over 666,667.
        }

        return tax; // Return the calculated withholding tax.
    }

    /**
     * Computes the employee's net pay.
     * @param grossIncome The total gross income.
     * @param benefits The total allowances/benefits.
     * @param withholdingTax The calculated withholding tax.
     * @param governmentContributions The sum of SSS, PhilHealth, and Pag-IBIG premiums.
     * @return The final net pay.
     */
    public static double computeNetPay(double grossIncome, double benefits, double withholdingTax, double governmentContributions) {
        // Net pay is gross income plus benefits, minus all deductions (withholding tax and government contributions).
        return grossIncome + benefits - withholdingTax - governmentContributions;
    }

    /**
     * Rounds a double value to two decimal places.
     * @param value The double value to round.
     * @return The rounded double value.
     */
    public static double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0; // Multiplies by 100, rounds to nearest whole number, then divides by 100.
    }
}