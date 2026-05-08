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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class CircleImagePanel extends JPanel {

    // ── Listener interface ────────────────────────────────────────────────────

    public interface ImageChangeListener {
        void onImageChanged(String newPath, BufferedImage newImage);
        void onImageRemoved();
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private static File   storageDir     = new File("src/LocalStorage/images");

    private BufferedImage  image          = null;
    private String         imagePath      = "";
    private String         storedFileName = "";

    private Color  ringColor       = new Color(37, 99, 235);
    private int    ringThickness   = 3;
    private Color  ringHoverColor  = new Color(99, 102, 241);

    private Color  placeholderBg   = new Color(59, 130, 246);
    private Color  placeholderBg2  = new Color(99, 102, 241);
    private Color  placeholderFg   = Color.WHITE;
    private String placeholderText = "?";

    private boolean showRing       = true;
    private boolean showShadow     = true;
    private boolean hoverEffect    = true;
    private boolean clickToUpload  = true;

    private boolean hovered = false;

    private ImageChangeListener imageChangeListener = null;

    // ── Constructor ───────────────────────────────────────────────────────────

    public CircleImagePanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(100, 100));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (hoverEffect) { hovered = true;  repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (hoverEffect) { hovered = false; repaint(); }
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && clickToUpload) {
                    openFileChooser();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getX(), e.getY());
                }
            }
        });
    }

    // ── File Chooser ──────────────────────────────────────────────────────────

    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Image");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Image files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
        chooser.setAcceptAllFileFilterUsed(false);

        Window parent = SwingUtilities.getWindowAncestor(this);
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File   selected  = chooser.getSelectedFile();
        String origName  = selected.getName();
        String ext       = origName.contains(".") ? origName.substring(origName.lastIndexOf('.'))  : "";
        String stemOrig  = origName.contains(".") ? origName.substring(0, origName.lastIndexOf('.')) : origName;

        String newStem = (String) JOptionPane.showInputDialog(
                parent,
                "<html>Save as filename <i>(without extension)</i>:</html>",
                "Save Photo",
                JOptionPane.PLAIN_MESSAGE, null, null, stemOrig);

        if (newStem == null) return;
        newStem = newStem.trim().isEmpty() ? stemOrig : newStem.trim();
        newStem = newStem.replaceAll("[\\\\/:*?\"<>|]", "_");
        String finalName = newStem + ext;

        if (!storageDir.exists()) storageDir.mkdirs();

        File dest = new File(storageDir, finalName);
        try {
            Files.copy(selected.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            storedFileName = finalName;
            loadImageFromPath(dest.getAbsolutePath());
            if (imageChangeListener != null) {
                imageChangeListener.onImageChanged(dest.getAbsolutePath(), image);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Failed to save image:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showContextMenu(int x, int y) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem changeItem = new JMenuItem(image != null ? "Change Photo" : "Add Photo");
        changeItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        changeItem.addActionListener(e -> openFileChooser());
        menu.add(changeItem);

        if (image != null) {
            JMenuItem removeItem = new JMenuItem("Remove Photo");
            removeItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            removeItem.setForeground(new Color(220, 38, 38));
            removeItem.addActionListener(e -> {
                image          = null;
                imagePath      = "";
                storedFileName = "";
                repaint();
                if (imageChangeListener != null) {
                    imageChangeListener.onImageRemoved();
                }
            });
            menu.add(removeItem);
        }

        menu.show(this, x, y);
    }

    // ── Image Loading ─────────────────────────────────────────────────────────

    public void loadFromStoredPath(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) return;
        this.storedFileName = new File(fullPath).getName();
        loadImageFromPath(fullPath);
    }

    public void loadImageFromPath(String path) {
        this.imagePath = path == null ? "" : path;
        if (!this.imagePath.isEmpty()) {
            try {
                image = ImageIO.read(new File(this.imagePath));
            } catch (IOException ex) {
                image = null;
            }
        } else {
            image = null;
        }
        repaint();
    }

    public void setImage(BufferedImage img) {
        this.image = img;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void clearImage() {
        image          = null;
        imagePath      = "";
        storedFileName = "";
        repaint();
        if (imageChangeListener != null) {
            imageChangeListener.onImageRemoved();
        }
    }

    // ── Listener ──────────────────────────────────────────────────────────────

    public void setImageChangeListener(ImageChangeListener listener) {
        this.imageChangeListener = listener;
    }

    public ImageChangeListener getImageChangeListener() {
        return imageChangeListener;
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w     = getWidth();
        int h     = getHeight();
        int inset = showShadow ? 8 : 0;
        int size  = Math.min(w, h) - inset * 2;
        if (size <= 0) return;
        int cx    = (w - size) / 2;
        int cy    = (h - size) / 2 + (showShadow ? 2 : 0);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ── 1. Layered drop shadow ────────────────────────────────────────────
        if (showShadow) {
            for (int i = 10; i >= 1; i--) {
                g2.setColor(new Color(30, 60, 140, 4));
                g2.fillOval(cx - i + 2, cy - i + 4, size + i * 2, size + i * 2);
            }
            for (int i = 5; i >= 1; i--) {
                g2.setColor(new Color(20, 50, 120, 7));
                g2.fillOval(cx + 1, cy + i, size - 1, size - 1);
            }
        }

        // ── 2. Stroke-based ring with hover glow ──────────────────────────────
        if (showRing) {
            float rt   = ringThickness;
            float half = rt / 2f;
            Color ring = hovered ? ringHoverColor : ringColor;

            if (hovered) {
                g2.setColor(new Color(ring.getRed(), ring.getGreen(), ring.getBlue(), 55));
                g2.setStroke(new BasicStroke(rt + 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval((int)(cx + half - 3), (int)(cy + half - 3),
                            (int)(size - rt + 6),  (int)(size - rt + 6));
            }
            g2.setColor(ring);
            g2.setStroke(new BasicStroke(rt, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval((int)(cx + half), (int)(cy + half),
                        (int)(size - rt),  (int)(size - rt));
            g2.setStroke(new BasicStroke(1f));
        }

        // ── 3. Inner circle setup ─────────────────────────────────────────────
        int pad       = showRing ? ringThickness + 2 : 0;
        int innerSize = size - pad * 2;
        int ix        = cx + pad;
        int iy        = cy + pad;
        if (innerSize <= 0) { g2.dispose(); return; }

        Ellipse2D clip = new Ellipse2D.Float(ix, iy, innerSize, innerSize);
        g2.setClip(clip);

        // ── 4a. Draw image (cover-fit) ────────────────────────────────────────
        if (image != null) {
            float scaleX = (float) innerSize / image.getWidth();
            float scaleY = (float) innerSize / image.getHeight();
            float scale  = Math.max(scaleX, scaleY);
            int   sw     = (int)(image.getWidth()  * scale);
            int   sh     = (int)(image.getHeight() * scale);
            int   sx     = ix + (innerSize - sw) / 2;
            int   sy     = iy + (innerSize - sh) / 2;
            g2.drawImage(image, sx, sy, sw, sh, null);

        // ── 4b. Modern gradient placeholder ──────────────────────────────────
        } else {
            GradientPaint gp = new GradientPaint(
                ix,             iy,              placeholderBg,
                ix + innerSize, iy + innerSize,  placeholderBg2
            );
            g2.setPaint(gp);
            g2.fillOval(ix, iy, innerSize, innerSize);

            // Subtle depth ring
            g2.setColor(new Color(255, 255, 255, 20));
            g2.setStroke(new BasicStroke(1.5f));
            int di = innerSize / 6;
            g2.drawOval(ix + di, iy + di, innerSize - di * 2, innerSize - di * 2);
            g2.setStroke(new BasicStroke(1f));

            // Initials (hidden when hovered so upload icon shows clearly)
            if (!hovered || !clickToUpload) {
                String label = (placeholderText != null && !placeholderText.trim().isEmpty())
                               ? placeholderText.trim().toUpperCase() : "?";
                int fontSize = Math.max(10, (int)(innerSize * 0.36f));
                g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
                g2.setColor(new Color(255, 255, 255, 220));
                FontMetrics fm = g2.getFontMetrics();
                int tx = ix + (innerSize - fm.stringWidth(label)) / 2;
                int ty = iy + (innerSize + fm.getAscent() - fm.getDescent()) / 2 - 1;
                g2.drawString(label, tx, ty);
            }
        }

        // ── 5. Hover overlay with upload icon ─────────────────────────────────
        if (hovered && hoverEffect && clickToUpload) {
            g2.setClip(clip);
            g2.setColor(new Color(10, 15, 40, 145));
            g2.fillOval(ix, iy, innerSize, innerSize);

            int icx = ix + innerSize / 2;
            int icy = iy + innerSize / 2 - (int)(innerSize * 0.07f);
            g2.setColor(Color.WHITE);
            drawUploadIcon(g2, icx, icy, innerSize);

            String label = image != null ? "Change Photo" : "Upload Photo";
            int fontSize = Math.max(7, (int)(innerSize * 0.12f));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int lx = ix + (innerSize - fm.stringWidth(label)) / 2;
            int ly = iy + innerSize / 2 + (int)(innerSize * 0.30f);
            g2.setColor(new Color(255, 255, 255, 210));
            g2.drawString(label, lx, ly);
        }

        // ── 6. Inner vignette (edge depth) ────────────────────────────────────
        g2.setClip(clip);
        float icx2 = ix + innerSize / 2f;
        float icy2 = iy + innerSize / 2f;
        RadialGradientPaint vignette = new RadialGradientPaint(
            new Point2D.Float(icx2, icy2),
            innerSize / 2f,
            new float[] { 0.55f, 1.0f },
            new Color[]  { new Color(0, 0, 0, 0), new Color(0, 0, 0, 45) }
        );
        g2.setPaint(vignette);
        g2.fillOval(ix, iy, innerSize, innerSize);

        g2.dispose();
    }

    private void drawUploadIcon(Graphics2D g2, int cx, int cy, int size) {
        float s     = size * 0.30f;
        int aW      = (int)(s * 0.58f);
        int aH      = (int)(s * 0.48f);
        int shW     = Math.max(2, (int)(s * 0.20f));
        int shH     = (int)(s * 0.44f);
        int bW      = (int)(s * 0.90f);
        float lw    = Math.max(1.5f, s * 0.13f);
        int totalH  = aH + shH;
        int top     = cy - totalH / 2;

        // Arrow head (filled upward triangle)
        int[] xp = { cx,      cx - aW, cx + aW };
        int[] yp = { top,     top + aH, top + aH };
        g2.fillPolygon(xp, yp, 3);

        // Shaft
        g2.fillRect(cx - shW / 2, top + aH, shW, shH);

        // Base line
        int baseY = top + aH + shH + (int)(s * 0.20f);
        g2.setStroke(new BasicStroke(lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - bW / 2, baseY, cx + bW / 2, baseY);
        g2.setStroke(new BasicStroke(1f));
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize().width > 0
               ? super.getPreferredSize()
               : new Dimension(100, 100);
    }

    // ── JavaBeans Properties (NetBeans property editor) ───────────────────────

    public Color getRingColor()                  { return ringColor; }
    public void  setRingColor(Color c)           { this.ringColor = c; repaint(); }

    public int   getRingThickness()              { return ringThickness; }
    public void  setRingThickness(int t)         { this.ringThickness = t; repaint(); }

    public Color getRingHoverColor()             { return ringHoverColor; }
    public void  setRingHoverColor(Color c)      { this.ringHoverColor = c; repaint(); }

    public Color getPlaceholderBg()              { return placeholderBg; }
    public void  setPlaceholderBg(Color c)       { this.placeholderBg = c; repaint(); }

    public Color getPlaceholderBg2()             { return placeholderBg2; }
    public void  setPlaceholderBg2(Color c)      { this.placeholderBg2 = c; repaint(); }

    public Color getPlaceholderFg()              { return placeholderFg; }
    public void  setPlaceholderFg(Color c)       { this.placeholderFg = c; repaint(); }

    public String getPlaceholderText()           { return placeholderText; }
    public void   setPlaceholderText(String t)   { this.placeholderText = t; repaint(); }

    public boolean isShowRing()                  { return showRing; }
    public void    setShowRing(boolean b)        { this.showRing = b; repaint(); }

    public boolean isShowShadow()                { return showShadow; }
    public void    setShowShadow(boolean b)      { this.showShadow = b; repaint(); }

    public boolean isHoverEffect()               { return hoverEffect; }
    public void    setHoverEffect(boolean b)     { this.hoverEffect = b; }

    public boolean isClickToUpload()             { return clickToUpload; }
    public void    setClickToUpload(boolean b)   { this.clickToUpload = b;
                                                   setCursor(b ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                                               : Cursor.getDefaultCursor()); }

    public String getImagePath()                 { return imagePath; }
    public void   setImagePath(String p)         { loadImageFromPath(p); }

    public String getStoredFileName()            { return storedFileName; }
    public String getStoredFilePath()            {
        return storedFileName.isEmpty() ? "" : new File(storageDir, storedFileName).getAbsolutePath();
    }

    public static File getStorageDirectory()     { return storageDir; }
    public static void setStorageDirectory(File dir) { storageDir = dir; }
}
