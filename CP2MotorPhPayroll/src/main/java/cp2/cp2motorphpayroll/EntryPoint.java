package cp2.cp2motorphpayroll;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;


public class EntryPoint {
    
    // SHARED FILE PATHS
    public static final String ATTENDANCE_FILE = "CP2MotorPhPayroll/resources/MotorPH_Attendance Record.csv";
    public static final String EMPLOYEE_FILE   = "CP2MotorPhPayroll/resources/MotorPH_Employee Details.csv";
    // SHARED IN-MEMORY DATA MAPS  
    static HashMap<String, String[]>       employeeMap   = new HashMap<>();
    static HashMap<String, List<String[]>> attendanceMap = new HashMap<>();

    // MAIN ENTRY POINT
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            DataProcessing.loadEmployees(EMPLOYEE_FILE, employeeMap);
            DataProcessing.loadAttendance(ATTENDANCE_FILE, attendanceMap);
            SystemLogInPanel.showLogin();
        });
    }
}
