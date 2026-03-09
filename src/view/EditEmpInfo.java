package view;

import model.Employee;
import javax.swing.JOptionPane;
import java.util.Optional;
import javax.swing.JTextField;
import service.IEmployeeManagementService;
import service.EmployeeManagementService;

public class EditEmpInfo extends javax.swing.JFrame {

    private int empNumToEdit;
    private Employee employeeData;
    private boolean readOnly;
    private JTextField FirstName;
    private final boolean openedFromEmployeeTable;
    private final IEmployeeManagementService employeeService;

   public EditEmpInfo(int employeeNumber, boolean isReadOnly) {
    this(employeeNumber, isReadOnly, false);
	}

   public EditEmpInfo(int employeeNumber, boolean isReadOnly, boolean openedFromEmployeeTable) {
    this.employeeService = new EmployeeManagementService();
    this.openedFromEmployeeTable = openedFromEmployeeTable;
    initComponents();
    configureHrInputMasks();
    util.WindowNavigation.installReturnToMainMenuOnClose(this);
    configureCloseTarget();
    loadEmployeeData(employeeNumber); // Your method to fill text fields

    boolean isHR = isHrUser();
    boolean shouldBeReadOnly = isReadOnly || !isHR;

    if (shouldBeReadOnly) {
        disableEditing();
    } else {
        // HR can edit using the Edit button workflow.
        Save.setVisible(true);
        Edit.setVisible(true);
        setFieldsEditable(false);
    }
		}

private void configureCloseTarget() {
    if (!openedFromEmployeeTable) {
        return;
    }
    EmployeeTable table = EmployeeTable.getInstance();
    if (table == null || !table.isDisplayable()) {
        return;
    }

    util.WindowNavigation.suppressReturnToMainMenuOnClose(this);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            if (table.isDisplayable()) {
                table.setVisible(true);
                table.toFront();
                table.requestFocus();
            }
        }
    });
}

private boolean isHrUser() {
    model.Employee current = model.User.getLoggedInUser();
    return current != null && "HR".equalsIgnoreCase(current.getRole());
}

private void configureHrInputMasks() {
    if (!isHrUser()) {
        return;
    }
    employeeService.applyDigitGroupMask(PhoneNum, new int[]{3, 3, 3}, "000-000-000");
    employeeService.applyDigitGroupMask(SSS, new int[]{2, 7, 1}, "00-0000000-0");
    employeeService.applyDigitGroupMask(PHILHEALTH, new int[]{3, 3, 3, 3}, "000-000-000-000");
    employeeService.applyDigitGroupMask(TIN, new int[]{3, 3, 3, 3}, "000-000-000-000");
    employeeService.applyDigitGroupMask(PAGIBIG, new int[]{3, 3, 3, 2}, "000-000-000-00");
}


private void loadEmployeeData(int empNum) {
    Optional<Employee> result = employeeService.getEmployeeById(empNum);

    if (result.isPresent()) {
        this.employeeData = result.get();
        
        // Fill the fields with data
        Name.setText(employeeData.getLastName() + ", " + employeeData.getFirstName());
        Position.setText(employeeData.getPosition());
        Status.setText(employeeData.getStatus());
        PhoneNum.setText(employeeData.getPhoneNumber());
        Birthday.setText(employeeData.getBirthday());
        Address.setText(employeeData.getAddress());
        PositionInfo.setText(employeeData.getPosition());
        ImmSup.setText(employeeData.getSupervisor());
        
        // Fill financial fields
        SSS.setText(employeeData.getSssNumber());
        PAGIBIG.setText(employeeData.getPagIbigNumber());
        PHILHEALTH.setText(employeeData.getPhilHealthNumber());
        TIN.setText(employeeData.getTinNumber());
        Salary.setText(String.valueOf(employeeData.getBasicSalary()));
        Hourly.setText(String.valueOf(employeeData.getHourlyRate()));
        PhoneAll.setText(String.valueOf(employeeData.getPhoneAllowance()));
        ClothAll.setText(String.valueOf(employeeData.getClothingAllowance()));
        Rice.setText(String.valueOf(employeeData.getRiceSubsidy()));
        
        // Set a hidden field or variable for First Name if you need it for the Save action
        txtLname.setText(employeeData.getLastName());
    } else {
        JOptionPane.showMessageDialog(this, "Employee not found!");
        this.dispose();
    }
}

private boolean saveEmployeeChanges() {
    return employeeService.updateEmployeeFromForm(
            employeeData,
            txtLname.getText().trim(),
            PositionInfo.getText().trim(),
            PhoneNum.getText().trim(),
            Status.getText().trim(),
            ImmSup.getText().trim(),
            Address.getText().trim(),
            Birthday.getText().trim(),
            SSS.getText().trim(),
            PHILHEALTH.getText().trim(),
            TIN.getText().trim(),
            PAGIBIG.getText().trim(),
            Salary.getText().trim(),
            Hourly.getText().trim(),
            PhoneAll.getText().trim(),
            ClothAll.getText().trim(),
            Rice.getText().trim()
    );
}

private void refreshEmployeeTableIfPresent() {
    if (EmployeeTable.getInstance() != null) {
        EmployeeTable.getInstance().refreshEmployeeTable();
    }
}

private void finalizeSuccessfulSave() {
    JOptionPane.showMessageDialog(this, "Employee record updated successfully!");
    refreshEmployeeTableIfPresent();
    setFieldsEditable(false);
    dispose();
}





    private void disableEditing() {
        setFieldsEditable(false);
        Save.setVisible(false);
        Edit.setVisible(false);
    }

    private void setFieldsEditable(boolean editable) {
        Name.setEditable(false);
        Position.setEditable(false);
        PositionInfo.setEditable(editable);
        PhoneNum.setEditable(editable);
        Status.setEditable(editable);
        ImmSup.setEditable(editable);
        Address.setEditable(editable);
        Birthday.setEditable(editable);

        SSS.setEditable(editable);
        PAGIBIG.setEditable(editable);
        PHILHEALTH.setEditable(editable);
        TIN.setEditable(editable);
        Salary.setEditable(editable);
        Rice.setEditable(editable);
        PhoneAll.setEditable(editable);
        ClothAll.setEditable(editable);
        Hourly.setEditable(editable);

        PositionInfo.setFocusable(editable);
        PhoneNum.setFocusable(editable);
        Status.setFocusable(editable);
        ImmSup.setFocusable(editable);
        Address.setFocusable(editable);
        Birthday.setFocusable(editable);
        SSS.setFocusable(editable);
        PAGIBIG.setFocusable(editable);
        PHILHEALTH.setFocusable(editable);
        TIN.setFocusable(editable);
        Salary.setFocusable(editable);
        Rice.setFocusable(editable);
        PhoneAll.setFocusable(editable);
        ClothAll.setFocusable(editable);
        Hourly.setFocusable(editable);

        Save.setEnabled(editable);
        Edit.setEnabled(!editable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerLogo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        Name = new javax.swing.JTextField();
        Position = new javax.swing.JTextField();
        Status = new javax.swing.JTextField();
        textLogo = new javax.swing.JLabel();
        txtLname = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        SSSname = new javax.swing.JLabel();
        PAGIBIGname = new javax.swing.JLabel();
        PHILHEALTHname = new javax.swing.JLabel();
        TINname = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        Save = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        Edit = new javax.swing.JButton();
        PAGIBIG = new javax.swing.JTextField();
        PHILHEALTH = new javax.swing.JTextField();
        TIN = new javax.swing.JTextField();
        SSS = new javax.swing.JFormattedTextField();
        Salary = new javax.swing.JFormattedTextField();
        Hourly = new javax.swing.JFormattedTextField();
        PhoneAll = new javax.swing.JFormattedTextField();
        ClothAll = new javax.swing.JFormattedTextField();
        Rice = new javax.swing.JFormattedTextField();
        ImmSup = new javax.swing.JTextField();
        PhoneNum = new javax.swing.JTextField();
        Birthday = new javax.swing.JTextField();
        Address = new javax.swing.JTextField();
        PositionInfo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(14, 49, 113));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 176, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
        );

        Name.setEditable(false);
        Name.setBackground(new java.awt.Color(204, 204, 204));
        Name.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        Name.setForeground(new java.awt.Color(51, 51, 51));
        Name.setText("Garcia, Manuel III");

        Position.setEditable(false);
        Position.setBackground(new java.awt.Color(204, 204, 204));
        Position.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Position.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Position.setText("Chief Excecutive Officer");

        Status.setEditable(false);
        Status.setBackground(new java.awt.Color(204, 204, 204));
        Status.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Status.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Status.setText("Regular");

        textLogo.setFont(new java.awt.Font("Kinetika Bold", 0, 55)); // NOI18N
        textLogo.setForeground(new java.awt.Color(255, 255, 255));
        textLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textLogo.setText("MotorPH");

        txtLname.setBackground(new java.awt.Color(14, 49, 113));
        txtLname.setForeground(new java.awt.Color(14, 49, 113));
        txtLname.setText("jTextField1");
        txtLname.setCaretColor(new java.awt.Color(14, 49, 113));
        txtLname.setDisabledTextColor(new java.awt.Color(14, 49, 113));
        txtLname.setEnabled(false);
        txtLname.setFocusable(false);
        txtLname.setSelectedTextColor(new java.awt.Color(14, 49, 113));
        txtLname.setSelectionColor(new java.awt.Color(14, 49, 113));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 95, Short.MAX_VALUE)
                .addComponent(Status, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(Position, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(txtLname, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textLogo)
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Name, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Position, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(textLogo)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtLname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setToolTipText("");
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SSSname.setText("SSS:");
        jPanel3.add(SSSname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 84, 123, -1));

        PAGIBIGname.setText("PAGIBIG:");
        jPanel3.add(PAGIBIGname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 114, 123, -1));

        PHILHEALTHname.setText("PHILHEALTH:");
        jPanel3.add(PHILHEALTHname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 144, 123, -1));

        TINname.setText("TIN:");
        jPanel3.add(TINname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 174, 99, -1));
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 171, 123, -1));

        jLabel11.setText("Basic Salary:");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 81, 123, 24));

        jLabel14.setText("Phone Number:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 350, 146, -1));

        jLabel15.setText("Birthday:");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 380, 145, -1));

        jLabel16.setText("Address:");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 410, 145, -1));

        jLabel17.setText("Immediate Supervisor:");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 320, -1, -1));

        jLabel22.setText("Position:");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 290, 146, -1));

        jLabel18.setText("Rice Subsidy:");
        jPanel3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 204, 144, -1));

        jLabel19.setText("Phone Allowance:");
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 144, 123, -1));

        jLabel20.setText("Hourly Rate:");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 114, 123, -1));

        jLabel21.setText("Clothing Allowance:");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 174, 144, -1));

        Save.setBackground(new java.awt.Color(14, 49, 113));
        Save.setForeground(new java.awt.Color(255, 255, 255));
        Save.setText("Save");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        jPanel3.add(Save, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, 120, -1));

        jButton2.setBackground(new java.awt.Color(153, 0, 51));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 510, 85, -1));

        Edit.setBackground(new java.awt.Color(14, 49, 113));
        Edit.setForeground(new java.awt.Color(255, 255, 255));
        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });
        jPanel3.add(Edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, 120, -1));

        PAGIBIG.setEditable(false);
        PAGIBIG.setText("691295330870");
        jPanel3.add(PAGIBIG, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 111, 123, -1));

        PHILHEALTH.setEditable(false);
        PHILHEALTH.setText("820126853951");
        jPanel3.add(PHILHEALTH, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 141, 123, -1));

        TIN.setEditable(false);
        TIN.setText("442-605-657-000");
        jPanel3.add(TIN, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 171, 123, -1));

        SSS.setEditable(false);
        SSS.setText("44-4506057-3");
        jPanel3.add(SSS, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 81, 123, -1));

        Salary.setEditable(false);
        Salary.setText("₱90,000.00");
        jPanel3.add(Salary, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 101, -1));

        Hourly.setEditable(false);
        Hourly.setText("₱535.71");
        jPanel3.add(Hourly, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 111, 101, -1));

        PhoneAll.setEditable(false);
        PhoneAll.setText("₱2,000.00");
        jPanel3.add(PhoneAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 141, 101, -1));

        ClothAll.setEditable(false);
        ClothAll.setText("₱1,000.00");
        jPanel3.add(ClothAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 171, 101, -1));

        Rice.setEditable(false);
        Rice.setText("₱1,500.00");
        jPanel3.add(Rice, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 201, 101, -1));

        ImmSup.setEditable(false);
        ImmSup.setText("N/A");
        jPanel3.add(ImmSup, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 317, 123, -1));

        PhoneNum.setEditable(false);
        PhoneNum.setText("966-860-270");
        jPanel3.add(PhoneNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 347, 123, -1));

        Birthday.setEditable(false);
        Birthday.setText("10/11/1983");
        jPanel3.add(Birthday, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 377, 123, -1));

        Address.setEditable(false);
        Address.setText("Valero Carpark Building Valero Street 1227, Makati City");
        jPanel3.add(Address, new org.netbeans.lib.awtextra.AbsoluteConstraints(174, 407, 394, -1));

        PositionInfo.setEditable(false);
        PositionInfo.setText("Position");
        jPanel3.add(PositionInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 287, 230, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 8)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("CP2 H1102 SY24-25 Team Petix - C.Oreta, S.Singh, R.Sisles, J.Singh, D.Sumatra");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 570, 340, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        setFieldsEditable(true); //Unlock fields for editinG
 
    }//GEN-LAST:event_EditActionPerformed

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
    if (employeeData == null) {
        JOptionPane.showMessageDialog(this, "Error: No employee data loaded!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        boolean updated = saveEmployeeChanges();
        if (!updated) {
            JOptionPane.showMessageDialog(this, "Failed to save employee changes.", "Save Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        finalizeSuccessfulSave();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving employee changes: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
    }
}//GEN-LAST:event_SaveActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose ();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String selectedEmpNum = null;
          
                new EditEmpInfo(Integer.parseInt(selectedEmpNum), true).setVisible(true);
}
        });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Address;
    private javax.swing.JTextField Birthday;
    private javax.swing.JFormattedTextField ClothAll;
    private javax.swing.JButton Edit;
    private javax.swing.JFormattedTextField Hourly;
    private javax.swing.JTextField ImmSup;
    private javax.swing.JTextField Name;
    private javax.swing.JTextField PAGIBIG;
    private javax.swing.JLabel PAGIBIGname;
    private javax.swing.JTextField PHILHEALTH;
    private javax.swing.JLabel PHILHEALTHname;
    private javax.swing.JFormattedTextField PhoneAll;
    private javax.swing.JTextField PhoneNum;
    private javax.swing.JTextField PositionInfo;
    private javax.swing.JTextField Position;
    private javax.swing.JFormattedTextField Rice;
    private javax.swing.JFormattedTextField SSS;
    private javax.swing.JLabel SSSname;
    private javax.swing.JFormattedTextField Salary;
    private javax.swing.JButton Save;
    private javax.swing.JTextField Status;
    private javax.swing.JTextField TIN;
    private javax.swing.JLabel TINname;
    private javax.swing.JLabel headerLogo;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel textLogo;
    private javax.swing.JTextField txtLname;
    // End of variables declaration//GEN-END:variables
}





