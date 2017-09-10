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

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * list of nodes that are toggled On/Off by clicking on the 1st node
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXNodesList extends VBox {

    private final HashMap<Node, Callback<Boolean, Collection<KeyValue>>> animationsMap = new HashMap<>();
    private boolean expanded = false;
    private final Timeline animateTimeline = new Timeline();

    /**
     * Creates empty nodes list.
     */
    public JFXNodesList() {
        this.setPickOnBounds(false);
        this.getStyleClass().add("jfx-nodes-list");
    }

    /**
     * Adds node to list.
     * Note: this method must be called instead of getChildren().add().
     *
     * @param node {@link Region} to add
     */
    public void addAnimatedNode(Region node) {
        addAnimatedNode(node, null);
    }

    /**
     * add node to list with a specified callback that is triggered after the node animation is finished.
     * Note: this method must be called instead of getChildren().add().
     *
     * @param node {@link Region} to add
     */
    public void addAnimatedNode(Region node, Callback<Boolean, Collection<KeyValue>> animationCallBack) {
        // create container for the node if it's a sub nodes list
        if (node instanceof JFXNodesList) {
            StackPane container = new StackPane(node);
            container.setPickOnBounds(false);
            addAnimatedNode(container, animationCallBack);
            return;
        }

        // init node property
        node.setVisible(false);
        if (this.getChildren().size() > 0) {
            initNode(node);
        } else {
            if (node instanceof Button) {
                ((Button) node).setOnAction((action) -> this.animateList());
            } else {
                node.setOnMouseClicked((click) -> this.animateList());
            }
            node.getStyleClass().add("trigger-node");
            node.setVisible(true);
        }

        // add the node and its listeners
        this.getChildren().add(node);
        this.rotateProperty()
            .addListener((o, oldVal, newVal) -> node.setRotate(newVal.doubleValue() % 180 == 0 ? newVal.doubleValue() : -newVal
                .doubleValue()));
        if (animationCallBack == null && this.getChildren().size() != 1) {
            animationCallBack = (expanded) -> initDefaultAnimation(node, expanded);
        } else if (animationCallBack == null && this.getChildren().size() == 1) {
            animationCallBack = (expanded) -> new ArrayList<>();
        }
        animationsMap.put(node, animationCallBack);
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

    /**
     * Animates the list to show/hide the nodes.
     */
    public void animateList() {
        expanded = !expanded;

        if (animateTimeline.getStatus() == Status.RUNNING) {
            animateTimeline.stop();
        }

        animateTimeline.getKeyFrames().clear();
        double duration = 120 / (double) this.getChildren().size();

        // show child nodes
        if (expanded) {
            this.getChildren().forEach(child -> child.setVisible(true));
        }

        // add child nodes animation
        for (int i = 1; i < this.getChildren().size(); i++) {
            Node child = this.getChildren().get(i);
            Collection<KeyValue> keyValues = animationsMap.get(child).call(expanded);
            animateTimeline.getKeyFrames()
                .add(new KeyFrame(Duration.millis(i * duration),
                    keyValues.toArray(new KeyValue[keyValues.size()])));
        }
        // add 1st element animation
        Collection<KeyValue> keyValues = animationsMap.get(this.getChildren().get(0)).call(expanded);
        animateTimeline.getKeyFrames()
            .add(new KeyFrame(Duration.millis(160), keyValues.toArray(new KeyValue[keyValues.size()])));

        // hide child nodes to allow mouse events on the nodes behind them
        if (!expanded) {
            animateTimeline.setOnFinished((finish) -> {
                for (int i = 1; i < this.getChildren().size(); i++) {
                    this.getChildren().get(i).setVisible(false);
                }
            });
        } else {
            animateTimeline.setOnFinished(null);
        }

        animateTimeline.play();
    }

    protected void initNode(Node node) {
        node.setScaleX(0);
        node.setScaleY(0);
        node.getStyleClass().add("sub-node");
    }

    // init default animation keyvalues
    private ArrayList<KeyValue> initDefaultAnimation(Region region, boolean expanded) {
        ArrayList<KeyValue> defaultAnimationValues = new ArrayList<>();
        defaultAnimationValues.add(new KeyValue(region.scaleXProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH));
        defaultAnimationValues.add(new KeyValue(region.scaleYProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH));
        return defaultAnimationValues;
    }
}
