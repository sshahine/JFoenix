package com.aquafx_project.controls.skin.styles.styler;

import javafx.scene.control.Slider;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

/**
 * The SliderStyler with fluent API to change the default style of a Slider.
 * 
 * @author claudinezillmann
 * 
 */
public class SliderStyler extends Styler<Slider> {

    /**
     * Creates a new Instance of SliderStyler. This has to be the first invocation on SliderStyler.
     * 
     * @return The SliderStyler.
     */
    public static SliderStyler create() {
        return new SliderStyler();
    }

    @Override public SliderStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (SliderStyler) super.setSizeVariant(sizeVariant);
    }
}
