package com.aquafx_project.controls.skin.styles.styler;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Control;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;
import com.aquafx_project.controls.skin.styles.StyleDefinition;

/**
 * The Styler with fluent API to change the default style of a Control.
 * 
 * @author claudinezillmann
 * 
 * @param <T>
 *            T of type Control
 */
public class Styler<T extends Control> {

    /**
     * The SizeVariant for the Control.
     */
    protected ControlSizeVariant sizeVariant;

    /**
     * Adds all StyleDfinitions, which were defined, to the Controls' style class definitions. This
     * has to be the last invocation on a Styler.
     * 
     * @param control
     *            The Control to be styled.
     */
    public void style(T control) {
        for (StyleDefinition definition : getAll()) {
            if (definition != null) {
                String style = definition.getStyleName();
                if (style != null) {
                    control.getStyleClass().add(style);
                }
            }
        }
    }

    /**
     * Adds a ControlSizeVariant to the Control.
     * 
     * @param sizeVariant
     *            The ControlSizeVariant for the Control.
     * @return the Styler with the added ControlSizeVariant.
     */
    public Styler<T> setSizeVariant(ControlSizeVariant sizeVariant) {
        this.sizeVariant = sizeVariant;
        check();
        return this;
    }

    /**
     * Retrieves the list with all StyleDefinitions added to the Styler.
     * 
     * @return The List of StyleDefinitions.
     */
    protected List<StyleDefinition> getAll() {
        List<StyleDefinition> ret = new ArrayList<>();
        ret.add(sizeVariant);
        return ret;
    }

    /**
     * The check method, which can be implemented in Styler subclasses to assure that no
     * incompatible Styles can be combined.
     */
    public void check() {}
}
