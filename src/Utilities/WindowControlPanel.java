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

public class WindowControlPanel extends JPanel {

    // ── Inner button ──────────────────────────────────────────────────────────

    private static class ControlBtn extends JButton {

        enum Kind { MINIMIZE, CLOSE }

        private final Kind    kind;
        private       boolean hovered = false;
        private       boolean pressed = false;

        private static final Color CLOSE_BG       = new Color(0,   0,   0,   0);
        private static final Color CLOSE_BG_HOVER = new Color(196, 43,  28);
        private static final Color CLOSE_BG_PRESS = new Color(149, 32,  21);
        private static final Color MIN_BG         = new Color(0,   0,   0,   0);
        private static final Color MIN_BG_HOVER   = new Color(0,   0,   0,   30);
        private static final Color MIN_BG_PRESS   = new Color(0,   0,   0,   55);
        private static final Color ICON_COLOR      = new Color(30,  30,  30);

        ControlBtn(Kind kind) {
            this.kind = kind;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(46, 32));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,  RenderingHints.VALUE_STROKE_PURE);

            // ── Background (full-fill flat rect, Windows style) ───────────────
            Color bg;
            if (kind == Kind.CLOSE) {
                bg = pressed ? CLOSE_BG_PRESS : (hovered ? CLOSE_BG_HOVER : CLOSE_BG);
            } else {
                bg = pressed ? MIN_BG_PRESS : (hovered ? MIN_BG_HOVER : MIN_BG);
            }
            g2.setColor(bg);
            g2.fillRect(0, 0, w, h);

            // ── Icon color: white on red close, dark otherwise ────────────────
            boolean onRed = kind == Kind.CLOSE && (hovered || pressed);
            g2.setColor(onRed ? Color.WHITE : ICON_COLOR);
            g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            int cy = h / 2;
            int r  = 5;

            if (kind == Kind.CLOSE) {
                g2.drawLine(cx - r, cy - r, cx + r, cy + r);
                g2.drawLine(cx + r, cy - r, cx - r, cy + r);
            } else {
                g2.drawLine(cx - r, cy, cx + r, cy);
            }

            g2.dispose();
        }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final ControlBtn btnMinimize = new ControlBtn(ControlBtn.Kind.MINIMIZE);
    private final ControlBtn btnClose    = new ControlBtn(ControlBtn.Kind.CLOSE);

    private boolean useBuiltinActions = true;

    // ── Constructor ───────────────────────────────────────────────────────────

    public WindowControlPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        setPreferredSize(new Dimension(120, 32));

        add(btnMinimize);
        add(btnClose);

        btnMinimize.addActionListener(e -> {
            if (!useBuiltinActions) return;
            Container c = getTopLevelAncestor();
            if (c instanceof JInternalFrame) {
                try { ((JInternalFrame) c).setIcon(true); } catch (Exception ex) { /* ignore */ }
            } else if (c instanceof Frame) {
                ((Frame) c).setState(Frame.ICONIFIED);
            }
        });

        btnClose.addActionListener(e -> {
            if (!useBuiltinActions) return;
            Container c = getTopLevelAncestor();
            if (c instanceof JInternalFrame) {
                ((JInternalFrame) c).dispose();
            } else if (c instanceof Window) {
                ((Window) c).dispose();
            }
        });
    }

    // ── Custom listeners (override built-in behaviour) ────────────────────────

    public void addCloseListener(ActionListener l) {
        useBuiltinActions = false;
        btnClose.addActionListener(l);
    }

    public void addMinimizeListener(ActionListener l) {
        useBuiltinActions = false;
        btnMinimize.addActionListener(l);
    }

    // ── JavaBeans properties ──────────────────────────────────────────────

    public boolean isUseBuiltinActions()          { return useBuiltinActions; }
    public void    setUseBuiltinActions(boolean b) { this.useBuiltinActions = b; }
}
