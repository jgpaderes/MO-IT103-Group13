package cp2.cp2motorphpayroll;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class SystemLogInPanel{

    private static int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    static void showLogin() {
        failedAttempts = 0; // reset on each new login window

        JFrame frame = new JFrame("MotorPh Payroll System — Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 490);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(SystemGUIHelper.COLOR_BG);

        // ── Header banner ──
        JPanel header = new JPanel();
        header.setBackground(SystemGUIHelper.COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(420, 110));
        header.setLayout(new GridBagLayout());

        JLabel logo = new JLabel("MotorPh");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Automatic Payroll System");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(84, 149, 233));

        JPanel logoStack = new JPanel();
        logoStack.setOpaque(false);
        logoStack.setLayout(new BoxLayout(logoStack, BoxLayout.Y_AXIS));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoStack.add(logo);
        logoStack.add(sub);
        header.add(logoStack);

        // ── Login form ──
        JPanel form = new JPanel();
        form.setBackground(SystemGUIHelper.COLOR_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SystemGUIHelper.COLOR_BORDER),
            BorderFactory.createEmptyBorder(28, 36, 28, 36)));
        form.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 1;

        // Sign In title
        JLabel titleLbl = new JLabel("Sign In");
        titleLbl.setFont(SystemGUIHelper.FONT_TITLE);
        titleLbl.setForeground(SystemGUIHelper.COLOR_SECONDARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        form.add(titleLbl, gbc);

        // Username label + field
        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(SystemGUIHelper.FONT_BOLD);
        gbc.gridy = 1; gbc.insets = new Insets(4, 0, 2, 0);
        form.add(userLbl, gbc);

        JTextField userField = SystemGUIHelper.makeField(20);
        userField.setToolTipText("Enter: valid username");
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 10, 0);
        form.add(userField, gbc);

        // Password label + field
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(SystemGUIHelper.FONT_BOLD);
        gbc.gridy = 3; gbc.insets = new Insets(4, 0, 2, 0);
        form.add(passLbl, gbc);

        JPasswordField passField = new JPasswordField(20);
        passField.setFont(SystemGUIHelper.FONT_LABEL);
        passField.setBackground(SystemGUIHelper.COLOR_FIELD_BG);
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SystemGUIHelper.COLOR_BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        passField.setToolTipText("Enter: valid password");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 6, 0);
        form.add(passField, gbc);

        // Attempt warning label
        JLabel attemptLbl = new JLabel(" ");
        attemptLbl.setFont(SystemGUIHelper.FONT_SMALL);
        attemptLbl.setForeground(SystemGUIHelper.COLOR_ERROR);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 8, 0);
        form.add(attemptLbl, gbc);

        // Login button
        JButton loginBtn = SystemGUIHelper.makeButton("LOGIN",
                SystemGUIHelper.COLOR_PRIMARY);
        loginBtn.setPreferredSize(new Dimension(100, 36));
        gbc.gridy = 6; gbc.insets = new Insets(4, 0, 0, 0);
        form.add(loginBtn, gbc);

        // ── Login action ──
        Runnable doLogin = () -> {
            // Lockout check
            if (failedAttempts >= MAX_ATTEMPTS) {
                SystemGUIHelper.showError(frame,
                    "Too many failed attempts.\nPlease restart the application.");
                return;
            }

            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            // Safeguard 1: both empty
            if (username.isEmpty() && password.isEmpty()) {
                SystemGUIHelper.showError(frame,
                    "Username and password cannot be empty.");
                return;
            }
            // Safeguard 2: username empty
            if (username.isEmpty()) {
                SystemGUIHelper.showError(frame, "Username cannot be empty.");
                userField.requestFocus(); return;
            }
            // Safeguard 3: password empty
            if (password.isEmpty()) {
                SystemGUIHelper.showError(frame, "Password cannot be empty.");
                passField.requestFocus(); return;
            }
            
            // Safeguard 4: invalid username
            if (!PasswordValidator.usernameExists(username)) {
            failedAttempts++;
            int rem = MAX_ATTEMPTS - failedAttempts;
            attemptLbl.setText(rem > 0
                ? "Invalid username. " + rem + " attempt(s) left."
                : "Account locked. Please restart.");
            if (rem <= 0) loginBtn.setEnabled(false);
                passField.setText(""); return;
            }
            
            // Safeguard 5: wrong password
            String role = PasswordValidator.ValidatePassword(
            username, password);
            if (role == null) {
                failedAttempts++;
            int rem = MAX_ATTEMPTS - failedAttempts;
            attemptLbl.setText(rem > 0
                ? "Incorrect password. " + rem + " attempt(s) left."
                : "Account locked. Please restart.");
            if (rem <= 0) loginBtn.setEnabled(false);
                passField.setText(""); return;
            }
            
            //Login success - route to correct portal
            failedAttempts = 0;
            frame.dispose();
            if (role.equals("employee")) {
                 SystemEmployeePanel.show();
            } else {
                SystemPayrollPanel.show();
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());

        // Enter key on either field triggers login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin.run();
            }
        };
        userField.addKeyListener(enterKey);
        passField.addKeyListener(enterKey);

        // Assemble 
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(SystemGUIHelper.COLOR_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));
        outer.add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel(
            "MotorPh Automatic Payroll System © 2026",
            SwingConstants.CENTER);
        hint.setFont(SystemGUIHelper.FONT_SMALL);
        hint.setForeground(Color.GRAY);
        outer.add(hint, BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout());
        frame.add(header, BorderLayout.NORTH);
        frame.add(outer,  BorderLayout.CENTER);
        frame.setVisible(true);
        userField.requestFocusInWindow();
        }
}


