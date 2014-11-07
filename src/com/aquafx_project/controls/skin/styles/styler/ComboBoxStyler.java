package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ComboBox;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ComboBoxStyler with fluent API to change the default style of a ComboBox.
 * 
 * @author claudinezillmann
 * 
 */
public class ComboBoxStyler extends Styler<ComboBox<?>> {

    /**
     * Creates a new Instance of ComboBoxStyler. This has to be the first invocation on
     * ComboBoxStyler.
     * 
     * @return The ComboBoxStyler.
     */
    public static ComboBoxStyler create() {
        return new ComboBoxStyler();
    }

    @Override public ComboBoxStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ComboBoxStyler) super.setSizeVariant(sizeVariant);
    }
}
