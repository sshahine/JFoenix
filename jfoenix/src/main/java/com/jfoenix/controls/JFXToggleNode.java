/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jfoenix.controls;

import com.jfoenix.skins.JFXToggleNodeSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.ColorConverter;
import javafx.beans.DefaultProperty;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
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
        return getClass().getResource("/css/controls/jfx-toggle-node.css").toExternalForm();
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

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                SELECTED_COLOR,
                UNSELECTED_COLOR,
                DISABLE_ANIMATION
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Control.getClassCssMetaData());
            styleables.addAll(getClassCssMetaData());
            styleables.addAll(Labeled.getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }

}
