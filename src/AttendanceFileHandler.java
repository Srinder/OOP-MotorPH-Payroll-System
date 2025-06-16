import com.opencsv.CSVReader;
import java.io.FileReader;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * `AttendanceFileHandler` processes employee attendance records from a CSV.
 * It calculates total hours worked, overtime, late minutes, and absent days.
 */
public class AttendanceFileHandler {
    private static final String FILE_PATH = "src/data/Attendance Record.csv";
    private static final double STANDARD_WORK_HOURS = 40.0; // Weekly full-time hours.
    private static final double STANDARD_DAILY_HOURS = 8.0;  // Standard hours per workday.

    /**
     * Calculates an employee's total **worked hours** and **overtime hours** for a given period.
     * @param empNo The employee's ID.
     * @param targetMonthYear The payroll period (e.g., "January 2023").
     * @return A `double` array: `[totalWorkedHours, overtimeHours]`.
     */
    public static double[] computeMonthlyHoursAndOT(int empNo, String targetMonthYear) {
        double totalWorkedHours = 0.0;
        double overtimeHours = 0.0;

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            reader.readNext(); // Skip CSV header.

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");

            LocalTime officeStart = LocalTime.of(8, 0);
            LocalTime officeEnd = LocalTime.of(17, 0);

            while ((row = reader.readNext()) != null) {
                if (row.length < 6) continue; // Skip incomplete records.
                int recordEmpNo = Integer.parseInt(row[0].trim());
                if (recordEmpNo != empNo) continue;

                LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);
                if (!logDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")).equalsIgnoreCase(targetMonthYear.trim())) continue;
                if (logDate.getDayOfWeek() == DayOfWeek.SUNDAY) continue; // Exclude Sundays.

                LocalTime logIn = LocalTime.parse(row[4].trim(), timeFmt);
                LocalTime logOut = LocalTime.parse(row[5].trim(), timeFmt);

                if (logOut.isAfter(logIn)) {
                    double workedHours = Duration.between(logIn, logOut).toMinutes() / 60.0;
                    totalWorkedHours += workedHours;
                }

                if (logIn.isBefore(officeStart)) {
                    overtimeHours += Duration.between(logIn, officeStart).toMinutes() / 60.0;
                }
                if (logOut.isAfter(officeEnd)) {
                    overtimeHours += Duration.between(officeEnd, logOut).toMinutes() / 60.0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading attendance: " + e.getMessage());
        }

        return new double[] {
            Math.round(totalWorkedHours * 100.0) / 100.0,
            Math.round(overtimeHours * 100.0) / 100.0
        };
    }

    /**
     * Computes the number of absent days for an employee.
     * @param empNo The employee's ID.
     * @param targetMonthYear The payroll period.
     * @return The calculated number of absent days.
     */
    public static int computeAbsentDays(int empNo, String targetMonthYear) {
        double totalWorkedHours = computeMonthlyHoursAndOT(empNo, targetMonthYear)[0];

        if (totalWorkedHours >= STANDARD_WORK_HOURS) {
            return 0;
        }

        double missingHours = STANDARD_WORK_HOURS - totalWorkedHours;
        return (int) Math.ceil(missingHours / STANDARD_DAILY_HOURS);
    }

    /**
     * Computes total late minutes for an employee.
     * @param empNo The employee's ID.
     * @param targetMonthYear The payroll period.
     * @return The total minutes considered late.
     */
    public static double computeLateMinutes(int empNo, String targetMonthYear) {
        double totalLateMinutes = 0.0;

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] row;
            reader.readNext(); // Skip CSV header.

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");

            LocalTime officeStart = LocalTime.of(8, 0);
            LocalTime graceCutoff = officeStart.plusMinutes(10);
            LocalTime officeEnd = LocalTime.of(17, 0);

            while ((row = reader.readNext()) != null) {
                if (row.length < 6) continue;
                int recordEmpNo = Integer.parseInt(row[0].trim());
                if (recordEmpNo != empNo) continue;

                LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);
                if (!logDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")).equalsIgnoreCase(targetMonthYear.trim())) continue;
                if (logDate.getDayOfWeek() == DayOfWeek.SUNDAY) continue; // Exclude Sundays.

                LocalTime logIn = LocalTime.parse(row[4].trim(), timeFmt);
                LocalTime logOut = LocalTime.parse(row[5].trim(), timeFmt);

                if (logIn.isAfter(graceCutoff)) {
                    totalLateMinutes += Duration.between(graceCutoff, logIn).toMinutes();
                }

                if (logOut.isBefore(officeEnd)) {
                    totalLateMinutes += Duration.between(logOut, officeEnd).toMinutes();
                }
            }
        } catch (Exception e) {
            System.err.println("Error computing late minutes: " + e.getMessage());
        }

        return totalLateMinutes;
    }
    
    public static List<String[]> getAttendanceRecords(String empNo, LocalDate startDate, LocalDate endDate) {
    List<String[]> records = new ArrayList<>();

    try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
        String[] row;
        reader.readNext(); // Skip CSV header.

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        while ((row = reader.readNext()) != null) {
            if (row.length < 6) continue;
            if (!row[0].trim().equals(empNo)) continue; // Filter by employee number

            LocalDate logDate = LocalDate.parse(row[3].trim(), dateFmt);

            // Ensure the date falls within the selected range
            if (!logDate.isBefore(startDate) && !logDate.isAfter(endDate)) {
                records.add(new String[]{row[3], row[4], row[5]}); // Date, Time In, Time Out
            }
        }
    } catch (Exception e) {
        System.err.println("Error fetching attendance records: " + e.getMessage());
    }

    return records;
}

}
