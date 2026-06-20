package cp2.cp2motorphpayroll;

import java.util.*;  
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import static cp2.cp2motorphpayroll.TempraryClass.showEmployeeAddPanel;


public class SystemEmployeePanel {

    //Column headers shown in the JTable
    static final String[] COLUMNS = {
        "Emp #", "Last Name", "First Name", "Birthday",
        "SSS Number", "PhilHealth No.", "TIN", "Pag-IBIG No.",
        "Position", "Status", "Hourly Rate"
    };

    //Corresponding CSV column indices for each display column
   static final int[] CSV_INDICES = {0, 1, 2, 3, 5, 6, 7, 8, 10, 9, 16};

    static DefaultTableModel tableModel;
    static JTable            table;
    static JFrame            frame;



    // SHOW PANEL
    static void show() {
        frame = new JFrame("MotorPh — Employee Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1107, 640);
        frame.setLocationRelativeTo(null);


        JPanel header = SystemGUIHelper.buildHeader(
            "Employee Portal", frame, 1207,
            () -> SystemLogInPanel.showLogin());

        JPanel searchBar = buildSearchBar();
        buildTable();

        JLabel statusBar = new JLabel(
            "  " + EntryPoint.employeeMap.size()
            + " employee(s) loaded.");
        statusBar.setFont(SystemGUIHelper.FONT_SMALL);
        statusBar.setForeground(Color.GRAY);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                SystemGUIHelper.COLOR_BORDER));
        statusBar.setPreferredSize(new Dimension(1107, 24));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                SystemGUIHelper.COLOR_BORDER));

        JPanel center = new JPanel(new BorderLayout());
        center.add(searchBar,  BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(header,    BorderLayout.NORTH);
        frame.add(center,    BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // BUILD TABLE
    private static void buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(SystemGUIHelper.FONT_TABLE);
        table.setRowHeight(24);
        table.setGridColor(SystemGUIHelper.COLOR_BORDER);
        table.setSelectionBackground(new Color(84, 149, 233));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(SystemGUIHelper.FONT_BOLD);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] colWidths = {60, 110, 110, 90, 110, 110, 100, 100, 130, 80, 90};
        for (int i = 0; i < colWidths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
    }

    // REFRESH TABLE - shows ALL rows
    // Call after : Show All Records
    static void refreshTable() {
    tableModel.setRowCount(0);
        var keys = new ArrayList<String>();
    keys.addAll(EntryPoint.employeeMap.keySet());
    Collections.sort(keys);

    for (String key : keys) {
        String[] data = EntryPoint.employeeMap.get(key);
        Object[] row  = new Object[COLUMNS.length];
        for (int i = 0; i < CSV_INDICES.length; i++)
            row[i] = DataLoading.safeGet(data, CSV_INDICES[i]);
        tableModel.addRow(row);
    }
}

    // REFRESH TABLE FILTERED - shows rows matching search input
    // Call after: search button click
    static void refreshTableFiltered(String empNum, String name) {
    tableModel.setRowCount(0);
        var keys = new ArrayList<String>();
    keys.addAll(EntryPoint.employeeMap.keySet());
    Collections.sort(keys);

    for (String key : keys) {
        String[] data = EntryPoint.employeeMap.get(key);
        Object[] row  = new Object[COLUMNS.length];
        for (int i = 0; i < CSV_INDICES.length; i++)
            row[i] = DataLoading.safeGet(data, CSV_INDICES[i]);

        boolean matchEmp  = empNum.isEmpty()
            || String.valueOf(row[0]).contains(empNum);
        boolean matchName = name.isEmpty()
            || String.valueOf(row[1]).toLowerCase().contains(name)
            || String.valueOf(row[2]).toLowerCase().contains(name);

        if (matchEmp && matchName)
            tableModel.addRow(row);
    }
}

    // BUILD SEARCH BAR
    private static JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(SystemGUIHelper.COLOR_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                SystemGUIHelper.COLOR_BORDER));

        JLabel empNumLbl = new JLabel("Employee Number:");
        empNumLbl.setFont(SystemGUIHelper.FONT_BOLD);

        JTextField empNumField = SystemGUIHelper.makeField(10);
        empNumField.setPreferredSize(new Dimension(110, 28));

        JLabel nameLbl = new JLabel("Employee Name:");
        nameLbl.setFont(SystemGUIHelper.FONT_BOLD);

        JTextField nameField = SystemGUIHelper.makeField(14);
        nameField.setPreferredSize(new Dimension(160, 28));

        JButton searchBtn = SystemGUIHelper.makeButton("Search",
                SystemGUIHelper.COLOR_PRIMARY);
        searchBtn.setPreferredSize(new Dimension(90, 28));

        JButton clearBtn = SystemGUIHelper.makeButton("Clear",
            new Color(120, 120, 120));
        clearBtn.setPreferredSize(new Dimension(70, 28));

        JButton showAllBtn = SystemGUIHelper.makeButton("Show All Records",
                SystemGUIHelper.COLOR_PRIMARY);
        showAllBtn.setPreferredSize(new Dimension(150, 28));

        JButton addRcrd = SystemGUIHelper.makeButton("Add Employee",
                SystemGUIHelper.COLOR_PRIMARY);
        addRcrd.setPreferredSize(new Dimension(145, 28));

             // Search button - uses refreshTableFiltered()
            searchBtn.addActionListener(e -> {
            String empNum = empNumField.getText().trim();
            String name   = nameField.getText().trim().toLowerCase();

            // Safeguard: emp number must be numeric if provided
            if (!empNum.isEmpty() && !empNum.matches("\\d+")) {
                SystemGUIHelper.showError(frame,
                    """
                    Employee number must contain digits only.
                   Example: 10001""");
                empNumField.requestFocus();
                return;
            }

            // Safeguard: at least one field should have input
            if (empNum.isEmpty() && name.isEmpty()) {
                SystemGUIHelper.showWarning(frame,
                    "Please enter an employee number or name to search.");
                return;
            }

            // Safeguard: employee number not found in CSV
            if (!empNum.isEmpty()
                && !EntryPoint.employeeMap.containsKey(empNum)) {
                SystemGUIHelper.showError(frame,
                "Employee Number \"" + empNum + "\" was not found.\n"
                + "Please check the number and try again.");
            empNumField.requestFocus();
            return;
            }

            // Run the filtered search
             refreshTableFiltered(empNum, name);

             // Exception handling: employee name not found in CSV
             if (tableModel.getRowCount() == 0 && !name.isEmpty()) {
                 SystemGUIHelper.showError(frame,
                    "No employee found with name containing \""
                    + nameField.getText().trim() + "\".\n"
                    + "Please check the name and try again.");
            return;
        }

            refreshTableFiltered(empNum, name);
        });

        // Show All Records button - uses refreshTable()
        showAllBtn.addActionListener(e -> {
            empNumField.setText("");
            nameField.setText("");
            refreshTable();
        });

        // Clear button
        clearBtn.addActionListener(e -> {
            empNumField.setText("");
            nameField.setText("");
        tableModel.setRowCount(0);

            SystemGUIHelper.showInfo(frame,
                "Table Cleared");
        });

//      Add empployee button
        addRcrd.addActionListener(e -> {
            showEmployeeAddPanel(frame);
        });

        // Enter key on either field triggers search
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    searchBtn.doClick();
            }
        };
        empNumField.addKeyListener(enterKey);
        nameField.addKeyListener(enterKey);

        bar.add(empNumLbl); bar.add(empNumField);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(nameLbl);   bar.add(nameField);
        bar.add(searchBtn); bar.add(clearBtn);
        bar.add(showAllBtn); bar.add(showAllBtn);
        bar.add(addRcrd); bar.add(addRcrd);

        return bar;

    }
}
    
   