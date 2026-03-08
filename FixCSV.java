import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FixCSV {
    public static void main(String[] args) throws IOException {
        String csvFile = "data/employee_info.csv";
        
        // Read the CSV file
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parse CSV line (handle quoted fields)
                rows.add(parseCSVLine(line));
            }
        }
        
        if (rows.isEmpty()) {
            System.out.println("Error: CSV file is empty");
            return;
        }
        
        // Keep header as-is
        String[] header = rows.get(0);
        
        // Swap columns 5 and 6 (0-indexed) for all data rows
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 6) {
                // Swap Position and Supervisor
                String temp = row[5];
                row[5] = row[6];
                row[6] = temp;
            }
        }
        
        // Write the corrected data back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            // Write header
            bw.write(formatCSVLine(header));
            bw.newLine();
            
            // Write data rows
            for (int i = 1; i < rows.size(); i++) {
                bw.write(formatCSVLine(rows.get(i)));
                bw.newLine();
            }
        }
        
        System.out.println("✓ CSV file fixed successfully!");
        System.out.println("✓ Swapped columns 6 and 7 (Position and Supervisor) for " + (rows.size() - 1) + " data rows");
        System.out.println("✓ Header remained unchanged");
        System.out.println("✓ File saved: " + csvFile);
    }
    
    // Parse a CSV line handling quoted fields
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Handle escaped quote
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
    
    // Format a CSV line with proper quoting
    private static String formatCSVLine(String[] fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) line.append(',');
            String field = fields[i];
            // Quote fields that contain comma, quote, or newline
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                line.append('"').append(field.replace("\"", "\"\"")).append('"');
            } else {
                line.append(field);
            }
        }
        return line.toString();
    }
}
