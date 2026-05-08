/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class DraggableShadowPanel extends JPanel {

    private Point mouseOffset;
    private int arc = 20;

    // Shadow settings
    private int shadowOffset = 8;
    private Color shadowColor = new Color(0, 0, 0, 90);

    public DraggableShadowPanel() {
        setOpaque(false);
        setBackground(new Color(245, 247, 250));
        //enableDragging();
    }

    private void enableDragging() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseOffset = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point parentPoint = SwingUtilities.convertPoint(
                        DraggableShadowPanel.this,
                        e.getPoint(),
                        getParent()
                );

                setLocation(
                        parentPoint.x - mouseOffset.x,
                        parentPoint.y - mouseOffset.y
                );
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Shadow (same size, offset downward)
        g2.setColor(shadowColor);
        g2.fill(new RoundRectangle2D.Float(
                0,
                shadowOffset,
                w,
                h - shadowOffset,
                arc,
                arc
        ));

        // Main panel
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(
                0,
                0,
                w,
                h - shadowOffset,
                arc,
                arc
        ));

        g2.dispose();
        super.paintComponent(g);
    }

    /* Customization */
    public void setShadowColor(Color color) {
        this.shadowColor = color;
        repaint();
    }

    public void setShadowOffset(int offset) {
        this.shadowOffset = offset;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.arc = radius;
        repaint();
    }
}