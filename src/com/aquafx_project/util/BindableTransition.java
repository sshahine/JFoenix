package com.aquafx_project.util;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

/**
 * A simple Transition thats fraction property can be bound to any other
 * properties.
 * 
 * @author hendrikebbers
 * 
 */
public class BindableTransition extends Transition {

    private DoubleProperty fraction;

    public BindableTransition(Duration duration) {
        fraction = new SimpleDoubleProperty();
        setCycleDuration(duration);
    }

    @Override
    protected final void interpolate(double frac) {
        fraction.set(frac);
    }

    public ReadOnlyDoubleProperty fractionProperty() {
        return fraction;
    }
}
