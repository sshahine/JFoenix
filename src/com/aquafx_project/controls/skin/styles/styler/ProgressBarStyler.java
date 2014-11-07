package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ProgressBar;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ProgressBarStyler with fluent API to change the default style of a ProgressBar.
 * 
 * @author claudinezillmann
 * 
 */
public class ProgressBarStyler extends Styler<ProgressBar> {

    /**
     * Creates a new Instance of ProgressBarStyler. This has to be the first invocation on
     * ProgressBarStyler.
     * 
     * @return The ProgressBarStyler.
     */
    public static ProgressBarStyler create() {
        return new ProgressBarStyler();
    }

    @Override public ProgressBarStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ProgressBarStyler) super.setSizeVariant(sizeVariant);
    }
}
