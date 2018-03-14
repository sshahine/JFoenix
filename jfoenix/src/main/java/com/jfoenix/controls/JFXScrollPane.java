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

package com.jfoenix.controls;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.util.function.Function;

/**
 * <h1>Material Design ScrollPane with header </h1>
 *
 * @author Shadi Shaheen & Bassel El Mabsout
 * @version 1.0
 * @since 2017-02-06
 */

@DefaultProperty(value = "content")
public class JFXScrollPane extends StackPane {

    private static final String DEFAULT_STYLE_CLASS = "jfx-scroll-pane";

    private final VBox contentContainer = new VBox();
    private final StackPane headerSpace = new StackPane();
    private final StackPane condensedHeaderBackground = new StackPane();
    private final StackPane headerBackground = new StackPane();

    private double initY = -1;
    private double maxHeight = -1;
    private double minHeight = -1;

    private final StackPane bottomBar;
    private final Scale scale = new Scale(1, 1, 0, 0);
    private Transform oldSceneTransform = null;

    private final StackPane midBar;
    private final StackPane topBar;

    public JFXScrollPane() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);

        // clip content
        Rectangle clip = new Rectangle();
        this.setClip(clip);
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());


        condensedHeaderBackground.setOpacity(0);
        condensedHeaderBackground.getStyleClass().add("condensed-header");
        condensedHeaderBackground.setBackground(new Background(new BackgroundFill(Color.valueOf("#1E88E5"),
                                                                                  CornerRadii.EMPTY,
                                                                                  Insets.EMPTY)));
        headerBackground.setBackground(new Background(new BackgroundFill(Color.valueOf("#3949AB"),
                                                                         CornerRadii.EMPTY,
                                                                         Insets.EMPTY)));
        headerBackground.getStyleClass().add("main-header");
        StackPane bgContainer = new StackPane();
        bgContainer.getChildren().setAll(condensedHeaderBackground, headerBackground);
        bgContainer.setMouseTransparent(true);

        topBar = new StackPane();
        topBar.setPickOnBounds(false);
        topBar.setMaxHeight(64);

        midBar = new StackPane();
        midBar.setMaxHeight(64);
        midBar.setPickOnBounds(false);

        bottomBar = new StackPane();
        bottomBar.setMaxHeight(64);
        bottomBar.getTransforms().add(scale);
        scale.pivotYProperty().bind(bottomBar.heightProperty().divide(2));
        bottomBar.setPickOnBounds(false);

        final StackPane barsContainer = new StackPane(topBar, midBar, bottomBar);
        StackPane.setAlignment(topBar, Pos.TOP_CENTER);
        StackPane.setAlignment(bottomBar, Pos.BOTTOM_CENTER);

        StackPane header = new StackPane();
        header.setPrefHeight(64 * 3);
        header.maxHeightProperty().bind(header.prefHeightProperty());
        header.getChildren().setAll(bgContainer, barsContainer);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        headerSpace.minHeightProperty().bind(header.prefHeightProperty());
        headerSpace.maxHeightProperty().bind(header.prefHeightProperty());
        headerSpace.setFocusTraversable(true);

        contentContainer.getChildren().setAll(headerSpace);

        contentContainer.localToSceneTransformProperty().addListener((o, oldVal, newVal) -> oldSceneTransform = oldVal);
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().addListener((o, oldVal, newVal) -> {
            if (minHeight == -1) {
                minHeight = bottomBar.getBoundsInParent().getMinY();
                maxHeight = header.getHeight();
            }
            if (initY == -1) {
                initY = oldSceneTransform.getTy();
            }

            // translation
            double ty = contentContainer.getLocalToSceneTransform().getTy();
            double opacity = Math.abs(ty - initY) / minHeight;
            opacity = opacity > 1 ? 1 : (opacity < 0) ? 0 : opacity;
            // update properties according to the scroll value
            // opacity
            headerBackground.setOpacity(1 - opacity);
            condensedHeaderBackground.setOpacity(opacity);

            if (newVal.doubleValue() == 0) {
                header.setTranslateY(0);
                topBar.setTranslateY(0);
//            } else if (newVal.doubleValue() == 1) {
//                topBar.setTranslateY(minHeight);
//                header.setStyle("-fx-border-color: RED");
//                header.setTranslateY(-maxHeight);
            } else {
                double dy = ty - initY;
                topBar.setTranslateY(-dy <= minHeight ? -dy : minHeight);

                double oldTy = oldSceneTransform.getTy();
                double diff = oldTy - ty;

                if (-dy > minHeight && newVal.doubleValue() < oldVal.doubleValue()) {
                    if (-(header.getTranslateY() - diff) > minHeight) {
                        header.setTranslateY(header.getTranslateY() - diff);
                    } else {
                        header.setTranslateY(-minHeight);
                    }
                } else {
                    if (-dy > maxHeight) {
                        if (-(header.getTranslateY() - diff) < maxHeight) {
                            header.setTranslateY(header.getTranslateY() - diff);
                        } else {
                            header.setTranslateY(-maxHeight);
                        }
                    } else {
                        if (diff > maxHeight) {
                            header.setTranslateY(-maxHeight);
                        } else {
                            header.setTranslateY(dy);
                        }
                    }
                }
            }
            // scale
            scale.setX(map(opacity, 0, 1, 1, 0.75));
            scale.setY(map(opacity, 0, 1, 1, 0.75));
        });
        scrollPane.setPannable(true);
        getChildren().setAll(scrollPane, header);
    }

    private double map(double val, double min1, double max1, double min2, double max2) {
        return min2 + (max2 - min2) * ((val - min1) / (max1 - min1));
    }

    public void setContent(Node content) {
        if (contentContainer.getChildren().size() == 2) {
            contentContainer.getChildren().set(1, content);
        } else if (contentContainer.getChildren().size() == 1) {
            contentContainer.getChildren().add(content);
        } else {
            contentContainer.getChildren().setAll(headerSpace, content);
        }
        VBox.setVgrow(content, Priority.ALWAYS);
    }

    public Node getContent() {
        return contentContainer.getChildren().size() == 2 ? contentContainer.getChildren().get(1) : null;
    }

    public StackPane getTopBar() {
        return topBar;
    }

    public StackPane getMidBar() {
        return midBar;
    }

    public StackPane getBottomBar() {
        return bottomBar;
    }

    public StackPane getMainHeader() {
        return headerBackground;
    }

    public StackPane getCondensedHeader() {
        return condensedHeaderBackground;
    }


    private static void customScrolling(ScrollPane scrollPane, DoubleProperty scrollDriection, Function<Bounds, Double> sizeFunc) {
        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        final double[] pushes = {1};
        final double[] derivatives = new double[frictions.length];

        Timeline timeline = new Timeline();
        final EventHandler<MouseEvent> dragHandler = event -> timeline.stop();
        final EventHandler<ScrollEvent> scrollHandler = event -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {
                int direction = event.getDeltaY() > 0 ? -1 : 1;
                for (int i = 0; i < pushes.length; i++) {
                    derivatives[i] += direction * pushes[i];
                }
                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }
                event.consume();
            }
        };
        if (scrollPane.getContent().getParent() != null) {
            scrollPane.getContent().getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollPane.getContent().getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollPane.getContent().parentProperty().addListener((o,oldVal, newVal)->{
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                oldVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
            if (newVal != null) {
                newVal.addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                newVal.addEventHandler(ScrollEvent.ANY, scrollHandler);
            }
        });
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3), (event) -> {
            for (int i = 0; i < derivatives.length; i++) {
                derivatives[i] *= frictions[i];
            }
            for (int i = 1; i < derivatives.length; i++) {
                derivatives[i] += derivatives[i - 1];
            }
            double dy = derivatives[derivatives.length - 1];
            double size = sizeFunc.apply(scrollPane.getContent().getLayoutBounds());
            scrollDriection.set(Math.min(Math.max(scrollDriection.get() + dy / size, 0), 1));
            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public static void smoothScrolling(ScrollPane scrollPane) {
        customScrolling(scrollPane, scrollPane.vvalueProperty(), bounds -> bounds.getHeight());
    }

    public static void smoothHScrolling(ScrollPane scrollPane) {
        customScrolling(scrollPane, scrollPane.hvalueProperty(), bounds -> bounds.getWidth());
    }

}
