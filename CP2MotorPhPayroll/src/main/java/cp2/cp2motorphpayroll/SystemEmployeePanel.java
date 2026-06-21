package cp2.cp2motorphpayroll;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class SystemEmployeePanel {

    static JDialog addEmployeePopup;

    // Column headers shown in the JTable
    static final String[] COLUMNS = {
            "Emp #", "Last Name", "First Name", "Birthday",
            "SSS Number", "PhilHealth No.", "TIN", "Pag-IBIG No.",
            "Position", "Status", "Hourly Rate"
    };

    // Corresponding CSV column indices for each display column
    static final int[] CSV_INDICES = {0, 1, 2, 3, 5, 6, 7, 8, 10, 9, 16};

    // ── SHARED FIELD DEFINITIONS ──────────────────────────────
    // Used by View, Add, and Edit dialogs so the field list,
    // label, CSV index, and required flag are defined ONCE.
    // { label, csvIndex, required }
    static final Object[][] FIELD_DEFS = {
            { "Employee #",            0,  true  },
            { "Last Name",             1,  true  },
            { "First Name",            2,  true  },
            { "Birthday (MM/DD/YYYY)", 3,  true  },
            { "Phone Number",          4,  true  },
            { "SSS #",                 5,  true  },
            { "PhilHealth #",          6,  true  },
            { "TIN #",                 7,  true  },
            { "Pag-IBIG #",            8,  true  },
            { "Status",                9,  true  },
            { "Position",              10, true  },
            { "Basic Salary",          11, true  },
            { "Rice Subsidy",          12, false },
            { "Phone Allowance",       13, false },
            { "Clothing Allowance",    14, false },
            { "Gross Semi-monthly",    15, false },
            { "Hourly Rate",           16, true  },
            { "Immediate Supervisor",  17, false },
            { "Address",               18, false },
    };

    // CSV indices that must be a valid number when filled in
    static final int[] NUMERIC_INDICES = {11, 12, 13, 14, 15, 16};

    static DefaultTableModel tableModel;
    static JTable            table;
    static JFrame            frame;

    // ============================================================
    // SHOW PANEL
    // ============================================================
    static void show() {
        frame = new JFrame("MotorPh — Employee Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1107, 680);
        frame.setLocationRelativeTo(null);

        JPanel header = SystemGUIHelper.buildHeader(
                "Employee Portal", frame, 1207,
                () -> SystemLogInPanel.showLogin());

        JPanel searchBar = buildSearchBar();
        buildTable();

        // Double-click a row opens the View dialog
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) showViewDialog(row);
                }
            }
        });

        JLabel statusBar = new JLabel(
                "  " + EntryPoint.employeeMap.size() + " employee(s) loaded.");
        statusBar.setFont(SystemGUIHelper.FONT_SMALL);
        statusBar.setForeground(Color.GRAY);
        statusBar.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, SystemGUIHelper.COLOR_BORDER));
        statusBar.setPreferredSize(new Dimension(1107, 24));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(SystemGUIHelper.COLOR_BORDER));

        JPanel actionBar = buildActionBar(table);

        JPanel center = new JPanel(new BorderLayout());
        center.add(searchBar,  BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(actionBar,  BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout());
        frame.add(header,    BorderLayout.NORTH);
        frame.add(center,    BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // ============================================================
    // ACTION BAR — CRUD buttons below the table
    // ============================================================
    private static JPanel buildActionBar(JTable table) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(SystemGUIHelper.COLOR_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, SystemGUIHelper.COLOR_BORDER));

        JButton viewBtn   = SystemGUIHelper.makeButton("View Employee", SystemGUIHelper.COLOR_PRIMARY);
        JButton addBtn    = SystemGUIHelper.makeButton("Add Employee", SystemGUIHelper.COLOR_SUCCESS);
        JButton editBtn   = SystemGUIHelper.makeButton("Edit Employee", new Color(255, 165, 100));
        JButton deleteBtn = SystemGUIHelper.makeButton("Delete Employee", new Color(180, 30, 30));

        viewBtn.setPreferredSize(new Dimension(130, 28));
        addBtn.setPreferredSize(new Dimension(130, 28));
        editBtn.setPreferredSize(new Dimension(130, 28));
        deleteBtn.setPreferredSize(new Dimension(140, 28));

        // Disabled until a row is selected
        viewBtn.setEnabled(false);
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = table.getSelectedRow() != -1;
                viewBtn.setEnabled(hasSelection);
                editBtn.setEnabled(hasSelection);
                deleteBtn.setEnabled(hasSelection);
            }
        });

        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) showViewDialog(row);
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) showEditDialog(row);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) confirmAndDelete(row);
        });

        bar.add(viewBtn);
        bar.add(addBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        return bar;
    }

    // ============================================================
    // BUILD TABLE
    // ============================================================
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

    // ============================================================
    // REFRESH TABLE — shows ALL rows
    // ============================================================
    static void refreshTable() {
        refreshTableFiltered("", "");
    }

    // ============================================================
    // REFRESH TABLE FILTERED — rows matching search input
    // (refreshTable() now just calls this with empty filters,
    //  so the row-building logic only exists in one place)
    // ============================================================
    static void refreshTableFiltered(String empNum, String name) {
        tableModel.setRowCount(0);
        var keys = new ArrayList<String>(EntryPoint.employeeMap.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            String[] data = EntryPoint.employeeMap.get(key);
            Object[] row  = new Object[COLUMNS.length];
            for (int i = 0; i < CSV_INDICES.length; i++)
                row[i] = DataProcessing.safeGet(data, CSV_INDICES[i]);

            boolean matchEmp  = empNum.isEmpty()
                    || String.valueOf(row[0]).contains(empNum);
            boolean matchName = name.isEmpty()
                    || String.valueOf(row[1]).toLowerCase().contains(name)
                    || String.valueOf(row[2]).toLowerCase().contains(name);

            if (matchEmp && matchName)
                tableModel.addRow(row);
        }
    }

    // ============================================================
    // VIEW DIALOG — shows full details for the selected row
    // ============================================================
    private static void showViewDialog(int tableRow) {

        String empNum = String.valueOf(tableModel.getValueAt(tableRow, 0));
        String[] data = EntryPoint.employeeMap.get(empNum);

        if (data == null) {
            SystemGUIHelper.showError(frame, "Could not load details for Employee #" + empNum);
            return;
        }

        JDialog dialog = new JDialog(frame,
                "Employee Details — " + DataProcessing.safeGet(data, 2)
                        + " " + DataProcessing.safeGet(data, 1), true);
        dialog.setSize(480, 580);
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);

        // Header strip
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(SystemGUIHelper.COLOR_PRIMARY);
        dialogHeader.setPreferredSize(new Dimension(480, 66));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(15, 14, 0, 14));

        JLabel titleLbl = new JLabel(
                DataProcessing.safeGet(data, 2) + " " + DataProcessing.safeGet(data, 1));
        titleLbl.setFont(SystemGUIHelper.FONT_BOLD);
        titleLbl.setForeground(Color.WHITE);

        JLabel posLbl = new JLabel(DataProcessing.safeGet(data, 10));
        posLbl.setFont(SystemGUIHelper.FONT_SMALL);
        posLbl.setForeground(new Color(180, 200, 255));

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(titleLbl);
        titleStack.add(posLbl);
        dialogHeader.add(titleStack, BorderLayout.CENTER);

        // Fields grid — read-only label/value pairs, built from FIELD_DEFS
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints gl = new GridBagConstraints();
        gl.anchor = GridBagConstraints.WEST;
        gl.insets = new Insets(4, 4, 4, 10);

        GridBagConstraints gv = new GridBagConstraints();
        gv.anchor    = GridBagConstraints.WEST;
        gv.fill      = GridBagConstraints.HORIZONTAL;
        gv.weightx   = 1.0;
        gv.insets    = new Insets(4, 0, 4, 4);
        gv.gridwidth = GridBagConstraints.REMAINDER;

        for (Object[] def : FIELD_DEFS) {
            String label    = (String)  def[0];
            int    csvIndex = (Integer) def[1];
            String value    = DataProcessing.safeGet(data, csvIndex);

            gl.gridx = 0; gl.gridy = GridBagConstraints.RELATIVE;
            JLabel lbl = new JLabel(label.replace(" (MM/DD/YYYY)", "") + ":");
            lbl.setFont(SystemGUIHelper.FONT_BOLD);
            lbl.setForeground(SystemGUIHelper.COLOR_SECONDARY);
            grid.add(lbl, gl);

            gv.gridx = 1;
            JLabel val = new JLabel(value.isEmpty() ? "—" : value);
            val.setFont(SystemGUIHelper.FONT_TABLE);
            grid.add(val, gv);
        }

        JButton closeBtn = SystemGUIHelper.makeButton("Close", SystemGUIHelper.COLOR_PRIMARY);
        closeBtn.setPreferredSize(new Dimension(100, 32));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        btnRow.setBackground(SystemGUIHelper.COLOR_BG);
        btnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemGUIHelper.COLOR_BORDER));
        btnRow.add(closeBtn);

        JScrollPane gridScroll = new JScrollPane(grid);
        gridScroll.setBorder(null);

        dialog.setLayout(new BorderLayout());
        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(gridScroll,   BorderLayout.CENTER);
        dialog.add(btnRow,       BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ============================================================
    // ADD EMPLOYEE DIALOG
    // ============================================================
    static void showAddDialog() {

        JTextField[] inputs = new JTextField[19];

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(240, 240, 245));
        titleBar.setPreferredSize(new Dimension(1180, 40));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemGUIHelper.COLOR_BORDER));

        JLabel titleLbl = new JLabel("  Add New Employee");
        titleLbl.setFont(SystemGUIHelper.FONT_BOLD);
        titleBar.add(titleLbl, BorderLayout.WEST);

        JButton xBtn = new JButton("✕");
        xBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        xBtn.setFocusPainted(false);
        xBtn.setBorderPainted(false);
        xBtn.setContentAreaFilled(false);
        xBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        xBtn.setPreferredSize(new Dimension(40, 40));
        xBtn.addActionListener(e -> SystemGUIHelper.closePopup(addEmployeePopup));
        titleBar.add(xBtn, BorderLayout.EAST);

        // Split FIELD_DEFS into 4 even columns for the Add layout
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 14, 5, 14);

        int perColumn = (int) Math.ceil(FIELD_DEFS.length / 4.0);
        for (int col = 0; col < 4; col++) {
            int start = col * perColumn;
            int end   = Math.min(start + perColumn, FIELD_DEFS.length);
            if (start >= end) continue;
            Object[][] columnDefs = Arrays.copyOfRange(FIELD_DEFS, start, end);
            addColumn(grid, gbc, columnDefs, col, inputs);
        }

        // Auto-generate and lock the Employee # field
        String nextEmpNum = DataProcessing.generateNextEmpNumber(EntryPoint.employeeMap);
        inputs[0].setText(nextEmpNum);
        inputs[0].setEditable(false);
        inputs[0].setBackground(new Color(238, 238, 238));

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemGUIHelper.COLOR_BORDER));
        statusBar.setPreferredSize(new Dimension(1180, 50));

        JLabel legend = new JLabel("Required fields");
        legend.setFont(SystemGUIHelper.FONT_SMALL);
        legend.setForeground(SystemGUIHelper.COLOR_REQFIELD);
        statusBar.add(legend);

        JButton cancelBtn = SystemGUIHelper.makeButton("Cancel", new Color(120, 120, 180));
        cancelBtn.setPreferredSize(new Dimension(90, 28));
        cancelBtn.addActionListener(e -> SystemGUIHelper.closePopup(addEmployeePopup));
        statusBar.add(cancelBtn);

        JButton confirmBtn = SystemGUIHelper.makeButton("Confirm", new Color(120, 120, 180));
        confirmBtn.setPreferredSize(new Dimension(90, 28));
        statusBar.add(confirmBtn);

        // Save action — validation now goes through the shared helper
        confirmBtn.addActionListener(e -> {
            String[] newData = collectInputs(inputs);
            if (!validateEmployeeData(frame, newData, inputs)) return;

            boolean saved = DataProcessing.saveEmployee(
                    EntryPoint.EMPLOYEE_FILE, newData, EntryPoint.employeeMap);

            if (saved) {
                refreshTable();
                SystemGUIHelper.closePopup(addEmployeePopup);
                SystemGUIHelper.showInfo(frame,
                        "Employee #" + newData[0] + " has been added successfully.");
            }
        });

        JPanel finalPanel = new JPanel(new BorderLayout());
        finalPanel.setPreferredSize(new Dimension(1180, 560));
        finalPanel.add(titleBar,  BorderLayout.NORTH);
        finalPanel.add(grid,      BorderLayout.CENTER);
        finalPanel.add(statusBar, BorderLayout.SOUTH);
        finalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        addEmployeePopup = new JDialog(frame, true);
        addEmployeePopup.setUndecorated(true);
        addEmployeePopup.getContentPane().add(finalPanel);
        addEmployeePopup.pack();

        Point frameLocation = frame.getLocationOnScreen();
        addEmployeePopup.setLocation(frameLocation.x + 80, frameLocation.y + 60);
        addEmployeePopup.setVisible(true); // blocks until closed
    }

    // ADD COLUMN — helper for showAddDialog()'s 4-column layout
    private static void addColumn(JPanel container, GridBagConstraints gbc,
                                  Object[][] fields, int colIndex,
                                  JTextField[] inputs) {
        gbc.gridx = colIndex;
        for (int row = 0; row < fields.length; row++) {
            String  label    = (String)  fields[row][0];
            int     csvIdx   = (Integer) fields[row][1];
            boolean required = (Boolean) fields[row][2];

            gbc.gridy = row * 2;
            JLabel lbl = new JLabel(label);
            lbl.setFont(SystemGUIHelper.FONT_BOLD);
            lbl.setForeground(required ? SystemGUIHelper.COLOR_REQFIELD : Color.GRAY);
            container.add(lbl, gbc);

            gbc.gridy = row * 2 + 1;
            JTextField tf = SystemGUIHelper.makeField(12);
            tf.setPreferredSize(new Dimension(220, 28));
            inputs[csvIdx] = tf;
            container.add(tf, gbc);
        }
    }

    // ============================================================
    // EDIT EMPLOYEE DIALOG
    // ============================================================
    private static void showEditDialog(int tableRow) {

        String oldEmpNum = String.valueOf(tableModel.getValueAt(tableRow, 0));
        String[] currentData = EntryPoint.employeeMap.get(oldEmpNum);

        if (currentData == null) {
            SystemGUIHelper.showError(frame, "Could not load details for Employee #" + oldEmpNum);
            return;
        }

        JDialog dialog = new JDialog(frame, "Edit Employee — #" + oldEmpNum, true);
        dialog.setSize(500, 640);
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);

        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(SystemGUIHelper.COLOR_PRIMARY);
        dialogHeader.setPreferredSize(new Dimension(500, 46));
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        JLabel headerLbl = new JLabel("Edit Employee — #" + oldEmpNum);
        headerLbl.setFont(SystemGUIHelper.FONT_BOLD);
        headerLbl.setForeground(Color.WHITE);
        dialogHeader.add(headerLbl, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints gl = new GridBagConstraints();
        gl.anchor = GridBagConstraints.WEST;
        gl.insets = new Insets(4, 4, 4, 10);

        GridBagConstraints gf = new GridBagConstraints();
        gf.fill      = GridBagConstraints.HORIZONTAL;
        gf.weightx   = 1.0;
        gf.insets    = new Insets(4, 0, 4, 4);
        gf.gridwidth = GridBagConstraints.REMAINDER;

        // One JTextField per CSV column, pre-filled with current data
        JTextField[] inputs = new JTextField[19];

        for (Object[] def : FIELD_DEFS) {
            String  label    = (String)  def[0];
            int     csvIdx   = (Integer) def[1];
            boolean required = (Boolean) def[2];

            gl.gridx = 0; gl.gridy = GridBagConstraints.RELATIVE;
            JLabel lbl = new JLabel(label + ":");
            lbl.setFont(SystemGUIHelper.FONT_BOLD);
            lbl.setForeground(required ? SystemGUIHelper.COLOR_REQFIELD : Color.GRAY);
            form.add(lbl, gl);

            gf.gridx = 1;
            JTextField tf = SystemGUIHelper.makeField(20);
            tf.setPreferredSize(new Dimension(240, 28));
            tf.setText(DataProcessing.safeGet(currentData, csvIdx)); // pre-fill
            inputs[csvIdx] = tf;
            form.add(tf, gf);
        }

        JLabel legend = new JLabel("   Required fields");
        legend.setFont(SystemGUIHelper.FONT_SMALL);
        legend.setForeground(SystemGUIHelper.COLOR_REQFIELD);

        JButton saveBtn   = SystemGUIHelper.makeButton("Save Changes", SystemGUIHelper.COLOR_SUCCESS);
        JButton cancelBtn = SystemGUIHelper.makeButton("Cancel", new Color(120, 120, 120));
        saveBtn.setPreferredSize(new Dimension(130, 32));
        cancelBtn.setPreferredSize(new Dimension(90, 32));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(SystemGUIHelper.COLOR_BG);
        btnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemGUIHelper.COLOR_BORDER));
        btnRow.add(legend);
        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        cancelBtn.addActionListener(e -> dialog.dispose());

        // Save action — validation now goes through the shared helper
        saveBtn.addActionListener(e -> {
            String[] newData = collectInputs(inputs);
            if (!validateEmployeeData(dialog, newData, inputs)) return;

            // Warn if Employee # is being changed
            String newEmpNum = newData[0];
            if (!newEmpNum.equals(oldEmpNum)) {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to change \"Employee #\"?\n"
                                + "Current: " + oldEmpNum + "  →  New: " + newEmpNum,
                        "Confirm Employee # Change",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) return; // stay on the form
            }

            boolean updated = DataProcessing.updateEmployee(
                    EntryPoint.EMPLOYEE_FILE, oldEmpNum, newData, EntryPoint.employeeMap);

            if (updated) {
                refreshTable();
                dialog.dispose();
                SystemGUIHelper.showInfo(frame,
                        "Employee #" + newEmpNum + " has been updated successfully.");
            }
        });

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);

        dialog.setLayout(new BorderLayout());
        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(formScroll,   BorderLayout.CENTER);
        dialog.add(btnRow,       BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ============================================================
    // SHARED HELPER — collect text from all 19 input fields
    // Used by both Add and Edit so the array-building code
    // only needs to exist once.
    // ============================================================
    private static String[] collectInputs(JTextField[] inputs) {
        String[] data = new String[19];
        for (int i = 0; i < 19; i++)
            data[i] = inputs[i] != null ? inputs[i].getText().trim() : "";
        return data;
    }

    // ============================================================
    // SHARED HELPER — validate employee data
    // Used by both Add and Edit so all the required-field and
    // numeric-field checks only need to be written once.
    // Returns true if the data is valid, false if not
    // (and shows the appropriate error message itself).
    // ============================================================
    private static boolean validateEmployeeData(Component parent,
                                                String[] data,
                                                JTextField[] inputs) {

        // 1. Required fields — built from FIELD_DEFS, no separate list needed
        for (Object[] def : FIELD_DEFS) {
            boolean required = (Boolean) def[2];
            int     csvIdx   = (Integer) def[1];
            if (required && data[csvIdx].isEmpty()) {
                String label = ((String) def[0]).replace(" (MM/DD/YYYY)", "");
                SystemGUIHelper.showError(parent,
                        "\"" + label + "\" is required.\nPlease fill in all required fields.");
                inputs[csvIdx].requestFocus();
                return false;
            }
        }

        // 2. Employee # must be digits only
        if (!data[0].matches("\\d+")) {
            SystemGUIHelper.showError(parent,
                    "Employee # must contain digits only.\nExample: 10045");
            inputs[0].requestFocus();
            return false;
        }

        // 3. Numeric fields must parse as valid numbers if filled in
        for (int idx : NUMERIC_INDICES) {
            String val = data[idx];
            if (!val.isEmpty()) {
                try {
                    Double.parseDouble(val);
                } catch (NumberFormatException ex) {
                    String label = (String) FIELD_DEFS[indexOfField(idx)][0];
                    SystemGUIHelper.showError(parent,
                            "\"" + label + "\" must be a valid number.\nExample: 15000.00");
                    inputs[idx].requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    // Finds the FIELD_DEFS row index for a given CSV column index
    // (small helper so validateEmployeeData can look up field labels)
    private static int indexOfField(int csvIdx) {
        for (int i = 0; i < FIELD_DEFS.length; i++)
            if ((Integer) FIELD_DEFS[i][1] == csvIdx) return i;
        return -1;
    }

    // ============================================================
    // DELETE CONFIRMATION
    // ============================================================
    private static void confirmAndDelete(int tableRow) {

        String empNum    = String.valueOf(tableModel.getValueAt(tableRow, 0));
        String lastName  = String.valueOf(tableModel.getValueAt(tableRow, 1));
        String firstName = String.valueOf(tableModel.getValueAt(tableRow, 2));

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete this employee?\n\n"
                        + "Employee #: " + empNum + "\n"
                        + "Name: " + firstName + " " + lastName + "\n\n"
                        + "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean deleted = DataProcessing.deleteEmployee(
                EntryPoint.EMPLOYEE_FILE, empNum, EntryPoint.employeeMap);

        if (deleted) {
            refreshTable();
            SystemGUIHelper.showInfo(frame,
                    "Employee #" + empNum + " has been deleted successfully.");
        }
        // If not deleted, deleteEmployee() already showed the error
    }

    // ============================================================
    // SEARCH BAR
    // ============================================================
    private static JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(SystemGUIHelper.COLOR_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemGUIHelper.COLOR_BORDER));

        JLabel empNumLbl = new JLabel("Employee Number:");
        empNumLbl.setFont(SystemGUIHelper.FONT_BOLD);

        JTextField empNumField = SystemGUIHelper.makeField(10);
        empNumField.setPreferredSize(new Dimension(110, 28));

        JLabel nameLbl = new JLabel("Employee Name:");
        nameLbl.setFont(SystemGUIHelper.FONT_BOLD);

        JTextField nameField = SystemGUIHelper.makeField(14);
        nameField.setPreferredSize(new Dimension(160, 28));

        JButton searchBtn  = SystemGUIHelper.makeButton("Search", SystemGUIHelper.COLOR_PRIMARY);
        JButton clearBtn   = SystemGUIHelper.makeButton("Clear", new Color(120, 120, 120));
        JButton showAllBtn = SystemGUIHelper.makeButton("Show All Records", SystemGUIHelper.COLOR_PRIMARY);

        searchBtn.setPreferredSize(new Dimension(90, 28));
        clearBtn.setPreferredSize(new Dimension(70, 28));
        showAllBtn.setPreferredSize(new Dimension(150, 28));

        // Search button
        searchBtn.addActionListener(e -> {
            String empNum = empNumField.getText().trim();
            String name   = nameField.getText().trim().toLowerCase();

            if (!empNum.isEmpty() && !empNum.matches("\\d+")) {
                SystemGUIHelper.showError(frame,
                        "Employee number must contain digits only.\nExample: 10001");
                empNumField.requestFocus();
                return;
            }
            if (empNum.isEmpty() && name.isEmpty()) {
                SystemGUIHelper.showWarning(frame,
                        "Please enter an employee number or name to search.");
                return;
            }
            if (!empNum.isEmpty() && !EntryPoint.employeeMap.containsKey(empNum)) {
                SystemGUIHelper.showError(frame,
                        "Employee Number \"" + empNum + "\" was not found.\n"
                                + "Please check the number and try again.");
                empNumField.requestFocus();
                return;
            }

            refreshTableFiltered(empNum, name);

            if (tableModel.getRowCount() == 0 && !name.isEmpty()) {
                SystemGUIHelper.showError(frame,
                        "No employee found with name containing \""
                                + nameField.getText().trim() + "\".\n"
                                + "Please check the name and try again.");
            }
        });

        // Show All Records button
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
            SystemGUIHelper.showInfo(frame, "Table Cleared");
        });

        // Enter key on either field triggers search
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) searchBtn.doClick();
            }
        };
        empNumField.addKeyListener(enterKey);
        nameField.addKeyListener(enterKey);

        bar.add(empNumLbl);  bar.add(empNumField);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(nameLbl);    bar.add(nameField);
        bar.add(searchBtn);  bar.add(clearBtn);
        bar.add(showAllBtn);

        return bar;
    }
}
