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
import com.sun.javafx.scene.control.skin.CheckBoxSkin;
import javafx.animation.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * <h1>Material Design CheckBox Skin v1.1</h1>
 * the old skin is still supported using {@link JFXCheckBoxOldSkin}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-09-06
 */
public class JFXCheckBoxSkin extends CheckBoxSkin {

    private final StackPane box = new StackPane();
    private final StackPane mark = new StackPane();
    private double lineThick = 2;
    private double padding = 10;
    private final JFXRippler rippler;


    private final AnchorPane container = new AnchorPane();
    private double labelOffset = -8;

    private Transition transition;

    private boolean invalid = true;
    private JFXFillTransition select;

    public JFXCheckBoxSkin(JFXCheckBox control) {
        super(control);

        box.setMinSize(18, 18);
        box.setPrefSize(18, 18);
        box.setMaxSize(18, 18);
        box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(2), Insets.EMPTY)));
        box.setBorder(new Border(new BorderStroke(control.getUnCheckedColor(),
            BorderStrokeStyle.SOLID,
            new CornerRadii(2),
            new BorderWidths(lineThick))));
        //
        StackPane boxContainer = new StackPane();
        boxContainer.getChildren().add(box);
        boxContainer.setPadding(new Insets(padding));
        rippler = new JFXRippler(boxContainer, RipplerMask.CIRCLE, JFXRippler.RipplerPos.BACK);
        updateRippleColor();

        SVGPath shape = new SVGPath();
        shape.setContent("M384 690l452-452 60 60-512 512-238-238 60-60z");
        mark.setShape(shape);
        mark.setMaxSize(15, 12);
        mark.setStyle("-fx-background-color:WHITE; -fx-border-color:WHITE; -fx-border-width:2px;");
        mark.setVisible(false);
        mark.setScaleX(0);
        mark.setScaleY(0);
        boxContainer.getChildren().add(mark);

        container.getChildren().add(rippler);
        AnchorPane.setRightAnchor(rippler, labelOffset);

        // add listeners
        control.selectedProperty().addListener((o, oldVal, newVal) -> {
            updateRippleColor();
            playSelectAnimation(newVal);
        });

        // show focused state
        control.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                if (!getSkinnable().isPressed()) {
                    rippler.showOverlay();
                }
            } else {
                rippler.hideOverlay();
            }
        });
        control.pressedProperty().addListener((o, oldVal, newVal) -> rippler.hideOverlay());

        updateChildren();

        registerChangeListener(control.checkedColorProperty(), "CHECKED_COLOR");

        // create animation
        transition = new CheckBoxTransition();
        createFillTransition();
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
        if (container != null) {
            getChildren().remove(1);
            getChildren().add(container);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height,
            topInset,
            rightInset,
            bottomInset,
            leftInset) + snapSize(box.minWidth(-1)) + labelOffset + 2 * padding;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height,
            topInset,
            rightInset,
            bottomInset,
            leftInset) + snapSize(box.prefWidth(-1)) + labelOffset + 2 * padding;
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {

        final CheckBox checkBox = getSkinnable();
        final double boxWidth = snapSize(container.prefWidth(-1));
        final double boxHeight = snapSize(container.prefHeight(-1));
        final double computeWidth = Math.min(checkBox.prefWidth(-1), checkBox.minWidth(-1)) + labelOffset + 2 * padding;
        final double labelWidth = Math.min(computeWidth - boxWidth, w - snapSize(boxWidth)) + labelOffset + 2 * padding;
        final double labelHeight = Math.min(checkBox.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(boxHeight, labelHeight);
        final double xOffset = computeXOffset(w, labelWidth + boxWidth, checkBox.getAlignment().getHpos()) + x;
        final double yOffset = computeYOffset(h, maxHeight, checkBox.getAlignment().getVpos()) + x;

        if (invalid) {
            if (getSkinnable().isSelected()) {
                playSelectAnimation(true);
            }
            invalid = false;
        }

        layoutLabelInArea(xOffset + boxWidth, yOffset, labelWidth, maxHeight, checkBox.getAlignment());
        container.resize(boxWidth, boxHeight);
        positionInArea(container,
            xOffset,
            yOffset,
            boxWidth,
            maxHeight,
            0,
            checkBox.getAlignment().getHpos(),
            checkBox.getAlignment().getVpos());

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

    private void playSelectAnimation(Boolean selection) {
        if (selection == null) {
            selection = false;
        }
        JFXCheckBox control = (JFXCheckBox) getSkinnable();
        transition.setRate(selection ? 1 : -1);
        select.setRate(selection ? 1 : -1);
        transition.play();
        select.play();
        box.setBorder(new Border(new BorderStroke(selection ? control.getCheckedColor() : control.getUnCheckedColor(),
            BorderStrokeStyle.SOLID,
            new CornerRadii(2),
            new BorderWidths(lineThick))));
    }

    private void createFillTransition() {
        select = new JFXFillTransition(Duration.millis(120),
            box,
            Color.TRANSPARENT,
            (Color) ((JFXCheckBox) getSkinnable()).getCheckedColor());
        select.setInterpolator(Interpolator.EASE_OUT);
    }

    private final class CheckBoxTransition extends CachedTransition {
        CheckBoxTransition() {
            super(mark, new Timeline(
                    new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(mark.visibleProperty(), false, Interpolator.EASE_BOTH),
                        new KeyValue(mark.scaleXProperty(), 0.5, Interpolator.EASE_OUT),
                        new KeyValue(mark.scaleYProperty(), 0.5, Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(Duration.millis(400),
                        new KeyValue(mark.visibleProperty(), true, Interpolator.EASE_OUT),
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

    }


}
