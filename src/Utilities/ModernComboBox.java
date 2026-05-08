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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModernComboBox extends JPanel {

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // ── Listener ──────────────────────────────────────────────────────────────

    public interface SelectionListener {
        void onSelected(String value, int index);
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private List<String> items         = new ArrayList<>();
    private int          selectedIndex = -1;
    private String       placeholder   = "Select an option";

    private int   arc            = 12;
    private Color bgColor        = Color.WHITE;
    private Color borderNormal   = new Color(209, 213, 219);
    private Color borderFocus    = new Color(37,  99,  235);
    private Color textColor      = new Color(17,  24,  39);
    private Color placeholderClr = new Color(156, 163, 175);
    private Color itemHoverBg    = new Color(239, 246, 255);
    private Color itemSelectBg   = new Color(37,  99,  235);
    private Color itemSelectFg   = Color.WHITE;

    private boolean open    = false;
    private boolean hovered = false;

    private JPopupMenu        popupMenu;
    private SelectionListener selectionListener;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ModernComboBox() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, 40));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { togglePopup(); }
        });
    }

    // ── Popup logic ───────────────────────────────────────────────────────────

    private void togglePopup() {
        if (open) hidePopup();
        else      showPopup();
    }

    private void showPopup() {
        if (items.isEmpty()) return;
        open = true;
        repaint();

        final int ITEM_H  = 38;
        final int VISIBLE = Math.min(items.size(), 7);
        final int W       = Math.max(getWidth(), getPreferredSize().width);

        popupMenu = new JPopupMenu();
        popupMenu.setBackground(bgColor);
        popupMenu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        for (int i = 0; i < items.size(); i++) {
            list.add(buildRow(i, W - 10));
        }

        if (items.size() > 7) {
            JScrollPane scroll = new JScrollPane(list,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.getVerticalScrollBar().setUnitIncrement(ITEM_H);
            scroll.setPreferredSize(new Dimension(W - 10, ITEM_H * 7));
            popupMenu.add(scroll);
        } else {
            list.setPreferredSize(new Dimension(W - 10, ITEM_H * VISIBLE));
            popupMenu.add(list);
        }

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { open = false; repaint(); }
            @Override public void popupMenuCanceled(PopupMenuEvent e)            { open = false; repaint(); }
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e)   { }
        });

        popupMenu.show(this, 0, getHeight() + 2);
    }

    private void hidePopup() {
        if (popupMenu != null) { popupMenu.setVisible(false); popupMenu = null; }
        open = false;
        repaint();
    }

    // ── Row builder ───────────────────────────────────────────────────────────

    private JPanel buildRow(int idx, int rowWidth) {
        final String  text  = items.get(idx);
        final boolean isSel = (idx == selectedIndex);

        JPanel row = new JPanel(new BorderLayout()) {
            boolean rHov = false;

            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { rHov = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { rHov = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        selectedIndex = idx;
                        if (selectionListener != null) selectionListener.onSelected(text, idx);
                        hidePopup();
                    }
                });
            }

            @Override public Dimension getPreferredSize() { return new Dimension(rowWidth, 38); }
            @Override public Dimension getMaximumSize()   { return new Dimension(Integer.MAX_VALUE, 38); }
            @Override public Dimension getMinimumSize()   { return new Dimension(0, 38); }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSel) {
                    g2.setColor(itemSelectBg);
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                } else if (rHov) {
                    g2.setColor(itemHoverBg);
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(isSel ? itemSelectFg : textColor);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
        row.add(lbl, BorderLayout.CENTER);

        if (isSel) {
            JLabel chk = new JLabel("\u2713");
            chk.setFont(new Font("Segoe UI", Font.BOLD, 13));
            chk.setForeground(itemSelectFg);
            chk.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            row.add(chk, BorderLayout.EAST);
        }

        return row;
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 5 * i));
            g2.fill(new RoundRectangle2D.Float(i / 2f, i, w - i, h - i + 1f, arc, arc));
        }

        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        Color  bdr = open ? borderFocus : borderNormal;
        float  sw  = open ? 2f : 1.5f;
        if (open) {
            g2.setColor(new Color(borderFocus.getRed(), borderFocus.getGreen(), borderFocus.getBlue(), 28));
            g2.setStroke(new BasicStroke(sw + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc));
        }
        g2.setColor(bdr);
        g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Float(sw / 2f, sw / 2f, w - sw, h - sw, arc, arc));

        String display  = selectedIndex >= 0 ? items.get(selectedIndex) : placeholder;
        boolean isPlhdr = selectedIndex < 0;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(isPlhdr ? placeholderClr : textColor);
        FontMetrics fm = g2.getFontMetrics();
        int textAreaW = w - 14 - 32; // stop 32px from right edge (chevron 12px + 20px padding)
        g2.setClip(14, 0, Math.max(0, textAreaW), h);
        g2.drawString(display, 14, (h + fm.getAscent() - fm.getDescent()) / 2);
        g2.setClip(null);

        drawChevron(g2, w - 26, h / 2 - 3, open);
        g2.dispose();
    }

    private void drawChevron(Graphics2D g2, int x, int y, boolean up) {
        g2.setColor(open || hovered ? borderFocus : placeholderClr);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (up) {
            g2.drawLine(x,     y + 6, x + 6, y);
            g2.drawLine(x + 6, y,     x + 12, y + 6);
        } else {
            g2.drawLine(x,     y,     x + 6,  y + 6);
            g2.drawLine(x + 6, y + 6, x + 12, y);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void   addItem(String item)           { items.add(item); repaint(); }
    public void   setItems(List<String> list)    { items = new ArrayList<>(list); selectedIndex = -1; repaint(); }
    public void   clearItems()                   { items.clear(); selectedIndex = -1; repaint(); }
    public String getSelectedItem()              { return selectedIndex >= 0 ? items.get(selectedIndex) : null; }
    public int    getSelectedIndex()             { return selectedIndex; }
    public void   setSelectedIndex(int i)        { this.selectedIndex = i; repaint(); }
    public void   setSelectedItem(String v)      { this.selectedIndex = items.indexOf(v); repaint(); }
    public void   setSelectionListener(SelectionListener l) { this.selectionListener = l; }

    // ── JavaBeans Properties ──────────────────────────────────────────────────

    public String[] getOptions()                 { return items.toArray(new String[0]); }
    public void     setOptions(String[] opts)    { items = new ArrayList<>(Arrays.asList(opts)); selectedIndex = -1; repaint(); }

    public int    getArc()                       { return arc; }
    public void   setArc(int v)                  { this.arc = v; repaint(); }
    public Color  getBgColor()                   { return bgColor; }
    public void   setBgColor(Color c)            { this.bgColor = c; repaint(); }
    public Color  getBorderNormal()              { return borderNormal; }
    public void   setBorderNormal(Color c)       { this.borderNormal = c; repaint(); }
    public Color  getBorderFocus()               { return borderFocus; }
    public void   setBorderFocus(Color c)        { this.borderFocus = c; repaint(); }
    public Color  getTextColor()                 { return textColor; }
    public void   setTextColor(Color c)          { this.textColor = c; repaint(); }
    public Color  getPlaceholderColor()          { return placeholderClr; }
    public void   setPlaceholderColor(Color c)   { this.placeholderClr = c; repaint(); }
    public String getPlaceholder()               { return placeholder; }
    public void   setPlaceholder(String s)       { this.placeholder = s; repaint(); }
}
