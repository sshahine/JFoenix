package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ToolBar;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ToolBarStyler with fluent API to change the default style of a ToolBar.
 * 
 * @author claudinezillmann
 * 
 */
public class ToolBarStyler extends Styler<ToolBar> {

    /**
     * Creates a new Instance of ToolBarStyler. This has to be the first invocation on
     * ToolBarStyler.
     * 
     * @return The ToolBarStyler.
     */
    public static ToolBarStyler create() {
        return new ToolBarStyler();
    }

    @Override public ToolBarStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ToolBarStyler) super.setSizeVariant(sizeVariant);
    }
}
