package Utilities;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

public enum StaffStatus implements TableBadgeCellRenderer.Info {
    ACTIVE("Active", new Color(59, 155, 60), "active.svg"),
    ON_LEAVE("On Leave", new Color(255, 164, 75), "leave.svg"),
    INACTIVE("Inactive", new Color(255, 75, 101), "inactive.svg");

    StaffStatus(String text, Color color, String icon) {
        this.text = text;
        this.color = color;
        
        // 1. Create the FlatSVGIcon object
        FlatSVGIcon svgIcon = new FlatSVGIcon("raven/icon/" + icon, 0.35f);
        
        // 2. Apply the color filter (this returns void, so we just call it on the object)
        svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
        
        // 3. Assign the modified icon to your variable
        this.icon = svgIcon;
    }

    private final String text;
    private final Color color;
    private final Icon icon;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }
}
