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

package com.jfoenix.skins;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.scene.control.skin.ProgressIndicatorSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * <h1>Material Design ProgressBar Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-10-06
 */
public class JFXProgressBarSkin extends ProgressIndicatorSkin {

    private StackPane track;
    private StackPane secondaryBar;
    private StackPane bar;
    private double barWidth = 0;
    private double secondaryBarWidth = 0;

    private Region clip;

    public JFXProgressBarSkin(JFXProgressBar bar) {
        super(bar);
        bar.widthProperty().addListener(observable -> {
            updateProgress();
            updateSecondaryProgress();
        });
        registerChangeListener(bar.secondaryProgressProperty(), "SECONDARY_PROGRESS");
        registerChangeListener(bar.visibleProperty(), "VISIBLE");
        registerChangeListener(bar.parentProperty(), "PARENT");
        registerChangeListener(bar.sceneProperty(), "SCENE");
        getSkinnable().requestLayout();
    }

    public void initialize() {

        track = new StackPane();
        track.getStyleClass().setAll("track");

        bar = new StackPane();
        bar.getStyleClass().setAll("bar");

        secondaryBar = new StackPane();
        secondaryBar.getStyleClass().setAll("secondary-bar");

        clip = new Region();
        clip.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        bar.backgroundProperty().addListener(observable -> JFXNodeUtils.updateBackground(bar.getBackground(), clip));

        getChildren().setAll(track, secondaryBar, bar);
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("SECONDARY_PROGRESS".equals(p)) {
            updateSecondaryProgress();
        } else if ("VISIBLE".equals(p)) {
            updateAnimation();
        } else if ("PARENT".equals(p)) {
            updateAnimation();
        } else if ("SCENE".equals(p)) {
            updateAnimation();
        }
    }

    @Override
    public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(100, leftInset + bar.prefWidth(getSkinnable().getWidth()) + rightInset);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + bar.prefHeight(width) + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        track.resizeRelocate(x, y, w, h);
        secondaryBar.resizeRelocate(x, y, secondaryBarWidth, h);
        bar.resizeRelocate(x, y, getSkinnable().isIndeterminate() ? w : barWidth, h);
        clip.resizeRelocate(0,0, w, h);

        if (getSkinnable().isIndeterminate()) {
            createIndeterminateTimeline();
            if (getSkinnable().impl_isTreeVisible()) {
                indeterminateTransition.play();
            }
            // apply clip
            bar.setClip(clip);
        } else if (indeterminateTransition != null) {
            clearAnimation();
            // remove clip
            bar.setClip(null);
        }
    }

    protected void updateSecondaryProgress() {
        final JFXProgressBar control = (JFXProgressBar) getSkinnable();
        secondaryBarWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2
                             * Math.min(1, Math.max(0, control.getSecondaryProgress()))) / 2.0F;
        control.requestLayout();
    }

    boolean wasIndeterminate = false;

    @Override
    protected void pauseTimeline(boolean pause) {
        if (getSkinnable().isIndeterminate()) {
            if (indeterminateTransition == null) {
                createIndeterminateTimeline();
            }
            if (pause) {
                indeterminateTransition.pause();
            } else {
                indeterminateTransition.play();
            }
        }
    }

    @Override
    protected void updateAnimation() {
        ProgressIndicator control = getSkinnable();
        final boolean isTreeVisible = control.isVisible() &&
                                      control.getParent() != null &&
                                      control.getScene() != null;
        if (indeterminateTransition != null) {
            pauseTimeline(!isTreeVisible);
        } else if (isTreeVisible) {
            createIndeterminateTimeline();
        }
    }

    @Override
    protected void updateProgress() {
        final ProgressIndicator control = getSkinnable();
        final boolean isIndeterminate = control.isIndeterminate();
        if (!(isIndeterminate && wasIndeterminate)) {
            barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2
                        * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
            control.requestLayout();
        }
        wasIndeterminate = isIndeterminate;
    }

    @Override
    protected void createIndeterminateTimeline() {
        if (indeterminateTransition != null) {
            clearAnimation();
        }
        double dur = 1;
        ProgressIndicator control = getSkinnable();
        final double w = control.getWidth() - (snappedLeftInset() + snappedRightInset());
        indeterminateTransition = new Timeline(new KeyFrame(
            Duration.ZERO,
            new KeyValue(clip.scaleXProperty(), 0.0, Interpolator.EASE_IN),
            new KeyValue(clip.translateXProperty(), -w/2, Interpolator.LINEAR)
        ),
            new KeyFrame(
                Duration.seconds(0.5* dur),
                new KeyValue(clip.scaleXProperty(), 0.4, Interpolator.LINEAR)
            ),
            new KeyFrame(
                Duration.seconds(0.9 * dur),
                new KeyValue(clip.translateXProperty(), w/2, Interpolator.LINEAR)
            ),
            new KeyFrame(
                Duration.seconds(1 * dur),
                new KeyValue(clip.scaleXProperty(), 0.0, Interpolator.EASE_OUT)
            ));
        indeterminateTransition.setCycleCount(Timeline.INDEFINITE);
    }

    private void clearAnimation() {
        indeterminateTransition.stop();
        ((Timeline) indeterminateTransition).getKeyFrames().clear();
        indeterminateTransition = null;
    }

    @Override
    public void dispose() {
        super.dispose();

        if (indeterminateTransition != null) {
            clearAnimation();
        }
    }
}
