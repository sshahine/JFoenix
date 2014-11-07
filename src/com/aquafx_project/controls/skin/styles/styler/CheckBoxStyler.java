package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.CheckBox;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The CheckBoxStyler with fluent API to change the default style of a CheckBox.
 * 
 * @author claudinezillmann
 * 
 */
public class CheckBoxStyler extends Styler<CheckBox> {

    /**
     * Creates a new Instance of CheckBoxStyler. This has to be the first invocation on
     * CheckBoxStyler.
     * 
     * @return The CheckBoxStyler.
     */
    public static CheckBoxStyler create() {
        return new CheckBoxStyler();
    }

    @Override public CheckBoxStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (CheckBoxStyler) super.setSizeVariant(sizeVariant);
    }
}
