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

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.transitions.CachedTransition;
import com.jfoenix.transitions.JFXFillTransition;
import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import com.sun.javafx.scene.control.skin.LabeledSkinBase;
import javafx.animation.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * <h1>Material Design CheckBox Skin v1.1</h1>
 * the old skin is still supported using {@link JFXCheckBoxOldSkin}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-09-06
 */
public class JFXCheckBoxSkin extends LabeledSkinBase<JFXCheckBox, ButtonBehavior<JFXCheckBox>> {

    private final StackPane box = new StackPane();
    private final StackPane mark = new StackPane();
    private final StackPane indeterminateMark = new StackPane();
    private final JFXRippler rippler;

    private Transition transition;
    private Transition indeterminateTransition;

    private boolean invalid = true;
    private JFXFillTransition select;
    private final StackPane boxContainer;

    public JFXCheckBoxSkin(JFXCheckBox control) {
        super(control, new ButtonBehavior<>(control));

        indeterminateMark.getStyleClass().setAll("indeterminate-mark");
        indeterminateMark.setOpacity(0);
        indeterminateMark.setScaleX(0);
        indeterminateMark.setScaleY(0);

        mark.getStyleClass().setAll("mark");
        mark.setOpacity(0);
        mark.setScaleX(0);
        mark.setScaleY(0);

        box.getStyleClass().setAll("box");
        box.setBorder(new Border(new BorderStroke(control.getUnCheckedColor(),
            BorderStrokeStyle.SOLID,
            new CornerRadii(2),
            new BorderWidths(2))));
        box.getChildren().setAll(indeterminateMark, mark);

        boxContainer = new StackPane();
        boxContainer.getChildren().add(box);
        boxContainer.getStyleClass().add("box-container");
        rippler = new JFXRippler(boxContainer, RipplerMask.CIRCLE, JFXRippler.RipplerPos.BACK);

        updateRippleColor();

        // add listeners
        control.selectedProperty().addListener( observable -> {
            updateRippleColor();
            playSelectAnimation(control.isSelected(), true);
        });
        control.indeterminateProperty().addListener(observable -> {
            updateRippleColor();
            playIndeterminateAnimation(control.isIndeterminate(), true);
        });

        // show focused state
        control.focusedProperty().addListener((o, oldVal, newVal) -> {
            if(!control.isDisableVisualFocus()){
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
        updateChildren();

        // create animation
        transition = new CheckBoxTransition(mark);
        indeterminateTransition = new CheckBoxTransition(indeterminateMark);
        createFillTransition();

        registerChangeListener(control.checkedColorProperty(), "CHECKED_COLOR");
    }

    private void updateRippleColor() {
        rippler.setRipplerFill(getSkinnable().isSelected() ? ((JFXCheckBox) getSkinnable()).getCheckedColor() : ((JFXCheckBox) getSkinnable())
            .getUnCheckedColor());
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("CHECKED_COLOR".equals(p)) {
            createFillTransition();
        }
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippler != null) {
            getChildren().add(rippler);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset)
               + snapSize(box.minWidth(-1)) + getLabelOffset();
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset)
               + snapSize(box.prefWidth(-1)) + getLabelOffset();
    }

    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width - box.minWidth(-1), topInset, rightInset, bottomInset, leftInset),
            topInset + box.minHeight(-1) + bottomInset);
    }

    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computePrefHeight(width - box.prefWidth(-1), topInset, rightInset, bottomInset, leftInset),
            topInset + box.prefHeight(-1) + bottomInset);
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double labelOffset = getLabelOffset();
        final CheckBox checkBox = getSkinnable();
        final double boxWidth = snapSize(box.prefWidth(-1));
        final double boxHeight = snapSize(box.prefHeight(-1));
        final double computeWidth = Math.min(checkBox.prefWidth(-1), checkBox.minWidth(-1));
        final double labelWidth = Math.min(computeWidth - boxWidth, w - snapSize(boxWidth)) + labelOffset;
        final double labelHeight = Math.min(checkBox.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(boxHeight, labelHeight);
        final double xOffset = computeXOffset(w, labelWidth + boxWidth, checkBox.getAlignment().getHpos()) + x;
        final double yOffset = computeYOffset(h, maxHeight, checkBox.getAlignment().getVpos()) + x;

        if (invalid) {
            if(checkBox.isIndeterminate()){
                playIndeterminateAnimation(true, false);
            }else if (checkBox.isSelected()) {
                playSelectAnimation(true, false);
            }
            invalid = false;
        }

        layoutLabelInArea(xOffset + boxWidth + labelOffset, yOffset, labelWidth, maxHeight, checkBox.getAlignment());
        rippler.resize(boxWidth, boxHeight);
        positionInArea(rippler,
            xOffset, yOffset,
            boxWidth, maxHeight, 0,
            checkBox.getAlignment().getHpos(),
            checkBox.getAlignment().getVpos());

    }

    private double getLabelOffset() {
        return 0.2 * boxContainer.snappedRightInset();
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

    private void playSelectAnimation(Boolean selection, boolean playAnimation) {
        if (selection == null) {
            selection = false;
        }
        transition.setRate(selection ? 1 : -1);
        select.setRate(selection ? 1 : -1);
        if (playAnimation) {
            transition.play();
            select.play();
        } else {
            CornerRadii radii = box.getBackground() == null ?
                null : box.getBackground().getFills().get(0).getRadii();
            Insets insets = box.getBackground() == null ?
                null : box.getBackground().getFills().get(0).getInsets();
            if (selection) {
                mark.setScaleY(1);
                mark.setScaleX(1);
                mark.setOpacity(1);
                box.setBackground(new Background(new BackgroundFill(getSkinnable().getCheckedColor(), radii, insets)));
                select.playFrom(select.getCycleDuration());
                transition.playFrom(transition.getCycleDuration());
            } else {
                mark.setScaleY(0);
                mark.setScaleX(0);
                mark.setOpacity(0);
                box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, radii, insets)));
                select.playFrom(Duration.ZERO);
                transition.playFrom(Duration.ZERO);
            }
        }
        box.setBorder(new Border(new BorderStroke(selection ? getSkinnable().getCheckedColor() : getSkinnable().getUnCheckedColor(),
            BorderStrokeStyle.SOLID,
            new CornerRadii(2),
            new BorderWidths(2))));
    }

    private void playIndeterminateAnimation(Boolean indeterminate, boolean playAnimation) {
        if (indeterminate == null) {
            indeterminate = false;
        }
        indeterminateTransition.setRate(indeterminate ? 1 : -1);
        if (playAnimation) {
            indeterminateTransition.play();
        } else {
            if (indeterminate) {
                CornerRadii radii = indeterminateMark.getBackground() == null ?
                    null : indeterminateMark.getBackground().getFills().get(0).getRadii();
                Insets insets = indeterminateMark.getBackground() == null ?
                    null : indeterminateMark.getBackground().getFills().get(0).getInsets();
                indeterminateMark.setOpacity(1);
                indeterminateMark.setScaleY(1);
                indeterminateMark.setScaleX(1);
                indeterminateMark.setBackground(new Background(new BackgroundFill(getSkinnable().getCheckedColor(), radii, insets)));
                indeterminateTransition.playFrom(indeterminateTransition.getCycleDuration());
            } else {
                indeterminateMark.setOpacity(0);
                indeterminateMark.setScaleY(0);
                indeterminateMark.setScaleX(0);
                indeterminateTransition.playFrom(Duration.ZERO);
            }
        }

        if(getSkinnable().isSelected()){
            if(indeterminate)
                mark.setVisible(false);
            playSelectAnimation(!indeterminate, playAnimation);
        }else if(!getSkinnable().isSelected()){
            if(!indeterminate)
                mark.setVisible(false);
        }
    }

    private void createFillTransition() {
        select = new JFXFillTransition(Duration.millis(120),
            box,
            Color.TRANSPARENT,
            (Color) ((JFXCheckBox) getSkinnable()).getCheckedColor());
        select.setInterpolator(Interpolator.EASE_OUT);
    }

    private final class CheckBoxTransition extends CachedTransition {
        CheckBoxTransition(Node mark) {
            super(mark, new Timeline(
                    new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(mark.opacityProperty(), 0, Interpolator.EASE_OUT),
                        new KeyValue(mark.scaleXProperty(), 0.5, Interpolator.EASE_OUT),
                        new KeyValue(mark.scaleYProperty(), 0.5, Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(Duration.millis(400),
                        new KeyValue(mark.opacityProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(mark.scaleXProperty(), 0.5, Interpolator.EASE_OUT),
                        new KeyValue(mark.scaleYProperty(), 0.5, Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(
                        Duration.millis(1000),
                        new KeyValue(mark.scaleXProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(mark.scaleYProperty(), 1, Interpolator.EASE_OUT)
                    )
                )
            );
            // reduce the number to increase the shifting , increase number to reduce shifting
            setCycleDuration(Duration.seconds(0.12));
            setDelay(Duration.seconds(0.05));
        }

        @Override
        protected void starting() {
            super.starting();
        }

        @Override
        protected void stopping() {
            super.stopping();
            node.setOpacity(getRate() == 1 ? 1 : 0);
            node.setVisible(true);
        }
    }
}
