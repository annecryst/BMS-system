/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;

/**
 *
 * @author User
 */
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class SidebarInitializer {

    /**
     * Create a sidebar panel with buttons and attach it to a JFrame.
     * @param mainPanel The main content area panel (to switch content)
     * @param buttons A LinkedHashMap of button text -> ActionListener
     * @return JPanel sidebar ready to be added to JFrame
     */
    public static JPanel createSidebar(JPanel mainPanel, LinkedHashMap<String, ActionListener> buttons) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(52, 58, 64));
        sidebar.setLayout(new GridLayout(buttons.size(), 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        for (Map.Entry<String, ActionListener> entry : buttons.entrySet()) {
            JButton btn = new JButton(entry.getKey());
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(52, 58, 64));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(entry.getValue());

            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(73, 80, 87));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(52, 58, 64));
                }
            });

            sidebar.add(btn);
        }

        return sidebar;
    }

    /**
     * Utility method to switch main panel content to a simple label
     */
    public static void switchPanel(JPanel mainPanel, String panelName) {
        mainPanel.removeAll();
        JLabel label = new JLabel(panelName + " Panel", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Example main for testing independently
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard Example");
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setLayout(new BorderLayout());

            LinkedHashMap<String, ActionListener> buttonMap = new LinkedHashMap<>();
            buttonMap.put("Home", e -> switchPanel(mainPanel, "Home"));
            buttonMap.put("Profile", e -> switchPanel(mainPanel, "Profile"));
            buttonMap.put("Settings", e -> switchPanel(mainPanel, "Settings"));
            buttonMap.put("Reports", e -> switchPanel(mainPanel, "Reports"));
            buttonMap.put("About", e -> switchPanel(mainPanel, "About"));
            buttonMap.put("Exit", e -> System.exit(0));

            JPanel sidebar = createSidebar(mainPanel, buttonMap);

            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(sidebar, BorderLayout.WEST);
            frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}
