/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

/**
 *
 * @author User
 */
// --- SIMPLE NATIVE RENDERER ---
    // This just draws the buttons on the screen
public class SimpleButtonRenderer implements javax.swing.table.TableCellRenderer {
    private final javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 5));

    // Apply your exact colors here
    private final RoundedButton btnEdit   = new RoundedButton("Edit",     new java.awt.Color(76, 159, 246));
    private final RoundedButton btnDelete = new RoundedButton("Archived", new java.awt.Color(231, 76, 60));

    public SimpleButtonRenderer() {
        this(true);
    }

    public SimpleButtonRenderer(boolean showArchiveBtn) {
        panel.setOpaque(true);
        panel.add(btnEdit);
        if (showArchiveBtn) panel.add(btnDelete);
    }

    private static final java.awt.Color ARCHIVED_BG      = new java.awt.Color(210, 210, 210);
    private static final java.awt.Color COLOR_ARCHIVE     = new java.awt.Color(231, 76,  60);
    private static final java.awt.Color COLOR_UNARCHIVE   = new java.awt.Color(46,  204, 113);

    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            int modelRow = table.convertRowIndexToModel(row);
            int statusCol = table.getModel().getColumnCount() - 1; // STATUS is last column
            Object statusVal = table.getModel().getValueAt(modelRow, statusCol);
            boolean archived = "ARCHIVED".equals(statusVal);
            panel.setBackground(archived ? ARCHIVED_BG : table.getBackground());
            btnDelete.setText(archived ? "Unarchive" : "Archived");
            btnDelete.setBackgroundColor(archived ? COLOR_UNARCHIVE : COLOR_ARCHIVE);
        }
        return panel;
    }
}