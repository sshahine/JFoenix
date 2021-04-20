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
import com.jfoenix.skins.JFXCheckBoxSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;
import javafx.css.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXCheckBox is the material design implementation of a checkbox.
 * it shows ripple effect and a custom selection animation.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXCheckBox extends CheckBox {

    /**
     * {@inheritDoc}
     */
    public JFXCheckBox(String text) {
        super(text);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXCheckBox() {
        initialize();
        // init in scene builder workaround ( TODO : remove when JFoenix is well integrated in scenebuilder by gluon )
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length && i < 15; i++) {
            if (stackTraceElements[i].getClassName().toLowerCase().contains(".scenebuilder.kit.fxom.")) {
                this.setText("CheckBox");
                break;
            }
        }
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXCheckBoxSkin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return USER_AGENT_STYLESHEET;
    }


    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-check-box'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-check-box";
    private static final String USER_AGENT_STYLESHEET = JFoenixResources.load("css/controls/jfx-check-box.css").toExternalForm();
    /**
     * checkbox color property when selected
     */
    private StyleableObjectProperty<Paint> checkedColor = new SimpleStyleableObjectProperty<>(StyleableProperties.CHECKED_COLOR,
        JFXCheckBox.this,
        "checkedColor",
        Color.valueOf(
            "#0F9D58"));

    public Paint getCheckedColor() {
        return checkedColor == null ? Color.valueOf("#0F9D58") : checkedColor.get();
    }

    public StyleableObjectProperty<Paint> checkedColorProperty() {
        return this.checkedColor;
    }

    public void setCheckedColor(Paint color) {
        this.checkedColor.set(color);
    }

    /**
     * checkbox color property when not selected
     */
    private StyleableObjectProperty<Paint> unCheckedColor = new SimpleStyleableObjectProperty<>(StyleableProperties.UNCHECKED_COLOR,
        JFXCheckBox.this,
        "unCheckedColor",
        Color.valueOf(
            "#5A5A5A"));

    public Paint getUnCheckedColor() {
        return unCheckedColor == null ? Color.valueOf("#5A5A5A") : unCheckedColor.get();
    }

    public StyleableObjectProperty<Paint> unCheckedColorProperty() {
        return this.unCheckedColor;
    }

    public void setUnCheckedColor(Paint color) {
        this.unCheckedColor.set(color);
    }

    /**
     * Disable the visual indicator for focus.
     */
    private StyleableBooleanProperty disableVisualFocus = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_VISUAL_FOCUS,
        JFXCheckBox.this,
        "disableVisualFocus",
        false);

    /**
     * Setting this property disables this {@link JFXCheckBox} from showing keyboard focus.
     * @return A property that will disable visual focus if true and enable it if false.
     */
    public final StyleableBooleanProperty disableVisualFocusProperty() {
        return this.disableVisualFocus;
    }

    /**
     * Indicates whether or not this {@link JFXCheckBox} will show focus when it receives keyboard focus.
     * @return False if this {@link JFXCheckBox} will show visual focus and true if it will not.
     */
    public final Boolean isDisableVisualFocus() {
        return disableVisualFocus != null && this.disableVisualFocusProperty().get();
    }

    /**
     * Setting this to true will disable this {@link JFXCheckBox} from showing focus when it receives keyboard focus.
     * @param disabled True to disable visual focus and false to enable it.
     */
    public final void setDisableVisualFocus(final Boolean disabled) {
        this.disableVisualFocusProperty().set(disabled);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXCheckBox, Paint> CHECKED_COLOR =
            new CssMetaData<JFXCheckBox, Paint>("-jfx-checked-color",
                PaintConverter.getInstance(), Color.valueOf("#0F9D58")) {
                @Override
                public boolean isSettable(JFXCheckBox control) {
                    return control.checkedColor == null || !control.checkedColor.isBound();
                }

                @Override
                public StyleableProperty<Paint> getStyleableProperty(JFXCheckBox control) {
                    return control.checkedColorProperty();
                }
            };
        private static final CssMetaData<JFXCheckBox, Paint> UNCHECKED_COLOR =
            new CssMetaData<JFXCheckBox, Paint>("-jfx-unchecked-color",
                PaintConverter.getInstance(), Color.valueOf("#5A5A5A")) {
                @Override
                public boolean isSettable(JFXCheckBox control) {
                    return control.unCheckedColor == null || !control.unCheckedColor.isBound();
                }

                @Override
                public StyleableProperty<Paint> getStyleableProperty(JFXCheckBox control) {
                    return control.unCheckedColorProperty();
                }
            };
        private static final CssMetaData<JFXCheckBox, Boolean> DISABLE_VISUAL_FOCUS =
            new CssMetaData<JFXCheckBox, Boolean>("-jfx-disable-visual-focus",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXCheckBox control) {
                    return control.disableVisualFocus == null || !control.disableVisualFocus.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXCheckBox control) {
                    return control.disableVisualFocusProperty();
                }
            };
        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(CheckBox.getClassCssMetaData());
            Collections.addAll(styleables,
                CHECKED_COLOR,
                UNCHECKED_COLOR,
                DISABLE_VISUAL_FOCUS
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
