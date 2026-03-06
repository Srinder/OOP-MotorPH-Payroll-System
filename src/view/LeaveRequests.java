
package view;


import service.ILeaveManagement;
import service.LeaveManagement;
import repository.LeaveRepository;
import model.LeaveRequest;
/**
 *
 * @author singh
 */
public class LeaveRequests extends javax.swing.JFrame {
    
    private final service.ILeaveManagement leaveService = new service.LeaveManagement(); 
    private final repository.LeaveRepository repo = new repository.LeaveRepository();
    private int currentEmployeeID;
    private model.Employee currentUser;
    

    
    public LeaveRequests(int empId) {
        this.currentEmployeeID = empId;
        initComponents(); 
        
        

       
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter headerFormatter = 
        java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        
 
        lblLeaveRequestStatus.setText("Leave Request Status as of " + today.format(headerFormatter));

       
        loadTableData();      
        updateSummaryCards(); 
    }
    
    private void tblLeaveHistoryMouseClicked(java.awt.event.MouseEvent evt) {
    if (this.currentUser.getRole().equalsIgnoreCase("Admin")) {
        int selectedRow = tblLeaveHistory.getSelectedRow();
        if (selectedRow != -1) {
            // 1. Extract data from the selected row
            String empId = tblLeaveHistory.getValueAt(selectedRow, 0).toString();
            String type = tblLeaveHistory.getValueAt(selectedRow, 1).toString();
            String start = tblLeaveHistory.getValueAt(selectedRow, 2).toString();
            String end = tblLeaveHistory.getValueAt(selectedRow, 3).toString();
            String status = tblLeaveHistory.getValueAt(selectedRow, 5).toString();
            String reason = tblLeaveHistory.getValueAt(selectedRow, 6).toString();

            // 2. Open the Approval Dialog
            showApprovalDialog(empId, type, start, end, status, reason);
        }
    }
    }
    
    private void showApprovalDialog(String id, String type, String s, String e, String status, String reason) {
    String message = "Employee ID: " + id + "\nType: " + type + 
                     "\nDates: " + s + " to " + e + 
                     "\n\nReason: " + reason;

    Object[] options = {"Approve", "Decline", "Cancel"};
    int choice = javax.swing.JOptionPane.showOptionDialog(this, message, 
            "Review Leave Request", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

    if (choice == 0) updateStatus(id, s, "Approved");
    else if (choice == 1) updateStatus(id, s, "Rejected");
}

private void updateStatus(String empId, String startDate, String newStatus) {
    // This updates the CSV and refreshes  credit cards
    if (leaveService.updateLeaveStatus(Integer.parseInt(empId), startDate, newStatus)) {
        loadTableData();
        updateSummaryCards(); // Refresh the 20/10/10 balances
    }
}

    private void loadTableData() {
        // 1. Get the UI Table Model
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblLeaveHistory.getModel();
        model.setRowCount(0); // Clear old rows

        // 2. Request data from the Service
        java.util.List<model.LeaveRequest> history = leaveService.getEmployeeLeaveHistory(this.currentEmployeeID);

        // 3. Fill the table using the Model's getters
        for (model.LeaveRequest req : history) {
            // Calculate duration for the 'Days' column using service logic
            double days = leaveService.calculateLeaveDays(req.getStartDate(), req.getEndDate());

            model.addRow(new Object[]{
                req.getEmployeeId(), 
                req.getLeaveType(),   
                req.getStartDate(),   
                req.getEndDate(),     
                (int)days,            
                req.getStatus(),      
                req.getReason()       
            });
        }
    }

    private void updateSummaryCards() {
        // 1. Get individual balances from the Service
        double vacation = leaveService.getRemainingLeaveCredits(this.currentEmployeeID, "Vacation");
        double sick = leaveService.getRemainingLeaveCredits(this.currentEmployeeID, "Sick");
        double emergency = leaveService.getRemainingLeaveCredits(this.currentEmployeeID, "Emergency");
        
        // 2. Calculate the Total Credits sum
        double total = vacation + sick + emergency;

        // 3. Update the Labels in your design
        lblVacationCreditsValue.setText(String.valueOf((int)vacation));
        lblSickLeaveCreditsValue.setText(String.valueOf((int)sick));
        lblEmergencyLeaveCreditsValue.setText(String.valueOf((int)emergency));
        lblTotalCreditsValue.setText(String.valueOf((int)total));
    }

    private void calculateDays() {
        if (jcStartDateValue.getDate() != null && jcEndDateValue.getDate() != null) {
            // Use MM/dd/yyyy to match your Service's DateTimeFormatter
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
            String s = sdf.format(jcStartDateValue.getDate());
            String e = sdf.format(jcEndDateValue.getDate());
            
            double days = leaveService.calculateLeaveDays(s, e);
            lblTotalDaysValue.setText(String.valueOf((int)days));
        }
    }

    private void clearForm() {
        jcLeaveTypeValue.setSelectedIndex(0);
        jcStartDateValue.setDate(null);
        jcEndDateValue.setDate(null);
        txtReasonValue.setText("");
        lblTotalDaysValue.setText("0");
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
        lblLeaveType = new javax.swing.JLabel();
        jcLeaveTypeValue = new javax.swing.JComboBox<>();
        lblStartDate = new javax.swing.JLabel();
        jcStartDateValue = new com.toedter.calendar.JDateChooser();
        lblEndDate = new javax.swing.JLabel();
        jcEndDateValue = new com.toedter.calendar.JDateChooser();
        lblTotalDays = new javax.swing.JLabel();
        lblTotalDaysValue = new javax.swing.JLabel();
        lblReason = new javax.swing.JLabel();
        txtReasonValue = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblLeaveCreditBalance = new javax.swing.JLabel();
        pnlVacationCredits = new javax.swing.JPanel();
        lblVacationCredits = new javax.swing.JLabel();
        lblVacationCreditsValue = new javax.swing.JLabel();
        pnlSickLeaveCredits = new javax.swing.JPanel();
        lblSickLeaveCredits = new javax.swing.JLabel();
        lblSickLeaveCreditsValue = new javax.swing.JLabel();
        pnlEmergencyLeave = new javax.swing.JPanel();
        lblEmergencyLeaveCredits = new javax.swing.JLabel();
        lblEmergencyLeaveCreditsValue = new javax.swing.JLabel();
        pnlTotalCredits = new javax.swing.JPanel();
        lblTotalCredits = new javax.swing.JLabel();
        lblTotalCreditsValue = new javax.swing.JLabel();
        pnlLeaveHistory = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLeaveHistory = new javax.swing.JTable();
        btnExit = new javax.swing.JButton();
        lblMyLeaveHistory = new javax.swing.JLabel();
        lblSubmitNewRequest = new javax.swing.JLabel();
        lblLeaveRequestStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLeaveType.setText("Leave Type:");
        jPanel1.add(lblLeaveType, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 66, 110, -1));

        jcLeaveTypeValue.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Select Leave>", "Vacation", "Sick", "Emergency" }));
        jPanel1.add(jcLeaveTypeValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 63, 131, -1));

        lblStartDate.setText("Start Date:");
        jPanel1.add(lblStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 109, 110, -1));

        jcStartDateValue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jcStartDateValuePropertyChange(evt);
            }
        });
        jPanel1.add(jcStartDateValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 103, 131, -1));

        lblEndDate.setText("End Date:");
        jPanel1.add(lblEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 149, 110, -1));

        jcEndDateValue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jcEndDateValuePropertyChange(evt);
            }
        });
        jPanel1.add(jcEndDateValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 143, 131, -1));

        lblTotalDays.setText("Total Days:");
        jPanel1.add(lblTotalDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 184, 110, -1));

        lblTotalDaysValue.setBackground(new java.awt.Color(255, 255, 255));
        lblTotalDaysValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel1.add(lblTotalDaysValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 183, 131, 20));

        lblReason.setText("Reason:");
        jPanel1.add(lblReason, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, 110, -1));

        txtReasonValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        txtReasonValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReasonValueActionPerformed(evt);
            }
        });
        jPanel1.add(txtReasonValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 280, 150));

        btnSubmit.setText("Submit Request");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        jPanel1.add(btnSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 430, -1, -1));

        btnCancel.setText("Cancel Request");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 460, 113, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblLeaveCreditBalance.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblLeaveCreditBalance.setText("LEAVE CREDIT BALANCE");

        pnlVacationCredits.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlVacationCredits.setPreferredSize(new java.awt.Dimension(135, 90));

        lblVacationCredits.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVacationCredits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVacationCredits.setText("Vacation: ");
        lblVacationCredits.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblVacationCreditsValue.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblVacationCreditsValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVacationCreditsValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnlVacationCreditsLayout = new javax.swing.GroupLayout(pnlVacationCredits);
        pnlVacationCredits.setLayout(pnlVacationCreditsLayout);
        pnlVacationCreditsLayout.setHorizontalGroup(
            pnlVacationCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVacationCreditsLayout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(pnlVacationCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblVacationCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVacationCreditsValue, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );
        pnlVacationCreditsLayout.setVerticalGroup(
            pnlVacationCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVacationCreditsLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblVacationCredits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblVacationCreditsValue, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        pnlSickLeaveCredits.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlSickLeaveCredits.setPreferredSize(new java.awt.Dimension(135, 90));

        lblSickLeaveCredits.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSickLeaveCredits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSickLeaveCredits.setText("Sick: ");
        lblSickLeaveCredits.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblSickLeaveCreditsValue.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblSickLeaveCreditsValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSickLeaveCreditsValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnlSickLeaveCreditsLayout = new javax.swing.GroupLayout(pnlSickLeaveCredits);
        pnlSickLeaveCredits.setLayout(pnlSickLeaveCreditsLayout);
        pnlSickLeaveCreditsLayout.setHorizontalGroup(
            pnlSickLeaveCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSickLeaveCreditsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlSickLeaveCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSickLeaveCreditsValue, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSickLeaveCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );
        pnlSickLeaveCreditsLayout.setVerticalGroup(
            pnlSickLeaveCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSickLeaveCreditsLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblSickLeaveCredits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSickLeaveCreditsValue, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        pnlEmergencyLeave.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlEmergencyLeave.setPreferredSize(new java.awt.Dimension(135, 90));

        lblEmergencyLeaveCredits.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEmergencyLeaveCredits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmergencyLeaveCredits.setText("Emergency:");
        lblEmergencyLeaveCredits.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblEmergencyLeaveCreditsValue.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblEmergencyLeaveCreditsValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmergencyLeaveCreditsValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnlEmergencyLeaveLayout = new javax.swing.GroupLayout(pnlEmergencyLeave);
        pnlEmergencyLeave.setLayout(pnlEmergencyLeaveLayout);
        pnlEmergencyLeaveLayout.setHorizontalGroup(
            pnlEmergencyLeaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEmergencyLeaveLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlEmergencyLeaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmergencyLeaveCreditsValue, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmergencyLeaveCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );
        pnlEmergencyLeaveLayout.setVerticalGroup(
            pnlEmergencyLeaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmergencyLeaveLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblEmergencyLeaveCredits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEmergencyLeaveCreditsValue, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        pnlTotalCredits.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlTotalCredits.setPreferredSize(new java.awt.Dimension(135, 90));

        lblTotalCredits.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalCredits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalCredits.setText("Total Credits:");
        lblTotalCredits.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblTotalCreditsValue.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTotalCreditsValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalCreditsValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnlTotalCreditsLayout = new javax.swing.GroupLayout(pnlTotalCredits);
        pnlTotalCredits.setLayout(pnlTotalCreditsLayout);
        pnlTotalCreditsLayout.setHorizontalGroup(
            pnlTotalCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTotalCreditsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlTotalCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalCreditsValue, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );
        pnlTotalCreditsLayout.setVerticalGroup(
            pnlTotalCreditsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalCreditsLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblTotalCredits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalCreditsValue, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        pnlLeaveHistory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        tblLeaveHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Employee ID", "Leave Type", "Start Date", "End Date", "Days", "Status", "Reason"
            }
        ));
        tblLeaveHistory.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tblLeaveHistory);
        tblLeaveHistory.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout pnlLeaveHistoryLayout = new javax.swing.GroupLayout(pnlLeaveHistory);
        pnlLeaveHistory.setLayout(pnlLeaveHistoryLayout);
        pnlLeaveHistoryLayout.setHorizontalGroup(
            pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLeaveHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        pnlLeaveHistoryLayout.setVerticalGroup(
            pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLeaveHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        lblMyLeaveHistory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMyLeaveHistory.setText("My Leave History");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnExit))
                    .addComponent(lblLeaveCreditBalance, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(pnlVacationCredits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSickLeaveCredits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlEmergencyLeave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlTotalCredits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlLeaveHistory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMyLeaveHistory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblLeaveCreditBalance)
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSickLeaveCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlVacationCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEmergencyLeave, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlTotalCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(lblMyLeaveHistory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLeaveHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExit)
                .addContainerGap())
        );

        lblSubmitNewRequest.setText("Submit New Request");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubmitNewRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLeaveRequestStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLeaveRequestStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSubmitNewRequest, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtReasonValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReasonValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReasonValueActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
    try {
        // 1. Capture inputs from UI components
        String type = jcLeaveTypeValue.getSelectedItem().toString();
        java.util.Date start = jcStartDateValue.getDate();
        java.util.Date end = jcEndDateValue.getDate();
        String reason = txtReasonValue.getText();

        // 2. Simple Validation
        if (start == null || end == null || reason.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // 3. Format dates for the CSV
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
        String startDateStr = sdf.format(start);
        String endDateStr = sdf.format(end);

        // 4. Use Service to apply leave (returns true if within 15-day limit)
        boolean success = leaveService.applyLeave(
            this.currentEmployeeID, 
            type, 
            startDateStr, 
            endDateStr, 
            reason
        );

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Leave Request Submitted Successfully!");
            // 5. Refresh the UI
            loadTableData();
            updateSummaryCards();
            clearForm();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Insufficient leave credits!");
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        
    }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void jcStartDateValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jcStartDateValuePropertyChange
        // TODO add your handling code here:
        calculateDays();
    }//GEN-LAST:event_jcStartDateValuePropertyChange

    private void jcEndDateValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jcEndDateValuePropertyChange
        // TODO add your handling code here:
        calculateDays();
    }//GEN-LAST:event_jcEndDateValuePropertyChange

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        // 1. Clear all input fields
        jcLeaveTypeValue.setSelectedIndex(0);
        jcStartDateValue.setDate(null);
        jcEndDateValue.setDate(null);
        txtReasonValue.setText("");
    
        // 2. Reset the calculated days label
        lblTotalDaysValue.setText("0");
    
        // 3. Show a brief confirmation (Optional)
        System.out.println("Form cleared by user.");
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed

    this.dispose(); 
    }//GEN-LAST:event_btnExitActionPerformed

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
            java.util.logging.Logger.getLogger(LeaveRequests.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LeaveRequests.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LeaveRequests.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LeaveRequests.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LeaveRequests(10001).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jcEndDateValue;
    private javax.swing.JComboBox<String> jcLeaveTypeValue;
    private com.toedter.calendar.JDateChooser jcStartDateValue;
    private javax.swing.JLabel lblEmergencyLeaveCredits;
    private javax.swing.JLabel lblEmergencyLeaveCreditsValue;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblLeaveCreditBalance;
    private javax.swing.JLabel lblLeaveRequestStatus;
    private javax.swing.JLabel lblLeaveType;
    private javax.swing.JLabel lblMyLeaveHistory;
    private javax.swing.JLabel lblReason;
    private javax.swing.JLabel lblSickLeaveCredits;
    private javax.swing.JLabel lblSickLeaveCreditsValue;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JLabel lblSubmitNewRequest;
    private javax.swing.JLabel lblTotalCredits;
    private javax.swing.JLabel lblTotalCreditsValue;
    private javax.swing.JLabel lblTotalDays;
    private javax.swing.JLabel lblTotalDaysValue;
    private javax.swing.JLabel lblVacationCredits;
    private javax.swing.JLabel lblVacationCreditsValue;
    private javax.swing.JPanel pnlEmergencyLeave;
    private javax.swing.JPanel pnlLeaveHistory;
    private javax.swing.JPanel pnlSickLeaveCredits;
    private javax.swing.JPanel pnlTotalCredits;
    private javax.swing.JPanel pnlVacationCredits;
    private javax.swing.JTable tblLeaveHistory;
    private javax.swing.JTextField txtReasonValue;
    // End of variables declaration//GEN-END:variables
}
