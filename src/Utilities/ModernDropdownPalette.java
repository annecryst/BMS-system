package Utilities;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ModernDropdownPalette extends JPanel {

    private JTextField inputField;
    private JLabel searchIcon;
    private JPopupMenu suggestionPopup;
    private List<String> items;
    private List<Consumer<String>> searchListeners = new ArrayList<>();

    public ModernDropdownPalette() {
        this(List.of());
    }

    public ModernDropdownPalette(List<String> items) {
        this.items = new ArrayList<>(items);
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 40));
        
        JPanel inputContainer = new JPanel(new BorderLayout());
        inputContainer.setOpaque(false);
        inputContainer.setBorder(new EmptyBorder(2, 5, 2, 5));

        inputField = new JTextField();
        inputField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Color.BLACK);
        inputField.setCaretColor(Color.BLACK);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/search.png"));
            searchIcon = new JLabel(icon);
            searchIcon.setBorder(new EmptyBorder(0, 5, 0, 10));
            searchIcon.setHorizontalAlignment(SwingConstants.CENTER);
            inputContainer.add(searchIcon, BorderLayout.EAST);
        } catch (Exception e) {
            System.out.println("Search icon not found.");
        }

        inputContainer.add(inputField, BorderLayout.CENTER);
        add(inputContainer, BorderLayout.CENTER);

        suggestionPopup = new JPopupMenu();
        suggestionPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionPopup.setFocusable(false); 

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = inputField.getText();
                updateSuggestions(text);
                fireSearchEvent(text); 
            }
        });
    }

    private void updateSuggestions(String text) {
        suggestionPopup.setVisible(false);
        suggestionPopup.removeAll();

        if (text.isEmpty()) {
            return;
        }

        boolean hasMatch = false;
        for (String item : items) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                JMenuItem menuItem = new JMenuItem(item);
                menuItem.setFocusable(false); 
                menuItem.setBackground(Color.WHITE);
                menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                // Add a little padding to the menu items so they look cleaner
                menuItem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                menuItem.addActionListener(e -> {
                    inputField.setText(item);
                    suggestionPopup.setVisible(false);
                    fireSearchEvent(item); 
                });
                suggestionPopup.add(menuItem);
                hasMatch = true;
            }
        }

        if (hasMatch) {
            // THE FIX: Sync the dropdown width perfectly with your panel's width
            suggestionPopup.setPreferredSize(null); // Reset first so Java calculates the correct height
            int naturalHeight = suggestionPopup.getPreferredSize().height;
            suggestionPopup.setPreferredSize(new Dimension(this.getWidth(), naturalHeight));
            
            // Show it perfectly aligned with the bottom left corner
            suggestionPopup.show(this, 0, getHeight());
            inputField.requestFocusInWindow(); 
        }
    }

    public void addSearchListener(Consumer<String> listener) {
        searchListeners.add(listener);
    }

    private void fireSearchEvent(String text) {
        for (Consumer<String> listener : searchListeners) {
            listener.accept(text);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int arc = 20;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
    }

    public void setItems(List<String> items) {
        this.items = new ArrayList<>(items);
    }

    public String getText() {
        return inputField.getText();
    }

    public void setText(String text) {
        inputField.setText(text);
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}