/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author singh
 */
package repository;

import com.opencsv.CSVReader;
import java.io.FileReader;

/**
 * Repository class responsible for fetching SSS contribution data from CSV.
 * This separates Data Access from Business Logic.
 */
public class SSSRepository {
    private static final String SSS_FILE_PATH = "data/SSS Contribution.csv";

    /**
     * Finds the SSS premium based on the gross income by reading the CSV table.
     */
    public double getPremiumByIncome(double grossIncome) {
        double sssPremium = 0.0;

        try (CSVReader reader = new CSVReader(new FileReader(SSS_FILE_PATH))) {
            String[] row;
            reader.readNext(); // Skip the header row

            while ((row = reader.readNext()) != null) {
                if (row.length < 4) continue;

                // 1. Clean and parse the values from the CSV
                double minBracket = parseValue(row[0]);
                String maxRaw = row[2].toLowerCase();
                double premium = parseValue(row[3]);

                // 2. Bracket Matching Logic
                if (row[0].toLowerCase().contains("below")) {
                    if (grossIncome < minBracket) {
                        sssPremium = premium;
                        break;
                    }
                } else if (maxRaw.contains("over")) {
                    if (grossIncome > minBracket) {
                        sssPremium = premium;
                        break;
                    }
                } else {
                    double maxBracket = parseValue(row[2]);
                    if (grossIncome >= minBracket && grossIncome <= maxBracket) {
                        sssPremium = premium;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error accessing SSS Data: " + e.getMessage());
        }
        return sssPremium;
    }

    /**
     * Helper to clean strings and turn them into doubles safely.
     */
    private double parseValue(String value) {
        String cleaned = value.replaceAll("[^\\d.]", "");
        return cleaned.isEmpty() ? 0.0 : Double.parseDouble(cleaned);
    }
}