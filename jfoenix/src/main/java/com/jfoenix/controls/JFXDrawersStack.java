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

import com.jfoenix.cache.CachePolicy;
import com.jfoenix.utils.JFXNodeUtils;
import javafx.beans.DefaultProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * DrawersStack is used to show multiple drawers at once
 * <p>
 * UPDATE : DrawersStack extends Region instead of StackPane to
 * encapsulate the getChildren() method and hide it from the user.
 *
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


    @Override
    public void requestLayout() {
        if (performingLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    protected void layoutChildren() {
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
     * <p>
     * NOTE: this method is also called inside {@link JFXDrawersStack#toggle(JFXDrawer)} to add
     * the drawer if not added
     *
     * @param drawer
     */
    public void addDrawer(JFXDrawer drawer) {
        if (drawer == null) {
            return;
        }

        if (drawer.getCachePolicy().equals(CachePolicy.IMAGE)) {
            throw new RuntimeException("Drawer is using unsupported cache strategy inside JFXDrawerStack");
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

    /**
     * @return a list of sides that corresponds to the current drawers order
     */
    public List<Side> getOpenedDrawersOrder() {
        List<Side> order = new ArrayList<>();
        for (int i = 0, drawersSize = drawers.size(); i < drawersSize; i++) {
            final JFXDrawer jfxDrawer = drawers.get(i);
            if (jfxDrawer.isOpened()) {
                order.add(Side.valueOf(jfxDrawer.getDirection().toString()));
            }
        }
        return order;
    }
}
