/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

import Frontend.forms.BlotterRegForms;
import Frontend.forms.CertificateRegForms;
import Frontend.forms.OfficialsRegForm;
import Frontend.forms.ResidentRegForm;
import Frontend.pages.Blotter;
import Frontend.pages.Certificates;
import Frontend.pages.Officials;
import Frontend.pages.Residents;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class SimpleButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
    private final javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 5));
    
    // Exact same buttons as the renderer so nothing changes size or shape
    private final RoundedButton btnEdit = new RoundedButton("Edit", new java.awt.Color(76, 159, 246));
    private final RoundedButton btnDelete = new RoundedButton("Archived", new java.awt.Color(231, 76, 60));

    public SimpleButtonEditor(final javax.swing.JTable table, JInternalFrame frame) {
        this(table, frame, true);
    }

    public SimpleButtonEditor(final javax.swing.JTable table, JInternalFrame frame, boolean showArchiveBtn) {
        panel.add(btnEdit);
        if (showArchiveBtn) panel.add(btnDelete);

        // EDIT BUTTON LOGIC
        btnEdit.addActionListener(e -> {
            fireEditingStopped(); 
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int residentId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 5);
                String name = table.getValueAt(viewRow, 0).toString();
                System.out.println("Edit resident ID: " + residentId + " | Name: " + name);
                ResidentRegForm regForm = new ResidentRegForm((Residents) frame);
                regForm.populateForEdit(residentId);
                regForm.setVisible(true);
            }
        });

        // ARCHIVE BUTTON LOGIC
        if (showArchiveBtn) {
            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    int residentId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 5);
                    System.out.println("Archive resident ID: " + residentId);
                    int confirm = javax.swing.JOptionPane.showConfirmDialog(null, "Archive this resident?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
                    if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                        ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                    }
                }
            });
        }
    }

    public SimpleButtonEditor(final javax.swing.JTable table, Officials officialsPage) {
        panel.add(btnEdit);
        panel.add(btnDelete);

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int officialId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 5);
                OfficialsRegForm regForm = new OfficialsRegForm(officialsPage);
                regForm.populateForEdit(officialId);
                regForm.setVisible(true);
            }
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow       = table.convertRowIndexToModel(viewRow);
                int officialId     = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 5);
                Object statusVal   = ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 6);
                boolean isArchived = "ARCHIVED".equals(statusVal);
                String newStatus   = isArchived ? "LIVED" : "ARCHIVED";
                String confirmMsg  = isArchived ? "Unarchive this official?" : "Archive this official?";
                int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
                        confirmMsg, "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    int result = Backend.DB_INSERT.setOfficialStatus(officialId, newStatus);
                    if (result > 0) {
                        officialsPage.refreshTable();
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(null, "Failed to update official status.");
                    }
                }
            }
        });
    }

    public SimpleButtonEditor(final javax.swing.JTable table, Certificates certPage) {
        panel.add(btnEdit);
        panel.add(btnDelete);

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int lastCol  = table.getModel().getColumnCount() - 1;
                int certId   = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, lastCol - 1);
                CertificateRegForms form = new CertificateRegForms(certPage);
                form.populateForEdit(certId);
                form.setVisible(true);
            }
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow    = table.convertRowIndexToModel(viewRow);
                int lastCol     = table.getModel().getColumnCount() - 1;
                int certId      = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, lastCol - 1);
                Object statusVal = ((DefaultTableModel) table.getModel()).getValueAt(modelRow, lastCol);
                boolean isArchived = "ARCHIVED".equals(statusVal);
                String newStatus   = isArchived ? "ACTIVE" : "ARCHIVED";
                String msg         = isArchived ? "Unarchive this certificate?" : "Archive this certificate?";
                int confirm = javax.swing.JOptionPane.showConfirmDialog(null, msg, "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    int result = Backend.DB_INSERT.setCertificateStatus(certId, newStatus);
                    if (result > 0) certPage.refreshTable();
                    else javax.swing.JOptionPane.showMessageDialog(null, "Failed to update certificate status.");
                }
            }
        });
    }

    public SimpleButtonEditor(final javax.swing.JTable table, Blotter blotterPage) {
        panel.add(btnEdit);
        panel.add(btnDelete);
        btnDelete.setText("Delete");

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow  = table.convertRowIndexToModel(viewRow);
                int blotterId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 6);
                BlotterRegForms form = new BlotterRegForms(blotterPage);
                form.populateForEdit(blotterId);
                form.setVisible(true);
            }
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow  = table.convertRowIndexToModel(viewRow);
                int blotterId = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 6);
                int confirm   = javax.swing.JOptionPane.showConfirmDialog(null,
                        "Delete this blotter record?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    int result = Backend.DB_INSERT.deleteBlotter(blotterId);
                    if (result > 0) blotterPage.refreshTable();
                    else javax.swing.JOptionPane.showMessageDialog(null, "Failed to delete blotter record.");
                }
            }
        });
    }

    private static final java.awt.Color ARCHIVED_BG    = new java.awt.Color(210, 210, 210);
    private static final java.awt.Color COLOR_ARCHIVE   = new java.awt.Color(231, 76,  60);
    private static final java.awt.Color COLOR_UNARCHIVE = new java.awt.Color(46,  204, 113);

    @Override
    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
        int modelRow = table.convertRowIndexToModel(row);
        int lastCol = table.getModel().getColumnCount() - 1;
        if (lastCol >= 0 && table.getModel().getColumnClass(lastCol) == String.class) {
            Object statusVal   = table.getModel().getValueAt(modelRow, lastCol);
            boolean archived   = "ARCHIVED".equals(statusVal);
            panel.setBackground(archived ? ARCHIVED_BG : table.getSelectionBackground());
            btnDelete.setText(archived ? "Unarchive" : "Archived");
            btnDelete.setBackgroundColor(archived ? COLOR_UNARCHIVE : COLOR_ARCHIVE);
        } else {
            panel.setBackground(table.getSelectionBackground());
        }
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}