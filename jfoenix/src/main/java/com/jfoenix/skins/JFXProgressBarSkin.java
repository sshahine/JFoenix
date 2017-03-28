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

import com.jfoenix.controls.JFXProgressBar;
import com.sun.javafx.scene.control.skin.ProgressBarSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * <h1>Material Design ProgressBar Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXProgressBarSkin extends ProgressBarSkin {

    private static Color indicatorColor = Color.valueOf("#0F9D58");
    private static Color trackColor = Color.valueOf("#E0E0E0");

    private StackPane bar;
    private StackPane track;
    private Region clip;

    public JFXProgressBarSkin(JFXProgressBar bar) {
        super(bar);
    }

    public void initialize() {
        super.initialize();
        bar = (StackPane) getChildren().get(1);
        bar.setBackground(new Background(new BackgroundFill(indicatorColor, CornerRadii.EMPTY, Insets.EMPTY)));
        bar.setPadding(new Insets(1.5));
        track = (StackPane) getChildren().get(0);
        track.setBackground(new Background(new BackgroundFill(trackColor, CornerRadii.EMPTY, Insets.EMPTY)));
        clip = new Region();
        clip.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        getSkinnable().setClip(clip);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        clip.resizeRelocate(x, y, w, h);
    }

    @Override
    protected void createIndeterminateTimeline() {
        super.createIndeterminateTimeline();
        if (indeterminateTransition != null) indeterminateTransition.stop();
        ProgressIndicator control = getSkinnable();
        final double w = control.getWidth() - (snappedLeftInset() + snappedRightInset());
        final double bWidth = bar.getWidth();
        indeterminateTransition = new Timeline(new KeyFrame(
            Duration.ZERO,
            new KeyValue(bar.scaleXProperty(), 0, Interpolator.EASE_IN),
            new KeyValue(bar.translateXProperty(), -bWidth, Interpolator.LINEAR)
        ),
                                               new KeyFrame(
                                                   Duration.seconds(0.5),
                                                   new KeyValue(bar.scaleXProperty(), 3, Interpolator.LINEAR),
                                                   new KeyValue(bar.translateXProperty(), w / 2, Interpolator.LINEAR)),
                                               new KeyFrame(
                                                   Duration.seconds(1),
                                                   new KeyValue(bar.scaleXProperty(), 0, Interpolator.EASE_OUT),
                                                   new KeyValue(bar.translateXProperty(), w, Interpolator.LINEAR)));
        indeterminateTransition.setCycleCount(Timeline.INDEFINITE);


    }
}
