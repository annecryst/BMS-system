/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

/**
 *
 * @author User
 */
public class RoundedButton extends javax.swing.JButton {
    private java.awt.Color backgroundColor;
    private int radius = 15; // How curved the corners are

    public RoundedButton(String text, java.awt.Color bgColor) {
        super(text);
        this.backgroundColor = bgColor;
        setForeground(java.awt.Color.WHITE); // White text
        setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        
        // Disable default square drawing and behaviors
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        
        // Lock the size so it never jumps
        setPreferredSize(new java.awt.Dimension(75, 30));
    }

    public void setBackgroundColor(java.awt.Color c) {
        this.backgroundColor = c;
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        // Turn on anti-aliasing for smooth, non-pixelated curves
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Make the button slightly darker when actively clicked
        if (getModel().isPressed()) {
            g2.setColor(backgroundColor.darker());
        } else {
            g2.setColor(backgroundColor);
        }
        
        // 1. Draw the curved background
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // 2. THE FIX: Draw the text manually to guarantee it never moves
        g2.setColor(getForeground());
        g2.setFont(getFont());
        java.awt.FontMetrics metrics = g2.getFontMetrics(getFont());
        
        // Calculate exact center coordinates
        int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        
        // Paint the text at the exact center
        g2.drawString(getText(), textX, textY);
        
        g2.dispose();
        
        // Notice: We completely removed super.paintComponent(g); 
        // so Java Swing can't apply the default "text push" animation anymore!
    }
}