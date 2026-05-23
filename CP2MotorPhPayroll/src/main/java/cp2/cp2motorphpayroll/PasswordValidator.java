package cp2.cp2motorphpayroll;


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
    }
