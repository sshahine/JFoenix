package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.Label;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The LabelStyler with fluent API to change the default style of a Label.
 * 
 * @author claudinezillmann
 * 
 */
public class LabelStyler extends Styler<Label> {

    /**
     * Creates a new Instance of LabelStyler. This has to be the first invocation on LabelStyler.
     * 
     * @return The LabelStyler.
     */
    public static LabelStyler create() {
        return new LabelStyler();
    }

    @Override public LabelStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (LabelStyler) super.setSizeVariant(sizeVariant);
    }
}
