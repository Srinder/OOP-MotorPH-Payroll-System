package view;

import model.AttendanceRecord;
import javax.swing.JFrame;
import java.util.List;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import service.IAttendanceService;
import service.AttendanceService;
import java.util.ArrayList;
import java.util.Vector;
import service.AuthorizationService;
import service.IAuthorizationService;

public class Attendance extends JFrame {

    private String empNo;
    private final IAttendanceService attendanceService;
    private final IAuthorizationService authzService = new AuthorizationService();

    public Attendance(String empNo) {
        this(empNo, false, new AttendanceService());
    }
    
    public Attendance(String empNo, boolean skipInitialSearchPrompt) {
        this(empNo, skipInitialSearchPrompt, new AttendanceService());
    }
    
    @SuppressWarnings("unchecked")
    private void loadAttendanceData() {
    
}
    
    public Attendance(String empNo, boolean skipInitialSearchPrompt, IAttendanceService attendanceService) {
        this.empNo = empNo;
        this.attendanceService = attendanceService;
        initComponents();
        util.WindowNavigation.installReturnToMainMenuOnClose(this);

        // Identify the user and their role
        model.Employee current = model.User.getLoggedInUser();
        boolean powerUser = isPowerUser();
       
        if (powerUser) {
            // Setup for Admin/HR/Finance
            jLabel3.setText("View Mode: Administrative Control");
            updateAttendanceButton.setVisible(true);
            jButtonSave.setVisible(true);

            if (skipInitialSearchPrompt) {
                employeeIDLabel.setText("Employee ID: " + empNo);
                employeeIDLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                for (java.awt.event.MouseListener ml : employeeIDLabel.getMouseListeners()) {
                    employeeIDLabel.removeMouseListener(ml);
                }
                loadEmployeeName();
                loadAttendanceRecords(empNo, LocalDate.now().minusMonths(1), LocalDate.now());
            } else {
                employeeIDLabel.setText("Click to Search ID");
                employeeIDLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                employeeIDLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        showSearchDialog();
                    }
                });

                java.awt.EventQueue.invokeLater(() -> {
                    showSearchDialog();
                });
            }

        } else {
            // Setup for Regular, Probationary, and IT
            updateAttendanceButton.setVisible(false);
            jButtonSave.setVisible(false);
            
            employeeIDLabel.setText("Employee ID: " + empNo);
            employeeIDLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            
            for (java.awt.event.MouseListener ml : employeeIDLabel.getMouseListeners()) {
                employeeIDLabel.removeMouseListener(ml);
            }

            loadEmployeeName();
            loadAttendanceRecords(empNo, LocalDate.now().minusMonths(1), LocalDate.now());
        }
    }

    public Attendance() {
        this("", false, new AttendanceService());
    }

    private boolean isPowerUser() {
        model.Employee current = model.User.getLoggedInUser();
        return current != null && authzService.canManageAttendanceRecords(current);
    }

    private void loadEmployeeName() {
        String employeeName = getEmployeeName(empNo);
        jLabel3.setText("Employee Name: " + employeeName);
    }

    private String getEmployeeName(String empNo) {
        return attendanceService.getEmployeeDisplayName(empNo);
    }

    public void loadAttendanceRecords(String targetEmpNo, LocalDate startDate, LocalDate endDate) {
        DefaultTableModel model = (DefaultTableModel) jTableAttendance.getModel();
        model.setRowCount(0);

        if (startDate == null || endDate == null) {
            return;
        }
        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this,
                    "End date cannot be earlier than start date.",
                    "Invalid Date Range",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Object[] row : attendanceService.buildAttendanceTableRows(targetEmpNo, startDate, endDate)) {
            model.addRow(row);
        }
    }
    private LocalDate getSelectedStartDateOrDefault() {
        return startDateLabel.getDate() != null
                ? startDateLabel.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now().minusMonths(1);
    }

    private LocalDate getSelectedEndDateOrDefault() {
        return endDateLabel.getDate() != null
                ? endDateLabel.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now();
    }

    private void applyTableEditMode(boolean editable) {
        DefaultTableModel model = (DefaultTableModel) jTableAttendance.getModel();

        @SuppressWarnings("rawtypes")
        Vector data = model.getDataVector();

        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames.add(model.getColumnName(i));
        }

        DefaultTableModel nextModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable && (column == 1 || column == 2);
            }
        };

        jTableAttendance.setModel(nextModel);
    }

    private List<Object[]> collectEditableRowsFromTable() {
        DefaultTableModel model = (DefaultTableModel) jTableAttendance.getModel();
        List<Object[]> rows = new ArrayList<>();

        for (int row = 0; row < model.getRowCount(); row++) {
            String date = model.getValueAt(row, 0).toString().trim();
            String logIn = model.getValueAt(row, 1).toString().trim();
            String logOut = model.getValueAt(row, 2).toString().trim();
            rows.add(new Object[]{date, logIn, logOut});
        }
        return rows;
    }

    private void showSearchDialog() {
        String query = JOptionPane.showInputDialog(this,
                "Enter Employee ID or Full Name (Last, First):",
                "Administrative Search",
                JOptionPane.QUESTION_MESSAGE);

        if (query == null || query.trim().isEmpty()) return;

        List<model.Employee> matches = attendanceService.searchEmployees(query);

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employee found matching: " + query);
        } else if (matches.size() == 1) {
            applySearchSelection(matches.get(0));
        } else {
            model.Employee selected = (model.Employee) JOptionPane.showInputDialog(this,
                    "Multiple matches found. Please select the correct employee:",
                    "Duplicate Results Found",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    matches.toArray(),
                    matches.get(0));

            if (selected != null) {
                applySearchSelection(selected);
            }
        }
    }

    private void applySearchSelection(model.Employee e) {
        this.empNo = String.valueOf(e.getEmployeeNumber());
        jLabel3.setText("Name: " + e.getLastName() + ", " + e.getFirstName());
        employeeIDLabel.setText("Employee ID: " + empNo);
        loadAttendanceRecords(empNo, LocalDate.now().minusMonths(1), LocalDate.now());
    }

  
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAttendance = new javax.swing.JTable();
        jLblEmpAtt = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButtonExit = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        startDateLabel = new com.toedter.calendar.JDateChooser();
        endDateLabel = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        checkAttendanceButton = new javax.swing.JButton();
        employeeIDLabel = new javax.swing.JLabel();
        updateAttendanceButton = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        jLabel6.setText("jLabel6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(14, 49, 113));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableAttendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Time IN", "Time OUT", "Late", "Overtime", "Undertime"
            }
        ));
        jScrollPane1.setViewportView(jTableAttendance);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 161, 753, 422));

        jLblEmpAtt.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLblEmpAtt.setForeground(new java.awt.Color(255, 255, 255));
        jLblEmpAtt.setText("Employee Attendance");
        jPanel1.add(jLblEmpAtt, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 20, 416, 55));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Select Date Range:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 64, 195, -1));

        jButtonExit.setBackground(new java.awt.Color(153, 0, 0));
        jButtonExit.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 590, 85, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Name : Garcia, Manuel III");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 87, 416, -1));
        jPanel1.add(startDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(501, 87, 126, -1));
        jPanel1.add(endDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(656, 87, 126, -1));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("From");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 93, -1, -1));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("To");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(633, 93, -1, -1));

        checkAttendanceButton.setBackground(new java.awt.Color(0, 102, 102));
        checkAttendanceButton.setForeground(new java.awt.Color(255, 255, 255));
        checkAttendanceButton.setText("Check Attendance");
        checkAttendanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAttendanceButtonActionPerformed(evt);
            }
        });
        jPanel1.add(checkAttendanceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 120, 140, -1));

        employeeIDLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        employeeIDLabel.setForeground(new java.awt.Color(255, 255, 255));
        employeeIDLabel.setText("Employee ID: ");
        jPanel1.add(employeeIDLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 115, 324, -1));

        updateAttendanceButton.setBackground(new java.awt.Color(0, 102, 102));
        updateAttendanceButton.setForeground(new java.awt.Color(255, 255, 255));
        updateAttendanceButton.setText("Update Attendance");
        updateAttendanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateAttendanceButtonActionPerformed(evt);
            }
        });
        jPanel1.add(updateAttendanceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(491, 119, 140, -1));

        jButtonSave.setBackground(new java.awt.Color(153, 0, 0));
        jButtonSave.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 590, 85, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 8)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("CP2 H1102 SY24-25 Team Petix - C.Oreta, S.Singh, R.Sisles, J.Singh, D.Sumatra");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 620, 330, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void checkAttendanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAttendanceButtonActionPerformed
    if (startDateLabel.getDate() != null && endDateLabel.getDate() != null) {
        LocalDate startDate = getSelectedStartDateOrDefault();
        LocalDate endDate = getSelectedEndDateOrDefault();
        loadAttendanceRecords(empNo, startDate, endDate);
        applyTableEditMode(false);
    } else {
        JOptionPane.showMessageDialog(this,
                "Please select both start and end dates.",
                "Date Range Required",
                JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_checkAttendanceButtonActionPerformed

    private void updateAttendanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAttendanceButtonActionPerformed
    applyTableEditMode(true);

    JOptionPane.showMessageDialog(this,
        "Table is now editable. You can update Time In and Time Out.",
        "Update Mode Enabled",
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_updateAttendanceButtonActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
    if (jTableAttendance.isEditing() && jTableAttendance.getCellEditor() != null) {
        jTableAttendance.getCellEditor().stopCellEditing();
    }

    String activeEmpNo = this.empNo;
    List<AttendanceRecord> updatedRecords = attendanceService.mapRowsToAttendanceRecords(activeEmpNo, collectEditableRowsFromTable());

    try {
        boolean success = attendanceService.updateAttendanceRecords(activeEmpNo, updatedRecords);

        if (success) {
            JOptionPane.showMessageDialog(this, "Records saved and Overtime recalculated.");
            loadAttendanceRecords(activeEmpNo, getSelectedStartDateOrDefault(), getSelectedEndDateOrDefault());
            applyTableEditMode(false);
        } else {
            JOptionPane.showMessageDialog(this, "No matching records were updated.", "Save Warning", JOptionPane.WARNING_MESSAGE);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Failed to save attendance records: " + e.getMessage(),
                "Save Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Attendance().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton checkAttendanceButton;
    private javax.swing.JLabel employeeIDLabel;
    private com.toedter.calendar.JDateChooser endDateLabel;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLblEmpAtt;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableAttendance;
    private com.toedter.calendar.JDateChooser startDateLabel;
    private javax.swing.JButton updateAttendanceButton;
    // End of variables declaration//GEN-END:variables



}



