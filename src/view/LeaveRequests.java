
package view;


import service.ILeaveManagement;
import service.LeaveManagement;
import model.LeaveRequest;
/**
 *
 * @author singh
 */
public class LeaveRequests extends javax.swing.JFrame {
    
    private final service.ILeaveManagement leaveService = new service.LeaveManagement(); 
    private boolean statusOnlyMode = false;
    private boolean tableSelectionConfigured = false;
    private int currentEmployeeID;
    

    
    public LeaveRequests(int empId) {
        this(empId, false);
    }

    public LeaveRequests(int empId, boolean statusOnlyMode) {
        this.currentEmployeeID = empId;
        this.statusOnlyMode = statusOnlyMode;
        initComponents(); 
        util.WindowNavigation.installReturnToMainMenuOnClose(this);
        
        

       
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter headerFormatter = 
        java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        
 
        lblLeaveRequestStatus.setText("Leave Request Status as of " + today.format(headerFormatter));

       
        loadTableData();      
        updateSummaryCards(); 
        updateCancelButtonState();
        applyEntryModeRestrictions();
    }

    private void applyEntryModeRestrictions() {
        if (statusOnlyMode) {
            // View-only mode when opened from EmployeeTable -> Leave.
            lblSubmitNewRequest.setVisible(false);
            jPanel1.setVisible(false);
            btnCancel.setVisible(false);
            btnSubmit.setVisible(false);
            btnApprove.setVisible(true);
            btnDeny.setVisible(true);

            // Collapse hidden left panel from GroupLayout sizing.
            java.awt.LayoutManager lm = getContentPane().getLayout();
            if (lm instanceof javax.swing.GroupLayout) {
                javax.swing.GroupLayout gl = (javax.swing.GroupLayout) lm;
                gl.setHonorsVisibility(jPanel1, Boolean.TRUE);
                gl.setHonorsVisibility(lblSubmitNewRequest, Boolean.TRUE);
            }
            pack();
            setLocationRelativeTo(null);
        } else {
            btnApprove.setVisible(false);
            btnDeny.setVisible(false);
        }
    }

    private void configureTableSelectionBehavior() {
        tblLeaveHistory.setRowSelectionAllowed(true);
        tblLeaveHistory.setColumnSelectionAllowed(false);
        tblLeaveHistory.setCellSelectionEnabled(false);
        tblLeaveHistory.setSelectionMode(
                statusOnlyMode
                        ? javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                        : javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLeaveHistory.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblLeaveHistory.setRowHeight(24);
        tblLeaveHistory.setSelectionBackground(new java.awt.Color(14, 49, 113));
        tblLeaveHistory.setSelectionForeground(java.awt.Color.WHITE);
        tblLeaveHistory.setFocusable(true);
        tblLeaveHistory.setRequestFocusEnabled(true);

        javax.swing.table.DefaultTableCellRenderer fullRowRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                boolean rowSelected = table.getSelectedRow() == row;
                if (rowSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
                return c;
            }
        };
        for (int i = 0; i < tblLeaveHistory.getColumnCount(); i++) {
            if (statusOnlyMode && i == 0) {
                // Keep native checkbox renderer for selection column.
                continue;
            }
            tblLeaveHistory.getColumnModel().getColumn(i).setCellRenderer(fullRowRenderer);
        }

        // Header + spacing
        if (statusOnlyMode) {
            tblLeaveHistory.getColumnModel().getColumn(0).setHeaderValue("Select");
            tblLeaveHistory.getColumnModel().getColumn(1).setHeaderValue("EID");
        } else {
            tblLeaveHistory.getColumnModel().getColumn(0).setHeaderValue("EID");
        }
        tblLeaveHistory.getTableHeader().repaint();

        if (statusOnlyMode) {
            tblLeaveHistory.getColumnModel().getColumn(0).setPreferredWidth(55);  // Select
            tblLeaveHistory.getColumnModel().getColumn(1).setPreferredWidth(70);  // EID
            tblLeaveHistory.getColumnModel().getColumn(2).setPreferredWidth(92);  // Leave Type
            tblLeaveHistory.getColumnModel().getColumn(3).setPreferredWidth(88);  // Start Date
            tblLeaveHistory.getColumnModel().getColumn(4).setPreferredWidth(88);  // End Date
            tblLeaveHistory.getColumnModel().getColumn(5).setPreferredWidth(50);  // Days
            tblLeaveHistory.getColumnModel().getColumn(6).setPreferredWidth(90);  // Status
            tblLeaveHistory.getColumnModel().getColumn(7).setPreferredWidth(210); // Reason
        } else {
            tblLeaveHistory.getColumnModel().getColumn(0).setPreferredWidth(70);  // EID
            tblLeaveHistory.getColumnModel().getColumn(1).setPreferredWidth(92);  // Leave Type
            tblLeaveHistory.getColumnModel().getColumn(2).setPreferredWidth(88);  // Start Date
            tblLeaveHistory.getColumnModel().getColumn(3).setPreferredWidth(88);  // End Date
            tblLeaveHistory.getColumnModel().getColumn(4).setPreferredWidth(50);  // Days
            tblLeaveHistory.getColumnModel().getColumn(5).setPreferredWidth(90);  // Status
            tblLeaveHistory.getColumnModel().getColumn(6).setPreferredWidth(230); // Reason
        }

        if (!tableSelectionConfigured) {
            tblLeaveHistory.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tblLeaveHistory.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        tblLeaveHistory.requestFocusInWindow();
                        tblLeaveHistory.setRowSelectionInterval(row, row);
                    }
                }
            });

            tblLeaveHistory.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateCancelButtonState();
                }
            });
            tableSelectionConfigured = true;
        }
    }

    private void updateCancelButtonState() {
        boolean hasSelection = tblLeaveHistory.getSelectedRow() != -1;
        btnCancel.setText(hasSelection ? "Cancel Selected Request" : "Cancel Request");
    }
    
    private void loadTableData() {
        javax.swing.table.DefaultTableModel model = createLeaveHistoryTableModel();
        tblLeaveHistory.setModel(model);

        String selectedFilter = cmbStatusFilter.getSelectedItem() == null
                ? "All"
                : cmbStatusFilter.getSelectedItem().toString();
        // 2. Request data from the Service
        java.util.List<model.LeaveRequest> history = leaveService.getEmployeeLeaveHistoryFiltered(this.currentEmployeeID, selectedFilter);

        // 3. Fill the table using the Model's getters
        for (model.LeaveRequest req : history) {
            int days = leaveService.getLeaveDaysInt(req.getStartDate(), req.getEndDate());

            if (statusOnlyMode) {
                model.addRow(new Object[]{
                    Boolean.FALSE,
                    req.getEmployeeId(),
                    req.getLeaveType(),
                    leaveService.formatDateForDisplay(req.getStartDate()),
                    leaveService.formatDateForDisplay(req.getEndDate()),
                    days,
                    req.getStatus(),
                    req.getReason()
                });
            } else {
                model.addRow(new Object[]{
                    req.getEmployeeId(),
                    req.getLeaveType(),
                    leaveService.formatDateForDisplay(req.getStartDate()),
                    leaveService.formatDateForDisplay(req.getEndDate()),
                    days,
                    req.getStatus(),
                    req.getReason()
                });
            }
        }
        configureTableSelectionBehavior();
    }

    private javax.swing.table.DefaultTableModel createLeaveHistoryTableModel() {
        if (statusOnlyMode) {
            return new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{"Select", "EID", "Leave Type", "Start Date", "End Date", "Days", "Status", "Reason"}) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return Boolean.class;
                    }
                    return Object.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0;
                }
            };
        }
        return new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"EID", "Leave Type", "Start Date", "End Date", "Days", "Status", "Reason"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private int getColumnIndexEid() { return statusOnlyMode ? 1 : 0; }
    private int getColumnIndexLeaveType() { return statusOnlyMode ? 2 : 1; }
    private int getColumnIndexStartDate() { return statusOnlyMode ? 3 : 2; }
    private int getColumnIndexEndDate() { return statusOnlyMode ? 4 : 3; }
    private int getColumnIndexStatus() { return statusOnlyMode ? 6 : 5; }
    private int getColumnIndexReason() { return statusOnlyMode ? 7 : 6; }

    private void refreshViewData() {
        loadTableData();
        updateSummaryCards();
    }

    private java.util.List<model.LeaveRequest> collectSelectedRequestsForStatusMode() {
        if (!statusOnlyMode) {
            return java.util.Collections.emptyList();
        }

        if (tblLeaveHistory.isEditing() && tblLeaveHistory.getCellEditor() != null) {
            tblLeaveHistory.getCellEditor().stopCellEditing();
        }

        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblLeaveHistory.getModel();
        java.util.Set<Integer> selectedRows = new java.util.LinkedHashSet<>();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object selected = model.getValueAt(row, 0);
            if (Boolean.TRUE.equals(selected)) {
                selectedRows.add(row);
            }
        }
        for (int row : tblLeaveHistory.getSelectedRows()) {
            selectedRows.add(row);
        }

        java.util.List<Object[]> selectedRowsData = new java.util.ArrayList<>();
        for (Integer row : selectedRows) {
            selectedRowsData.add(new Object[]{
                    model.getValueAt(row, 0),
                    model.getValueAt(row, getColumnIndexEid()),
                    model.getValueAt(row, getColumnIndexLeaveType()),
                    model.getValueAt(row, getColumnIndexStartDate()),
                    model.getValueAt(row, getColumnIndexEndDate()),
                    null,
                    model.getValueAt(row, getColumnIndexStatus()),
                    model.getValueAt(row, getColumnIndexReason())
            });
        }
        return leaveService.mapRowsToLeaveRequests(selectedRowsData, true);
    }

    private Integer parseSelectedRequestEmployeeId(int selectedRow) {
        try {
            return Integer.parseInt(String.valueOf(tblLeaveHistory.getValueAt(selectedRow, getColumnIndexEid())).trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private void processSelectedRequests(String targetStatus) {
        if (!statusOnlyMode) {
            return;
        }

        java.util.List<model.LeaveRequest> selectedRequests = collectSelectedRequestsForStatusMode();
        if (selectedRequests.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Select at least one request.");
            return;
        }
        int updated = leaveService.processStatusUpdates(selectedRequests, targetStatus);
        refreshViewData();
        if (updated == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No pending requests were updated.");
        }
    }

    private void updateSummaryCards() {
        int[] summary = leaveService.getLeaveCreditsSummary(this.currentEmployeeID);
        lblVacationCreditsValue.setText(String.valueOf(summary[0]));
        lblSickLeaveCreditsValue.setText(String.valueOf(summary[1]));
        lblEmergencyLeaveCreditsValue.setText(String.valueOf(summary[2]));
        lblTotalCreditsValue.setText(String.valueOf(summary[3]));
    }

    private void calculateDays() {
        int days = leaveService.getLeaveDaysInt(jcStartDateValue.getDate(), jcEndDateValue.getDate());
        lblTotalDaysValue.setText(String.valueOf(days));
    }

    private void clearForm() {
        jcLeaveTypeValue.setSelectedIndex(0);
        jcStartDateValue.setDate(null);
        jcEndDateValue.setDate(null);
        txtReasonValue.setText("");
        lblTotalDaysValue.setText("0");
        updateCancelButtonState();
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
        lblStatusFilter = new javax.swing.JLabel();
        cmbStatusFilter = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLeaveHistory = new javax.swing.JTable();
        btnApprove = new javax.swing.JButton();
        btnDeny = new javax.swing.JButton();
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
        jPanel1.add(txtReasonValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 280, 150));

        btnSubmit.setText("Submit Request");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        jPanel1.add(btnSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 430, 168, -1));

        btnCancel.setText("Cancel Request");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 460, 168, -1));

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

        lblStatusFilter.setText("Filter:");

        cmbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Pending", "Approved", "Rejected", "Cancelled" }));
        cmbStatusFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStatusFilterActionPerformed(evt);
            }
        });

        btnApprove.setText("Approve");
        btnApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveActionPerformed(evt);
            }
        });

        btnDeny.setText("Deny");
        btnDeny.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDenyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLeaveHistoryLayout = new javax.swing.GroupLayout(pnlLeaveHistory);
        pnlLeaveHistory.setLayout(pnlLeaveHistoryLayout);
        pnlLeaveHistoryLayout.setHorizontalGroup(
            pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLeaveHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlLeaveHistoryLayout.createSequentialGroup()
                        .addComponent(lblStatusFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlLeaveHistoryLayout.createSequentialGroup()
                        .addComponent(btnApprove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeny))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        pnlLeaveHistoryLayout.setVerticalGroup(
            pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLeaveHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatusFilter)
                    .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLeaveHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApprove)
                    .addComponent(btnDeny))
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

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
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

        String startDateStr = leaveService.normalizeDateForStorage(start);
        String endDateStr = leaveService.normalizeDateForStorage(end);

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
            refreshViewData();
            clearForm();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Insufficient leave credits!");
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        
    }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void jcStartDateValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jcStartDateValuePropertyChange
        calculateDays();
    }//GEN-LAST:event_jcStartDateValuePropertyChange

    private void jcEndDateValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jcEndDateValuePropertyChange
        calculateDays();
    }//GEN-LAST:event_jcEndDateValuePropertyChange

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        int selectedRow = tblLeaveHistory.getSelectedRow();

        if (selectedRow != -1) {
            Integer requestEmployeeId = parseSelectedRequestEmployeeId(selectedRow);
            if (requestEmployeeId == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Unable to read selected request.");
                return;
            }
            String requestStartDate = String.valueOf(tblLeaveHistory.getValueAt(selectedRow, getColumnIndexStartDate()));
            String requestStatus = String.valueOf(tblLeaveHistory.getValueAt(selectedRow, getColumnIndexStatus()));

            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Cancel this pending leave request?",
                    "Confirm Cancel",
                    javax.swing.JOptionPane.YES_NO_OPTION);
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                int result = leaveService.cancelOwnPendingLeave(
                        this.currentEmployeeID,
                        requestEmployeeId,
                        requestStartDate,
                        requestStatus);
                if (result == ILeaveManagement.CANCEL_SUCCESS) {
                    refreshViewData();
                    clearForm();
                    tblLeaveHistory.clearSelection();
                } else {
                    if (result == ILeaveManagement.CANCEL_NOT_OWNER) {
                        javax.swing.JOptionPane.showMessageDialog(this, "You can only cancel your own leave requests.");
                    } else if (result == ILeaveManagement.CANCEL_NOT_PENDING) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Only pending requests can be canceled.");
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this, "Unable to cancel the selected request.");
                    }
                }
            }
            return;
        }

        clearForm();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed

    this.dispose(); 
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveActionPerformed
        processSelectedRequests("Approved");
    }//GEN-LAST:event_btnApproveActionPerformed

    private void btnDenyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDenyActionPerformed
        processSelectedRequests("Rejected");
    }//GEN-LAST:event_btnDenyActionPerformed

    private void cmbStatusFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusFilterActionPerformed
        loadTableData();
    }//GEN-LAST:event_cmbStatusFilterActionPerformed

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
    private javax.swing.JButton btnApprove;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeny;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<String> cmbStatusFilter;
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
    private javax.swing.JLabel lblStatusFilter;
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
