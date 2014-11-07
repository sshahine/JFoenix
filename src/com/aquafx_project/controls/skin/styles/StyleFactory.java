package com.aquafx_project.controls.skin.styles;

import javafx.scene.control.Control;

/**
 * 
 * A Style Factory adds a Style Class to a Control for the possibility of CSS styling.
 * 
 * @author claudinezillmann
 * 
 */
public class StyleFactory {

    public StyleFactory() {}

    /**
     * Adds the style a StyleClass.
     * 
     * @param <T>
     *            The Control Type.
     * @param control
     *            The Control to be styled.
     * @param definitions
     *            The StyleDefinitions for the Control.
     */
    public <T extends Control> void addStyles(T control, StyleDefinition... definitions) {
        for (StyleDefinition definition : definitions) {
            String style = definition.getStyleName();
            if (style != null) {
                control.getStyleClass().add(style);
            }
        }
    }
}
