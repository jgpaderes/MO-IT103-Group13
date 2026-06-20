package cp2.cp2motorphpayroll;

import javax.swing.*;
import java.awt.*;

public class TempraryClass {
    public static void showEmployeeAddPanel(JFrame frame) {
        SwingUtilities.invokeLater(() -> {
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel sub = new JLabel("Automatic Payroll System");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            sub.setForeground(new Color(84, 149, 233));
            header.add(sub);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setPreferredSize(new Dimension(500,500));
            panel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 10, 5, 10);

            JLabel labelEmployeeNumber = new JLabel("Employee Number");
            JTextField employeeNumber = new JTextField(20);

            JLabel labelFirstName = new JLabel("First Name");
            JTextField firstName = new JTextField(20);

            JLabel labelLastName = new JLabel("Last Name");
            JTextField lastName = new JTextField(20);

            JLabel labelSssNumber = new JLabel("SSS Number");
            JTextField sssNumber = new JTextField(20);

            JLabel labelPhilHealthNumber = new JLabel("PhilHealth Number");
            JTextField philHealthNumber = new JTextField(20);

            JLabel labelTinNumber = new JLabel("TIN Number");
            JTextField tinNumber = new JTextField(20);

            JLabel labelPagIbigNumber = new JLabel("Pag-IBIG Number");
            JTextField pagIbigNumber = new JTextField(20);


            // Left column
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            panel.add(labelEmployeeNumber, gbc);
            gbc.gridy = 1;
            panel.add(employeeNumber, gbc);

            gbc.gridy = 2;
            panel.add(labelFirstName, gbc);
            gbc.gridy = 3;
            panel.add(firstName, gbc);

            gbc.gridy = 4;
            panel.add(labelLastName, gbc);
            gbc.gridy = 5;
            panel.add(lastName, gbc);

            // middle
            gbc.gridx = 1;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.CENTER;

            gbc.gridy = 0;
            panel.add(labelSssNumber, gbc);
            gbc.gridy = 1;
            panel.add(sssNumber, gbc);

            gbc.gridy = 2;
            panel.add(labelPhilHealthNumber, gbc);
            gbc.gridy = 3;
            panel.add(philHealthNumber, gbc);

            gbc.gridy = 4;
            panel.add(labelTinNumber, gbc);
            gbc.gridy = 5;
            panel.add(tinNumber, gbc);

            // right
            gbc.gridx = 2;
            gbc.weightx = 0.4;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            panel.add(labelPagIbigNumber, gbc);
            gbc.gridy = 1;
            panel.add(pagIbigNumber, gbc);




            SystemGUIHelper.makePopup(frame, panel, 50, 50);
        });
    }
}
