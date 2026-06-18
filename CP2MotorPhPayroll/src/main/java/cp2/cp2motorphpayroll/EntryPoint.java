package cp2.cp2motorphpayroll;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;


public class EntryPoint {
    
    // SHARED FILE PATHS
    static final String ATTENDANCE_FILE = "resources/MotorPH_Attendance Record.csv";
    static final String EMPLOYEE_FILE   = "resources/MotorPH_Employee Details.csv";
   
    // SHARED IN-MEMORY DATA MAPS  
    static HashMap<String, String[]>       employeeMap   = new HashMap<>();
    static HashMap<String, List<String[]>> attendanceMap = new HashMap<>();

    // MAIN ENTRY POINT
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            DataLoading.loadEmployees(EMPLOYEE_FILE, employeeMap);
            DataLoading.loadAttendance(ATTENDANCE_FILE, attendanceMap);
            SystemLogInPanel.showLogin();
        });
    }
}
