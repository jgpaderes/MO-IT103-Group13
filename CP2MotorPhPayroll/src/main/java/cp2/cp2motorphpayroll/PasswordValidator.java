package cp2.cp2motorphpayroll;


import javax.swing.*;
import java.awt.*;

public class PasswordValidator{

    //ACCOUNTS - add new accounts here:
    // username, password, role
    private static final String[][] ACCOUNTS = {
        { "employee",      "12345", "employee"      },
        { "payroll_staff", "12345", "payroll_staff" },
        { "group13", "12345", "employee" },
    };

     //VALIDATE PASSWORD
    static String ValidatePassword(String usernameInput,
                                    String passwordInput) {
        if (usernameInput == null || passwordInput == null) {
            return null;
        }
        for (String[] account : ACCOUNTS) {
            if (account[0].equals(usernameInput)
                    && account[1].equals(passwordInput)) {
                return account[2]; 
            }
        }
        return null; 
    }

    // HELPER — check if a username exists in the registry
    static boolean usernameExists(String usernameInput) {
        for (String[] account : ACCOUNTS) {
            if (account[0].equals(usernameInput)) {
                return true;
            }
        }
        return false;
        }

    static String[] extractEmployeeData(JTextField[] fields, String[] employee) {
        for (int i = 0; i < fields.length; i++) {
            employee[i] = fields[i].getText();
            System.out.println("I read: " + employee[i]);
        }
        return employee;
    }
        //
    static String[] allFieldsEntered(JTextField[] fields, String[] employee){
        int i = 0;
        for (JTextField field : fields){
            if (field.getText() == null || field.getText().isBlank()){
                field.setForeground(Color.RED);
                field.setText("Missing content");
            }
            employee[i] = field.getText();
            i++;
            System.out.println("I read!" + field.getText());
        }
        return employee;
    }


}




