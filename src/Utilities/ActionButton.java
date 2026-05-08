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
import java.awt.geom.RoundRectangle2D;

public class ActionButton extends JButton {

    private int arc = 10;
    private Color buttonColor = new Color(225, 245, 254); // Default subtle blue
    private Color borderColor = new Color(187, 222, 251); 

    public ActionButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        // Draw Button Background
        g2.setColor(buttonColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        // Draw Button Border
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        g2.dispose();
        super.paintComponent(g);
    }

    /* Customization */
    public void setColors(Color background, Color border, Color text) {
        this.buttonColor = background;
        this.borderColor = border;
        setForeground(text);
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.arc = radius;
        repaint();
    }
}