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

public class RoundedInputField extends JTextField {

    // ── Icon position constants ───────────────────────────────────────────────

    public static final int ICON_LEFT  = 0;
    public static final int ICON_RIGHT = 1;

    // ── Fields ────────────────────────────────────────────────────────────────

    private int   arc          = 12;
    private Color bgColor      = Color.WHITE;
    private Color borderNormal = new Color(209, 213, 219);
    private Color borderFocus  = new Color(37,  99,  235);
    private Color borderError  = new Color(220, 38,  38);
    private Color textColor    = new Color(17,  24,  39);
    private Color placeholderColor = new Color(156, 163, 175);

    private String  placeholder   = "";
    private Icon    icon          = null;
    private int     iconPosition  = ICON_LEFT;
    private int     iconTextGap   = 8;
    private int     hPad          = 14;
    private int     vPad          = 6;

    private boolean focused = false;
    private boolean error   = false;

    // ── Constructor ───────────────────────────────────────────────────────────

    public RoundedInputField() {
        super();
        init();
    }

    public RoundedInputField(String placeholder) {
        super();
        this.placeholder = placeholder;
        init();
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    private void init() {
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(textColor);
        setCaretColor(borderFocus);
        updateInsets();

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true;  repaint(); }
            @Override public void focusLost (FocusEvent e)  { focused = false; repaint(); }
        });
    }

    private void updateInsets() {
        int left  = hPad;
        int right = hPad;
        int iconW = (icon != null) ? icon.getIconWidth() + iconTextGap : 0;

        if (icon != null && iconPosition == ICON_LEFT)  left  += iconW;
        if (icon != null && iconPosition == ICON_RIGHT) right += iconW;

        setBorder(BorderFactory.createEmptyBorder(vPad, left, vPad, right));
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);

        // ── Subtle drop shadow ────────────────────────────────────────────────
        if (!error && !focused) {
            for (int i = 3; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 5 * i));
                g2.fill(new RoundRectangle2D.Float(i / 2f, i, w - i, h - i + 1, arc, arc));
            }
        }

        // ── Background ────────────────────────────────────────────────────────
        g2.setColor(bgColor);
        g2.fill(shape);

        // ── Border ────────────────────────────────────────────────────────────
        Color border;
        float strokeW;
        if (error) {
            border  = borderError;
            strokeW = 2f;
        } else if (focused) {
            border  = borderFocus;
            strokeW = 2f;
        } else {
            border  = borderNormal;
            strokeW = 1.5f;
        }

        // Focus glow
        if (focused && !error) {
            g2.setColor(new Color(borderFocus.getRed(), borderFocus.getGreen(), borderFocus.getBlue(), 30));
            g2.setStroke(new BasicStroke(strokeW + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));
        }

        g2.setColor(border);
        g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Float(strokeW / 2f, strokeW / 2f,
                                           w - strokeW,  h - strokeW, arc, arc));

        g2.dispose();

        // ── Text & caret (delegated to Swing) ─────────────────────────────────
        super.paintComponent(g);

        // ── Placeholder ───────────────────────────────────────────────────────
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            Graphics2D pg = (Graphics2D) g.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            pg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            pg.setColor(placeholderColor);
            pg.setFont(getFont());
            Insets ins = getInsets();
            FontMetrics fm = pg.getFontMetrics();
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
            pg.drawString(placeholder, ins.left, ty);
            pg.dispose();
        }

        // ── Icon ──────────────────────────────────────────────────────────────
        if (icon != null) {
            int iy  = (h - icon.getIconHeight()) / 2;
            int ix;
            if (iconPosition == ICON_LEFT) {
                ix = hPad / 2 + 4;
            } else {
                ix = w - hPad / 2 - 4 - icon.getIconWidth();
            }
            icon.paintIcon(this, g, ix, iy);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        // handled in paintComponent
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        FontMetrics fm = getFontMetrics(getFont());
        d.height = Math.max(d.height, vPad * 2 + fm.getAscent() + fm.getDescent() + fm.getLeading() + fm.getDescent());
        return d;
    }

    // ── JavaBeans Properties ──────────────────────────────────────────────────

    public int   getArc()                        { return arc; }
    public void  setArc(int arc)                 { this.arc = arc; repaint(); }

    public Color getBgColor()                    { return bgColor; }
    public void  setBgColor(Color c)             { this.bgColor = c; repaint(); }

    public Color getBorderNormal()               { return borderNormal; }
    public void  setBorderNormal(Color c)        { this.borderNormal = c; repaint(); }

    public Color getBorderFocus()                { return borderFocus; }
    public void  setBorderFocus(Color c)         { this.borderFocus = c; setCaretColor(c); repaint(); }

    public Color getBorderError()                { return borderError; }
    public void  setBorderError(Color c)         { this.borderError = c; repaint(); }

    public Color getTextColor()                  { return textColor; }
    public void  setTextColor(Color c)           { this.textColor = c; setForeground(c); repaint(); }

    public Color getPlaceholderColor()           { return placeholderColor; }
    public void  setPlaceholderColor(Color c)    { this.placeholderColor = c; repaint(); }

    public String getPlaceholder()               { return placeholder; }
    public void   setPlaceholder(String s)       { this.placeholder = s; repaint(); }

    public Icon  getFieldIcon()                  { return icon; }
    public void  setFieldIcon(Icon icon)         { this.icon = icon; updateInsets(); repaint(); }

    public int   getIconPosition()               { return iconPosition; }
    public void  setIconPosition(int pos)        { this.iconPosition = pos; updateInsets(); repaint(); }

    public int   getIconTextGap()                { return iconTextGap; }
    public void  setIconTextGap(int gap)         { this.iconTextGap = gap; updateInsets(); repaint(); }

    public int   getHPad()                       { return hPad; }
    public void  setHPad(int p)                  { this.hPad = p; updateInsets(); repaint(); }

    public int   getVPad()                       { return vPad; }
    public void  setVPad(int p)                  { this.vPad = p; updateInsets(); repaint(); }

    public boolean isError()                     { return error; }
    public void    setError(boolean b)           { this.error = b; repaint(); }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
