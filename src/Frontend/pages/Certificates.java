/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Frontend.pages;

import Backend.DB_FETCHER;
import Backend.DB_INSERT;
import Utilities.SimpleButtonEditor;
import Utilities.SimpleButtonRenderer;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import raven.toast.Notifications;
import raven.toast.Notifications.Type;

/**
 *
 * @author User
 */
public class Certificates extends javax.swing.JInternalFrame{
    private DefaultTableModel tableModel;

    public Certificates() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI bui = (BasicInternalFrameUI) this.getUI();
        bui.setNorthPane(null);
        this.setBorder(null);
        this.setResizable(false);
        this.setClosable(false);
        this.setIconifiable(false);
        this.setMaximizable(false);
        DB_INSERT.setupCertificatesTable();
        initResTable();
    }
    
    public void receivedDataFromTheForm(String frameStat) {
        if ("closed".equals(frameStat)) refreshTable();
    }

    private void initResTable() {
        String[] columns = {
            "APPLICANT NAME", "CERTIFICATE TYPE", "PURPOSE",
            "ISSUED BY", "DATE ISSUED", "DATE RELEASED",
            "ACTIONS", "ID", "STATUS"
        };
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 7 ? Integer.class : String.class;
            }
        };

        filterType.addItem("ALL");
        filterType.addItem("ACTIVE");
        filterType.addItem("ARCHIVED");
        filterType.setSelectedItem("ACTIVE");
        filterType.setSelectionListener((value, index) -> {
            tableModel.setRowCount(0);
            displayDataToTable(tableModel, value);
        });

        displayDataToTable(tableModel, "ACTIVE");
//        model.addRow(new Object[]{"Mater Dei", "mdc", "mdc.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Bohol Island State University", "BISU Calape", "bisu.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Cipher Society", "cphs", "gmail.com", "Inactive", ""});

        officialTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        officialTable.setModel(tableModel);
        officialTable.setRowHeight(40);
        officialTable.getTableHeader().setReorderingAllowed(false);
        officialTable.getTableHeader().setResizingAllowed(false);
        officialTable.setSelectionBackground(new java.awt.Color(235, 238, 240));
        officialTable.setSelectionForeground(java.awt.Color.BLACK);

        java.awt.Color ARCHIVED_BG = new java.awt.Color(210, 210, 210);
        java.awt.Color ARCHIVED_FG = new java.awt.Color(110, 110, 110);

        javax.swing.table.DefaultTableCellRenderer paddedRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = value != null ? value.toString() : "";
                setText(text);
                setToolTipText(text.isEmpty() ? null : text);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object status = table.getModel().getValueAt(modelRow, 8);
                    if ("ARCHIVED".equals(status)) { setBackground(ARCHIVED_BG); setForeground(ARCHIVED_FG); }
                    else { setBackground(table.getBackground()); setForeground(table.getForeground()); }
                }
                return this;
            }
        };

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = value != null ? value.toString() : "";
                setText(text);
                setToolTipText(text.isEmpty() ? null : text);
                setHorizontalAlignment(javax.swing.JLabel.CENTER);
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object status = table.getModel().getValueAt(modelRow, 8);
                    if ("ARCHIVED".equals(status)) { setBackground(ARCHIVED_BG); setForeground(ARCHIVED_FG); }
                    else { setBackground(table.getBackground()); setForeground(table.getForeground()); }
                }
                return this;
            }
        };

        javax.swing.table.JTableHeader customHeader = new javax.swing.table.JTableHeader(officialTable.getColumnModel()) {
            @Override protected void processMouseEvent(java.awt.event.MouseEvent e) {}
            @Override protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {}
        };

        javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> rowSorter = new javax.swing.table.TableRowSorter<>(tableModel);
        officialTable.setRowSorter(rowSorter);
        modernDropdownPalette2.addSearchListener(searchText -> {
            if (searchText.trim().isEmpty()) rowSorter.setRowFilter(null);
            else rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        });

        customHeader.setPreferredSize(new java.awt.Dimension(customHeader.getWidth(), 40));
        customHeader.setReorderingAllowed(false);
        customHeader.setResizingAllowed(false);
        customHeader.setDefaultRenderer(new javax.swing.table.TableCellRenderer() {
            private final javax.swing.JLabel headerLabel = new javax.swing.JLabel();
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                headerLabel.setOpaque(true);
                headerLabel.setBackground(new java.awt.Color(30, 65, 217));
                headerLabel.setForeground(java.awt.Color.WHITE);
                headerLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                headerLabel.setText(value != null ? value.toString() : "");
                if (column == 1 || column == 4 || column == 5 || column == 6) {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                } else {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                }
                return headerLabel;
            }
        });
        officialTable.setTableHeader(customHeader);

        officialTable.getColumnModel().getColumn(0).setCellRenderer(paddedRenderer);
        officialTable.getColumnModel().getColumn(2).setCellRenderer(paddedRenderer);
        officialTable.getColumnModel().getColumn(3).setCellRenderer(paddedRenderer);
        officialTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        officialTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        officialTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        officialTable.getColumnModel().getColumn(6).setCellRenderer(new SimpleButtonRenderer());
        officialTable.getColumnModel().getColumn(6).setCellEditor(new SimpleButtonEditor(officialTable, (Certificates) this));

        officialTable.getColumnModel().getColumn(7).setMinWidth(0);
        officialTable.getColumnModel().getColumn(7).setMaxWidth(0);
        officialTable.getColumnModel().getColumn(7).setPreferredWidth(0);
        officialTable.getColumnModel().getColumn(8).setMinWidth(0);
        officialTable.getColumnModel().getColumn(8).setMaxWidth(0);
        officialTable.getColumnModel().getColumn(8).setPreferredWidth(0);

        officialTable.getColumnModel().getColumn(0).setMinWidth(160); officialTable.getColumnModel().getColumn(0).setPreferredWidth(210);
        officialTable.getColumnModel().getColumn(1).setMinWidth(140); officialTable.getColumnModel().getColumn(1).setPreferredWidth(165);
        officialTable.getColumnModel().getColumn(2).setMinWidth(150); officialTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        officialTable.getColumnModel().getColumn(3).setMinWidth(160); officialTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        officialTable.getColumnModel().getColumn(4).setMinWidth(90);  officialTable.getColumnModel().getColumn(4).setPreferredWidth(105);
        officialTable.getColumnModel().getColumn(5).setMinWidth(90);  officialTable.getColumnModel().getColumn(5).setPreferredWidth(105);
        officialTable.getColumnModel().getColumn(6).setMinWidth(165); officialTable.getColumnModel().getColumn(6).setPreferredWidth(185);

        // 3. THE FIX: PERMANENTLY ENFORCE CURVES USING JLAYER
        canvasPanel.remove(jScrollPane1);
        
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder()); 
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane1.setOpaque(false);

        // This LayerUI intercepts ALL repaints (even localized clicks) to enforce the curve
        javax.swing.plaf.LayerUI<javax.swing.JScrollPane> layerUI = new javax.swing.plaf.LayerUI<javax.swing.JScrollPane>() {
            @Override
            public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create the permanent curved mask
                java.awt.geom.RoundRectangle2D.Float clipShape = 
                    new java.awt.geom.RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 30, 30);
                g2.clip(clipShape);
                
                // Paint the white background and the table INSIDE the mask
                g2.setColor(java.awt.Color.WHITE);
                g2.fill(clipShape);
                super.paint(g2, c);
                g2.dispose();
                
                // Draw a clean gray border line on top
                java.awt.Graphics2D g2Border = (java.awt.Graphics2D) g.create();
                g2Border.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2Border.setColor(new java.awt.Color(220, 225, 230)); 
                g2Border.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 30, 30);
                g2Border.dispose();
            }
        };

        // Wrap the original scroll pane in the protective JLayer
        javax.swing.JLayer<javax.swing.JScrollPane> roundedLayer = new javax.swing.JLayer<>(jScrollPane1, layerUI);

        // 4. PUT IT EXACTLY WHERE NETBEANS WANTS IT
        int x = 20;
        int y = 100;
        int width = 900;
        int height = 510;

        roundedLayer.setBounds(x, y, width, height);
        canvasPanel.add(roundedLayer, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width, height));
        
        // Push it behind your dropdown palette
        canvasPanel.setComponentZOrder(roundedLayer, canvasPanel.getComponentCount() - 1);

        canvasPanel.revalidate();
        canvasPanel.repaint();
    }
    
    private void updateTable() {
        String status = filterType.getSelectedItem() != null ? filterType.getSelectedItem() : "ACTIVE";
        tableModel.setRowCount(0);
        displayDataToTable(tableModel, status);
    }

    public void refreshTable() { updateTable(); }

    private void displayDataToTable(DefaultTableModel model, String status) {
        ResultSet data = DB_FETCHER.fetchCertificatesByStatus(status);
        if (data == null) return;
        java.util.LinkedHashSet<String> uniqueNames = new java.util.LinkedHashSet<>();
        try {
            while (data.next()) {
                int    certId      = data.getInt("certificate_id");
                String applicant   = data.getString("applicant_name");
                String certType    = data.getString("certificate");
                String purpose     = data.getString("purpose");
                String issuedBy    = data.getString("issued_by_name");
                String dateIssued  = data.getString("date_issued");
                String dateReleased = data.getString("date_released");
                String certStatus  = data.getString("cert_status");
                if (applicant != null && !applicant.isBlank()) uniqueNames.add(applicant);
                model.addRow(new Object[]{
                    applicant, certType, purpose, issuedBy,
                    dateIssued, dateReleased, "", certId, certStatus
                });
            }
            modernDropdownPalette2.setItems(new java.util.ArrayList<>(uniqueNames));
        } catch (SQLException ex) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Error fetching certificate data.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        simpleButtonRenderer1 = new Utilities.SimpleButtonRenderer();
        canvasPanel = new javax.swing.JPanel();
        modernDropdownPalette2 = new Utilities.ModernDropdownPalette();
        jScrollPane1 = new javax.swing.JScrollPane();
        officialTable = new javax.swing.JTable();
        addOfficials = new Utilities.ModernButton();
        filterType = new Utilities.ModernComboBox();

        setBackground(new java.awt.Color(221, 233, 235));
        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(927, 590));

        canvasPanel.setBackground(new java.awt.Color(221, 233, 235));
        canvasPanel.setPreferredSize(new java.awt.Dimension(927, 590));
        canvasPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        canvasPanel.add(modernDropdownPalette2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 480, -1));

        officialTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        officialTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        officialTable.setFocusable(false);
        officialTable.getTableHeader().setResizingAllowed(false);
        officialTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(officialTable);
        if (officialTable.getColumnModel().getColumnCount() > 0) {
            officialTable.getColumnModel().getColumn(0).setResizable(false);
            officialTable.getColumnModel().getColumn(1).setResizable(false);
            officialTable.getColumnModel().getColumn(2).setResizable(false);
            officialTable.getColumnModel().getColumn(3).setResizable(false);
        }

        canvasPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 900, 440));

        addOfficials.setText("New Request");
        addOfficials.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addOfficials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOfficialsActionPerformed(evt);
            }
        });
        canvasPanel.add(addOfficials, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 50, 140, 40));

        filterType.setPlaceholder("Ex. LIVED");

        javax.swing.GroupLayout filterTypeLayout = new javax.swing.GroupLayout(filterType);
        filterType.setLayout(filterTypeLayout);
        filterTypeLayout.setHorizontalGroup(
            filterTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        filterTypeLayout.setVerticalGroup(
            filterTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        canvasPanel.add(filterType, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 50, 130, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addOfficialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOfficialsActionPerformed
        Frontend.forms.CertificateRegForms form = new Frontend.forms.CertificateRegForms(this);
        form.setVisible(true);
    }//GEN-LAST:event_addOfficialsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Utilities.ModernButton addOfficials;
    private javax.swing.JPanel canvasPanel;
    private Utilities.ModernComboBox filterType;
    private javax.swing.JScrollPane jScrollPane1;
    private Utilities.ModernDropdownPalette modernDropdownPalette2;
    private javax.swing.JTable officialTable;
    private Utilities.SimpleButtonRenderer simpleButtonRenderer1;
    // End of variables declaration//GEN-END:variables
}
