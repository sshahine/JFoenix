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

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * <h1>Material Design ToggleButton Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXToggleButtonSkin extends ToggleButtonSkin {

    private Line line;

    private Circle circle;
    private final double circleRadius;
    private StackPane circleContainer = new StackPane();

    private JFXRippler rippler;
    private Timeline transition;
    private Runnable releaseManualRippler = null;

    public JFXToggleButtonSkin(JFXToggleButton toggleButton) {
        super(toggleButton);

        final double size = toggleButton.getSize();
        final double startY = 0;
        final double endX = size * 2 + 2;
        final double startX = 0;
        circleRadius = toggleButton.getSize();
        line = new Line(startX, startY, endX, startY);

        line.setStroke(toggleButton.getUnToggleLineColor());
        line.setStrokeWidth(size * 1.5);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        circle = new Circle(startX - circleRadius, startY, circleRadius);
        circle.setFill(toggleButton.getUnToggleColor());
        circle.setSmooth(true);
        JFXDepthManager.setDepth(circle, 1);


        StackPane circlePane = new StackPane();
        circlePane.getChildren().add(circle);
        circlePane.setPadding(new Insets(size * 1.5));
        rippler = new JFXRippler(circlePane, RipplerMask.CIRCLE, RipplerPos.BACK) {
//            @Override
//            protected void initListeners() {
//                ripplerPane.setOnMousePressed((event) -> {
//                    if (releaseManualRippler != null) {
//                        releaseManualRippler.run();
//                    }
//                    releaseManualRippler = null;
//                    createRipple(event.getX(), event.getY());
//                });
//            }
        };
        rippler.setRipplerFill(toggleButton.getUnToggleLineColor());

        circleContainer.getChildren().add(rippler);
        circleContainer.setTranslateX(-(line.getLayoutBounds().getWidth() / 2) + circleRadius);

        final StackPane main = new StackPane();
        main.getChildren().add(line);
        main.getChildren().add(circleContainer);
        main.setCursor(Cursor.HAND);

        // show focus traversal effect
        getSkinnable().armedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                releaseManualRippler = rippler.createManualRipple();
            } else if (releaseManualRippler != null) {
                releaseManualRippler.run();
            }
        });
        toggleButton.focusedProperty().addListener((o, oldVal, newVal) -> {
            if(!toggleButton.isDisableVisualFocus()) {
                if (newVal) {
                    if (!getSkinnable().isPressed()) {
                        rippler.showOverlay();
                    }
                } else {
                    rippler.hideOverlay();
                }
            }
        });
        toggleButton.pressedProperty().addListener((o, oldVal, newVal) -> rippler.hideOverlay());

        // add change listener to selected property
        getSkinnable().selectedProperty().addListener((o, oldVal, newVal) -> {
            rippler.setRipplerFill(newVal ? toggleButton.getToggleColor() : toggleButton.getUnToggleLineColor());
            transition.setRate(newVal ? 1 : -1);
            transition.play();
        });

        getSkinnable().setGraphic(main);

        updateToggleTransition();

        toggleButton.toggleColorProperty().addListener((o, oldVal, newVal) -> {
            updateToggleTransition();
            updateCircle();
        });
        toggleButton.unToggleColorProperty().addListener((o, oldVal, newVal) -> {
            updateToggleTransition();
            updateCircle();
        });
        toggleButton.toggleLineColorProperty().addListener((o, oldVal, newVal) -> {
            updateToggleTransition();
            updateLine();
        });
        toggleButton.unToggleLineColorProperty().addListener((o, oldVal, newVal) -> {
            updateToggleTransition();
            updateLine();
        });

        // init selected state
        rippler.setRipplerFill(getSkinnable().isSelected() ? toggleButton.getToggleColor() : toggleButton.getUnToggleLineColor());
        if (getSkinnable().isSelected()) {
            circleContainer.setTranslateX((line.getLayoutBounds().getWidth() / 2) - circleRadius);
            line.setStroke(((JFXToggleButton) getSkinnable()).getToggleLineColor());
            circle.setFill(((JFXToggleButton) getSkinnable()).getToggleColor());
        }
    }

    private void updateCircle() {
        circle.setFill(getSkinnable().isSelected() ? ((JFXToggleButton) getSkinnable()).getToggleColor() : ((JFXToggleButton) getSkinnable())
            .getUnToggleColor());
    }

    private void updateLine() {
        line.setStroke(getSkinnable().isSelected() ? ((JFXToggleButton) getSkinnable()).getToggleLineColor() : ((JFXToggleButton) getSkinnable())
            .getUnToggleLineColor());

    }

    private void updateToggleTransition() {
        transition = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(circleContainer.translateXProperty(),
                    -(line.getLayoutBounds().getWidth() / 2) + circleRadius,
                    Interpolator.EASE_BOTH),
                new KeyValue(line.strokeProperty(),
                    ((JFXToggleButton) getSkinnable()).getUnToggleLineColor(),
                    Interpolator.EASE_BOTH),
                new KeyValue(circle.fillProperty(),
                    ((JFXToggleButton) getSkinnable()).getUnToggleColor(),
                    Interpolator.EASE_BOTH)
            ),
            new KeyFrame(
                Duration.millis(100),
                new KeyValue(circleContainer.translateXProperty(),
                    (line.getLayoutBounds().getWidth() / 2) - circleRadius,
                    Interpolator.EASE_BOTH),
                new KeyValue(line.strokeProperty(),
                    ((JFXToggleButton) getSkinnable()).getToggleLineColor(),
                    Interpolator.EASE_BOTH),
                new KeyValue(circle.fillProperty(),
                    ((JFXToggleButton) getSkinnable()).getToggleColor(),
                    Interpolator.EASE_BOTH)
            )

        );
    }

}
