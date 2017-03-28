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
