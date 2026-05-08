/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Frontend.pages;

import Backend.DB_FETCHER;
import Backend.DB_INSERT;
import Frontend.forms.BlotterRegForms;
import Utilities.SimpleButtonEditor;
import Utilities.SimpleButtonRenderer;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import raven.toast.Notifications;

/**
 *
 * @author User
 */
public class Blotter extends javax.swing.JInternalFrame{
    private DefaultTableModel tableModel;
    private Utilities.ModernComboBox filterTypeCB;

    public Blotter() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI bui = (BasicInternalFrameUI) this.getUI();
        bui.setNorthPane(null);
        this.setBorder(null);
        this.setResizable(false);            // cannot resize
        this.setClosable(false);             // cannot close
        this.setIconifiable(false);          // cannot minimize
        this.setMaximizable(false);
        initResTable();
    }
    
    public void receivedDataFromTheForm(String frameStat) {
        if (frameStat.equals("closed")) {
            refreshTable();
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        displayDataToTable(tableModel);
    }

    private void initResTable() {
        // 1. SETUP THE MODEL & DATA
        String[] columns = {
            "COMPLAINANT",
            "RESPONDENT",
            "INCIDENT",
            "STATUS",
            "DATE REPORTED",
            "ACTIONS",
            "ID"
        };
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 6 ? Integer.class : String.class;
            }
        };

        //resident record fetching
        displayDataToTable(tableModel);
//        model.addRow(new Object[]{"Mater Dei", "mdc", "mdc.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Bohol Island State University", "BISU Calape", "bisu.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Cipher Society", "cphs", "gmail.com", "Inactive", ""});

        residentsTable.setModel(tableModel);
        residentsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        residentsTable.setRowHeight(40); 
        // Lock the headers: Stop dragging (reordering) AND stop resizing
        residentsTable.getTableHeader().setReorderingAllowed(false); 
        residentsTable.getTableHeader().setResizingAllowed(false); // <--- ADD THIS LINE

        // --- NEW: SET SELECTED ROW COLOR ---
        // Light grey background with black text when clicked
        residentsTable.setSelectionBackground(new java.awt.Color(235, 238, 240)); 
        residentsTable.setSelectionForeground(java.awt.Color.BLACK);


       // --- NEW: SET HEADER TO BLUE (BULLETPROOF VERSION) ---
        javax.swing.table.JTableHeader header = residentsTable.getTableHeader();
        header.setPreferredSize(new java.awt.Dimension(header.getWidth(), 40)); 
        
        // Use a pure TableCellRenderer to completely block the system's hover effects
        header.setDefaultRenderer(new javax.swing.table.TableCellRenderer() {
            // Create a single standard label to do all the drawing
            private final javax.swing.JLabel headerLabel = new javax.swing.JLabel();
            
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                // 1. Force absolute solid colors (No super() call means no system interference!)
                headerLabel.setOpaque(true);
                headerLabel.setBackground(new java.awt.Color(30, 65, 217)); // Solid Blue
                headerLabel.setForeground(java.awt.Color.WHITE); // Solid White text
                headerLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                
                // 2. Set the actual column name text
                headerLabel.setText(value != null ? value.toString() : "");
                
                // 3. Align and Pad based on column
                if (column == 2 || column == 3 || column == 4) {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                } else {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                }
                
                return headerLabel;
            }
        });
        // --- CENTER RENDERER for STATUS (3) and DATE REPORTED (4) with tooltip ---
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = value != null ? value.toString() : "";
                setText(text);
                setToolTipText(text.isEmpty() ? null : text);
                setHorizontalAlignment(javax.swing.JLabel.CENTER);
                return this;
            }
        };
        residentsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        residentsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // --- PADDED RENDERER for text columns (with tooltip for overflow) ---
        javax.swing.table.DefaultTableCellRenderer paddedRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = value != null ? value.toString() : "";
                setText(text);
                setToolTipText(text.isEmpty() ? null : text);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return this;
            }
        };

        // --- HEADER THAT IGNORES THE MOUSE ---
        javax.swing.table.JTableHeader customHeader = new javax.swing.table.JTableHeader(residentsTable.getColumnModel()) {
            @Override protected void processMouseEvent(java.awt.event.MouseEvent e) {}
            @Override protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {}
        };

        // SEARCH + STATUS FILTER
        javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> rowSorter = new javax.swing.table.TableRowSorter<>(tableModel);
        residentsTable.setRowSorter(rowSorter);

        final String[] filterState = {"", "ALL"}; // [0]=search text, [1]=status

        Runnable applyFilters = () -> {
            String search = filterState[0].trim();
            String status = filterState[1];
            java.util.List<javax.swing.RowFilter<javax.swing.table.DefaultTableModel, Object>> filters = new java.util.ArrayList<>();
            if (!search.isEmpty()) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(search)));
            }
            if (!"ALL".equals(status)) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)^" + java.util.regex.Pattern.quote(status) + "$", 3));
            }
            if (filters.isEmpty()) rowSorter.setRowFilter(null);
            else if (filters.size() == 1) rowSorter.setRowFilter(filters.get(0));
            else rowSorter.setRowFilter(javax.swing.RowFilter.andFilter(filters));
        };

        modernDropdownPalette2.addSearchListener(searchText -> {
            filterState[0] = searchText;
            applyFilters.run();
        });

        // STATUS FILTER COMBOBOX
        filterTypeCB = new Utilities.ModernComboBox();
        filterTypeCB.setPlaceholder("Filter by Status");
        filterTypeCB.addItem("ALL");
        filterTypeCB.addItem("Pending");
        filterTypeCB.addItem("Ongoing");
        filterTypeCB.addItem("Under Investigation");
        filterTypeCB.addItem("For Mediation");
        filterTypeCB.addItem("Scheduled for Hearing");
        filterTypeCB.addItem("Settled / Amicably Settled");
        filterTypeCB.addItem("Resolved");
        filterTypeCB.addItem("Referred to Police");
        filterTypeCB.addItem("Referred to Court");
        filterTypeCB.addItem("Dismissed");
        filterTypeCB.addItem("Withdrawn");
        filterTypeCB.addItem("Archived");
        filterTypeCB.addItem("Closed");
        filterTypeCB.setSelectionListener((value, index) -> {
            filterState[1] = value;
            applyFilters.run();
        });
        canvasPanel.add(filterTypeCB, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 50, 245, 40));

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
                if (column >= 3) {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                } else {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                }
                return headerLabel;
            }
        });

        // Finally, swap out the default NetBeans header for our custom one
        residentsTable.setTableHeader(customHeader);
        
        residentsTable.getColumnModel().getColumn(0).setCellRenderer(paddedRenderer);
        residentsTable.getColumnModel().getColumn(1).setCellRenderer(paddedRenderer);
        residentsTable.getColumnModel().getColumn(2).setCellRenderer(paddedRenderer);

        residentsTable.getColumnModel().getColumn(5).setCellRenderer(new SimpleButtonRenderer(false));
        residentsTable.getColumnModel().getColumn(5).setCellEditor(new SimpleButtonEditor(residentsTable, (Blotter) this));

        // Hide ID column
        residentsTable.getColumnModel().getColumn(6).setMinWidth(0);
        residentsTable.getColumnModel().getColumn(6).setMaxWidth(0);
        residentsTable.getColumnModel().getColumn(6).setPreferredWidth(0);

        // Column widths — min + preferred so columns never shrink below readable size
        residentsTable.getColumnModel().getColumn(0).setMinWidth(140);
        residentsTable.getColumnModel().getColumn(0).setPreferredWidth(185);
        residentsTable.getColumnModel().getColumn(1).setMinWidth(140);
        residentsTable.getColumnModel().getColumn(1).setPreferredWidth(185);
        residentsTable.getColumnModel().getColumn(2).setMinWidth(180);
        residentsTable.getColumnModel().getColumn(2).setPreferredWidth(230);
        residentsTable.getColumnModel().getColumn(3).setMinWidth(110);
        residentsTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        residentsTable.getColumnModel().getColumn(4).setMinWidth(100);
        residentsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        residentsTable.getColumnModel().getColumn(5).setMinWidth(120);
        residentsTable.getColumnModel().getColumn(5).setPreferredWidth(120);

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
    
    //HANDLES THE FETCHING PROCESS
    private void displayDataToTable(DefaultTableModel model) {
        ResultSet blotterData = DB_FETCHER.fetchAllBlotters();
        if (blotterData == null) return;
        java.util.LinkedHashSet<String> uniqueNames = new java.util.LinkedHashSet<>();

        try {
            while (blotterData.next()) {
                int    blotterId   = blotterData.getInt("blotter_id");
                String complainant = blotterData.getString("complainant_name");
                String respondent  = blotterData.getString("respondent_name");
                String incident    = blotterData.getString("incident");
                String status      = blotterData.getString("status");
                String date        = blotterData.getString("date_reported");

                if (complainant != null && !complainant.isBlank()) uniqueNames.add(complainant);
                if (respondent  != null && !respondent.isBlank())  uniqueNames.add(respondent);

                model.addRow(new Object[]{
                    complainant, respondent, incident, status, date, "", blotterId
                });
            }
            modernDropdownPalette2.setItems(new java.util.ArrayList<>(uniqueNames));
        } catch (SQLException ex) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "An error occurred while fetching blotter data.");
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
        residentsTable = new javax.swing.JTable();
        addBlotter = new Utilities.ModernButton();

        setBackground(new java.awt.Color(221, 233, 235));
        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(927, 590));

        canvasPanel.setBackground(new java.awt.Color(221, 233, 235));
        canvasPanel.setPreferredSize(new java.awt.Dimension(927, 590));
        canvasPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        canvasPanel.add(modernDropdownPalette2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 480, -1));

        residentsTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        residentsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        residentsTable.setFocusable(false);
        residentsTable.getTableHeader().setResizingAllowed(false);
        residentsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(residentsTable);
        if (residentsTable.getColumnModel().getColumnCount() > 0) {
            residentsTable.getColumnModel().getColumn(0).setResizable(false);
            residentsTable.getColumnModel().getColumn(1).setResizable(false);
            residentsTable.getColumnModel().getColumn(2).setResizable(false);
            residentsTable.getColumnModel().getColumn(3).setResizable(false);
        }

        canvasPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 900, 440));

        addBlotter.setText("New Resident");
        addBlotter.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addBlotter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBlotterActionPerformed(evt);
            }
        });
        canvasPanel.add(addBlotter, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 50, 150, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(canvasPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 940, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addBlotterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBlotterActionPerformed
        BlotterRegForms blotregform = new BlotterRegForms(this);
        blotregform.setVisible(true);
    }//GEN-LAST:event_addBlotterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Utilities.ModernButton addBlotter;
    private javax.swing.JPanel canvasPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private Utilities.ModernDropdownPalette modernDropdownPalette2;
    private javax.swing.JTable residentsTable;
    private Utilities.SimpleButtonRenderer simpleButtonRenderer1;
    // End of variables declaration//GEN-END:variables
}
