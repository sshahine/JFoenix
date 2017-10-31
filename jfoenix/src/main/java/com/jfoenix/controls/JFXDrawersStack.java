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

import com.jfoenix.utils.JFXNodeUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * DrawersStack is used to show multiple drawers at once
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "content")
public class JFXDrawersStack extends StackPane {

    private Node content;

    /**
     * creates empty drawers stack
     */
    public JFXDrawersStack() {

    }

    /**
     * @return the content of the drawer stack (Note: the parent of the content node is that latest active drawer if
     * existed)
     */
    public Node getContent() {
        return this.content;
    }

    /**
     * set the content of the drawers stack, similar to {@link JFXDrawer#setContent(Node...)}
     *
     * @param content
     */
    public void setContent(Node content) {
        int index = 0;
        boolean replace = false;
        if (this.content != null) {
            index = getChildren().indexOf(this.content);
            replace = true;
        }
        this.content = content;
        if (replace) {
            this.getChildren().set(index, this.content);
        } else {
            this.getChildren().add(0, this.content);
        }
    }

    /**
     * add JFXDrawer to the stack
     *
     * @param drawer
     */
    private void addDrawer(JFXDrawer drawer) {
        if (drawer == null) {
            return;
        }

        getChildren().add(drawer);

        drawer.setPickOnBounds(false);

        JFXNodeUtils.addPressAndHoldHandler(drawer.sidePane, Duration.millis(300), event -> {
            drawer.bringToFront((param) -> {
                drawer.toFront();
                return param;
            });
        });
    }

    /**
     * toggle a drawer in the stack
     *
     * @param drawer the drawer to be toggled
     */
    public void toggle(JFXDrawer drawer) {
        if (!getChildren().contains(drawer)) {
            addDrawer(drawer);
        }
        if (drawer.isShown() || drawer.isShowing()) {
            drawer.close();
        } else {
            drawer.toFront();
            drawer.open();
        }
    }

    /**
     * toggle on/off a drawer in the stack
     *
     * @param drawer the drawer to be toggled
     * @param show   true to toggle on, false to toggle off
     */
    public void toggle(JFXDrawer drawer, boolean show) {
        if (!getChildren().contains(drawer)) {
            addDrawer(drawer);
        }
        if (!show) {
            if (drawer.isShown() || drawer.isShowing()) {
                drawer.close();
            }
        } else {
            if (!drawer.isShown() && !drawer.isShowing()) {
                drawer.toFront();
                drawer.open();
            }
        }
    }


}
