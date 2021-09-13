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
import com.jfoenix.skins.JFXToggleNodeSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.ColorConverter;
import javafx.beans.DefaultProperty;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFX Toggle Node , allows any node set as its graphic to be toggled
 * not that JFXToggleNode background color MUST match the unselected
 * color property, else the toggle animation will not be consistent.
 * Notice that the default value for unselected color is set to
 * transparent color.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "graphic")
public class JFXToggleNode extends ToggleButton {

    /**
     * {@inheritDoc}
     */
    public JFXToggleNode() {
        initialize();
    }

    public JFXToggleNode(String text) {
        super(text);
        initialize();
    }

    public JFXToggleNode(Node graphic) {
        super("", graphic);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXToggleNodeSkin(this);
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResources.load("css/controls/jfx-toggle-node.css").toExternalForm();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-toggle-node'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-toggle-node";

    /**
     * default color used when the node is toggled
     */
    private StyleableObjectProperty<Color> selectedColor = new SimpleStyleableObjectProperty<>(StyleableProperties.SELECTED_COLOR,
        JFXToggleNode.this,
        "selectedColor",
        Color.rgb(0,
            0,
            0,
            0.2));

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
     * default color used when the node is not toggled
     */
    private StyleableObjectProperty<Color> unSelectedColor = new SimpleStyleableObjectProperty<>(
        StyleableProperties.UNSELECTED_COLOR,
        JFXToggleNode.this,
        "unSelectedCOlor",
        Color.TRANSPARENT);

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
     * disable animation on button action
     */
    private StyleableBooleanProperty disableAnimation = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_ANIMATION,
        JFXToggleNode.this,
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


    /**
     * Disable the visual indicator for focus
     */
    private StyleableBooleanProperty disableVisualFocus = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_VISUAL_FOCUS,
        JFXToggleNode.this,
        "disableVisualFocus",
        false);

    /**
     * Setting this property disables this {@link JFXToggleNode} from showing keyboard focus.
     *
     * @return A property that will disable visual focus if true and enable it if false.
     */
    public final StyleableBooleanProperty disableVisualFocusProperty() {
        return this.disableVisualFocus;
    }

    /**
     * Indicates whether or not this {@link JFXToggleNode} will show focus when it receives keyboard focus.
     *
     * @return False if this {@link JFXToggleNode} will show visual focus and true if it will not.
     */
    public final Boolean isDisableVisualFocus() {
        return disableVisualFocus != null && this.disableVisualFocusProperty().get();
    }

    /**
     * Setting this to true will disable this {@link JFXToggleNode} from showing focus when it receives keyboard focus.
     *
     * @param disabled True to disable visual focus and false to enable it.
     */
    public final void setDisableVisualFocus(final Boolean disabled) {
        this.disableVisualFocusProperty().set(disabled);
    }


    private static class StyleableProperties {
        private static final CssMetaData<JFXToggleNode, Color> SELECTED_COLOR =
            new CssMetaData<JFXToggleNode, Color>("-jfx-toggle-color",
                ColorConverter.getInstance(), Color.rgb(255, 255, 255, 0.87)) {
                @Override
                public boolean isSettable(JFXToggleNode control) {
                    return control.selectedColor == null || !control.selectedColor.isBound();
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(JFXToggleNode control) {
                    return control.selectedColorProperty();
                }
            };

        private static final CssMetaData<JFXToggleNode, Color> UNSELECTED_COLOR =
            new CssMetaData<JFXToggleNode, Color>("-jfx-untoggle-color",
                ColorConverter.getInstance(), Color.TRANSPARENT) {
                @Override
                public boolean isSettable(JFXToggleNode control) {
                    return control.unSelectedColor == null || !control.unSelectedColor.isBound();
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(JFXToggleNode control) {
                    return control.unSelectedColorProperty();
                }
            };

        private static final CssMetaData<JFXToggleNode, Boolean> DISABLE_ANIMATION =
            new CssMetaData<JFXToggleNode, Boolean>("-jfx-disable-animation",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXToggleNode control) {
                    return control.disableAnimation == null || !control.disableAnimation.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXToggleNode control) {
                    return control.disableAnimationProperty();
                }
            };

        private static final CssMetaData<JFXToggleNode, Boolean> DISABLE_VISUAL_FOCUS =
            new CssMetaData<JFXToggleNode, Boolean>("-jfx-disable-visual-focus",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXToggleNode control) {
                    return control.disableVisualFocus == null || !control.disableVisualFocus.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXToggleNode control) {
                    return control.disableVisualFocusProperty();
                }
            };


        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(ToggleButton.getClassCssMetaData());
            Collections.addAll(styleables,
                SELECTED_COLOR,
                UNSELECTED_COLOR,
                DISABLE_ANIMATION,
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
