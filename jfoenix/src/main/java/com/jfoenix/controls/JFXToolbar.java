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

import com.jfoenix.effects.JFXDepthManager;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * JFXToolbar is the material design implementation of a tool bar.
 * toolbar is a borderpane, where the right/left content are HBoxs
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXToolbar extends BorderPane {

    private HBox leftBox = new HBox();
    private HBox rightBox = new HBox();

    /**
     * creates empty tool bar
     */
    public JFXToolbar() {
        initialize();
        this.setLeft(leftBox);
        leftBox.getStyleClass().add("tool-bar-left-box");
        leftBox.setPickOnBounds(false);
        this.setRight(rightBox);
        rightBox.getStyleClass().add("tool-bar-right-box");
        rightBox.setPickOnBounds(false);
        JFXDepthManager.setDepth(this, 1);
    }

    /***************************************************************************
     *                                                                         *
     * Setters / Getters                                                       *
     *                                                                         *
     **************************************************************************/

    public void setLeftItems(Node... nodes) {
        this.leftBox.getChildren().setAll(nodes);
    }

    public ObservableList<Node> getLeftItems() {
        return this.leftBox.getChildren();
    }

    public void setRightItems(Node... nodes) {
        this.rightBox.getChildren().setAll(nodes);
    }

    public ObservableList<Node> getRightItems() {
        return this.rightBox.getChildren();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-tool-bar'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-tool-bar";

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }


}
