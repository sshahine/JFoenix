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

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author Shadi Shaheen
 */
public class JFXTableColumnHeader extends TableColumnHeader {

    private StackPane container = new StackPane();
    private StackPane arrowContainer = new StackPane();
    private GridPane arrowPane;
    private Region arrow;
    private Timeline arrowAnimation;
    private double currentArrowRotation = -1;
    private boolean invalid = true;
    private Insets oldMargin = null;

    public JFXTableColumnHeader(TableViewSkinBase skin, TableColumnBase tc) {
        super(skin, tc);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double w = snapSize(getWidth()) - (snappedLeftInset() + snappedRightInset());
        container.resizeRelocate(snappedLeftInset(), 0, w, getHeight());


        if (!getChildren().contains(container)) {
            invalid = true;
            container.getChildren().remove(arrowContainer);
            for (int i = 0; i < getChildren().size(); ) {
                Node child = getChildren().get(i);
                container.getChildren().add(child);
            }
            getChildren().add(container);
        }

        // add animation to sorting arrow
        if (invalid) {
            if (container.getChildren().size() > 1 && !container.getChildren().contains(arrowContainer)) {
                // setup children
                arrowPane = (GridPane) container.getChildren().get(1);
                arrow = (Region) arrowPane.getChildren().get(0);
                arrowContainer.getChildren().clear();
                container.getChildren().remove(1);
                container.getChildren().add(arrowContainer);

                for (int i = 0; i < arrowPane.getChildren().size(); ) {
                    Node child = arrowPane.getChildren().get(i);
                    arrowContainer.getChildren().add(child);
                    if (child instanceof HBox) {
                        HBox dotsContainer = (HBox) child;
                        dotsContainer.setMaxHeight(5);
                        dotsContainer.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
                            return arrow.getHeight() + 2;
                        }, arrow.heightProperty()));
                    } else if (child instanceof Label) {
                        Label labelContainer = (Label) child;
                        labelContainer.setMaxHeight(5);
                        labelContainer.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
                            return arrow.getHeight() + 3;
                        }, arrow.heightProperty()));
                    }
                }

                arrowContainer.maxWidthProperty().bind(arrow.widthProperty());
                StackPane.setAlignment(arrowContainer, Pos.CENTER_RIGHT);


                // set padding to the label to replace it with ... if it's too close to the sorting arrow
                Label label = (Label) container.getChildren().get(0);
                oldMargin = StackPane.getMargin(label);
                StackPane.setMargin(label,
                    new Insets(oldMargin == null ? 0 : oldMargin.getTop(),
                        oldMargin == null || oldMargin.getRight() < 30 ? 30 : oldMargin.getRight(),
                        oldMargin == null ? 0 : oldMargin.getBottom(),
                        oldMargin == null || oldMargin.getLeft() < 30 ? 30 : oldMargin.getLeft()));

                // fixed the issue of arrow translate X while resizing the column header
                arrowContainer.translateXProperty().bind(Bindings.createDoubleBinding(() -> {
                    if (arrowContainer.getLayoutX() <= 8) {
                        return -arrowContainer.getLayoutX() - 2;
                    }
                    return -10.0;
                }, arrowContainer.layoutXProperty()));


                if (arrowAnimation != null && arrowAnimation.getStatus() == Status.RUNNING) {
                    arrowAnimation.stop();
                }
                if (arrow.getRotate() == 180 && arrow.getRotate() != currentArrowRotation) {
                    arrowContainer.setOpacity(0);
                    arrowContainer.setTranslateY(getHeight() / 4);
                    arrowAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                        new KeyValue(arrowContainer.opacityProperty(),
                            1,
                            Interpolator.EASE_BOTH),
                        new KeyValue(arrowContainer.translateYProperty(),
                            0,
                            Interpolator.EASE_BOTH)));
                } else if (arrow.getRotate() == 0 && arrow.getRotate() != currentArrowRotation) {
                    arrow.setRotate(-180);
                    arrowAnimation = new Timeline(new KeyFrame(Duration.millis(160),
                        new KeyValue(arrow.rotateProperty(),
                            0,
                            Interpolator.EASE_BOTH),
                        new KeyValue(arrowContainer.opacityProperty(),
                            1,
                            Interpolator.EASE_BOTH),
                        new KeyValue(arrowContainer.translateYProperty(),
                            0,
                            Interpolator.EASE_BOTH)));
                }
                arrowAnimation.setOnFinished((finish) -> currentArrowRotation = arrow.getRotate());
                arrowAnimation.play();

            }

            if (arrowContainer != null && arrowPane != null && container.getChildren()
                .size() == 1 && !arrowPane.isVisible()) {
                if (arrowAnimation != null && arrowAnimation.getStatus() == Status.RUNNING) {
                    arrowAnimation.stop();
                }
                Label label = (Label) container.getChildren().get(0);
                // dont change the padding if arrow is not showing
                if (currentArrowRotation == 0) {
                    StackPane.setMargin(label,
                        new Insets(oldMargin == null ? 0 : oldMargin.getTop(),
                            oldMargin == null || oldMargin.getRight() < 30 ? 30 : oldMargin
                                .getRight(),
                            oldMargin == null ? 0 : oldMargin.getBottom(),
                            oldMargin == null || oldMargin.getLeft() < 30 ? 30 : oldMargin
                                .getLeft()));
                }

                container.getChildren().add(arrowContainer);
                arrowAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(arrowContainer.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH),
                    new KeyValue(arrowContainer.translateYProperty(),
                        getHeight() / 4,
                        Interpolator.EASE_BOTH)));
                arrowAnimation.setOnFinished((finish) -> {
                    currentArrowRotation = -1;
                    StackPane.setMargin(label, null);
                });
                arrowAnimation.play();
            }
        }

    }

}
