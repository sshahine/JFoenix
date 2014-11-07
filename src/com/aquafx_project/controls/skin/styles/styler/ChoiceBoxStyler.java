package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ChoiceBox;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ChoiceBoxStyler with fluent API to change the default style of a ChoiceBox.
 * 
 * @author claudinezillmann
 * 
 */
public class ChoiceBoxStyler extends Styler<ChoiceBox<?>> {

    /**
     * Creates a new Instance of ChoiceBoxStyler. This has to be the first invocation on
     * ChoiceBoxStyler.
     * 
     * @return The ChoiceBoxStyler.
     */
    public static ChoiceBoxStyler create() {
        return new ChoiceBoxStyler();
    }

    @Override public ChoiceBoxStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ChoiceBoxStyler) super.setSizeVariant(sizeVariant);
    }
}