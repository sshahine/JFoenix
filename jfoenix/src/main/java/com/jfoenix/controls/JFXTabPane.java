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
import com.jfoenix.skins.JFXTabPaneSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXTabPane is the material design implementation of a tab pane.
 *
 * TODO: REWORK SWITCH ANIMATION
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTabPane extends TabPane {
    /**
     * Initialize the style class to 'jfx-tab-pane'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-tab-pane";
    private static final String USER_AGENT_STYLESHEET = JFoenixResources.load("css/controls/jfx-tab-pane.css").toExternalForm();

    /**
     * {@inheritDoc}
     */
    public JFXTabPane() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXTabPaneSkin(this);
    }

    private void initialize() {
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return USER_AGENT_STYLESHEET;
    }

    /**
     * propagate any mouse events on the tab pane to its parent
     */
    public void propagateMouseEventsToParent() {
        this.addEventHandler(MouseEvent.ANY, e -> {
            e.consume();
            this.getParent().fireEvent(e);
        });
    }

    /**
     * disable animation on validation
     */
    private StyleableBooleanProperty disableAnimation = new SimpleStyleableBooleanProperty(JFXTabPane.StyleableProperties.DISABLE_ANIMATION,
        JFXTabPane.this,
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
        private static final CssMetaData<JFXTabPane, Boolean> DISABLE_ANIMATION =
            new CssMetaData<JFXTabPane, Boolean>("-jfx-disable-animation",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXTabPane control) {
                    return control.disableAnimation == null || !control.disableAnimation.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXTabPane control) {
                    return control.disableAnimationProperty();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                TabPane.getClassCssMetaData());
            Collections.addAll(styleables, DISABLE_ANIMATION);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return JFXTabPane.StyleableProperties.CHILD_STYLEABLES;
    }

}
