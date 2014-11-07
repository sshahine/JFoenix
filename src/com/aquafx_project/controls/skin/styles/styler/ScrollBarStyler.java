package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ScrollBar;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ScrollBarStyler with fluent API to change the default style of a ScrollBar.
 * 
 * @author claudinezillmann
 * 
 */
public class ScrollBarStyler extends Styler<ScrollBar> {

    /**
     * Creates a new Instance of ScrollBarStyler. This has to be the first invocation on
     * ScrollBarStyler.
     * 
     * @return The ScrollBarStyler.
     */
    public static ScrollBarStyler create() {
        return new ScrollBarStyler();
    }

    @Override public ScrollBarStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ScrollBarStyler) super.setSizeVariant(sizeVariant);
    }
}
