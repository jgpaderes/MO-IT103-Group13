package cp2.cp2motorphpayroll;


import javax.swing.*;

public class Validation {

    //ACCOUNTS - add new accounts here:
    // username, password, role
    private static final String[][] ACCOUNTS = {
            {"employee", "12345", "employee"},
            {"payroll_staff", "12345", "payroll_staff"},
            {"group13", "12345", "employee"},
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


    //Data validation methods
    static boolean onlyLetters(JTextField content) {
        return content != null && processJTextField(content).matches("[a-zA-Z]+");
    }

    static boolean onlyIntegers(JTextField contentField, int limit) {
        String content = processJTextField(contentField);
        if (content.length() != limit && limit != 0) {
            return false;
        }
        if (!content.matches("\\d+")) {
            return false;
        }
        return true;
    }

    static boolean onlyIntegers(String content, int limit) {
        if (content.length() != limit && limit != 0) {
            return false;
        }
        try {
            int parsedInteger = Integer.parseInt(content);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean onlyIntegers(String content) {
        try {
            int parsedInteger = Integer.parseInt(content);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    //Checks user input based on format defined by other methods (eg. xxx-xxx-xxx)
    //It splits input and format based on the format and checks if the input is an integer and of equal length to the template
    static boolean genericValidator(JTextField field, String format, String separator) {
        String input = processJTextField(field);
        String[] chunkedFormat = format.split(separator, -1);
        String[] chunkedInput = input.split(separator, -1);

        if (chunkedInput.length != chunkedFormat.length) {
            return false;
        }

        for (int i = 0; i < chunkedFormat.length; i++) {
            System.out.println("-----");
            System.out.println(chunkedInput[i]);
            System.out.println(chunkedFormat[i]);
            if (!(onlyIntegers(chunkedInput[i], chunkedFormat[i].length()))) {
                return false;
            }
        }
        return true;
    }


    // For SSS numbers: xx-xxxxxxx-x
    static boolean validateSSS(JTextField field) {
        return genericValidator(field, "xx-xxxxxxx-x", "-");
    }

    // For TIN numbers: xxxx-xxxx-xxxx
    static boolean validateTIN(JTextField field) {
        return genericValidator(field, "xxx-xxx-xxx-xxx", "-");
    }

    // FOr Pag-IBIG Numbers xxxx-xxxx-xxxx
    static boolean validatePagIbig(JTextField field) {
        return onlyIntegers(field, 12);
    }

    // For PhilHealth numbers: 12 digits, no delimiter
    static boolean validatePhilHealth(JTextField field) {
        return onlyIntegers(field, 12);
    }

    // For dates in MM/DD/YYYY format
    static boolean validateDate(JTextField dateField) {
        //call generic validator, if false return false. if it's true
        //check if dates and months are above the limit using a java library
        //if im gonna do that validateDate probably shouldn't use genericValidator()
        //but i had a cool eureka moment so ill use it anyways
        return genericValidator(dateField, "xx/xx/xxxx", "/");
    }

    static String processJTextField(JTextField field) {
        return field.getText();
    }
}






