/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

/**
 *
 * @author User
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ModernButton extends JButton {

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum Variant      { FILLED, OUTLINED, TEXT }
    public enum IconPosition { LEFT, RIGHT }

    // ── Fields ────────────────────────────────────────────────────────────────

    private Variant      variant      = Variant.FILLED;
    private IconPosition iconPosition = IconPosition.LEFT;

    private int     arc        = 12;
    private int     iconGap    = 8;
    private boolean showShadow = true;

    private Color buttonColor = new Color(37,  99,  235);
    private Color hoverColor  = new Color(29,  78,  216);
    private Color pressColor  = new Color(30,  64,  175);

    private boolean hov = false;
    private boolean prs = false;

    // ── Constructors ──────────────────────────────────────────────────────────

    public ModernButton()                           { super();            init(); }
    public ModernButton(String text)                { super(text);        init(); }
    public ModernButton(String text, Icon icon)     { super(text, icon);  init(); }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(Color.WHITE);
        setPreferredSize(new Dimension(140, 40));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { hov = true;              repaint(); }
            @Override public void mouseExited(MouseEvent e)   { hov = false; prs = false; repaint(); }
            @Override public void mousePressed(MouseEvent e)  { prs = true;              repaint(); }
            @Override public void mouseReleased(MouseEvent e) { prs = false;             repaint(); }
        });
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth(), h = getHeight();
        boolean en = isEnabled();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        switch (variant) {
            case FILLED:   paintFilled(g2, w, h, en);   break;
            case OUTLINED: paintOutlined(g2, w, h, en); break;
            case TEXT:     paintTextBg(g2, w, h, en);   break;
        }

        paintContent(g2, w, h, en);
        g2.dispose();
    }

    // ── Variant backgrounds ───────────────────────────────────────────────────

    private void paintFilled(Graphics2D g2, int w, int h, boolean en) {
        if (showShadow && !prs && en) {
            for (int i = 4; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6 * i));
                g2.fill(new RoundRectangle2D.Float(i / 2f, i + 1, w - i, h - i, arc, arc));
            }
        }

        Color bg;
        if (!en)     bg = new Color(156, 163, 175);
        else if (prs) bg = pressColor;
        else if (hov) bg = hoverColor;
        else          bg = buttonColor;

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        if (en && !prs) {
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h / 2f, arc, arc));
        }
    }

    private void paintOutlined(Graphics2D g2, int w, int h, boolean en) {
        int alpha = prs ? 24 : hov ? 12 : 0;
        if (alpha > 0 && en) {
            g2.setColor(new Color(buttonColor.getRed(), buttonColor.getGreen(), buttonColor.getBlue(), alpha));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        }
        g2.setColor(en ? buttonColor : new Color(156, 163, 175));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));
        g2.setStroke(new BasicStroke(1f));
    }

    private void paintTextBg(Graphics2D g2, int w, int h, boolean en) {
        int alpha = prs ? 20 : hov ? 10 : 0;
        if (alpha > 0 && en) {
            g2.setColor(new Color(buttonColor.getRed(), buttonColor.getGreen(), buttonColor.getBlue(), alpha));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        }
    }

    // ── Content (icon + text) ─────────────────────────────────────────────────

    private void paintContent(Graphics2D g2, int w, int h, boolean en) {
        Icon   icon = getIcon();
        String text = getText();

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();

        int iconW = (icon != null) ? icon.getIconWidth()  : 0;
        int iconH = (icon != null) ? icon.getIconHeight() : 0;
        int textW = (text != null && !text.isEmpty()) ? fm.stringWidth(text) : 0;
        int gap   = (icon != null && textW > 0) ? iconGap : 0;
        int total = iconW + gap + textW;

        int cx   = Math.max(8, (w - total) / 2);
        int cy   = h / 2;
        int textY = cy + (fm.getAscent() - fm.getDescent()) / 2;

        Color fg;
        if (!en) {
            fg = variant == Variant.FILLED
                    ? new Color(255, 255, 255, 140)
                    : new Color(156, 163, 175);
        } else {
            fg = variant == Variant.FILLED ? getForeground() : buttonColor;
        }

        if (iconPosition == IconPosition.LEFT) {
            if (icon != null) {
                icon.paintIcon(this, g2, cx, cy - iconH / 2);
                cx += iconW + gap;
            }
            if (textW > 0) {
                g2.setColor(fg);
                g2.drawString(text, cx, textY);
            }
        } else {
            if (textW > 0) {
                g2.setColor(fg);
                g2.drawString(text, cx, textY);
                cx += textW + gap;
            }
            if (icon != null) {
                icon.paintIcon(this, g2, cx, cy - iconH / 2);
            }
        }
    }

    // ── JavaBeans Properties ──────────────────────────────────────────────────

    public Variant      getVariant()                    { return variant; }
    public void         setVariant(Variant v)           { this.variant = v; repaint(); }

    public IconPosition getIconPosition()               { return iconPosition; }
    public void         setIconPosition(IconPosition p) { this.iconPosition = p; repaint(); }

    public int          getArc()                        { return arc; }
    public void         setArc(int v)                   { this.arc = v; repaint(); }

    public int          getIconGap()                    { return iconGap; }
    public void         setIconGap(int v)               { this.iconGap = v; repaint(); }

    public boolean      isShowShadow()                  { return showShadow; }
    public void         setShowShadow(boolean v)        { this.showShadow = v; repaint(); }

    public Color        getButtonColor()                { return buttonColor; }
    public void         setButtonColor(Color c) {
        this.buttonColor = c;
        this.hoverColor  = c.darker();
        this.pressColor  = c.darker().darker();
        repaint();
    }

    public Color        getHoverColor()                 { return hoverColor; }
    public void         setHoverColor(Color c)          { this.hoverColor = c; repaint(); }

    public Color        getPressColor()                 { return pressColor; }
    public void         setPressColor(Color c)          { this.pressColor = c; repaint(); }
}
