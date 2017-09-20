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
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.transitions.JFXFillTransition;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2016-03-09
 */
public class JFXToggleNodeSkin extends ToggleButtonSkin {

    private final StackPane selectionOverLay;
    private JFXRippler rippler;
    private JFXFillTransition ft;
    private Runnable releaseManualRippler = null;

    public JFXToggleNodeSkin(JFXToggleNode toggleNode) {
        super(toggleNode);

        selectionOverLay = new StackPane();
        selectionOverLay.getChildren().add(getSkinnable().getGraphic());
        rippler = new JFXRippler(selectionOverLay, RipplerPos.FRONT) {
            @Override
            protected Node getMask() {
                StackPane mask = createMask();
                selectionOverLay.setClip(createMask());
                return mask;
            }

            private StackPane createMask() {
                StackPane mask = new StackPane();
                mask.shapeProperty().bind(getSkinnable().shapeProperty());
                mask.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
                    return new Background(new BackgroundFill(Color.WHITE,
                        getSkinnable().getBackground() != null
                        && getSkinnable().getBackground().getFills().size() > 0 ?
                            getSkinnable().getBackground().getFills().get(0).getRadii() : CornerRadii.EMPTY,
                        getSkinnable().getBackground() != null
                        && getSkinnable().getBackground().getFills().size() > 0 ?
                            getSkinnable().getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
                }, getSkinnable().backgroundProperty()));
                mask.resize(getWidth() - snappedRightInset() - snappedLeftInset(),
                    getHeight() - snappedBottomInset() - snappedTopInset());
                return mask;
            }

            @Override
            protected void initListeners() {
                ripplerPane.setOnMousePressed((event) -> {
                    if (releaseManualRippler != null) {
                        releaseManualRippler.run();
                    }
                    releaseManualRippler = null;
                    createRipple(event.getX(), event.getY());
                });
            }
        };

        // listener to change background color
        getSkinnable().selectedProperty().addListener((o, oldVal, newVal) -> {
            // show animation only on user action
            if (!toggleNode.isDisableAnimation()) {
                if (ft == null) {
                    ft = new JFXFillTransition(Duration.millis(120), selectionOverLay);
                    ft.toValueProperty().bind(toggleNode.selectedColorProperty());
                    ft.fromValueProperty().bind(toggleNode.unSelectedColorProperty());
                }
                ft.stop();
                ft.setRate(newVal ? 1 : -1);
                ft.play();
            } else {
                // disable animation if the selected property changed from code
                CornerRadii radii = getSkinnable().getBackground() == null ? CornerRadii.EMPTY : getSkinnable().getBackground()
                    .getFills()
                    .get(0)
                    .getRadii();
                Insets insets = getSkinnable().getBackground() == null ? Insets.EMPTY : getSkinnable().getBackground()
                    .getFills()
                    .get(0)
                    .getInsets();
                selectionOverLay.setBackground(new Background(new BackgroundFill(getSkinnable().isSelected() ? ((JFXToggleNode) getSkinnable())
                    .getSelectedColor() : ((JFXToggleNode) getSkinnable()).getUnSelectedColor(), radii, insets)));
            }
        });

        // show focus traversal effect
        getSkinnable().armedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                releaseManualRippler = rippler.createManualRipple();
            } else if (releaseManualRippler != null) {
                releaseManualRippler.run();
            }
        });

        toggleNode.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                if (!getSkinnable().isPressed()) {
                    rippler.setOverlayVisible(true);
                }
            } else {
                rippler.setOverlayVisible(false);
            }
        });
        toggleNode.pressedProperty().addListener((o, oldVal, newVal) -> rippler.setOverlayVisible(false));

        updateChildren();
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippler != null) {
            getChildren().add(0, rippler);
        }
        for (int i = 1; i < getChildren().size(); i++) {
            getChildren().get(i).setMouseTransparent(true);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x,y,w,h);
        rippler.resizeRelocate(
            getSkinnable().getLayoutBounds().getMinX(),
            getSkinnable().getLayoutBounds().getMinY(),
            getSkinnable().getWidth(), getSkinnable().getHeight());
    }

}
