/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Frontend.forms;

import Backend.DB_FETCHER;
import Backend.DB_INSERT;
import Frontend.pages.Blotter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import raven.toast.Notifications;

/**
 *
 * @author User
 */
public class BlotterRegForms extends javax.swing.JFrame {

    private final Blotter blot;
    private int mouseX, mouseY;
    private int editBlotterId = -1;
    private final Map<String, Integer> residentIdMap = new HashMap<>();

    public BlotterRegForms(Blotter blotPage) {
        this.blot = blotPage;
        initComponents();
        initStatusOptions();
        initResidentNameMap();
        saveBtn.addActionListener(e -> saveBtnAction());
    }

    private void initStatusOptions() {
        statCM.addItem("Pending");
        statCM.addItem("Ongoing");
        statCM.addItem("Under Investigation");
        statCM.addItem("For Mediation");
        statCM.addItem("Scheduled for Hearing");
        statCM.addItem("Settled / Amicably Settled");
        statCM.addItem("Resolved");
        statCM.addItem("Referred to Police");
        statCM.addItem("Referred to Court");
        statCM.addItem("Dismissed");
        statCM.addItem("Withdrawn");
        statCM.addItem("Archived");
        statCM.addItem("Closed");
        statCM.setSelectedIndex(0);
    }

    private void initResidentNameMap() {
        ResultSet rs = DB_FETCHER.fetchAllResidents();
        if (rs == null) return;
        java.util.List<String> names = new java.util.ArrayList<>();
        try {
            while (rs.next()) {
                int id    = rs.getInt("resident_id");
                String fn = rs.getString("first_name");
                String mn = rs.getString("middle_name");
                String ln = rs.getString("last_name");
                String fullName = (fn + (mn != null && !mn.isEmpty() ? " " + mn : "") + " " + ln).trim();
                if (!fullName.isEmpty()) {
                    residentIdMap.put(fullName.toLowerCase(), id);
                    names.add(fullName);
                }
            }
        } catch (SQLException ex) { /* ignored */ }
        complainantTF.setItems(names);
        resTF.setItems(names);
    }

    private int lookupResidentId(String name) {
        if (name == null || name.trim().isEmpty()) return -1;
        return residentIdMap.getOrDefault(name.trim().toLowerCase(), -1);
    }

    private void saveBtnAction() {
        String incident    = nameTF.getText().trim();
        String complainant = complainantTF.getText() != null ? complainantTF.getText().trim() : "";
        String respondent  = resTF.getText() != null ? resTF.getText().trim() : "";
        String status      = statCM.getSelectedItem();
        LocalDate datePicked = daterepPF.getDate();

        if (incident.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please enter the incident / report type.");
            return;
        }
        if (status == null || status.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a status.");
            return;
        }
        if (datePicked == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select the date reported.");
            return;
        }

        Integer complainantId = lookupResidentId(complainant) >= 0 ? lookupResidentId(complainant) : null;
        Integer respondentId  = lookupResidentId(respondent)  >= 0 ? lookupResidentId(respondent)  : null;
        java.sql.Date sqlDate = java.sql.Date.valueOf(datePicked);

        int result;
        if (editBlotterId >= 0) {
            result = DB_INSERT.updateBlotter(editBlotterId, complainantId, respondentId, incident, status, sqlDate);
        } else {
            result = DB_INSERT.insertBlotter(complainantId, respondentId, incident, status, sqlDate);
        }
        if (result > 0) {
            Notifications.getInstance().show(Notifications.Type.SUCCESS, editBlotterId >= 0 ? "Blotter record updated successfully." : "Blotter record saved successfully.");
            blot.receivedDataFromTheForm("closed");
        } else {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to save blotter record.");
        }
        this.dispose();
    }

    private void clearBtnAction() {
        nameTF.setText("");
        complainantTF.setText("");
        resTF.setText("");
        statCM.setSelectedIndex(0);
        daterepPF.setDate(null);
        editBlotterId = -1;
    }

    public void populateForEdit(int blotterId) {
        this.editBlotterId = blotterId;
        ResultSet rs = DB_FETCHER.fetchBlotterById(blotterId);
        if (rs == null) return;
        try {
            if (rs.next()) {
                nameTF.setText(rs.getString("incident") != null ? rs.getString("incident") : "");
                java.sql.Date d = rs.getDate("date_reported");
                if (d != null) daterepPF.setDate(d.toLocalDate());
                String status = rs.getString("status");
                if (status != null) statCM.setSelectedItem(status);
                String cName = rs.getString("complainant_name");
                if (cName != null && !cName.isBlank()) complainantTF.setText(cName);
                String rName = rs.getString("respondent_name");
                if (rName != null && !rName.isBlank()) resTF.setText(rName);
            }
        } catch (SQLException ex) { /* ignored */ }
    }
    /**
     * Creates new form ResidentForm
     * @param off
     */
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        nameTF = new Utilities.RoundedInputField();
        statCM = new Utilities.ModernComboBox();
        saveBtn = new Utilities.ModernButton();
        daterepPF = new Utilities.DatePickerField();
        jLabel3 = new javax.swing.JLabel();
        resTF = new Utilities.ModernDropdownPalette();
        complainantTF = new Utilities.ModernDropdownPalette();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(" BLOTTER REPORTS");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Report Type");

        jLabel8.setText("Complainant");

        jLabel9.setText("Date Reported");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Status");

        nameTF.setHPad(9);
        nameTF.setPlaceholder("Ex. Incident Report");

        statCM.setPlaceholder("Ex. Brgy. Captain");

        javax.swing.GroupLayout statCMLayout = new javax.swing.GroupLayout(statCM);
        statCM.setLayout(statCMLayout);
        statCMLayout.setHorizontalGroup(
            statCMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 198, Short.MAX_VALUE)
        );
        statCMLayout.setVerticalGroup(
            statCMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 31, Short.MAX_VALUE)
        );

        saveBtn.setText("SAVE");
        saveBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout daterepPFLayout = new javax.swing.GroupLayout(daterepPF);
        daterepPF.setLayout(daterepPFLayout);
        daterepPFLayout.setHorizontalGroup(
            daterepPFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        daterepPFLayout.setVerticalGroup(
            daterepPFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );

        jLabel3.setText("Respondent");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(jLabel11))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nameTF, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resTF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(complainantTF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(statCM, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(daterepPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 118, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(complainantTF, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statCM, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(daterepPF, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resTF, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addGap(30, 30, 30)
                .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        mouseX = evt.getX();
        mouseY = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x-mouseX, y-mouseY);
    }//GEN-LAST:event_formMouseDragged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private Utilities.ModernDropdownPalette complainantTF;
    private Utilities.DatePickerField daterepPF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private Utilities.RoundedInputField nameTF;
    private Utilities.ModernDropdownPalette resTF;
    private Utilities.ModernButton saveBtn;
    private Utilities.ModernComboBox statCM;
    // End of variables declaration//GEN-END:variables
}
