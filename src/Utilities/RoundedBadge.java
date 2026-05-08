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

public class RoundedBadge extends JLabel {

    private int arc = 16;
    private Color badgeColor = new Color(233, 240, 253); // Default light blue

    public RoundedBadge(String text) {
        super(text);
        setOpaque(false); // Make label transparent so custom background shows
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12)); // Internal padding
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw rounded background[cite: 1]
        g2.setColor(badgeColor);
        g2.fill(new RoundRectangle2D.Float(
                0,
                0,
                w,
                h,
                arc,
                arc
        ));

        g2.dispose();
        super.paintComponent(g);
    }

    /* Customization */
    public void setBadgeColor(Color color) {
        this.badgeColor = color;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.arc = radius;
        repaint();
    }
}