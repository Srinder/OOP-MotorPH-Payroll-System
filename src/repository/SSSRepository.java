package repository;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SSSRepository {
    private static final String SSS_FILE_PATH = "data/SSS Contribution.csv";

    public List<String[]> readContributionRows() {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(SSS_FILE_PATH))) {
            String[] row;
            reader.readNext(); // Skip header row
            while ((row = reader.readNext()) != null) {
                if (row.length >= 4) {
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            System.err.println("Error accessing SSS data: " + e.getMessage());
        }
        return rows;
    }
}
