package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ProgressIndicator;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ProgressIndicatorStyler with fluent API to change the default style of a ProgressIndicator.
 * 
 * @author claudinezillmann
 * 
 */
public class ProgressIndicatorStyler extends Styler<ProgressIndicator> {

    /**
     * Creates a new Instance of ProgressIndicatorStyler. This has to be the first invocation on
     * ProgressIndicatorStyler.
     * 
     * @return The ProgressIndicatorStyler.
     */
    public static ProgressIndicatorStyler create() {
        return new ProgressIndicatorStyler();
    }

    @Override public ProgressIndicatorStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ProgressIndicatorStyler) super.setSizeVariant(sizeVariant);
    }
}
