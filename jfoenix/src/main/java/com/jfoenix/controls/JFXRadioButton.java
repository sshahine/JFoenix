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
import com.jfoenix.skins.JFXRadioButtonSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.ColorConverter;
import javafx.css.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXRadioButton is the material design implementation of a radio button.
 *
 * @author Bashir Elias & Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXRadioButton extends RadioButton {

    /**
     * {@inheritDoc}
     */
    public JFXRadioButton(String text) {
        super(text);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXRadioButton() {
        initialize();
        // init in scene builder workaround ( TODO : remove when JFoenix is well integrated in scenebuilder by gluon )
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length && i < 15; i++) {
            if (stackTraceElements[i].getClassName().toLowerCase().contains(".scenebuilder.kit.fxom.")) {
                this.setText("RadioButton");
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXRadioButtonSkin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return USER_AGENT_STYLESHEET;
    }


    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * Initialize the style class to 'jfx-radio-button'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-radio-button";
    private static final String USER_AGENT_STYLESHEET = JFoenixResources.load("css/controls/jfx-radio-button.css").toExternalForm();

    /**
     * default color used when the radio button is selected
     */
    private StyleableObjectProperty<Color> selectedColor = new SimpleStyleableObjectProperty<>(StyleableProperties.SELECTED_COLOR,
        JFXRadioButton.this,
        "selectedColor",
        Color.valueOf(
            "#0F9D58"));

    public final StyleableObjectProperty<Color> selectedColorProperty() {
        return this.selectedColor;
    }

    public final Color getSelectedColor() {
        return selectedColor == null ? Color.rgb(0, 0, 0, 0.2) : this.selectedColorProperty().get();
    }

    public final void setSelectedColor(final Color selectedColor) {
        this.selectedColorProperty().set(selectedColor);
    }

    /**
     * default color used when the radio button is not selected
     */
    private StyleableObjectProperty<Color> unSelectedColor = new SimpleStyleableObjectProperty<>(
        StyleableProperties.UNSELECTED_COLOR,
        JFXRadioButton.this,
        "unSelectedColor",
        Color.valueOf("#5A5A5A"));

    public final StyleableObjectProperty<Color> unSelectedColorProperty() {
        return this.unSelectedColor;
    }

    public final Color getUnSelectedColor() {
        return unSelectedColor == null ? Color.TRANSPARENT : this.unSelectedColorProperty().get();
    }

    public final void setUnSelectedColor(final Color unSelectedColor) {
        this.unSelectedColorProperty().set(unSelectedColor);
    }

    /**
     * Disable the visual indicator for focus
     */
    private StyleableBooleanProperty disableVisualFocus = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_VISUAL_FOCUS,
        JFXRadioButton.this,
        "disableVisualFocus",
        false);

    public final StyleableBooleanProperty disableVisualFocusProperty() {
        return this.disableVisualFocus;
    }

    public final Boolean isDisableVisualFocus() {
        return disableVisualFocus != null && this.disableVisualFocusProperty().get();
    }

    public final void setDisableVisualFocus(final Boolean disabled) {
        this.disableVisualFocusProperty().set(disabled);
    }

    /**
     * disable animation on button action
     */
    private StyleableBooleanProperty disableAnimation = new SimpleStyleableBooleanProperty(JFXRadioButton.StyleableProperties.DISABLE_ANIMATION,
        JFXRadioButton.this,
        "disableAnimation",
        false);

    public final StyleableBooleanProperty disableAnimationProperty() {
        return this.disableAnimation;
    }

    public final Boolean isDisableAnimation() {
        return disableAnimation != null && this.disableAnimationProperty().get();
    }

    public final void setDisableAnimation(final Boolean disabled) {
        this.disableAnimationProperty().set(disabled);
    }


    private static class StyleableProperties {
        private static final CssMetaData<JFXRadioButton, Color> SELECTED_COLOR =
            new CssMetaData<JFXRadioButton, Color>("-jfx-selected-color",
                ColorConverter.getInstance(), Color.valueOf("#0F9D58")) {
                @Override
                public boolean isSettable(JFXRadioButton control) {
                    return control.selectedColor == null || !control.selectedColor.isBound();
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(JFXRadioButton control) {
                    return control.selectedColorProperty();
                }
            };
        private static final CssMetaData<JFXRadioButton, Color> UNSELECTED_COLOR =
            new CssMetaData<JFXRadioButton, Color>("-jfx-unselected-color",
                ColorConverter.getInstance(), Color.valueOf("#5A5A5A")) {
                @Override
                public boolean isSettable(JFXRadioButton control) {
                    return control.unSelectedColor == null || !control.unSelectedColor.isBound();
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(JFXRadioButton control) {
                    return control.unSelectedColorProperty();
                }
            };
        private static final CssMetaData<JFXRadioButton, Boolean> DISABLE_VISUAL_FOCUS =
            new CssMetaData<JFXRadioButton, Boolean>("-jfx-disable-visual-focus",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXRadioButton control) {
                    return control.disableVisualFocus == null || !control.disableVisualFocus.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXRadioButton control) {
                    return control.disableVisualFocusProperty();
                }
            };

        private static final CssMetaData<JFXRadioButton, Boolean> DISABLE_ANIMATION =
            new CssMetaData<JFXRadioButton, Boolean>("-jfx-disable-animation",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXRadioButton control) {
                    return control.disableAnimation == null || !control.disableAnimation.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXRadioButton control) {
                    return control.disableAnimationProperty();
                }
            };


        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(RadioButton.getClassCssMetaData());
            Collections.addAll(styleables,
                SELECTED_COLOR,
                UNSELECTED_COLOR,
                DISABLE_VISUAL_FOCUS,
                DISABLE_ANIMATION
            );
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
