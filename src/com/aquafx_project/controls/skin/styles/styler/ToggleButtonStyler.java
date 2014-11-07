package com.aquafx_project.controls.skin.styles.styler;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;

import com.aquafx_project.controls.skin.AquaButtonSkin;
import com.aquafx_project.controls.skin.AquaToggleButtonSkin;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.ControlSizeVariant;
import com.aquafx_project.controls.skin.styles.IllegalStyleCombinationException;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;
import com.aquafx_project.controls.skin.styles.StyleDefinition;

/**
 * The ToggleButtonStyler with fluent API to change the default style of a ToggleButton.
 * 
 * @author claudinezillmann
 * 
 */
public class ToggleButtonStyler extends Styler<ToggleButton> {

    /**
     * ButtonType of a ToggleButton.
     */
    private ButtonType type;
    /**
     * Icon for a ToggleButton.
     */
    private MacOSDefaultIcons icon;

    private ToggleButtonStyler() {}

    /**
     * Creates a new Instance of ToggleButtonStyler. This has to be the first invocation on
     * ToggleButtonStyler.
     * 
     * @return The ToggleButtonStyler.
     */
    public static ToggleButtonStyler create() {
        return new ToggleButtonStyler();
    }

    /**
     * Adds a ButtonType to the ToggleButton
     * 
     * @param type
     *            The ButtonType for the ToggleButton.
     * @return the ToggleButtonStyler with the added ButtonType.
     */
    public ToggleButtonStyler setType(ButtonType type) {
        this.type = type;
        check();
        return this;
    }

    @Override public ToggleButtonStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ToggleButtonStyler) super.setSizeVariant(sizeVariant);
    }

    /**
     * Adds an Icon to the ToggleButton
     * 
     * @param icon
     *            The Icon of type MacOSDefaultIcons.
     * @return the ToggleButtonStyler with the added Icon.
     */
    public ToggleButtonStyler setIcon(MacOSDefaultIcons icon) {
        this.icon = icon;
        check();
        return this;
    }

    @Override public List<StyleDefinition> getAll() {
        List<StyleDefinition> ret = new ArrayList<>(super.getAll());
        ret.add(sizeVariant);
        ret.add(type);
        return ret;
    }

    @Override public void check() {
        if (type != null && type.equals(ButtonType.HELP) && icon != null) {
            throw new IllegalStyleCombinationException();
        }
    }

    @Override public void style(final ToggleButton button) {
        super.style(button);
        button.setSkin(new AquaToggleButtonSkin(button));
        Skin<?> skin = button.getSkin();
        if (skin != null && skin instanceof AquaButtonSkin) {
            ((AquaButtonSkin) skin).iconProperty().setValue(icon);
        }
    }
}
