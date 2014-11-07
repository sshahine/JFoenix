package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.TextArea;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The TextAreaStyler with fluent API to change the default style of a TextArea.
 * 
 * @author claudinezillmann
 * 
 */
public class TextAreaStyler extends Styler<TextArea> {

    /**
     * Creates a new Instance of TextAreaStyler. This has to be the first invocation on
     * TextAreaStyler.
     * 
     * @return The TextAreaStyler.
     */
    public static TextAreaStyler create() {
        return new TextAreaStyler();
    }

    @Override public TextAreaStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (TextAreaStyler) super.setSizeVariant(sizeVariant);
    }
}
