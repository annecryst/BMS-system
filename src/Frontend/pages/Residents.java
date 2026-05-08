/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Frontend.pages;

import Backend.DB_FETCHER;
import Backend.DB_INSERT;
import Backend.DB_UPDATE;
import Frontend.forms.ResidentRegForm;
import Utilities.SimpleButtonEditor;
import Utilities.SimpleButtonRenderer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import raven.toast.Notifications;

/**
 *
 * @author User
 */
public class Residents extends javax.swing.JInternalFrame{
    private DefaultTableModel tableModel;
    /**
     * Creates new form Dashboard
     */
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public Residents() {
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
        loadDataToTable();
    }
    
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        ResultSet result = DB_FETCHER.fetchAllResidents();
        if (result == null) return;
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        try {
            while (result.next()) {
                String fn  = result.getString("first_name");
                String mn  = result.getString("middle_name");
                String ln  = result.getString("last_name");
                String fullName = (fn
                        + (mn != null && !mn.isEmpty() ? " " + mn : "")
                        + " " + ln).trim();
                String sex    = result.getString("sex");
                String status = result.getString("status");
                String purok  = result.getString("purok");

                if (!fullName.isEmpty()) suggestions.add(fullName);
                tableModel.addRow(new Object[]{fullName, sex, status, purok, "", result.getInt("resident_id")});
            }
            modernDropdownPalette2.setItems(suggestions);
        } catch (SQLException ex) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Error loading residents.");
        }
    }
    
    public void updateButtonState(){addResident.setEnabled(true);}
    
    public void receivedDataFromTheForm(
            String firstName,
            String middleName,
            String lastName,
            String sex,
            String birthDate,
            String civilStatus,
            String nationality,
            String address,
            String purok,
            String lengthOfStay,
            String profileImage,
            String status,
            String frameStat){
        if (frameStat.equals("closed")) {
            DB_INSERT.insertResident(firstName, middleName, lastName, sex,
                    birthDate, civilStatus, nationality, address, purok,
                    lengthOfStay, profileImage, status);
            loadDataToTable();
            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Resident added successfully.");
        }
    }
    
    public void UpdateTableFromEdit(int residentID, String firstName,
            String middleName,
            String lastName,
            String sex,
            String birthDate,
            String civilStatus,
            String nationality,
            String address,
            String purok,
            String lengthOfStay,
            String profileImage,
            String status,
            String frameStat){
        if (frameStat.equals("closed")) {
            DB_UPDATE.updateResident(
                    residentID, firstName, middleName, lastName, 
                    sex,birthDate,civilStatus, nationality, 
                    address, purok, lengthOfStay, profileImage, status
            );
            loadDataToTable();
            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Resident added successfully.");
        }
    }

    private void initResTable() {
        // 1. SETUP THE MODEL & DATA
        String[] columns = {
            "NAME",
            "SEX",
            "STATUS",
            "PUROK",
            "ACTIONS",
            "ID"
        };
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the action buttons are clickable
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 5 ? Integer.class : String.class;      
            }
        };

        //resident record fetching is handled by loadDataToTable()
//        model.addRow(new Object[]{"Mater Dei", "mdc", "mdc.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Bohol Island State University", "BISU Calape", "bisu.edu.ph", "Active", ""});
//        model.addRow(new Object[]{"Cipher Society", "cphs", "gmail.com", "Inactive", ""});

        residentsTable.setModel(tableModel);
        residentsTable.getColumnModel().removeColumn(residentsTable.getColumnModel().getColumn(5));
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
        header.setDefaultRenderer(new javax.swing.table.TableCellRenderer(){
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
                if (column == 1 || column == 2 || column == 3 || column == 4) {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                } else {
                    headerLabel.setHorizontalAlignment(javax.swing.JLabel.LEFT);
                    headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                }
                
                return headerLabel;
            }
        });
        // --- NEW: CENTER CELL DATA FOR BIRTHDATE AND STATUS ---
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        residentsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        residentsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // --- NEW: FIX CUT-OFF TEXT FOR NAME AND ADDRESS ---
        javax.swing.table.DefaultTableCellRenderer paddedRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Add 10 pixels of padding to the left side of the text
                ((javax.swing.JComponent)c).setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return c;
            }
        };

        // --- THE NUCLEAR OPTION: A HEADER THAT IGNORES THE MOUSE ---
        javax.swing.table.JTableHeader customHeader = new javax.swing.table.JTableHeader(residentsTable.getColumnModel()) {
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
        residentsTable.setRowSorter(rowSorter);

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
                if (column == 1 || column == 2 || column == 3 || column == 4) {
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
        
        // Apply the padding to the Name (0) and Address (1) columns
        residentsTable.getColumnModel().getColumn(0).setCellRenderer(paddedRenderer);
        residentsTable.getColumnModel().getColumn(1).setCellRenderer(paddedRenderer);

        // 2. APPLY THE PERFECTLY CENTERED/CURVED BUTTONS TO COLUMN 4
        // (This assumes you still have the SimpleButtonRenderer and Editor classes from earlier!)
        residentsTable.getColumnModel().getColumn(4).setCellRenderer(new SimpleButtonRenderer(false));
        residentsTable.getColumnModel().getColumn(4).setCellEditor(new SimpleButtonEditor(residentsTable, this, false));

        // Column widths (total ~900px): NAME gets the widest share
        residentsTable.getColumnModel().getColumn(0).setPreferredWidth(310);
        residentsTable.getColumnModel().getColumn(0).setMinWidth(200);
        residentsTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        residentsTable.getColumnModel().getColumn(1).setMinWidth(70);
        residentsTable.getColumnModel().getColumn(2).setPreferredWidth(165);
        residentsTable.getColumnModel().getColumn(2).setMinWidth(80);
        residentsTable.getColumnModel().getColumn(3).setPreferredWidth(165);
        residentsTable.getColumnModel().getColumn(3).setMinWidth(80);
        residentsTable.getColumnModel().getColumn(4).setPreferredWidth(170);
        residentsTable.getColumnModel().getColumn(4).setMinWidth(140);

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
//    private void displayDataToTable(DefaultTableModel model) {
//        ResultSet residentData = DB_FETCHER.fetchAllResidents();
//        java.util.List<String> dynamicSuggestions = new java.util.ArrayList<>();
//        
//        try {
//            while(residentData.next()){
//                String fullName = residentData.getString("full_name");
//                
//                // Add the name to the dropdown suggestions
//                if (fullName != null && !fullName.trim().isEmpty()) {
//                    dynamicSuggestions.add(fullName);
//                }
//                
//                // Add the row to the table
//                model.addRow(new Object[]{
//                    fullName, 
//                    residentData.getString("address"), 
//                    residentData.getDate("birth_date"), 
//                    residentData.getString("status"),
//                    "" // Holds space for the Action Buttons
//                });
//            }
//            
//            // Push the names into your custom palette
//            modernDropdownPalette2.setItems(dynamicSuggestions);
//            
//        } catch (SQLException ex) {
//            Notifications.getInstance().show(Notifications.Type.WARNING, "An error occured while fetching the data");
//        }
//    }

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
        addResident = new Utilities.ModernButton();

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

        addResident.setText("New Resident");
        addResident.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addResident.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addResidentActionPerformed(evt);
            }
        });
        canvasPanel.add(addResident, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 50, 150, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void addResidentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResidentActionPerformed
        ResidentRegForm resregform = new ResidentRegForm(this);
        resregform.setVisible(true);
    }//GEN-LAST:event_addResidentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Utilities.ModernButton addResident;
    private javax.swing.JPanel canvasPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private Utilities.ModernDropdownPalette modernDropdownPalette2;
    private javax.swing.JTable residentsTable;
    private Utilities.SimpleButtonRenderer simpleButtonRenderer1;
    // End of variables declaration//GEN-END:variables
}
