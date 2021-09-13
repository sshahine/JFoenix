/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.controls;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.skins.JFXSpinnerSkin;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXSpinner is the material design implementation of a loading spinner.
 *
 * @author Bashir Elias & Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXSpinner extends ProgressIndicator {

    public static final double INDETERMINATE_PROGRESS = -1;

    public JFXSpinner() {
        this(INDETERMINATE_PROGRESS);
    }

    public JFXSpinner(double progress) {
        super(progress);
        init();
    }

    private void init(){
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXSpinnerSkin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResources.load("css/controls/jfx-spinner.css").toExternalForm();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-spinner'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-spinner";


    /**
     * specifies the radius of the spinner node, by default it's set to -1 (USE_COMPUTED_SIZE)
     */
    private StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(StyleableProperties.RADIUS,
        JFXSpinner.this,
        "radius",
        Region.USE_COMPUTED_SIZE);

    public final StyleableDoubleProperty radiusProperty() {
        return this.radius;
    }

    public final double getRadius() {
        return this.radiusProperty().get();
    }

    public final void setRadius(final double radius) {
        this.radiusProperty().set(radius);
    }

    /**
     * specifies from which angle the spinner should start spinning
     */
    private StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(StyleableProperties.STARTING_ANGLE,
        JFXSpinner.this,
        "starting_angle",
        360 - Math.random() * 720);

    public final StyleableDoubleProperty startingAngleProperty() {
        return this.startingAngle;
    }

    public final double getStartingAngle() {
        return this.startingAngleProperty().get();
    }

    public final void setStartingAngle(final double startingAngle) {
        this.startingAngleProperty().set(startingAngle);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXSpinner, Number> RADIUS =
            new CssMetaData<JFXSpinner, Number>("-jfx-radius",
                SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
                @Override
                public boolean isSettable(JFXSpinner control) {
                    return control.radius == null || !control.radius.isBound();
                }

                @Override
                public StyleableDoubleProperty getStyleableProperty(JFXSpinner control) {
                    return control.radius;
                }
            };

        private static final CssMetaData<JFXSpinner, Number> STARTING_ANGLE =
            new CssMetaData<JFXSpinner, Number>("-jfx-starting-angle",
                SizeConverter.getInstance(), 360 - Math.random() * 720) {
                @Override
                public boolean isSettable(JFXSpinner control) {
                    return control.startingAngle == null || !control.startingAngle.isBound();
                }

                @Override
                public StyleableDoubleProperty getStyleableProperty(JFXSpinner control) {
                    return control.startingAngle;
                }
            };


        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(ProgressIndicator.getClassCssMetaData());
            Collections.addAll(styleables, RADIUS, STARTING_ANGLE);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }


}
