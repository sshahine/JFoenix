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

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * <h1>Material Design Radio Button Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-09-29
 */
public class JFXRadioButtonSkin extends RadioButtonSkin {

    private boolean invalid = true;
    private double padding = 15;
    private final JFXRippler rippler;

    private Circle radio, dot;
    private Timeline timeline;

    private final AnchorPane container = new AnchorPane();
    private double labelOffset = -10;

    public JFXRadioButtonSkin(JFXRadioButton control) {
        super(control);

        final double radioRadius = 7;
        radio = new Circle(radioRadius);
        radio.getStyleClass().setAll("radio");
        radio.setStrokeWidth(2);
        radio.setFill(Color.TRANSPARENT);

        dot = new Circle();
        dot.getStyleClass().setAll("dot");
        dot.setRadius(radioRadius);
        dot.fillProperty().bind(control.selectedColorProperty());
        dot.setScaleX(0);
        dot.setScaleY(0);

        StackPane boxContainer = new StackPane();
        boxContainer.getChildren().addAll(radio, dot);
        boxContainer.setPadding(new Insets(padding));
        rippler = new JFXRippler(boxContainer, RipplerMask.CIRCLE);
        container.getChildren().add(rippler);
        AnchorPane.setRightAnchor(rippler, labelOffset);
        updateChildren();

        // show focused state
        control.focusedProperty().addListener((o, oldVal, newVal) -> {
            if(!control.disableVisualFocusProperty().get()) {
                if (newVal) {
                    if (!getSkinnable().isPressed()) {
                        rippler.setOverlayVisible(true);
                    }
                } else {
                    rippler.setOverlayVisible(false);
                }
            }
        });
        control.pressedProperty().addListener((o, oldVal, newVal) -> rippler.setOverlayVisible(false));

        registerChangeListener(control.selectedColorProperty(), "SELECTED_COLOR");
        registerChangeListener(control.unSelectedColorProperty(), "UNSELECTED_COLOR");
        registerChangeListener(control.selectedProperty(), "SELECTED");
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (radio != null) {
            removeRadio();
            getChildren().add(container);
        }
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("SELECTED_COLOR".equals(p)) {
            // update animation
            updateAnimation();
            // update current colors
            boolean isSelected = getSkinnable().isSelected();
            Color unSelectedColor = ((JFXRadioButton) getSkinnable()).getUnSelectedColor();
            Color selectedColor = ((JFXRadioButton) getSkinnable()).getSelectedColor();
            rippler.setRipplerFill(isSelected ? selectedColor : unSelectedColor);
            if (isSelected) {
                radio.strokeProperty().set(selectedColor);
            }
        } else if ("UNSELECTED_COLOR".equals(p)) {
            // update animation
            updateAnimation();
            // update current colors
            boolean isSelected = getSkinnable().isSelected();
            Color unSelectedColor = ((JFXRadioButton) getSkinnable()).getUnSelectedColor();
            Color selectedColor = ((JFXRadioButton) getSkinnable()).getSelectedColor();
            rippler.setRipplerFill(isSelected ? selectedColor : unSelectedColor);
            if (!isSelected) {
                radio.strokeProperty().set(unSelectedColor);
            }
        } else if ("SELECTED".equals(p)) {
            // update ripple color
            boolean isSelected = getSkinnable().isSelected();
            Color unSelectedColor = ((JFXRadioButton) getSkinnable()).getUnSelectedColor();
            Color selectedColor = ((JFXRadioButton) getSkinnable()).getSelectedColor();
            rippler.setRipplerFill(isSelected ? selectedColor : unSelectedColor);
            if(timeline == null) updateAnimation();
            // play selection animation
            playAnimation();
        }
    }


    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final RadioButton radioButton = getSkinnable();
        final double contWidth = snapSize(container.prefWidth(-1)) + (invalid ? 2 : 0);
        final double contHeight = snapSize(container.prefHeight(-1)) + (invalid ? 2 : 0);
        final double computeWidth = Math.min(radioButton.prefWidth(-1),
            radioButton.minWidth(-1)) + labelOffset + 2 * padding;
        final double labelWidth = Math.min(computeWidth - contWidth,
            w - snapSize(contWidth)) + labelOffset + 2 * padding;
        final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(contHeight, labelHeight);
        final double xOffset = computeXOffset(w, labelWidth + contWidth, radioButton.getAlignment().getHpos()) + x;
        final double yOffset = computeYOffset(h, maxHeight, radioButton.getAlignment().getVpos()) + x;

        if (invalid) {
            initializeComponents();
            invalid = false;
        }
        layoutLabelInArea(xOffset + contWidth, yOffset, labelWidth, maxHeight, radioButton.getAlignment());
        ((Text) getChildren().get((getChildren().get(0) instanceof Text) ? 0 : 1)).textProperty()
            .set(getSkinnable().textProperty()
                .get());
        container.resize(snapSize(contWidth), snapSize(contHeight));
        positionInArea(container,
            xOffset,
            yOffset,
            contWidth,
            maxHeight,
            0,
            radioButton.getAlignment().getHpos(),
            radioButton.getAlignment().getVpos());
    }

    private void initializeComponents() {
        Color unSelectedColor = ((JFXRadioButton) getSkinnable()).getUnSelectedColor();
        Color selectedColor = ((JFXRadioButton) getSkinnable()).getSelectedColor();
        radio.setStroke(unSelectedColor);
        rippler.setRipplerFill(getSkinnable().isSelected() ? selectedColor : unSelectedColor);
        updateAnimation();
        playAnimation();
    }

    private void playAnimation() {
        timeline.setRate(getSkinnable().isSelected() ? 1 : -1);
        timeline.play();
    }

    private void updateAnimation() {
        Color unSelectedColor = ((JFXRadioButton) getSkinnable()).getUnSelectedColor();
        Color selectedColor = ((JFXRadioButton) getSkinnable()).getSelectedColor();
        timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(dot.scaleXProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(dot.scaleYProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(radio.strokeProperty(), unSelectedColor, Interpolator.EASE_BOTH)),

            new KeyFrame(Duration.millis(200),
                new KeyValue(dot.scaleXProperty(), 0.6, Interpolator.EASE_BOTH),
                new KeyValue(dot.scaleYProperty(), 0.6, Interpolator.EASE_BOTH),
                new KeyValue(radio.strokeProperty(), selectedColor, Interpolator.EASE_BOTH)));
    }

    private void removeRadio() {
        for (int i = 0; i < getChildren().size(); i++) {
            if ("radio".equals(getChildren().get(i).getStyleClass().get(0))) {
                getChildren().remove(i);
            }
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height,
            topInset,
            rightInset,
            bottomInset,
            leftInset) + snapSize(radio.minWidth(-1)) + labelOffset + 2 * padding;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height,
            topInset,
            rightInset,
            bottomInset,
            leftInset) + snapSize(radio.prefWidth(-1)) + labelOffset + 2 * padding;
    }

    static double computeXOffset(double width, double contentWidth, HPos hpos) {
        switch (hpos) {
            case LEFT:
                return 0;
            case CENTER:
                return (width - contentWidth) / 2;
            case RIGHT:
                return width - contentWidth;
        }
        return 0;
    }

    static double computeYOffset(double height, double contentHeight, VPos vpos) {

        switch (vpos) {
            case TOP:
                return 0;
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            default:
                return 0;
        }
    }


}
