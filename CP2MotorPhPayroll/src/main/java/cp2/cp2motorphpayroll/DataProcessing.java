package cp2.cp2motorphpayroll;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * =========================================================
 * Employee CSV column layout (0-based index):
 *  0  = Employee # (key)    1  = Last Name
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
    static int count;
    static String filePath = EntryPoint.EMPLOYEE_FILE;

    // LOAD EMPLOYEES
    static void loadEmployees(String employeeFile,
                              HashMap<String, String[]> employeeMap) {
        File file = new File(employeeFile);
        if (!file.exists()) {
            //debug line start
            System.out.println(file.getAbsolutePath());
            SystemGUIHelper.showWarning(null,
                    "Employee file not found");
            return;
        }
        employeeMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header row
            String line;
            count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                line +="," + count;
                System.out.println(line);
                if (line.isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < MIN_EMPLOYEE_COLS) continue;
                employeeMap.put(data[0].trim(), data); //the key ID is
                count++;
            }
            System.out.println("[DataProcessing] Employees loaded: " + count);

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
            //debug line start
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
            System.out.println("[DataProcessing] Attendance records loaded: " + count);
        } catch (IOException e) {
            SystemGUIHelper.showWarning(null,
                    "Error Reading Attendance File");
        }
    }

    static String safeGet(String[] data, int index) {
        if (data == null || index >= data.length) return "";
        return data[index].trim();
    }

    static String[] readNewEmployeeField(JTextField[] fields) {
        int size = (fields.length);
        System.out.println(size);
        String[] content = new String[18];
        for (int i = 0; i < size; i++) {
            if(i==4){
                continue;
            }
            if(i>4){
                JTextField field = fields[i];
                content[i] = field.getText();
                System.out.println("Index " + i + ": " + content[i]);
            }
            if(i>10){
                JTextField field = fields[i];
                content[16] = field.getText();
                System.out.println("Index " + i + ": " + content[i]);
                break;
                //known bug [1, 3, 2, 4, null, 5, 6, 7, 8, 10, 9, 11, null, null, null, null, 11, null]
                //hr gets copied to 11th field
            }
            JTextField field = fields[i];
            content[i] = field.getText();
            System.out.println("Index " + i + ": " + content[i]);
        }
        return content;
    }

    static String[] addEmployeeToRecords(JTextField[] fields){
        String[] data = readNewEmployeeField(fields);
        //bug where if you add one entry and another it slips into the top of the latest entered array
        EntryPoint.employeeMap.put(data[0].trim(), data); //the key ID is
        System.out.println(data[0]);
        System.out.println(Arrays.toString(data));
        return data;
    }

    static void saveEmployeeToCSV(String[] data) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            // Join array elements with commas
            String line = String.join(",", data);
            // Append newline at the end
            writer.write(line + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Great for validating the length of an ID
    static boolean validLength (String content, int limit){

        return false;
    }

    static boolean onlyLetters (JTextField content){
        if (content != null && content.getText().matches("[a-zA-Z]+")){
            return true;
        }
        return false;
    }

    static boolean onlyIntegers (String content, int limit){
        if (content.length()!=limit){
            return false;
        }
        try{
            int parsedInteger = Integer.parseInt(content);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    static boolean onlyIntegers (String content){
        try{
            int parsedInteger = Integer.parseInt(content);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }


    static String removeSpaces(String input){
        input = input.replace(" ", "");
        return input;
    }
}