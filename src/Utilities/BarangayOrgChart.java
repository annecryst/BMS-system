/*
 * Custom JPanel — Barangay Officials Hierarchy (Org Chart)
 * Modern style with built-in scrolling.
 *
 * Usage:
 *   BarangayOrgChart chart = new BarangayOrgChart();            // defaults
 *   BarangayOrgChart chart = new BarangayOrgChart(7, 6, 5, 4); // explicit counts
 *
 *   chart.getCaptainCard().setOfficialName("Juan dela Cruz");
 *   chart.getCaptainCard().setProfileImage("src/LocalStorage/images/juan.jpg");
 *   chart.getKagawadCards().get(0).setOfficialName("Maria Santos");
 *
 *   somePanel.add(chart); // already scrollable — just add it
 */
package Utilities;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class BarangayOrgChart extends JPanel {

    // ── Design palette ────────────────────────────────────────────────────────

    private static final Color BG            = new Color(221, 233, 235);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color CARD_BORDER   = new Color(160, 195, 210, 140);
    private static final Color ACCENT_A      = new Color( 56, 116, 255);
    private static final Color ACCENT_B      = new Color( 99,  51, 255);
    private static final Color ACCENT_LINE   = new Color( 80, 130, 190, 200);
    private static final Color NAME_FG       = new Color( 15,  23,  42);
    private static final Color TITLE_FG      = new Color( 30,  80, 180);
    private static final Color SECTION_FG    = new Color( 20,  55, 120);
    private static final Color SCROLL_TRACK  = new Color(180, 200, 210,  80);
    private static final Color SCROLL_THUMB  = new Color( 90, 130, 160, 160);

    private static final Font NAME_FONT    = new Font("Segoe UI", Font.BOLD,   10);
    private static final Font TITLE_FONT   = new Font("Segoe UI", Font.ITALIC,  9);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD,   10);
    private static final Font GROUP_FONT   = new Font("Segoe UI", Font.BOLD,   10);
    private static final Font BADGE_FONT   = new Font("Segoe UI", Font.BOLD,    9);

    private static final int CARD_W   =  90;
    private static final int CARD_H   =  95;
    private static final int IMG_SIZE =  42;

    // ── OfficialCard ──────────────────────────────────────────────────────────

    public static class OfficialCard extends JPanel {

        private final CircleImagePanel imagePanel;
        private final JLabel           nameLabel;
        private final JLabel           titleLabel;

        public OfficialCard(String defaultTitle) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setPreferredSize(new Dimension(CARD_W, CARD_H));
            setMaximumSize(new Dimension(CARD_W, CARD_H));
            setMinimumSize(new Dimension(CARD_W, CARD_H));

            imagePanel = new CircleImagePanel();
            imagePanel.setPreferredSize(new Dimension(IMG_SIZE, IMG_SIZE));
            imagePanel.setMaximumSize(new Dimension(IMG_SIZE, IMG_SIZE));
            imagePanel.setMinimumSize(new Dimension(IMG_SIZE, IMG_SIZE));
            imagePanel.setAlignmentX(CENTER_ALIGNMENT);
            imagePanel.setClickToUpload(true);

            nameLabel = new JLabel("— Name —", SwingConstants.CENTER);
            nameLabel.setFont(NAME_FONT);
            nameLabel.setForeground(NAME_FG);
            nameLabel.setAlignmentX(CENTER_ALIGNMENT);

            titleLabel = new JLabel(
                    "<html><center>" + defaultTitle + "</center></html>",
                    SwingConstants.CENTER);
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(TITLE_FG);
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);

            add(Box.createVerticalStrut(8));
            add(centeredWrap(imagePanel));
            add(Box.createVerticalStrut(3));
            add(centeredWrap(nameLabel));
            add(Box.createVerticalStrut(1));
            add(centeredWrap(titleLabel));
            add(Box.createVerticalGlue());
        }

        private JPanel centeredWrap(JComponent c) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            p.setOpaque(false);
            p.add(c);
            return p;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

            int cw = w - 5, ch = h - 7;

            // Layered drop shadow
            for (int i = 5; i >= 1; i--) {
                g2.setColor(new Color(0, 10, 60, 6 * i));
                g2.fillRoundRect(i, i + 2, cw, ch, 20, 20);
            }

            // Glass card background
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, cw, ch, 20, 20);

            // Gradient header strip
            Shape savedClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(0, 0, cw, ch, 20, 20));
            GradientPaint header = new GradientPaint(0, 0, ACCENT_A, cw, 0, ACCENT_B);
            g2.setPaint(header);
            g2.fillRect(0, 0, cw, 9);
            g2.setClip(savedClip);

            // Subtle inner glow border
            g2.setColor(CARD_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, cw, ch, 20, 20);

            g2.dispose();
        }

        // ── Public API ────────────────────────────────────────────────────────

        public void setOfficialName(String name) {
            nameLabel.setText(name == null || name.isBlank() ? "— Name —" : name);
            repaint();
        }

        public void setOfficialTitle(String title) {
            titleLabel.setText("<html><center>" + (title != null ? title : "") + "</center></html>");
            repaint();
        }

        public void setProfileImage(String path) { imagePanel.loadFromStoredPath(path); }
        public void clearProfileImage()          { imagePanel.clearImage(); }
        public void setClickToUpload(boolean b)  { imagePanel.setClickToUpload(b); }
        public CircleImagePanel getImagePanel()  { return imagePanel; }
        public JLabel getNameLabel()             { return nameLabel; }
        public JLabel getTitleLabel()            { return titleLabel; }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private OfficialCard       captainCard;
    private List<OfficialCard> kagawadCards   = new ArrayList<>();
    private OfficialCard       skChairCard;
    private OfficialCard       secretaryCard;
    private OfficialCard       treasurerCard;
    private List<OfficialCard> tanodCards     = new ArrayList<>();
    private List<OfficialCard> luponCards     = new ArrayList<>();
    private List<OfficialCard> volunteerCards = new ArrayList<>();

    // ── Constructors ──────────────────────────────────────────────────────────

    public BarangayOrgChart() {
        this(7, 3, 3, 3);
    }

    public BarangayOrgChart(int kagawadCount, int tanodCount, int luponCount, int volunteerCount) {
        setLayout(new BorderLayout());
        setOpaque(false);
        JPanel content = buildContent(kagawadCount, tanodCount, luponCount, volunteerCount);
        JScrollPane scroll = buildScrollPane(content);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Build content panel ───────────────────────────────────────────────────

    private JPanel buildContent(int kagawadCount, int tanodCount, int luponCount, int volCount) {
        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setOpaque(false);

        root.add(Box.createVerticalStrut(8));

        // ── Tier 1 : Punong Barangay ──────────────────────────────────────────
        root.add(tierLabel("✦  Punong Barangay (Barangay Captain)  ✦"));
        root.add(Box.createVerticalStrut(4));
        captainCard = new OfficialCard("Punong Barangay");
        root.add(flowRow(captainCard));
        root.add(connector(14));

        // ── Tier 2a : Sangguniang Barangay Members (Kagawad) ─────────────────
        root.add(tierLabel("Sangguniang Barangay Members (Kagawad)"));
        root.add(Box.createVerticalStrut(4));
        JPanel kagawadRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        kagawadRow.setOpaque(false);
        for (int i = 0; i < kagawadCount; i++) {
            OfficialCard c = new OfficialCard("Kagawad");
            kagawadCards.add(c);
            kagawadRow.add(c);
        }
        root.add(kagawadRow);
        root.add(Box.createVerticalStrut(5));

        // ── Tier 2b : SK Chair + Secretary + Treasurer ────────────────────────
        root.add(tierLabel("Sangguniang Kabataan (SK)  ·  Secretary  ·  Treasurer"));
        root.add(Box.createVerticalStrut(4));
        JPanel keyRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        keyRow.setOpaque(false);
        skChairCard   = new OfficialCard("<html><center>SK Chairperson</center></html>");
        secretaryCard = new OfficialCard("Barangay Secretary");
        treasurerCard = new OfficialCard("Barangay Treasurer");
        keyRow.add(skChairCard);
        keyRow.add(secretaryCard);
        keyRow.add(treasurerCard);
        root.add(keyRow);
        root.add(connector(14));

        // ── Bottom tier: 3 groups side by side ───────────────────────────────
        JPanel bottomSection = new JPanel(new GridLayout(1, 3, 10, 0));
        bottomSection.setOpaque(false);

        // Tanod group
        JPanel tanodGroup = buildGroup("Barangay Tanod");
        JPanel tanodCards_ = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        tanodCards_.setOpaque(false);
        for (int i = 0; i < tanodCount; i++) {
            OfficialCard c = new OfficialCard("Barangay Tanod");
            tanodCards.add(c); tanodCards_.add(c);
        }
        tanodGroup.add(tanodCards_);
        bottomSection.add(tanodGroup);

        // Lupon group
        JPanel luponGroup = buildGroup("Lupon Tagapamayapa");
        JPanel luponCards_ = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        luponCards_.setOpaque(false);
        for (int i = 0; i < luponCount; i++) {
            OfficialCard c = new OfficialCard("Lupon Member");
            luponCards.add(c); luponCards_.add(c);
        }
        luponGroup.add(luponCards_);
        bottomSection.add(luponGroup);

        // Volunteers group
        JPanel volGroup = buildGroup("Health Workers / Daycare / Volunteers");
        JPanel volCards_ = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        volCards_.setOpaque(false);
        for (int i = 0; i < volCount; i++) {
            OfficialCard c = new OfficialCard("Volunteer");
            volunteerCards.add(c); volCards_.add(c);
        }
        volGroup.add(volCards_);
        bottomSection.add(volGroup);

        root.add(bottomSection);
        root.add(Box.createVerticalStrut(12));

        return root;
    }

    // ── Scroll pane with modern thin scrollbar ────────────────────────────────

    private JScrollPane buildScrollPane(JPanel content) {
        JScrollPane sp = new JScrollPane(content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getHorizontalScrollBar().setUnitIncrement(18);
        applyModernScrollBar(sp.getVerticalScrollBar());
        applyModernScrollBar(sp.getHorizontalScrollBar());
        return sp;
    }

    private void applyModernScrollBar(JScrollBar bar) {
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(8, 8));
        bar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor      = SCROLL_THUMB;
                trackColor      = SCROLL_TRACK;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 6, 6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, 6, 6);
                g2.dispose();
            }
        });
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private JPanel flowRow(JComponent... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        p.setOpaque(false);
        for (JComponent c : comps) p.add(c);
        return p;
    }

    private JPanel tierLabel(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(text);
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(SECTION_FG);
        p.add(lbl);
        return p;
    }

    private JPanel connector(int height) {
        JPanel c = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_LINE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 1f, new float[]{7, 5}, 0));
                int cx = getWidth() / 2;
                g2.drawLine(cx, 0, cx, getHeight());
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setPreferredSize(new Dimension(10, height));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return c;
    }

    private JPanel buildGroup(String title) {
        JPanel group = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 90));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(new Color(80, 130, 190, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(GROUP_FONT);
        lbl.setForeground(SECTION_FG);
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        group.add(Box.createVerticalStrut(6));
        group.add(centeredWrapStatic(lbl));
        group.add(Box.createVerticalStrut(4));
        return group;
    }

    private static JPanel centeredWrapStatic(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public OfficialCard       getCaptainCard()        { return captainCard; }
    public List<OfficialCard> getKagawadCards()       { return new ArrayList<>(kagawadCards); }
    public OfficialCard       getSkChairCard()        { return skChairCard; }
    public OfficialCard       getSecretaryCard()      { return secretaryCard; }
    public OfficialCard       getTreasurerCard()      { return treasurerCard; }
    public List<OfficialCard> getTanodCards()         { return new ArrayList<>(tanodCards); }
    public List<OfficialCard> getLuponCards()         { return new ArrayList<>(luponCards); }
    public List<OfficialCard> getVolunteerCards()     { return new ArrayList<>(volunteerCards); }
}
