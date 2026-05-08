/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Frontend.forms;

import Backend.DB_FETCHER;
import Backend.DB_INSERT;
import Frontend.pages.Certificates;
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
public class CertificateRegForms extends javax.swing.JFrame {

    private final Certificates certPage;
    private int mouseX, mouseY;
    private int editCertId = -1;
    private final Map<String, Integer> residentIdMap = new HashMap<>();
    private final Map<String, Integer> officialIdMap = new HashMap<>();

    public CertificateRegForms(Certificates page) {
        this.certPage = page;
        initComponents();
        initCertTypeOptions();
        initResidentNameMap();
        initOfficialNameMap();
        saveBtn.addActionListener(e -> saveBtnAction());
    }

    private void initCertTypeOptions() {
        statCM.addItem("Barangay Clearance");
        statCM.addItem("Barangay Indigency");
        statCM.addItem("Barangay Residency");
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
        applicantTF.setItems(names);
    }

    private void initOfficialNameMap() {
        ResultSet rs = DB_FETCHER.fetchAllActiveOfficialsWithNames();
        if (rs == null) return;
        java.util.List<String> names = new java.util.ArrayList<>();
        try {
            while (rs.next()) {
                int officialId = rs.getInt("official_id");
                String name    = rs.getString("official_name");
                if (name != null && !name.isBlank()) {
                    officialIdMap.put(name.toLowerCase(), officialId);
                    names.add(name);
                }
            }
        } catch (SQLException ex) { /* ignored */ }
        resTF.setItems(names);
    }

    private int lookupResidentId(String name) {
        if (name == null || name.trim().isEmpty()) return -1;
        return residentIdMap.getOrDefault(name.trim().toLowerCase(), -1);
    }

    private int lookupOfficialId(String name) {
        if (name == null || name.trim().isEmpty()) return -1;
        return officialIdMap.getOrDefault(name.trim().toLowerCase(), -1);
    }

    private void saveBtnAction() {
        String purpose      = nameTF.getText() != null ? nameTF.getText().trim() : "";
        String applicant    = applicantTF.getText() != null ? applicantTF.getText().trim() : "";
        String issuedByName = resTF.getText() != null ? resTF.getText().trim() : "";
        String certType     = statCM.getSelectedItem();
        LocalDate dateIssuedPicked   = daterepPF.getDate();
        LocalDate dateReleasedPicked = datePickerField1.getDate();
        /* if (applicantTF.isEmpty() || statCM.isEmpty() || nameTF.isEmpty()
                || resTF.isEmpty() || daterepPF.isEmpty()
                || datePickerField1.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please fill in all required fields and select a birthdate.",
                "Validation Error",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        */
        if (certType == null || certType.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a certificate type.");
            return;
        }
        if (dateIssuedPicked == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select the date issued.");
            return;
        }

        Integer residentId = lookupResidentId(applicant) >= 0 ? lookupResidentId(applicant) : null;
        Integer officialId = lookupOfficialId(issuedByName) >= 0 ? lookupOfficialId(issuedByName) : null;
        java.sql.Date sqlDateIssued   = java.sql.Date.valueOf(dateIssuedPicked);
        java.sql.Date sqlDateReleased = dateReleasedPicked != null ? java.sql.Date.valueOf(dateReleasedPicked) : null;

        int result;
        if (editCertId >= 0) {
            result = DB_INSERT.updateCertificate(editCertId, residentId, certType, purpose, officialId, sqlDateIssued, sqlDateReleased);
        } else {
            result = DB_INSERT.insertCertificate(residentId, certType, purpose, officialId, sqlDateIssued, sqlDateReleased);
        }
        if (result > 0) {
            Notifications.getInstance().show(Notifications.Type.SUCCESS, editCertId >= 0 ? "Certificate updated successfully." : "Certificate saved successfully.");
            certPage.receivedDataFromTheForm("closed");
            this.dispose();
            CertificatePrintFrame printFrame = new CertificatePrintFrame(
                certType, applicant, purpose, issuedByName, dateIssuedPicked.toString());
            printFrame.setVisible(true);
        } else {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Failed to save certificate.");
            this.dispose();
        }
    }

    private void clearBtnAction() {
        nameTF.setText("");
        applicantTF.setText("");
        resTF.setText("");
        statCM.setSelectedIndex(0);
        daterepPF.setDate(null);
        datePickerField1.setDate(null);
        editCertId = -1;
    }

    public void populateForEdit(int certId) {
        this.editCertId = certId;
        ResultSet rs = DB_FETCHER.fetchCertificateById(certId);
        if (rs == null) return;
        try {
            if (rs.next()) {
                String certType = rs.getString("certificate");
                if (certType != null) statCM.setSelectedItem(certType);
                nameTF.setText(rs.getString("purpose") != null ? rs.getString("purpose") : "");
                java.sql.Date di = rs.getDate("date_issued");
                if (di != null) daterepPF.setDate(di.toLocalDate());
                java.sql.Date dr = rs.getDate("date_released");
                if (dr != null) datePickerField1.setDate(dr.toLocalDate());
                String aName = rs.getString("applicant_name");
                if (aName != null && !aName.isBlank()) applicantTF.setText(aName);
                String iName = rs.getString("issued_by_name");
                if (iName != null && !iName.isBlank()) resTF.setText(iName);
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
        applicantTF = new Utilities.ModernDropdownPalette();
        datePickerField1 = new Utilities.DatePickerField();
        jLabel4 = new javax.swing.JLabel();

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
        jLabel1.setText("CERTIFICATE REQUEST");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Applicant");

        jLabel8.setText("Purpose");

        jLabel9.setText("Issued by");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Certificate Type");

        nameTF.setHPad(9);
        nameTF.setPlaceholder("Ex. Enrollment");

        statCM.setPlaceholder("Ex. Brgy. Captain");

        javax.swing.GroupLayout statCMLayout = new javax.swing.GroupLayout(statCM);
        statCM.setLayout(statCMLayout);
        statCMLayout.setHorizontalGroup(
            statCMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 198, Short.MAX_VALUE)
        );
        statCMLayout.setVerticalGroup(
            statCMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jLabel3.setText("Date Issued");

        javax.swing.GroupLayout datePickerField1Layout = new javax.swing.GroupLayout(datePickerField1);
        datePickerField1.setLayout(datePickerField1Layout);
        datePickerField1Layout.setHorizontalGroup(
            datePickerField1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        datePickerField1Layout.setVerticalGroup(
            datePickerField1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel4.setText("Date Released");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(applicantTF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                            .addComponent(nameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(resTF, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel9)
                                                .addComponent(statCM, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel11))))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(daterepPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(datePickerField1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))))
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(statCM, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(applicantTF, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameTF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resTF, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(datePickerField1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(daterepPF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
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
    private Utilities.ModernDropdownPalette applicantTF;
    private javax.swing.ButtonGroup buttonGroup1;
    private Utilities.DatePickerField datePickerField1;
    private Utilities.DatePickerField daterepPF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private Utilities.RoundedInputField nameTF;
    private Utilities.ModernDropdownPalette resTF;
    private Utilities.ModernButton saveBtn;
    private Utilities.ModernComboBox statCM;
    // End of variables declaration//GEN-END:variables
}
