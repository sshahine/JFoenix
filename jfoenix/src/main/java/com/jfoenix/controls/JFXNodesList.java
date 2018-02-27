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

import javafx.animation.*;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * list of nodes that are toggled On/Off by clicking on the 1st node
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXNodesList extends VBox {

    private static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    private static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            Object value = node.getProperties().get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static final String ALIGN_NODE_CONSTRAINT = "align-node";

    /**
     * set a child node as the alignment controller when applying alignments on
     * the nodes list.
     * @param node
     * @param child
     */
    public static void alignNodeToChild(Node node, Node child) {
        setConstraint(node, ALIGN_NODE_CONSTRAINT, child);
    }

    public static Node getAlignNodeToChild(Node node) {
        return (Node)getConstraint(node, ALIGN_NODE_CONSTRAINT);
    }


    private final HashMap<Node, BiFunction<Boolean, Duration, Collection<KeyFrame>>> animationsMap = new HashMap<>();
    private boolean expanded = false;
    private final Timeline animateTimeline = new Timeline();

    /**
     * Creates empty nodes list.
     */
    public JFXNodesList() {
        setPickOnBounds(false);
        getStyleClass().add("jfx-nodes-list");
        setAlignment(Pos.TOP_CENTER);
    }

    /**
     * Adds node to list.
     * Note: this method must be called instead of getChildren().add().
     *
     * @param node {@link Region} to add
     */
    public void addAnimatedNode(Region node) {
        addAnimatedNode(node, null, true);
    }

    /**
     * Adds node to list.
     * Note: this method must be called instead of getChildren().add().
     *
     * @param node {@link Region} to add
     */
    public void addAnimatedNode(Region node, boolean addTriggerListener) {
        addAnimatedNode(node, null, addTriggerListener);
    }

    public void addAnimatedNode(Region node, BiFunction<Boolean, Duration, Collection<KeyFrame>> animationFramesFunction){
        addAnimatedNode(node, animationFramesFunction, true);
    }
    /**
     * add node to list with a specified callback that is triggered after the node animation is finished.
     * Note: this method must be called instead of getChildren().add().
     *
     * @param node {@link Region} to add
     */
    public void addAnimatedNode(Region node, BiFunction<Boolean, Duration, Collection<KeyFrame>> animationFramesFunction, boolean addTriggerListener) {
        // create container for the node if it's a sub nodes list
        if (node instanceof JFXNodesList) {
            StackPane container = new StackPane(node);
            container.setPickOnBounds(false);
            addAnimatedNode(container, animationFramesFunction, addTriggerListener);
            return;
        }
        // init node property and its listeners
        initChild(node, getChildren().size(), animationFramesFunction, addTriggerListener);
        // add the node
        getChildren().add(node);
    }

    private void initChild(Node node, int index, BiFunction<Boolean, Duration, Collection<KeyFrame>> animationFramesFunction, boolean addTriggerListener) {
        if (index > 0) {
            initNode(node);
            node.setVisible(false);
        } else {
            if (addTriggerListener) {
                if (node instanceof Button) {
                    node.addEventHandler(ActionEvent.ACTION, event -> animateList());
                } else {
                    node.addEventHandler(MouseEvent.MOUSE_CLICKED, event-> animateList());
                }
            }
            node.getStyleClass().add("trigger-node");
            node.setVisible(true);
        }

        if (animationFramesFunction == null && index != 0) {
            animationFramesFunction = initDefaultAnimation(node);
        } else if (animationFramesFunction == null && index == 0) {
            animationFramesFunction = (aBoolean, duration) -> new ArrayList<>();
        }

        animationsMap.put(node, animationFramesFunction);
    }

    @Override
    protected double computePrefWidth(double height) {
        if (!getChildren().isEmpty()) {
            return getChildren().get(0).prefWidth(height);
        }
        return super.computePrefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        if (!getChildren().isEmpty()) {
            return getChildren().get(0).prefHeight(width);
        }
        return super.computePrefHeight(width);
    }

    @Override
    protected double computeMinHeight(double width) {
        return computePrefHeight(width);
    }

    @Override
    protected double computeMinWidth(double height) {
        return computePrefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return computePrefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return computePrefWidth(height);
    }

    private boolean performingLayout = false;
    @Override public void requestLayout() {
        if (performingLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    protected void layoutChildren() {
        performingLayout = true;

        List<Node> children = getChildren();

        Insets insets = getInsets();
        double width = getWidth();
        double rotate = getRotate();
        double height = getHeight();
        double left = snapSpace(insets.getLeft());
        double right = snapSpace(insets.getRight());
        double space = snapSpace(getSpacing());
        boolean isFillWidth = isFillWidth();
        double contentWidth = width - left - right;


        Pos alignment = getAlignment();
        alignment = alignment == null ? Pos.TOP_CENTER : alignment;
        final HPos hpos = alignment.getHpos();
        final VPos vpos = alignment.getVpos();

        double y = 0;

        for (int i = 0, size = children.size(); i < size; i++) {
            Node child = children.get(i);
            child.autosize();
            child.setRotate(rotate % 180 == 0 ? rotate : -rotate);

            // init child node if not added using addAnimatedChild method
            if (!animationsMap.containsKey(child)) {
                if (child instanceof JFXNodesList) {
                    StackPane container = new StackPane(child);
                    container.setPickOnBounds(false);
                    getChildren().set(i, container);
                }
                initChild(child, i, null, true);
            }

            double x = 0;
            double childWidth = child.getLayoutBounds().getWidth();
            double childHeight = child.getLayoutBounds().getHeight();


            if(childWidth > width){
                switch (hpos) {
                    case CENTER:
                        x = snapPosition(contentWidth - childWidth) / 2;
                        break;
                }
                Node alignToChild = getAlignNodeToChild(child);
                if (alignToChild != null && child instanceof Parent) {
                    ((Parent) child).layout();
                    double alignedWidth = alignToChild.getLayoutBounds().getWidth();
                    double alignedX = alignToChild.getLayoutX();
                    if(childWidth / 2 > alignedX + alignedWidth){
                        alignedWidth = -(childWidth / 2 - (alignedWidth/2 + alignedX));
                    }else{
                        alignedWidth = alignedWidth/2 + alignedX - childWidth / 2;
                    }
                    child.setTranslateX(-alignedWidth * Math.cos(Math.toRadians(rotate)));
                    child.setTranslateY(alignedWidth * Math.cos(Math.toRadians(90 - rotate)));
                }
            }else{
                childWidth = contentWidth;
            }

            final Insets margin = getMargin(child);
            if (margin != null) {
                childWidth += margin.getLeft() + margin.getRight();
                childHeight += margin.getTop() + margin.getRight();
            }

            layoutInArea(child, x, y, childWidth, childHeight,
                /* baseline shouldn't matter */0,
                margin, isFillWidth, true, hpos, vpos);

            y += child.getLayoutBounds().getHeight() + space;
            if (margin != null) {
                y += margin.getTop() + margin.getBottom();
            }
            y = snapPosition(y);
        }

        performingLayout = false;
    }

    /**
     * Animates the list to show/hide the nodes.
     */
    public void animateList() {
        expanded = !expanded;
        if (animateTimeline.getStatus() == Status.RUNNING) {
            animateTimeline.stop();
        }
        animateTimeline.getKeyFrames().clear();
        createAnimation(expanded, animateTimeline);
        animateTimeline.play();
    }

    public void animateList(boolean expand){
        if ((expanded && !expand) || (!expanded && expand)) {
            animateList();
        }
    }

    public boolean isExpanded(){
        return expanded;
    }

    public Animation getListAnimation(boolean expanded){
        Timeline animation = new Timeline();
        createAnimation(expanded, animation);
        return animation;
    }

    private void createAnimation(boolean expanded, Timeline animation) {
        double duration = 160 / (double) getChildren().size();
        // show child nodes
        if (expanded) {
            getChildren().forEach(child -> child.setVisible(true));
        }

        // add child nodes animation
        for (int i = 1; i < getChildren().size(); i++) {
            Node child = getChildren().get(i);
            Collection<KeyFrame> frames = animationsMap.get(child).apply(expanded, Duration.millis(i * duration));
            animation.getKeyFrames().addAll(frames);
        }
        // add 1st element animation
        Collection<KeyFrame> frames = animationsMap.get(getChildren().get(0)).apply(expanded, Duration.millis(160));
        animation.getKeyFrames().addAll(frames);

        // hide child nodes to allow mouse events on the nodes behind them
        if (!expanded) {
            animation.setOnFinished((finish) -> {
                for (int i = 1; i < getChildren().size(); i++) {
                    getChildren().get(i).setVisible(false);
                }
            });
        } else {
            animation.setOnFinished(null);
        }
    }

    private BiFunction<Boolean, Duration, Collection<KeyFrame>> initDefaultAnimation(Node child) {
        return (expanded, duration) -> {
            ArrayList<KeyFrame> frames = new ArrayList<>();
            frames.add(new KeyFrame(duration, event -> {
                child.setScaleX(expanded ? 1 : 0);
                child.setScaleY(expanded ? 1 : 0);
            },
                new KeyValue(child.scaleXProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH),
                new KeyValue(child.scaleYProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH)
            ));
            return frames;
        };
    }

    protected void initNode(Node node) {
        node.setScaleX(0);
        node.setScaleY(0);
        node.getStyleClass().add("sub-node");
    }
}
