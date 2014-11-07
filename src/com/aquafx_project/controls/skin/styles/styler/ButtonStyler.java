package com.aquafx_project.controls.skin.styles.styler;

import java.util.ArrayList;
import java.util.List;

import com.aquafx_project.controls.skin.AquaButtonSkin;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.ControlSizeVariant;
import com.aquafx_project.controls.skin.styles.IllegalStyleCombinationException;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;
import com.aquafx_project.controls.skin.styles.StyleDefinition;

import javafx.scene.control.Button;
import javafx.scene.control.Skin;

/**
 * The ButtonStyler with fluent API to change the default style of a Button.
 * 
 * @author claudinezillmann
 * 
 */
public class ButtonStyler extends Styler<Button> {

    /**
     * ButtonType of a Button.
     */
    private ButtonType type;
    /**
     * Icon for a Button.
     */
    private MacOSDefaultIcons icon;

    private ButtonStyler() {}

    /**
     * Creates a new Instance of ButtonStyler. This has to be the first invocation on ButtonStyler.
     * 
     * @return The ButtonStyler.
     */
    public static ButtonStyler create() {
        return new ButtonStyler();
    }

    /**
     * Adds a ButtonType to the Button
     * 
     * @param type
     *            The ButtonType for the Button.
     * @return the ButtonStyler with the added ButtonType.
     */
    public ButtonStyler setType(ButtonType type) {
        this.type = type;
        check();
        return this;
    }

    @Override public ButtonStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ButtonStyler) super.setSizeVariant(sizeVariant);
    }

    /**
     * Adds an Icon to the Button
     * 
     * @param icon
     *            The Icon of type MacOSDefaultIcons.
     * @return the ButtonStyler with the added Icon.
     */
    public ButtonStyler setIcon(MacOSDefaultIcons icon) {
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

    @Override public void style(final Button button) {
        super.style(button);
        button.setSkin(new AquaButtonSkin(button));
        Skin<?> skin = button.getSkin();
        if (skin != null && skin instanceof AquaButtonSkin) {
            ((AquaButtonSkin) skin).iconProperty().setValue(icon);
        }
    }
}
