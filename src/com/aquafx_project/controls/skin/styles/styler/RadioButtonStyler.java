package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.RadioButton;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The RadioButtonStyler with fluent API to change the default style of a RadioButton.
 * 
 * @author claudinezillmann
 * 
 */
public class RadioButtonStyler extends Styler<RadioButton> {

    /**
     * Creates a new Instance of RadioButtonStyler. This has to be the first invocation on
     * RadioButtonStyler.
     * 
     * @return The RadioButtonStyler.
     */
    public static RadioButtonStyler create() {
        return new RadioButtonStyler();
    }

    @Override public RadioButtonStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (RadioButtonStyler) super.setSizeVariant(sizeVariant);
    }
}
