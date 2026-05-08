package Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModernBarGraph extends JPanel {

    private final Map<String, Integer> values = new LinkedHashMap<>();
    private final Map<String, Color> colors = new LinkedHashMap<>();
    private final Map<String, Float> animatedValues = new LinkedHashMap<>();

    private String hoveredBar = null;
    private Timer animationTimer;

    private static final int MIN_BAR_HEIGHT = 5; // ensures tiny values are visible

    public ModernBarGraph() {
        setBackground(new Color(250, 250, 250));
        setPreferredSize(new Dimension(700, 500));

        // Sample categories
        values.put("Total Residents", 0);
        values.put("Active Blotters", 0);
        values.put("Certificate Records", 0);
        values.put("Official Counts", 0);

        // Colors
        colors.put("Total Residents", new Color(76,159,246));
        colors.put("Active Blotters", new Color(255,145,77));
        colors.put("Certificate Records", new Color(126,217,87));
        colors.put("Official Counts", new Color(255,87,87));

        // Initialize animated values
        for (String key : values.keySet()) {
            animatedValues.put(key, 0f);
        }

        startLoadAnimation();

        // Hover effect
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredBar = getBarAt(e.getX(), e.getY());
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredBar = null;
                repaint();
            }
        });
    }

    // --- Public method to update bar value ---
    public void setBarValue(String barName, int newValue) {
        if (!values.containsKey(barName)) return;
        values.put(barName, newValue);
        animateBars();
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        animationTimer = new Timer(16, e -> {
            boolean done = true;
            int maxValue = getMaxValue();
            for (String key : values.keySet()) {
                float target = values.get(key);
                float current = animatedValues.get(key);
                if (current < target) {
                    animatedValues.put(key, Math.min(current + Math.max(1, target/30f), target));
                    done = false;
                } else if (current > target) {
                    animatedValues.put(key, Math.max(current - Math.max(1, target/30f), target));
                    done = false;
                }
            }
            repaint();
            if (done) animationTimer.stop();
        });
        animationTimer.start();
    }
    
    // Smooth animation step
    private void animateBars() {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        animationTimer = new Timer(16, e -> { // ~60 FPS
            boolean done = true;
            for (String key : values.keySet()) {
                float target = values.get(key);
                float current = animatedValues.get(key);

                // Increment small fraction proportional to target
                float step = Math.max(0.5f, target * 0.05f);

                if (current < target) {
                    animatedValues.put(key, Math.min(current + step, target));
                    done = false;
                } else if (current > target) {
                    animatedValues.put(key, Math.max(current - step, target));
                    done = false;
                }
            }
            repaint();
            if (done) animationTimer.stop();
        });
        animationTimer.start();
    }

    private void startLoadAnimation() {
        animationTimer = new Timer(16, e -> {
            boolean done = true;
            int maxValue = getMaxValue();
            for (String key : values.keySet()) {
                float target = values.get(key);
                float current = animatedValues.get(key);
                if (current < target) {
                    animatedValues.put(key, Math.min(current + 2, target));
                    done = false;
                }
            }
            repaint();
            if (done) animationTimer.stop();
        });
        animationTimer.start();
    }

    private int getMaxValue() {
        int max = 1;
        for (int val : values.values()) {
            if (val > max) max = val;
        }
        return Math.max(max, 1);
    }

    private String getBarAt(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        int leftPadding = 50;
        int rightPadding = 30;
        int bottomPadding = 50;
        int barCount = values.size();
        int spacing = 20;
        int availableWidth = width - leftPadding - rightPadding;
        int barWidth = (availableWidth - (spacing * (barCount - 1))) / barCount;

        int i = 0;
        int maxValue = getMaxValue();
        int availableHeight = height - bottomPadding - 30;

        for (String key : values.keySet()) {
            int bx = leftPadding + i * (barWidth + spacing);
            int rawHeight = animatedValues.get(key).intValue();
            int barHeight = (int) ((rawHeight / (float) maxValue) * availableHeight);
            barHeight = rawHeight > 0 ? Math.max(barHeight, MIN_BAR_HEIGHT) : 0;
            int by = height - bottomPadding - barHeight;

            if (x >= bx && x <= bx + barWidth && y >= by && y <= by + barHeight) {
                return key;
            }
            i++;
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int topPadding = 30;
        int bottomPadding = 50;
        int leftPadding = 50;
        int rightPadding = 30;
        int barCount = values.size();
        int spacing = 20;

        int availableWidth = width - leftPadding - rightPadding;
        int barWidth = (availableWidth - (spacing * (barCount - 1))) / barCount;
        int availableHeight = height - topPadding - bottomPadding;
        int maxValue = getMaxValue();

        // --- Adaptive Y-axis divisions ---
        int divisions = Math.min(maxValue, 5);
        if (divisions == 0) divisions = 1;

        // Horizontal gridlines and labels
        g2.setStroke(new BasicStroke(1f));
        for (int j = 0; j <= divisions; j++) {
            int yLine = height - bottomPadding - (j * availableHeight / divisions);
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(leftPadding, yLine, width - rightPadding, yLine);

            g2.setColor(Color.GRAY);
            int labelValue = Math.round(j * (float) maxValue / divisions);
            String label = String.valueOf(labelValue);
            int lw = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, leftPadding - lw - 5, yLine + 5);
        }

        // --- Draw bars ---
        int i = 0;
        for (String key : values.keySet()) {
            int x = leftPadding + i * (barWidth + spacing);
            int rawHeight = animatedValues.get(key).intValue();
            int barHeight = (int) ((rawHeight / (float) maxValue) * availableHeight);
            barHeight = rawHeight > 0 ? Math.max(barHeight, MIN_BAR_HEIGHT) : 0;
            int y = height - bottomPadding - barHeight;

            Color base = colors.get(key);
            GradientPaint gp = new GradientPaint(x, y, base.brighter(), x, y + barHeight, base.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 15, 15);

            if (key.equals(hoveredBar)) {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRoundRect(x, y, barWidth, barHeight, 15, 15);
            }

            // Value above bar
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String valueText = String.valueOf(values.get(key));
            int valueWidth = g2.getFontMetrics().stringWidth(valueText);
            g2.drawString(valueText, x + barWidth / 2 - valueWidth / 2, y - 5);

            // Label below bar
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            int labelWidth = g2.getFontMetrics().stringWidth(key);
            g2.drawString(key, x + barWidth / 2 - labelWidth / 2, height - bottomPadding + 20);

            i++;
        }

        // Axes
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(leftPadding, height - bottomPadding, width - rightPadding, height - bottomPadding); // X-axis
        g2.drawLine(leftPadding, topPadding, leftPadding, height - bottomPadding); // Y-axis

        g2.dispose();
    }
}