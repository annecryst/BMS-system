/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Frontend.pages;

import Backend.DB_FETCHER;
import Backend.DB_INSERT;
import Backend.DB_QUERIES;
import Frontend.forms.OfficialsRegForm;
import Utilities.SimpleButtonEditor;
import Utilities.SimpleButtonRenderer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import raven.toast.Notifications;
import raven.toast.Notifications.Type;

/**
 *
 * @author User
 */
public class Officials extends javax.swing.JInternalFrame{
    private DefaultTableModel tableModel;
    /**
     * Creates new form Dashboard
     */
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public Officials() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI bui = (BasicInternalFrameUI) this.getUI();
        bui.setNorthPane(null);
        this.setBorder(null);
        this.setResizable(false);            // cannot resize
        this.setClosable(false);             // cannot close
        this.setIconifiable(false);          // cannot minimize
        this.setMaximizable(false);
        addOfficials.setEnabled(true);
        DB_INSERT.setupOfficialsTable();
        initResTable();
    }
    
    public void receivedDataFromTheForm(
            String profileImage,
            int residentId,
            String position,
            Date sqlTermStart,
            Date sqlTermEnd,
            String frameStat){
         if(frameStat.equals("closed")){
             int rs = DB_INSERT.insertOfficial(profileImage, residentId, position, sqlTermStart, sqlTermEnd);
             Type type = (rs==0) ? Notifications.Type.INFO : Notifications.Type.SUCCESS;
             String msg = (rs==0) ? "Something went wrong while saving" : "Official saved successfully";
             Notifications.getInstance().show(type, msg);
             updateTable();
             
         }
    }
    
    public void updateButtonState(){addOfficials.setEnabled(true);}
    
    public void UpdateTableFromEdit(){}

    private void initResTable() {
        // 1. SETUP THE MODEL & DATA
        String[] columns = {
            "NAME",
            "POSITION",
            "TERM START",
            "TERM END",
            "ACTIONS",
            "ID",
            "STATUS"
        };
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 5 ? Integer.class : String.class;
            }
        };

        // Filter combobox setup
        filterType.addItem("ALL");
        filterType.addItem("LIVED");
        filterType.addItem("ARCHIVED");
        filterType.setSelectedItem("LIVED");
        filterType.setSelectionListener((value, index) -> {
            tableModel.setRowCount(0);
            displayDataToTable(tableModel, value);
        });

        displayDataToTable(tableModel, "LIVED");
//        model.addRow(new Object[]{"Mater Dei", "mdc", "mdc.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Bohol Island State University", "BISU Calape", "bisu.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Cipher Society", "cphs", "gmail.com", "Inactive", ""});

        officialTable.setModel(tableModel);
        officialTable.setRowHeight(40); 
        // Lock the headers: Stop dragging (reordering) AND stop resizing
        officialTable.getTableHeader().setReorderingAllowed(false); 
        officialTable.getTableHeader().setResizingAllowed(false); // <--- ADD THIS LINE

        // --- NEW: SET SELECTED ROW COLOR ---
        // Light grey background with black text when clicked
        officialTable.setSelectionBackground(new java.awt.Color(235, 238, 240)); 
        officialTable.setSelectionForeground(java.awt.Color.BLACK);


       // --- NEW: SET HEADER TO BLUE (BULLETPROOF VERSION) ---
        javax.swing.table.JTableHeader header = officialTable.getTableHeader();
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
        // --- ARCHIVED-AWARE PADDED RENDERER (NAME, POSITION columns) ---
        java.awt.Color ARCHIVED_BG = new java.awt.Color(210, 210, 210);
        java.awt.Color ARCHIVED_FG = new java.awt.Color(110, 110, 110);

        javax.swing.table.DefaultTableCellRenderer paddedRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((javax.swing.JComponent) c).setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object status = table.getModel().getValueAt(modelRow, 6);
                    if ("ARCHIVED".equals(status)) { c.setBackground(ARCHIVED_BG); c.setForeground(ARCHIVED_FG); }
                    else { c.setBackground(table.getBackground()); c.setForeground(table.getForeground()); }
                }
                return c;
            }
        };

        // --- ARCHIVED-AWARE CENTER RENDERER (TERM START, TERM END columns) ---
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(javax.swing.JLabel.CENTER);
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object status = table.getModel().getValueAt(modelRow, 6);
                    if ("ARCHIVED".equals(status)) { c.setBackground(ARCHIVED_BG); c.setForeground(ARCHIVED_FG); }
                    else { c.setBackground(table.getBackground()); c.setForeground(table.getForeground()); }
                }
                return c;
            }
        };
        officialTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        officialTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // --- THE NUCLEAR OPTION: A HEADER THAT IGNORES THE MOUSE ---
        javax.swing.table.JTableHeader customHeader = new javax.swing.table.JTableHeader(officialTable.getColumnModel()) {
            @Override
            protected void processMouseEvent(java.awt.event.MouseEvent e) {
                // DO NOTHING! This completely kills the "click" effect.
            }
            @Override
            protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {
                // DO NOTHING! This completely kills the "hover" effect.
            }
        };

        // 7. SETUP THE SEARCH FILTER FOR THE DROPDOWN
        javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> rowSorter = new javax.swing.table.TableRowSorter<>(tableModel);
        officialTable.setRowSorter(rowSorter);

        modernDropdownPalette2.addSearchListener(searchText -> {
            if (searchText.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                // THE FIX: Use Pattern.quote to make exact string matching totally safe
                rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText))); 
            }
        });

        customHeader.setPreferredSize(new java.awt.Dimension(customHeader.getWidth(), 40)); 
        customHeader.setReorderingAllowed(false); // No dragging
        customHeader.setResizingAllowed(false);   // No resizing

        // Apply our solid blue renderer to this new dead-to-the-mouse header
        customHeader.setDefaultRenderer(new javax.swing.table.TableCellRenderer() {
            private final javax.swing.JLabel headerLabel = new javax.swing.JLabel();
            
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                headerLabel.setOpaque(true);
                headerLabel.setBackground(new java.awt.Color(30, 65, 217)); // Solid Blue
                headerLabel.setForeground(java.awt.Color.WHITE); // Solid White
                headerLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                headerLabel.setText(value != null ? value.toString() : "");
                
                // Align and Pad based on column
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

        // Finally, swap out the default NetBeans header for our custom one
        officialTable.setTableHeader(customHeader);
        
        // Apply the padding to the Name (0) and Address (1) columns
        officialTable.getColumnModel().getColumn(0).setCellRenderer(paddedRenderer);
        officialTable.getColumnModel().getColumn(1).setCellRenderer(paddedRenderer);

        // 2. APPLY THE PERFECTLY CENTERED/CURVED BUTTONS TO COLUMN 4
        // (This assumes you still have the SimpleButtonRenderer and Editor classes from earlier!)
        officialTable.getColumnModel().getColumn(4).setCellRenderer(new SimpleButtonRenderer());
        officialTable.getColumnModel().getColumn(4).setCellEditor(new SimpleButtonEditor(officialTable, (Officials) this));

        // Hide the ID and STATUS columns
        officialTable.getColumnModel().getColumn(5).setMinWidth(0);
        officialTable.getColumnModel().getColumn(5).setMaxWidth(0);
        officialTable.getColumnModel().getColumn(5).setPreferredWidth(0);
        officialTable.getColumnModel().getColumn(6).setMinWidth(0);
        officialTable.getColumnModel().getColumn(6).setMaxWidth(0);
        officialTable.getColumnModel().getColumn(6).setPreferredWidth(0);

        // Column widths (total ~900px): NAME gets the widest share
        officialTable.getColumnModel().getColumn(0).setPreferredWidth(290);
        officialTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        officialTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        officialTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        officialTable.getColumnModel().getColumn(4).setPreferredWidth(170);

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
    
    private String getOfficialFullName(int id){
        ResultSet residentData = DB_FETCHER.getOfficialsResidentialInfo(id);
        if(residentData != null){
            try {
                while(residentData.next()){
                    String fn = residentData.getString("first_name");
                    String mn = residentData.getString("middle_name");
                    String ln = residentData.getString("last_name");
                    String fullname = (fn + (mn != null && !mn.isEmpty() ? " " + mn : "") + " " + ln).trim();
                    return fullname;
                }
            } catch (SQLException ex) {
                return "Error";
            }
        }
        return "None";
    }
    
    private void updateTable(){
        String status = filterType.getSelectedItem() != null ? filterType.getSelectedItem() : "LIVED";
        tableModel.setRowCount(0);
        displayDataToTable(tableModel, status);
    }

    public void refreshTable() {
        updateTable();
    }
    
    //HANDLES THE FETCHING PROCESS
    private void displayDataToTable(DefaultTableModel model, String status) {
        ResultSet officialData = DB_FETCHER.fetchOfficialsByStatus(status);
        if (officialData == null) return;
        java.util.List<String> dynamicSuggestions = new java.util.ArrayList<>();
        
        try {
            while(officialData.next()){
                int    officialId = officialData.getInt("official_id");
                String fullName   = getOfficialFullName(officialData.getInt("official_resedential_id"));
                String position   = officialData.getString("position");
                String sdate      = officialData.getString("term_start");
                String edate      = officialData.getString("term_end");
                
                if (fullName != null && !fullName.trim().isEmpty()) {
                    dynamicSuggestions.add(fullName);
                }
                
                String rowStatus = officialData.getString("status");
                model.addRow(new Object[]{
                    fullName, 
                    position,
                    sdate, 
                    edate,
                    "",
                    officialId,
                    rowStatus
                });
            }
            
            modernDropdownPalette2.setItems(dynamicSuggestions);
            
        } catch (SQLException ex) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "An error occured while fetching the data");
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

        addOfficials.setText("New Officials");
        addOfficials.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addOfficials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOfficialsActionPerformed(evt);
            }
        });
        canvasPanel.add(addOfficials, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 50, 150, 40));

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
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 935, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addOfficialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOfficialsActionPerformed
        OfficialsRegForm offRegForm = new OfficialsRegForm(this);
        addOfficials.setEnabled(false);
        offRegForm.setVisible(true);
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
