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
import javafx.beans.DefaultProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * DrawersStack is used to show multiple drawers at once
 *
 * UPDATE : DrawersStack extends Region instead of StackPane to
 * encapsulate the getChildren() method and hide it from the user.
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "content")
public class JFXDrawersStack extends Region {

    private List<JFXDrawer> drawers = new ArrayList<>();
    private Node content;
    private boolean performingLayout;

    /**
     * creates empty drawers stack
     */
    public JFXDrawersStack() {
        final Rectangle clip = new Rectangle();
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());
        this.setClip(clip);
    }


    @Override public void requestLayout() {
        if (performingLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override protected void layoutChildren() {
        performingLayout = true;
        List<Node> managed = getManagedChildren();
        final double width = getWidth();
        double height = getHeight();
        double top = getInsets().getTop();
        double right = getInsets().getRight();
        double left = getInsets().getLeft();
        double bottom = getInsets().getBottom();
        double contentWidth = width - left - right;
        double contentHeight = height - top - bottom;
        for (int i = 0, size = managed.size(); i < size; i++) {
            Node child = managed.get(i);
            layoutInArea(child, left, top, contentWidth, contentHeight,
                0, Insets.EMPTY,
                HPos.CENTER, VPos.CENTER);
        }
        performingLayout = false;
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
        this.content = content;
        if (drawers.size() > 0) {
            drawers.get(0).setContent(content);
        } else {
            getChildren().add(this.content);
        }
    }

    /**
     * add JFXDrawer to the stack
     *
     * NOTE: this method will also be called when {@link JFXDrawersStack#toggle(JFXDrawer)} to add
     * the drawer if not added
     * @param drawer
     */
    public void addDrawer(JFXDrawer drawer) {
        if (drawer == null) {
            return;
        }

        if (drawers.isEmpty()) {
            if (content != null) {
                drawer.setContent(content);
            }
        } else {
            drawer.setContent(drawers.get(drawers.size() - 1));
        }

        drawers.add(drawer);
        getChildren().setAll(drawer);
        drawer.setPickOnBounds(false);

        // add listener to bring drawer to front on hold
        JFXNodeUtils.addPressAndHoldHandler(drawer.sidePane, Duration.millis(300), event -> {
            if (drawers.indexOf(drawer) < drawers.size() - 1) {
                drawer.bringToFront((param) -> {
                    updateDrawerPosition(drawer);
                    return param;
                });
            }
        });
    }


    private void updateDrawerPosition(JFXDrawer drawer) {
        int index = drawers.indexOf(drawer);
        if (index + 1 < drawers.size()) {
            // handle previous
            if (index - 1 >= 0) {
                drawers.get(index + 1).setContent(drawers.get(index - 1));
            } else if (index == 0) {
                drawers.get(index + 1).setContent(content);
            }
            drawer.setContent(drawers.get(drawers.size() - 1));
            drawers.remove(drawer);
            drawers.add(drawer);
            getChildren().setAll(drawer);
        }
    }

    /**
     * toggle a drawer in the stack
     *
     * @param drawer the drawer to be toggled
     */
    public void toggle(JFXDrawer drawer) {
        if (!drawers.contains(drawer)) {
            addDrawer(drawer);
        }
        if (drawer.isOpened() || drawer.isOpening()) {
            drawer.close();
        } else {
            updateDrawerPosition(drawer);
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
        if (!drawers.contains(drawer)) {
            addDrawer(drawer);
        }
        if (!show) {
            if (drawer.isOpened() || drawer.isOpening()) {
                drawer.close();
            }
        } else {
            if (!drawer.isOpened() && !drawer.isOpening()) {
                updateDrawerPosition(drawer);
                drawer.open();
            }
        }
    }


}
