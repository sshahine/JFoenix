package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.ColorPicker;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The ColorPickerStyler with fluent API to change the default style of a ColorPicker.
 * 
 * @author claudinezillmann
 * 
 */
public class ColorPickerStyler extends Styler<ColorPicker> {

    /**
     * Creates a new Instance of ColorPickerStyler. This has to be the first invocation on
     * ColorPickerStyler.
     * 
     * @return The ColorPickerStyler.
     */
    public static ColorPickerStyler create() {
        return new ColorPickerStyler();
    }

    @Override public ColorPickerStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (ColorPickerStyler) super.setSizeVariant(sizeVariant);
    }
}
