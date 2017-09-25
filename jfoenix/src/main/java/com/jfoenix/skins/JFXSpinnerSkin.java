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

package com.jfoenix.skins;

import com.jfoenix.controls.JFXSpinner;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Collections;

/**
 * JFXSpinner material design skin
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-09-25
 */
public class JFXSpinnerSkin extends BehaviorSkinBase<JFXSpinner, BehaviorBase<JFXSpinner>> {

    boolean invalid = true;
    private JFXSpinner control;

    private Color greenColor;
    private Color redColor;
    private Color yellowColor;
    private Color blueColor;
    private Timeline timeline;
    private Arc arc;
    private final StackPane arcPane;
    private final Rectangle fillRect;

    public JFXSpinnerSkin(JFXSpinner control) {
        super(control, new BehaviorBase<JFXSpinner>(control, Collections.emptyList()));

        blueColor = Color.valueOf("#4285f4");
        redColor = Color.valueOf("#db4437");
        yellowColor = Color.valueOf("#f4b400");
        greenColor = Color.valueOf("#0F9D58");

        arc = new Arc();
        arc.setManaged(false);
        arc.setStartAngle(0);
        arc.setLength(180);
        arc.getStyleClass().setAll("arc");
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeWidth(3);

        fillRect = new Rectangle();
        fillRect.setFill(Color.TRANSPARENT);
        final Group group = new Group(fillRect, arc);
        group.setManaged(false);
        arcPane = new StackPane(group);
        arcPane.setPrefSize(50, 50);

        getChildren().setAll(arcPane);

        this.control = control;

        // register listeners
        registerChangeListener(control.indeterminateProperty(), "INDETERMINATE");
        registerChangeListener(control.progressProperty(), "PROGRESS");
        registerChangeListener(control.visibleProperty(), "VISIBLE");
        registerChangeListener(control.parentProperty(), "PARENT");
        registerChangeListener(control.sceneProperty(), "SCENE");
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("VISIBLE".equals(p)) {
            updateAnimation();
        } else if ("PARENT".equals(p)) {
            updateAnimation();
        } else if ("SCENE".equals(p)) {
            updateAnimation();
        }
    }

    private KeyFrame[] getKeyFrames(double angle, double duration, Color color) {
        KeyFrame[] frames = new KeyFrame[4];
        frames[0] = new KeyFrame(Duration.seconds(duration),
            new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR),
            new KeyValue(arc.startAngleProperty(),
                angle + 45 + control.getStartingAngle(),
                Interpolator.LINEAR));
        frames[1] = new KeyFrame(Duration.seconds(duration + 0.4),
            new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR),
            new KeyValue(arc.startAngleProperty(),
                angle + 90 + control.getStartingAngle(),
                Interpolator.LINEAR));
        frames[2] = new KeyFrame(Duration.seconds(duration + 0.7),
            new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR),
            new KeyValue(arc.startAngleProperty(),
                angle + 135 + control.getStartingAngle(),
                Interpolator.LINEAR));
        frames[3] = new KeyFrame(Duration.seconds(duration + 1.1),
            new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR),
            new KeyValue(arc.startAngleProperty(),
                angle + 435 + control.getStartingAngle(),
                Interpolator.LINEAR),
            new KeyValue(arc.strokeProperty(), color, Interpolator.EASE_BOTH));
        return frames;
    }

    private void pauseTimeline(boolean pause) {
        if (getSkinnable().isIndeterminate()) {
            if (timeline == null) {
                createTransition();
            }
            if (pause) {
                timeline.pause();
            } else {
                timeline.play();
            }
        }
    }

    private void updateAnimation() {
        ProgressIndicator control = getSkinnable();
        final boolean isTreeVisible = control.isVisible() &&
                                      control.getParent() != null &&
                                      control.getScene() != null;
        if (timeline != null) {
            pauseTimeline(!isTreeVisible);
        } else if (isTreeVisible) {
            createTransition();
        }
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (Region.USE_COMPUTED_SIZE == control.getRadius()) {
            return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
        } else {
            return control.getRadius() * 2 + arc.getStrokeWidth() * 2;
        }
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (Region.USE_COMPUTED_SIZE == control.getRadius()) {
            return super.computeMaxHeight(height, topInset, rightInset, bottomInset, leftInset);
        } else {
            return control.getRadius() * 2 + arc.getStrokeWidth() * 2;
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return arcPane.prefWidth(-1);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return arcPane.prefHeight(-1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        final double strokeWidth = arc.getStrokeWidth();
        final double radius = Math.min(contentWidth, contentHeight) / 2 - strokeWidth / 2;
        final double arcSize = snapSize(radius * 2 + strokeWidth);
        arcPane.resizeRelocate((contentWidth - arcSize) / 2 + 1, (contentHeight - arcSize) / 2 + 1, arcSize, arcSize);

        fillRect.setWidth(arcSize);
        fillRect.setHeight(arcSize);

        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
        arc.setCenterX(arcSize / 2);
        arc.setCenterY(arcSize / 2);

        if (invalid) {
            createTransition();
            timeline.playFromStart();
            invalid = false;
        }
    }

    private void createTransition() {
        final Color initialColor = (Color) arc.getStroke();
        if (initialColor == null) {
            arc.setStroke(blueColor);
        }

        KeyFrame[] blueFrame = getKeyFrames(0, 0, initialColor == null ? blueColor : initialColor);
        KeyFrame[] redFrame = getKeyFrames(450, 1.4, initialColor == null ? redColor : initialColor);
        KeyFrame[] yellowFrame = getKeyFrames(900, 2.8, initialColor == null ? yellowColor : initialColor);
        KeyFrame[] greenFrame = getKeyFrames(1350, 4.2, initialColor == null ? greenColor : initialColor);

        KeyFrame endingFrame = new KeyFrame(Duration.seconds(5.6),
            new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR),
            new KeyValue(arc.startAngleProperty(),
                1845 + control.getStartingAngle(),
                Interpolator.LINEAR));

        if (timeline != null) {
            timeline.stop();
            timeline.getKeyFrames().clear();
        }
        timeline = new Timeline(blueFrame[0],
            blueFrame[1],
            blueFrame[2],
            blueFrame[3],
            redFrame[0],
            redFrame[1],
            redFrame[2],
            redFrame[3],
            yellowFrame[0],
            yellowFrame[1],
            yellowFrame[2],
            yellowFrame[3],
            greenFrame[0],
            greenFrame[1],
            greenFrame[2],
            greenFrame[3],
            endingFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setDelay(Duration.ZERO);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }

        if (arc != null) {
            arc = null;
        }

        control = null;
    }
}
