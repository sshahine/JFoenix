package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.PasswordField;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The PasswordFieldStyler with fluent API to change the default style of a PasswordField.
 * 
 * @author claudinezillmann
 * 
 */
public class PasswordFieldStyler extends Styler<PasswordField> {

    /**
     * Creates a new Instance of PasswordFieldStyler. This has to be the first invocation on
     * PasswordFieldStyler.
     * 
     * @return The PasswordFieldStyler.
     */
    public static PasswordFieldStyler create() {
        return new PasswordFieldStyler();
    }

    @Override public PasswordFieldStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (PasswordFieldStyler) super.setSizeVariant(sizeVariant);
    }
}
