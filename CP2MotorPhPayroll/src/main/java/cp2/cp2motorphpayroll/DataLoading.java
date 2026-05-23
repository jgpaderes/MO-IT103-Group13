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
public class DataLoading {

    static final int MIN_EMPLOYEE_COLS = 4;

    // LOAD EMPLOYEES
    static void loadEmployees(String employeeFile,
                               HashMap<String, String[]> employeeMap) {
        File file = new File(employeeFile);
        if (!file.exists()) {
            SystemMotorPhGUI.showWarning(null,
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
             SystemMotorPhGUI.showWarning(null,
                "Error reading employee file");
        }
    }

    // LOAD ATTENDANCE 
    static void loadAttendance(String attendanceFile,
                                HashMap<String, List<String[]>> attendanceMap) {
        File file = new File(attendanceFile);
        if (!file.exists()) {
             SystemMotorPhGUI.showWarning(null,
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
             SystemMotorPhGUI.showWarning(null,
                "Error Reading Attendance File");
        }
    }

    static String safeGet(String[] data, int index) {
        if (data == null || index >= data.length) return "";
        return data[index].trim();
    }
}