/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Frontend;

import Frontend.pages.Blotter;
import Frontend.pages.Certificates;
import Frontend.pages.Dashboard;
import Frontend.pages.Officials;
import Frontend.pages.Residents;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import raven.toast.Notifications;

/**
 *
 * @author User
 */
public class Homepage extends javax.swing.JFrame {
    private String username="Unknown";
    /**
     * Creates new form homepage
     */
    public Homepage() {
        initComponents();
        SwingUtilities.invokeLater(() -> {
            Notifications.getInstance().setJFrame(this);
            Notifications.getInstance().show(
                Notifications.Type.INFO,
                Notifications.Location.TOP_CENTER,
                "Good day! Let’s get things started"
            );
        });
        ImageIcon icon = new ImageIcon("src/assets/maitum_logo8.png");
        Image image = icon.getImage();
        this.setTitle("BMS");
        this.setIconImage(image);
        installSignOutAnimation(signOutLbl);
        setInternalFramePage(new Dashboard());
//        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Good day! Let’s get things started");
    }
    
    public void setUsername(String usn){
        this.username = usn;
        currentUserLabel.setText("Welcome, "+username);
    }
    
    private void toLogOut(){
        this.dispose();
    }

    private float signOutHover = 0f;
    private Timer signOutTimer;
    private final Color baseColor  = new Color(180, 180, 180);
    private final Color hoverColor = new Color(255, 85, 85);
    // Load SVG arrow once
    private final FlatSVGIcon arrowIcon = new FlatSVGIcon("assets/icons/arrow-right.svg", 20, 20);
    private void installSignOutAnimation(JLabel label) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setOpaque(false);

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                animate(label, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animate(label, false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                label.setForeground(hoverColor);
                System.out.println("To Logout");
                LoginFrame loginPage = new LoginFrame();
                loginPage.setVisible(true);
                toLogOut();
            }
        });

        label.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            protected void paintEnabledText(JLabel l, Graphics g, String text, int x, int y) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Text color blend
                Color blended = blend(baseColor, hoverColor, signOutHover);
                g2.setColor(blended);

                int slide = (int) (signOutHover * 6);
                g2.drawString(text, x + slide, y);

                // --- SVG ARROW ROTATION ---
                FontMetrics fm = g2.getFontMetrics();
                int iconX = x + slide + fm.stringWidth(text) + 6;
                int iconY = y - arrowIcon.getIconHeight() + 2;

                double angle = Math.toRadians(90 * signOutHover); // 0° → 90°

                Graphics2D ig = (Graphics2D) g2.create();
                ig.translate(
                    iconX + arrowIcon.getIconWidth() / 2.0,
                    iconY + arrowIcon.getIconHeight() / 2.0
                );
                ig.rotate(angle);
                ig.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, signOutHover));

                arrowIcon.paintIcon(l, ig,
                        -arrowIcon.getIconWidth() / 2,
                        -arrowIcon.getIconHeight() / 2);

                ig.dispose();
                g2.dispose();
            }
        });
    }
    
    private void animate(JLabel label, boolean enter) {
        if (signOutTimer != null && signOutTimer.isRunning())
            signOutTimer.stop();

        signOutTimer = new Timer(16, e -> {
            signOutHover += enter ? 0.08f : -0.08f;
            signOutHover = Math.max(0f, Math.min(1f, signOutHover));

            label.repaint();

            if (signOutHover == 0f || signOutHover == 1f)
                signOutTimer.stop();
        });
        signOutTimer.start();
    }

    private Color blend(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed()   + t * (c2.getRed()   - c1.getRed()));
        int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue()  + t * (c2.getBlue()  - c1.getBlue()));
        return new Color(r, g, b);
    }
    
    private void setInternalFramePage(JInternalFrame page){
        pageHolderDPane.setSelectedFrame(null);
        pageHolderDPane.setLayout(null);
        page.setBounds(0,0, pageHolderDPane.getWidth(), pageHolderDPane.getHeight());
        page.setVisible(true);
        pageHolderDPane.add(page);
        page.toFront();
        try {
            page.setSelected(true);
        } catch (PropertyVetoException ex) {
            Notifications.getInstance().show(Notifications.Type.ERROR, ex.getMessage());
        }
    }

    private int selectedOption=0;
    private void setColoredOnSelectedOption(int index_op){
        switch(index_op){
            case 1 -> {
                dashboardPanelBtn.setBackground(Color.decode("#4c9ff6"));
                selectedOption = 1;
                residentsPanelBtn.setBackground(Color.decode("#0B3769"));
                certPanelBtn.setBackground(Color.decode("#0B3769"));
                blotterPanelBtn.setBackground(Color.decode("#0B3769"));
                officialPanelBtn.setBackground(Color.decode("#0B3769"));
            }
            case 2 -> {
                residentsPanelBtn.setBackground(Color.decode("#4c9ff6"));
                selectedOption = 2;
                dashboardPanelBtn.setBackground(Color.decode("#0B3769"));
                certPanelBtn.setBackground(Color.decode("#0B3769"));
                blotterPanelBtn.setBackground(Color.decode("#0B3769"));
                officialPanelBtn.setBackground(Color.decode("#0B3769"));
            }
            case 3 -> {
                certPanelBtn.setBackground(Color.decode("#4c9ff6"));
                selectedOption = 3;
                dashboardPanelBtn.setBackground(Color.decode("#0B3769"));
                residentsPanelBtn.setBackground(Color.decode("#0B3769"));
                blotterPanelBtn.setBackground(Color.decode("#0B3769"));
                officialPanelBtn.setBackground(Color.decode("#0B3769"));
            }
            case 4 ->{
                blotterPanelBtn.setBackground(Color.decode("#4c9ff6"));
                selectedOption = 4;
                dashboardPanelBtn.setBackground(Color.decode("#0B3769"));
                residentsPanelBtn.setBackground(Color.decode("#0B3769"));
                certPanelBtn.setBackground(Color.decode("#0B3769"));
                officialPanelBtn.setBackground(Color.decode("#0B3769"));
            }
            case 5 ->{
                officialPanelBtn.setBackground(Color.decode("#4c9ff6"));
                selectedOption = 5;
                dashboardPanelBtn.setBackground(Color.decode("#0B3769"));
                residentsPanelBtn.setBackground(Color.decode("#0B3769"));
                certPanelBtn.setBackground(Color.decode("#0B3769"));
                blotterPanelBtn.setBackground(Color.decode("#0B3769"));
            }
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pageHolderDPane = new javax.swing.JDesktopPane();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        currentPage = new javax.swing.JLabel();
        currentUserLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        sideBarPanel = new javax.swing.JPanel();
        dashboardPanelBtn = new Utilities.PanelRound();
        dashbordIcon = new javax.swing.JLabel();
        dashboardLabel = new javax.swing.JLabel();
        residentsPanelBtn = new Utilities.PanelRound();
        residentsIcon = new javax.swing.JLabel();
        reslabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        certPanelBtn = new Utilities.PanelRound();
        certsIcon = new javax.swing.JLabel();
        certLabel = new javax.swing.JLabel();
        blotterPanelBtn = new Utilities.PanelRound();
        blotterIcon = new javax.swing.JLabel();
        blotterLabel = new javax.swing.JLabel();
        officialPanelBtn = new Utilities.PanelRound();
        officialsIcon = new javax.swing.JLabel();
        officialsLabel = new javax.swing.JLabel();
        signOutLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(11, 55, 105));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        pageHolderDPane.setFocusable(false);

        javax.swing.GroupLayout pageHolderDPaneLayout = new javax.swing.GroupLayout(pageHolderDPane);
        pageHolderDPane.setLayout(pageHolderDPaneLayout);
        pageHolderDPaneLayout.setHorizontalGroup(
            pageHolderDPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 942, Short.MAX_VALUE)
        );
        pageHolderDPaneLayout.setVerticalGroup(
            pageHolderDPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 624, Short.MAX_VALUE)
        );

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/admin.png"))); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/arrow_right_40dp_000000_FILL0_wght400_GRAD0_opsz40.png"))); // NOI18N

        currentPage.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        currentPage.setText("Dashboard");

        currentUserLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        currentUserLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        currentUserLabel.setText("Welcome, Unknown");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pageHolderDPane)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentPage, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(currentUserLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentUserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentPage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pageHolderDPane))
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/maitum_logo6_2.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("MANAGEMENT SYSTEM");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("BARANGAY");

        sideBarPanel.setBackground(new java.awt.Color(11, 55, 105));

        dashboardPanelBtn.setBackground(new java.awt.Color(11, 55, 105));
        dashboardPanelBtn.setPreferredSize(new java.awt.Dimension(177, 44));
        dashboardPanelBtn.setRoundBottomLeft(15);
        dashboardPanelBtn.setRoundBottomRight(15);
        dashboardPanelBtn.setRoundTopLeft(15);
        dashboardPanelBtn.setRoundTopRight(15);

        dashbordIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashbordIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/dashboard.png"))); // NOI18N
        dashbordIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dashbordIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashbordIconMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashbordIconMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashbordIconMouseExited(evt);
            }
        });

        dashboardLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        dashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
        dashboardLabel.setText("Dashboard");
        dashboardLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout dashboardPanelBtnLayout = new javax.swing.GroupLayout(dashboardPanelBtn);
        dashboardPanelBtn.setLayout(dashboardPanelBtnLayout);
        dashboardPanelBtnLayout.setHorizontalGroup(
            dashboardPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardPanelBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashbordIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dashboardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );
        dashboardPanelBtnLayout.setVerticalGroup(
            dashboardPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dashboardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dashboardPanelBtnLayout.createSequentialGroup()
                .addComponent(dashbordIcon)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        residentsPanelBtn.setBackground(new java.awt.Color(11, 55, 105));
        residentsPanelBtn.setPreferredSize(new java.awt.Dimension(167, 44));
        residentsPanelBtn.setRoundBottomLeft(15);
        residentsPanelBtn.setRoundBottomRight(15);
        residentsPanelBtn.setRoundTopLeft(15);
        residentsPanelBtn.setRoundTopRight(15);

        residentsIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        residentsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/residents.png"))); // NOI18N
        residentsIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                residentsIconMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                residentsIconMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                residentsIconMouseExited(evt);
            }
        });

        reslabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reslabel.setForeground(new java.awt.Color(255, 255, 255));
        reslabel.setText("Residents");
        reslabel.setPreferredSize(new java.awt.Dimension(92, 25));
        reslabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reslabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                reslabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reslabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout residentsPanelBtnLayout = new javax.swing.GroupLayout(residentsPanelBtn);
        residentsPanelBtn.setLayout(residentsPanelBtnLayout);
        residentsPanelBtnLayout.setHorizontalGroup(
            residentsPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentsPanelBtnLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(residentsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reslabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        residentsPanelBtnLayout.setVerticalGroup(
            residentsPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(residentsIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(reslabel, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
        );

        certPanelBtn.setBackground(new java.awt.Color(11, 55, 105));
        certPanelBtn.setRoundBottomLeft(15);
        certPanelBtn.setRoundBottomRight(15);
        certPanelBtn.setRoundTopLeft(15);
        certPanelBtn.setRoundTopRight(15);

        certsIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        certsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/certs.png"))); // NOI18N
        certsIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                certsIconMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                certsIconMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                certsIconMouseExited(evt);
            }
        });

        certLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        certLabel.setForeground(new java.awt.Color(255, 255, 255));
        certLabel.setText("Certificates");
        certLabel.setPreferredSize(new java.awt.Dimension(92, 25));
        certLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                certLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                certLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                certLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout certPanelBtnLayout = new javax.swing.GroupLayout(certPanelBtn);
        certPanelBtn.setLayout(certPanelBtnLayout);
        certPanelBtnLayout.setHorizontalGroup(
            certPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(certPanelBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(certsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(certLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        certPanelBtnLayout.setVerticalGroup(
            certPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(certsIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(certPanelBtnLayout.createSequentialGroup()
                .addComponent(certLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        blotterPanelBtn.setBackground(new java.awt.Color(11, 55, 105));
        blotterPanelBtn.setPreferredSize(new java.awt.Dimension(167, 44));
        blotterPanelBtn.setRoundBottomLeft(15);
        blotterPanelBtn.setRoundBottomRight(15);
        blotterPanelBtn.setRoundTopLeft(15);
        blotterPanelBtn.setRoundTopRight(15);

        blotterIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        blotterIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/blotter.png"))); // NOI18N
        blotterIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                blotterIconMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                blotterIconMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                blotterIconMouseExited(evt);
            }
        });

        blotterLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        blotterLabel.setForeground(new java.awt.Color(255, 255, 255));
        blotterLabel.setText("Blotter");
        blotterLabel.setPreferredSize(new java.awt.Dimension(92, 25));
        blotterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                blotterLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                blotterLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                blotterLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout blotterPanelBtnLayout = new javax.swing.GroupLayout(blotterPanelBtn);
        blotterPanelBtn.setLayout(blotterPanelBtnLayout);
        blotterPanelBtnLayout.setHorizontalGroup(
            blotterPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(blotterPanelBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(blotterIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blotterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        blotterPanelBtnLayout.setVerticalGroup(
            blotterPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(blotterIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
            .addComponent(blotterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        officialPanelBtn.setBackground(new java.awt.Color(11, 55, 105));
        officialPanelBtn.setPreferredSize(new java.awt.Dimension(167, 44));
        officialPanelBtn.setRoundBottomLeft(15);
        officialPanelBtn.setRoundBottomRight(15);
        officialPanelBtn.setRoundTopLeft(15);
        officialPanelBtn.setRoundTopRight(15);

        officialsIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        officialsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/officials.png"))); // NOI18N
        officialsIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                officialsIconMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                officialsIconMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                officialsIconMouseExited(evt);
            }
        });

        officialsLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        officialsLabel.setForeground(new java.awt.Color(255, 255, 255));
        officialsLabel.setText("Officials Info");
        officialsLabel.setPreferredSize(new java.awt.Dimension(92, 25));
        officialsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                officialsLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                officialsLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                officialsLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout officialPanelBtnLayout = new javax.swing.GroupLayout(officialPanelBtn);
        officialPanelBtn.setLayout(officialPanelBtnLayout);
        officialPanelBtnLayout.setHorizontalGroup(
            officialPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(officialPanelBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(officialsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(officialsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        officialPanelBtnLayout.setVerticalGroup(
            officialPanelBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(officialsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
            .addGroup(officialPanelBtnLayout.createSequentialGroup()
                .addComponent(officialsIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        signOutLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        signOutLbl.setForeground(new java.awt.Color(255, 255, 255));
        signOutLbl.setText("SignOut");

        javax.swing.GroupLayout sideBarPanelLayout = new javax.swing.GroupLayout(sideBarPanel);
        sideBarPanel.setLayout(sideBarPanelLayout);
        sideBarPanelLayout.setHorizontalGroup(
            sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sideBarPanelLayout.createSequentialGroup()
                        .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sideBarPanelLayout.createSequentialGroup()
                                .addGroup(sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(residentsPanelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                                    .addComponent(dashboardPanelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(certPanelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(blotterPanelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                                    .addComponent(officialPanelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                                .addGap(13, 13, 13))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sideBarPanelLayout.createSequentialGroup()
                        .addComponent(signOutLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))))
        );
        sideBarPanelLayout.setVerticalGroup(
            sideBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarPanelLayout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dashboardPanelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(residentsPanelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(certPanelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blotterPanelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(officialPanelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(signOutLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sideBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(sideBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void dashboardLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabelMouseClicked
        currentPage.setText("Dashboard");
        setColoredOnSelectedOption(1);
        setInternalFramePage(new Dashboard());
    }//GEN-LAST:event_dashboardLabelMouseClicked

    private void dashbordIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbordIconMouseClicked
        currentPage.setText("Dashboard");
        setColoredOnSelectedOption(1);
        setInternalFramePage(new Dashboard());
    }//GEN-LAST:event_dashbordIconMouseClicked

    private void dashboardLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabelMouseEntered
        dashboardPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_dashboardLabelMouseEntered

    private void dashbordIconMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbordIconMouseEntered
        dashboardPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_dashbordIconMouseEntered

    private void reslabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reslabelMouseClicked
        currentPage.setText("Residents");
       setColoredOnSelectedOption(2);
        setInternalFramePage(new Residents());
    }//GEN-LAST:event_reslabelMouseClicked

    private void residentsIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_residentsIconMouseClicked
        currentPage.setText("Residents");
        setColoredOnSelectedOption(2);
        setInternalFramePage(new Residents());
    }//GEN-LAST:event_residentsIconMouseClicked

    private void reslabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reslabelMouseEntered
        residentsPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_reslabelMouseEntered

    private void residentsIconMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_residentsIconMouseEntered
        residentsPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_residentsIconMouseEntered

    private void certsIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_certsIconMouseClicked
        currentPage.setText("Certificates");
        setColoredOnSelectedOption(3);
        setInternalFramePage(new Certificates());
    }//GEN-LAST:event_certsIconMouseClicked

    private void certsIconMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_certsIconMouseEntered
        certPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_certsIconMouseEntered

    private void certLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_certLabelMouseClicked
        currentPage.setText("Certificates");
        setColoredOnSelectedOption(3);
        setInternalFramePage(new Certificates());
    }//GEN-LAST:event_certLabelMouseClicked

    private void certLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_certLabelMouseEntered
        certPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_certLabelMouseEntered

    private void blotterIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blotterIconMouseClicked
        currentPage.setText("Blotter");
        setColoredOnSelectedOption(4);
        setInternalFramePage(new Blotter());
    }//GEN-LAST:event_blotterIconMouseClicked

    private void blotterIconMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blotterIconMouseEntered
        blotterPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_blotterIconMouseEntered

    private void blotterLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blotterLabelMouseClicked
        currentPage.setText("Blotter");
        setColoredOnSelectedOption(4);
        setInternalFramePage(new Blotter());
    }//GEN-LAST:event_blotterLabelMouseClicked

    private void blotterLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blotterLabelMouseEntered
        blotterPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_blotterLabelMouseEntered

    private void officialsIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officialsIconMouseClicked
        currentPage.setText("Officials Info");
        setColoredOnSelectedOption(5);
        setInternalFramePage(new Officials());
    }//GEN-LAST:event_officialsIconMouseClicked

    private void officialsIconMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officialsIconMouseEntered
        officialPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_officialsIconMouseEntered

    private void officialsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officialsLabelMouseClicked
        currentPage.setText("Officials Info");
        setColoredOnSelectedOption(5);
        setInternalFramePage(new Officials());
    }//GEN-LAST:event_officialsLabelMouseClicked

    private void officialsLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officialsLabelMouseEntered
        officialPanelBtn.setBackground(Color.decode("#85bff9"));
    }//GEN-LAST:event_officialsLabelMouseEntered

    private void officialsLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officialsLabelMouseExited
        officialPanelBtn.setBackground(Color.decode(selectedOption==5 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_officialsLabelMouseExited

    private void officialsIconMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officialsIconMouseExited
        officialPanelBtn.setBackground(Color.decode(selectedOption==5 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_officialsIconMouseExited

    private void blotterLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blotterLabelMouseExited
        blotterPanelBtn.setBackground(Color.decode(selectedOption==4 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_blotterLabelMouseExited

    private void blotterIconMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blotterIconMouseExited
        blotterPanelBtn.setBackground(Color.decode(selectedOption==4 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_blotterIconMouseExited

    private void certLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_certLabelMouseExited
        certPanelBtn.setBackground(Color.decode(selectedOption==3 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_certLabelMouseExited

    private void certsIconMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_certsIconMouseExited
        certPanelBtn.setBackground(Color.decode(selectedOption==3 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_certsIconMouseExited

    private void reslabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reslabelMouseExited
        residentsPanelBtn.setBackground(Color.decode(selectedOption==2 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_reslabelMouseExited

    private void residentsIconMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_residentsIconMouseExited
        residentsPanelBtn.setBackground(Color.decode(selectedOption==2 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_residentsIconMouseExited

    private void dashbordIconMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashbordIconMouseExited
        dashboardPanelBtn.setBackground(Color.decode(selectedOption==1 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_dashbordIconMouseExited

    private void dashboardLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabelMouseExited
        dashboardPanelBtn.setBackground(Color.decode(selectedOption==1 ? "#4c9ff6" : "#0B3769"));
    }//GEN-LAST:event_dashboardLabelMouseExited

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Homepage().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blotterIcon;
    private javax.swing.JLabel blotterLabel;
    private Utilities.PanelRound blotterPanelBtn;
    private javax.swing.JLabel certLabel;
    private Utilities.PanelRound certPanelBtn;
    private javax.swing.JLabel certsIcon;
    private javax.swing.JLabel currentPage;
    private javax.swing.JLabel currentUserLabel;
    private javax.swing.JLabel dashboardLabel;
    private Utilities.PanelRound dashboardPanelBtn;
    private javax.swing.JLabel dashbordIcon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private Utilities.PanelRound officialPanelBtn;
    private javax.swing.JLabel officialsIcon;
    private javax.swing.JLabel officialsLabel;
    private javax.swing.JDesktopPane pageHolderDPane;
    private javax.swing.JLabel residentsIcon;
    private Utilities.PanelRound residentsPanelBtn;
    private javax.swing.JLabel reslabel;
    private javax.swing.JPanel sideBarPanel;
    private javax.swing.JLabel signOutLbl;
    // End of variables declaration//GEN-END:variables
}
