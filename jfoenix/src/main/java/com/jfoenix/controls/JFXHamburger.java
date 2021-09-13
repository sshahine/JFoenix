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

import com.jfoenix.transitions.hamburger.HamburgerTransition;
import javafx.animation.Transition;
import javafx.beans.DefaultProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * the famous animated hamburger icon used in material design
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "animation")
public class JFXHamburger extends VBox {

    private Transition animation;

    /**
     * creates a hamburger icon
     */
    public JFXHamburger() {

        StackPane line1 = new StackPane();
        StackPane line2 = new StackPane();
        StackPane line3 = new StackPane();

        initStyle(line1);
        initStyle(line2);
        initStyle(line3);

        this.getChildren().add(line1);
        this.getChildren().add(line2);
        this.getChildren().add(line3);

        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.setAlignment(Pos.CENTER);
        this.setFillWidth(false);
        this.setSpacing(4);
    }

    /**
     * @return the current animation of the hamburger
     */
    public Transition getAnimation() {
        return animation;
    }

    /**
     * set a specified {@link HamburgerTransition}
     *
     * @param animation
     */
    public void setAnimation(Transition animation) {
        this.animation = ((HamburgerTransition) animation).getAnimation(this);
        this.animation.setRate(-1);
    }

    private void initStyle(StackPane pane) {
        pane.setOpacity(1);
        pane.setPrefSize(30, 4);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)));
    }

    /**
     * Initialize the style class to 'jfx-hamburger'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-hamburger";

}
