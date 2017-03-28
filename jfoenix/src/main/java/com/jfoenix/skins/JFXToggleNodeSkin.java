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
import javafx.beans.binding.ObjectBinding;
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
 */
public class JFXToggleNodeSkin extends ToggleButtonSkin {

    private final StackPane main = new StackPane();
    private JFXRippler rippler;
    private boolean invalid = true;
    private final CornerRadii defaultRadii = new CornerRadii(3);
    private JFXFillTransition ft;
    private Runnable releaseManualRippler = null;

    public JFXToggleNodeSkin(JFXToggleNode toggleNode) {
        super(toggleNode);
        toggleNode.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, defaultRadii, null)));
        toggleNode.setText(null);
        main.setPickOnBounds(false);
        getSkinnable().setPickOnBounds(false);

        StackPane toggleNodeContainer = new StackPane();
        toggleNodeContainer.getChildren().add(getSkinnable().getGraphic());
        toggleNodeContainer.prefWidthProperty().bind(getSkinnable().widthProperty());
        toggleNodeContainer.prefHeightProperty().bind(getSkinnable().heightProperty());

        rippler = new JFXRippler(toggleNodeContainer, RipplerPos.FRONT) {
            @Override
            protected Node getMask() {
                StackPane mask = new StackPane();
                mask.shapeProperty().bind(main.shapeProperty());
                mask.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
                    return new Background(new BackgroundFill(Color.WHITE,
                                                             main.backgroundProperty()
                                                                 .get() != null ? main.getBackground()
                                                                                      .getFills()
                                                                                      .get(0)
                                                                                      .getRadii() : CornerRadii.EMPTY,
                                                             main.backgroundProperty()
                                                                 .get() != null ? main.getBackground()
                                                                                      .getFills()
                                                                                      .get(0)
                                                                                      .getInsets() : Insets.EMPTY));
                }, main.backgroundProperty()));
                mask.resize(main.getWidth(), main.getHeight());
                return mask;
            }

            @Override
            protected void initListeners() {
                ripplerPane.setOnMousePressed((event) -> {
                    if (releaseManualRippler != null) releaseManualRippler.run();
                    releaseManualRippler = null;
                    createRipple(event.getX(), event.getY());
                });

            }
        };
        main.getChildren().add(rippler);

        main.borderProperty().bind(getSkinnable().borderProperty());
        ObjectBinding<Background> backgroundBinding = Bindings.createObjectBinding(() -> {
            CornerRadii radii = toggleNode.getBackground() == null ? null : toggleNode.getBackground()
                                                                                      .getFills()
                                                                                      .get(0)
                                                                                      .getRadii();
            Insets insets = toggleNode.getBackground() == null ? null : Insets.EMPTY;
            return new Background(new BackgroundFill(toggleNode.isSelected() ? toggleNode.getSelectedColor() : toggleNode
                .getUnSelectedColor(), radii, insets));
        }, toggleNode.unSelectedColorProperty(), toggleNode.backgroundProperty());
        main.backgroundProperty().bind(backgroundBinding);

        // listener to change background color
        getSkinnable().selectedProperty().addListener((o, oldVal, newVal) -> {
            main.backgroundProperty().unbind();
            // show animation only on user action
            if (!toggleNode.isDisableAnimation()) {
                if (ft == null) {
                    ft = new JFXFillTransition(Duration.millis(120), main);
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
                main.setBackground(new Background(new BackgroundFill(getSkinnable().isSelected() ? ((JFXToggleNode) getSkinnable())
                    .getSelectedColor() : ((JFXToggleNode) getSkinnable()).getUnSelectedColor(), radii, insets)));
            }
        });

        // show focus traversal effect
        getSkinnable().armedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) releaseManualRippler = rippler.createManualRipple();
            else if (releaseManualRippler != null) releaseManualRippler.run();
        });
        toggleNode.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                if (!getSkinnable().isPressed()) rippler.showOverlay();
            } else rippler.hideOverlay();
        });
        toggleNode.pressedProperty().addListener((o, oldVal, newVal) -> rippler.hideOverlay());
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (main != null) getChildren().add(0, main);
        for (int i = 1; i < getChildren().size(); i++)
            getChildren().get(i).setMouseTransparent(true);
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        if (invalid) {
            invalid = false;
        }
        double shift = 1;
        main.resizeRelocate(getSkinnable().getLayoutBounds().getMinX() - shift,
                            getSkinnable().getLayoutBounds().getMinY() - shift,
                            getSkinnable().getWidth() + (2 * shift),
                            getSkinnable().getHeight() + (2 * shift));
    }

}
