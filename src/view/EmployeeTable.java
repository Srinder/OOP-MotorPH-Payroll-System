package view;

import model.User;
import model.Employee;
import java.util.List;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import service.IEmployeeManagementService;
import service.EmployeeManagementService;

public class EmployeeTable extends javax.swing.JFrame {
    public static EmployeeTable instance;
    private final IEmployeeManagementService employeeService;
    private AddEmployee addemp;
    private final boolean openedFromPayslip;
    private final boolean openedFromAttendance;
    private final boolean openedFromLeave;
    private static final java.awt.Dimension ACTION_BUTTON_SIZE = new java.awt.Dimension(95, 34);
    
    public static EmployeeTable getInstance() {
        return instance;
    }
    
    public void refreshEmployeeTable() {
        loadEmployeeData();
    }

    public EmployeeTable() {
        this(false, false, false);
    }

    public EmployeeTable(boolean openedFromPayslip) {
        this(openedFromPayslip, false, false);
    }

    public static EmployeeTable forAttendanceSelection() {
        return new EmployeeTable(false, true, false);
    }

    public static EmployeeTable forLeaveSelection() {
        return new EmployeeTable(false, false, true);
    }

    private EmployeeTable(boolean openedFromPayslip, boolean openedFromAttendance, boolean openedFromLeave) {
        this(openedFromPayslip, openedFromAttendance, openedFromLeave, new EmployeeManagementService());
    }
    
    private EmployeeTable(boolean openedFromPayslip, boolean openedFromAttendance, boolean openedFromLeave, IEmployeeManagementService employeeService) {
        this.openedFromPayslip = openedFromPayslip;
        this.openedFromAttendance = openedFromAttendance;
        this.openedFromLeave = openedFromLeave;
        this.employeeService = employeeService;
        this.addemp = new AddEmployee(employeeService);
        instance = this;
        initComponents();
        WindowNavigation.installReturnToMainMenuOnClose(this);
        applyActionButtonSizes();
        
        setupTableColumns();
        loadEmployeeData();
        
        // Safety Check
        if (model.User.getLoggedInUser() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Session expired. Please log in again.");
            this.dispose();
            return;
        }

        applyRoleRestrictions();
        applyEntryModeRestrictions();
        jButtonUpdate.setVisible(false);
        jButtonUpdate.setEnabled(false);
        this.setLocationRelativeTo(null);
    }

    private void applyActionButtonSizes() {
        jButtonView.setMinimumSize(ACTION_BUTTON_SIZE);
        jButtonView.setPreferredSize(ACTION_BUTTON_SIZE);
        jButtonView.setMaximumSize(ACTION_BUTTON_SIZE);
    }
    
    private void setupTableColumns() {
        String[] columns = {"Employee Number", "Last Name", "First Name", "Phone Number", "Status", "Position", "Immediate Supervisor"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        jTableEmp.setModel(model);
    }
    
    public void loadEmployeeData() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTableEmp.getModel();
            model.setRowCount(0);

            // Use service to load employee data
            List<Employee> list = employeeService.getAllEmployees();

            System.out.println("DEBUG: Employees found: " + list.size());

            for (Employee emp : list) {
                model.addRow(new Object[]{
                    emp.getEmployeeNumber(),   // Col 0
                    emp.getLastName(),         // Col 1
                    emp.getFirstName(),        // Col 2
                    emp.getPhoneNumber(),      // Col 3
                    emp.getStatus(),           // Col 4
                    emp.getPosition(),         // Col 5
        emp.getSupervisor()        // Col 6 
    });
}
    } catch (Exception e) {
        System.out.println("CRASH DURING LOAD: " + e.getMessage());
        e.printStackTrace(); 
    }
}
    
       
    private void applyRoleRestrictions() {
        model.Employee currentUser = model.User.getLoggedInUser();
        
        if (currentUser == null){
        return;
    }
        String role = currentUser.getRole().toUpperCase().trim();

        boolean isHR = role.equals("HR");
        // Only HR can see and use Add/Update/Delete.
        jButtonAdd.setVisible(isHR);
        jButtonAdd.setEnabled(isHR);
        jButtonUpdate.setVisible(false);
        jButtonUpdate.setEnabled(false);
        jButtonDelete.setVisible(isHR);
        jButtonDelete.setEnabled(isHR);
    }

    private void applyEntryModeRestrictions() {
        if (openedFromPayslip || openedFromAttendance || openedFromLeave) {
            jButtonView.setMinimumSize(ACTION_BUTTON_SIZE);
            jButtonView.setPreferredSize(ACTION_BUTTON_SIZE);
            jButtonView.setMaximumSize(ACTION_BUTTON_SIZE);

            jButtonAdd.setVisible(false);
            jButtonDelete.setVisible(false);
            jButtonUpdate.setVisible(false);
            revalidate();
            repaint();
        }
    }
    
    private void adjustTableSettings() {
        jTableEmp.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Set widths for better visibility
        jTableEmp.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableEmp.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTableEmp.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTableEmp.getColumnModel().getColumn(6).setPreferredWidth(250);
    }




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonAdd = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jButtonView = new javax.swing.JButton();
        jButtonUpdate = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jLabelEmpInfo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableEmp = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 255));

        jPanel1.setBackground(new java.awt.Color(14, 49, 113));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 600));

        jButtonAdd.setText("Add New Employee");
        jButtonAdd.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonExit.setBackground(new java.awt.Color(153, 0, 0));
        jButtonExit.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit.setText("Exit");
        jButtonExit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jButtonView.setText("View");
        jButtonView.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewActionPerformed(evt);
            }
        });

        jButtonUpdate.setText("Update");
        jButtonUpdate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jLabelEmpInfo.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelEmpInfo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelEmpInfo.setText("Employee Information");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 8)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("CP2 H1102 SY24-25 Team Petix - C.Oreta, S.Singh, R.Sisles, J.Singh, D.Sumatra");

        jTableEmp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableEmp);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelEmpInfo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(496, 496, 496)
                                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jScrollPane1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButtonView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                    .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jButtonView, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelEmpInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addComponent(jLabel1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        dispose ();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        addemp.setVisible(true);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewActionPerformed
     int selectedRow = jTableEmp.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an employee first.");
        return;
    }

    try {
        int empNum = Integer.parseInt(jTableEmp.getValueAt(selectedRow, 0).toString());

        if (openedFromPayslip) {
            new Payslip(String.valueOf(empNum)).setVisible(true);
            return;
        }
        
        if (openedFromAttendance) {
            new Attendance(String.valueOf(empNum), true).setVisible(true);
            WindowNavigation.suppressReturnToMainMenuOnClose(this);
            this.dispose();
            return;
        }
        
        if (openedFromLeave) {
            new LeaveRequests(empNum, true).setVisible(true);
            WindowNavigation.suppressReturnToMainMenuOnClose(this);
            this.dispose();
            return;
        }

        // SAFE ROLE CHECK
        boolean isReadOnly = true; // Default to safe/read-only
        model.Employee currentUser = model.User.getLoggedInUser();

        if (currentUser != null) {
            String role = currentUser.getRole().toUpperCase();
            // Only Admin and Finance are restricted to Read-Only in this logic
            isReadOnly = role.equals("FINANCE") || role.equals("ADMIN");
        } else {
            // If testing without login, can set this to false to allow editing
            System.out.println("DEBUG: No user logged in, defaulting to Read-Only.");
        }

        new EditEmpInfo(empNum, isReadOnly, true).setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_jButtonViewActionPerformed

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
            int selectedRow = jTableEmp.getSelectedRow();

    // 1. Safety check: Did they select a row?
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an employee to update.");
        return;
    }

    try {
        // 2. Safety check: Is the ID cell empty?
        Object value = jTableEmp.getValueAt(selectedRow, 0);
        if (value == null) {
            JOptionPane.showMessageDialog(this, "Selected row has no ID data.");
            return;
        }
        
        int empNum = Integer.parseInt(value.toString());

        // 3. Safety check: Is a user actually logged in?
        boolean isReadOnly = false; 
        model.Employee currentUser = model.User.getLoggedInUser();

        if (currentUser != null) {
            String role = currentUser.getRole().toUpperCase();
            // Restrict Finance and Admin from editing if that's your rule
            isReadOnly = role.equals("FINANCE") || role.equals("ADMIN");
        } else {
            // If testing without a login, we'll assume full access (false) 
            // but print a warning in the console.
            System.out.println("Warning: No logged-in user detected. Defaulting to editable mode.");
        }

        // 4. Open the window
        new EditEmpInfo(empNum, isReadOnly, true).setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error during update: " + e.getMessage());
        e.printStackTrace();
    }
    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
    int selectedRow = jTableEmp.getSelectedRow();
        if (selectedRow != -1) {
            int empNum = Integer.parseInt(jTableEmp.getValueAt(selectedRow, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete employee " + empNum + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = employeeService.deleteEmployeeAndLogin(empNum);
                if (deleted) {
                    loadEmployeeData();
                    JOptionPane.showMessageDialog(this, "Deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee record.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EmployeeTable().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JButton jButtonView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelEmpInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableEmp;
    // End of variables declaration//GEN-END:variables
}
