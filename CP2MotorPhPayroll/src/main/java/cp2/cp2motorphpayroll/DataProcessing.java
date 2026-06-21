package cp2.cp2motorphpayroll;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * =========================================================
 * Employee CSV column layout (0-based index):
 *  0  = Employee #          1  = Last Name
 *  2  = First Name          3  = Birthday
 *  4  = Phone Number        5  = SSS #
 *  6  = PhilHealth #        7  = TIN #
 *  8  = Pag-IBIG #          9  = Status
 *  10 = Position            11 = Basic Salary
 *  12 = Rice Subsidy        13 = Phone Allowance
 *  14 = Clothing Allowance  15 = Gross Semi-monthly Rate
 *  16 = Hourly Rate         17 = Immediate Supervisor
 *  18 = Address
 * =========================================================
 */
public class DataProcessing {

    static final int MIN_EMPLOYEE_COLS = 4;

    // CSV HEADERS
    static final String EMPLOYEE_CSV_HEADER =
        "Employee #,Last Name,First Name,Birthday,Phone Number,"
        + "SSS #,PhilHealth #,TIN #,Pag-IBIG #,Status,"
        + "Position,Basic Salary,Rice Subsidy,Phone Allowance,"
        + "Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate,"
        + "Immediate Supervisor,Address";

    // LOAD EMPLOYEES
    static void loadEmployees(String employeeFile,
                               HashMap<String, String[]> employeeMap) {
        File file = new File(employeeFile);
        if (!file.exists()) {
            System.out.println(file.getAbsolutePath());
            SystemGUIHelper.showWarning(null,
                "Employee file not found");
            return;
        }
        employeeMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header row
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < MIN_EMPLOYEE_COLS) continue;
                employeeMap.put(data[0].trim(), data);
                count++;
            }
            System.out.println("[DataLoading] Employees loaded: " + count);
        } catch (IOException e) {
            SystemGUIHelper.showWarning(null,
                "Error reading employee file");
        }
    }

    // LOAD ATTENDANCE
    static void loadAttendance(String attendanceFile,
                                HashMap<String, List<String[]>> attendanceMap) {
        File file = new File(attendanceFile);
        if (!file.exists()) {
            System.out.println(file.getAbsolutePath());
            SystemGUIHelper.showWarning(null,
                "Attendance File not Found");
            return;
        }
        attendanceMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header row
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 6) continue;
                String empNumber = data[0].trim();
                attendanceMap.putIfAbsent(empNumber, new ArrayList<>());
                attendanceMap.get(empNumber).add(data);
                count++;
            }
            System.out.println("[DataLoading] Attendance records loaded: " + count);
        } catch (IOException e) {
            SystemGUIHelper.showWarning(null,
                "Error Reading Attendance File");
        }
    }

    // SAVE EMPLOYEE - appends one new employee row to the CSV.
    static boolean saveEmployee(String employeeFile,
                                 String[] data,
                                 HashMap<String, String[]> employeeMap) {
        if (data == null || data.length < 19) {
            SystemGUIHelper.showWarning(null,
                "Cannot save: employee data is incomplete.");
            return false;
        }

        String empNum = data[0].trim();
        if (employeeMap.containsKey(empNum)) {
            SystemGUIHelper.showError(null,
                "Employee Number \"" + empNum + "\" already exists.\n"
                + "Please use a unique Employee Number.");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String field = data[i] == null ? "" : data[i].trim();
            sb.append(field);
            if (i < data.length - 1) sb.append(",");
        }

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(employeeFile, true))) { // true = append
            bw.newLine();
            bw.write(sb.toString());
            System.out.println("[DataLoading] Employee saved: " + empNum);
        } catch (IOException e) {
            SystemGUIHelper.showWarning(null,
                "Error writing to employee file.\n"
                + "Check that the file is not open in another program.");
            return false;
        }

        employeeMap.put(empNum, data);
        return true;
    }
    // GENERATE NEXT EMPLOYEE NUMBER
    static String generateNextEmpNumber(HashMap<String, String[]> employeeMap) {
        int max = 0;
        for (String key : employeeMap.keySet()) {
            try {
                int num = Integer.parseInt(key.trim());
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.valueOf(max + 1);
    }

    // REWRITE EMPLOYEE FILE - overwrites the CSV from scratch
    static boolean rewriteEmployeeFile(String employeeFile,
                                        HashMap<String, String[]> employeeMap) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(employeeFile, false))) { // false = overwrite

            bw.write(EMPLOYEE_CSV_HEADER);
            bw.newLine();

            // Sort by Employee # so the file stays in a predictable order
            var keys = new ArrayList<String>(employeeMap.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                String[] data = employeeMap.get(key);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    String field = data[i] == null ? "" : data[i].trim();
                    sb.append(field);
                    if (i < data.length - 1) sb.append(",");
                }
                bw.write(sb.toString());
                bw.newLine();
            }

            System.out.println("[DataLoading] Employee file rewritten — "
                + keys.size() + " record(s).");
            return true;

        } catch (IOException e) {
            SystemGUIHelper.showWarning(null,
                "Error writing to employee file.\n"
                + "Check that the file is not open in another program.");
            return false;
        }
    }

    // EDIT EMPLOYEE 
    static boolean updateEmployee(String employeeFile,
                                   String oldEmpNum,
                                   String[] newData,
                                   HashMap<String, String[]> employeeMap) {

        if (newData == null || newData.length < 19) {
            SystemGUIHelper.showWarning(null,
                "Cannot update: employee data is incomplete.");
            return false;
        }

        if (!employeeMap.containsKey(oldEmpNum)) {
            SystemGUIHelper.showError(null,
                "Employee #" + oldEmpNum + " was not found.\n"
                + "It may have already been deleted or modified.");
            return false;
        }

        String newEmpNum = newData[0].trim();

        // Guard: Avoids duplicate Employee Number
        if (!newEmpNum.equals(oldEmpNum)
                && employeeMap.containsKey(newEmpNum)) {
            SystemGUIHelper.showError(null,
                "Employee Number \"" + newEmpNum + "\" is already in use.\n"
                + "Please choose a different Employee Number.");
            return false;
        }

        // Keep a backup of the original record in case the write fails
        String[] backup = employeeMap.get(oldEmpNum);

        // Change Employee Number
        employeeMap.remove(oldEmpNum);
        employeeMap.put(newEmpNum, newData);

        // Rewrite the whole file from the updated map
        boolean success = rewriteEmployeeFile(employeeFile, employeeMap);

        if (!success) {
            employeeMap.remove(newEmpNum);
            employeeMap.put(oldEmpNum, backup);
        }

        return success;
    }

    // DELETE EMPLOYEE FILE
    static boolean deleteEmployee(String employeeFile,
                                   String empNum,
                                   HashMap<String, String[]> employeeMap) {

        if (!employeeMap.containsKey(empNum)) {
            SystemGUIHelper.showError(null,
                "Employee #" + empNum + " was not found.\n"
                + "It may have already been deleted.");
            return false;
        }

        // Keep a backup in case the file write fails
        String[] backup = employeeMap.get(empNum);
        employeeMap.remove(empNum);

        boolean success = rewriteEmployeeFile(employeeFile, employeeMap);

        if (!success) {
            employeeMap.put(empNum, backup);
        }

        return success;
    }

    // SAFE GETTER — returns empty string if index is out of bounds
   static String safeGet(String[] data, int index) {
        if (data == null || index >= data.length) return "";
        return data[index].trim();
    }
}
