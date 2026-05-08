/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilities;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PopUpNotif {

    public static void showFieldError(JComponent field, String message) {

        // Red outline
        field.putClientProperty("JComponent.outline", "error");
        field.requestFocus();

        // Compact bubble
        JPanel bubble = new JPanel(new BorderLayout(6, 0));
        bubble.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:8; background:#3c3f41"
        );
        bubble.setBorder(new EmptyBorder(4, 6, 4, 6));

        // Small error icon
        JLabel icon = new JLabel(new FlatOptionPaneErrorIcon());
        icon.setPreferredSize(new Dimension(14, 14));

        // Small text
        JLabel text = new JLabel(message);
        text.setForeground(Color.WHITE);
        text.putClientProperty(
                FlatClientProperties.STYLE,
                "font:-1"
        );

        bubble.add(icon, BorderLayout.WEST);
        bubble.add(text, BorderLayout.CENTER);

        // Position BELOW the field
        Point p = field.getLocationOnScreen();
        int x = p.x;
        int y = p.y + field.getHeight() + 4;

        Popup popup = PopupFactory.getSharedInstance()
                .getPopup(field, bubble, x, y);

        popup.show();

        // Auto-hide (shorter)
        Timer timer = new Timer(2000, e -> popup.hide());
        timer.setRepeats(false);
        timer.start();
    }
}
