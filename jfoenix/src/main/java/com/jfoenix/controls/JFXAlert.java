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

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.event.EventHandlerManager;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.List;

/**
 * JFXAlert is similar to {@link JFXDialog} control, however it extends JavaFX {@link Dialog}
 * control, thus it support modality options and doesn't require a parent to be specified
 * unlike {@link JFXDialog}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-05-26
 */
public class JFXAlert<R> extends Dialog<R> {

    private final StackPane contentContainer;
    private InvalidationListener widthListener;
    private InvalidationListener heightListener;
    private InvalidationListener xListener;
    private InvalidationListener yListener;

    private boolean animateClosing = true;

    public JFXAlert() {
        this(null);
    }

    public JFXAlert(Stage stage) {
        // create content
        contentContainer = new StackPane();
        contentContainer.getStyleClass().add("jfx-alert-content-container");
        // add depth effect
        final Node materialNode = JFXDepthManager.createMaterialNode(contentContainer, 2);
        materialNode.setPickOnBounds(false);
        materialNode.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);

        // create custom dialog pane (will layout children in center)
        final DialogPane dialogPane = new DialogPane() {
            private boolean performingLayout = false;

            {
                getButtonTypes().add(ButtonType.CLOSE);
                Node closeButton = this.lookupButton(ButtonType.CLOSE);
                closeButton.managedProperty().bind(closeButton.visibleProperty());
                closeButton.setVisible(false);
            }

            @Override
            protected double computePrefHeight(double width) {
                Window owner = getOwner();
                if (owner != null) {
                    return owner.getHeight();
                } else {
                    return super.computePrefHeight(width);
                }
            }

            @Override
            protected double computePrefWidth(double height) {
                Window owner = getOwner();
                if (owner != null) {
                    return owner.getWidth();
                } else {
                    return super.computePrefWidth(height);
                }
            }

            @Override
            public void requestLayout() {
                if (performingLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override
            protected void layoutChildren() {
                performingLayout = true;
                List<Node> managed = getManagedChildren();
                final double width = getWidth();
                double height = getHeight();
                double top = getInsets().getTop();
                double right = getInsets().getRight();
                double left = getInsets().getLeft();
                double bottom = getInsets().getBottom();
                double contentWidth = width - left - right;
                double contentHeight = height - top - bottom;
                for (Node child : managed) {
                    layoutInArea(child, left, top, contentWidth, contentHeight,
                        0, Insets.EMPTY, HPos.CENTER, VPos.CENTER);
                }
                performingLayout = false;
            }

            public String getUserAgentStylesheet() {
                    return JFoenixResources.load("css/controls/jfx-alert.css").toExternalForm();
            }

            @Override
            protected Node createButtonBar() {
                return null;
            }
        };
        dialogPane.getStyleClass().add("jfx-alert-overlay");
        dialogPane.setContent(materialNode);
        setDialogPane(dialogPane);
        dialogPane.getScene().setFill(Color.TRANSPARENT);

        if (stage != null) {
            // set the stage to transparent
            initStyle(StageStyle.TRANSPARENT);
            initOwner(stage);

            // init style for overlay
            dialogPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (this.isOverlayClose()) {
                    hide();
                }
            });
            // bind dialog position to stage position
            widthListener = observable -> updateWidth();
            heightListener = observable -> updateHeight();
            xListener = observable -> updateX();
            yListener = observable -> updateY();
        }

        // handle animation / owner stage layout changes
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWING, event -> {
            addLayoutListeners();
            JFXAlertAnimation currentAnimation = getCurrentAnimation();
            currentAnimation.initAnimation(contentContainer.getParent(), dialogPane);
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWN, event -> {
            if (getOwner() != null) {
                updateLayout();
            }
            animateClosing = true;
            JFXAlertAnimation currentAnimation = getCurrentAnimation();
            Animation animation = currentAnimation.createShowingAnimation(dialogPane.getContent(), dialogPane);
            if (animation != null) {
                animation.play();
            }
        });

        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_CLOSE_REQUEST, event -> {
            if (animateClosing) {
                event.consume();
                hideWithAnimation();
            }
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_HIDDEN, event -> removeLayoutListeners());

        getDialogPane().getScene().getWindow().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                if (!isHideOnEscape()) {
                    keyEvent.consume();
                }
            }
        });
    }

    // this method ensure not null value for current animation
    private JFXAlertAnimation getCurrentAnimation() {
        JFXAlertAnimation usedAnimation = getAnimation();
        usedAnimation = usedAnimation == null ? JFXAlertAnimation.NO_ANIMATION : usedAnimation;
        return usedAnimation;
    }

    private void removeLayoutListeners() {
        Window stage = getOwner();
        if (stage != null) {
            stage.getScene().widthProperty().removeListener(widthListener);
            stage.getScene().heightProperty().removeListener(heightListener);
            stage.xProperty().removeListener(xListener);
            stage.yProperty().removeListener(yListener);
        }
    }

    private void addLayoutListeners() {
        Window stage = getOwner();
        if (stage != null) {
            if (widthListener == null) {
                throw new RuntimeException("Owner can only be set using the constructor");
            }
            stage.getScene().widthProperty().addListener(widthListener);
            stage.getScene().heightProperty().addListener(heightListener);
            stage.xProperty().addListener(xListener);
            stage.yProperty().addListener(yListener);
        }
    }

    private void updateLayout() {
        updateX();
        updateY();
        updateWidth();
        updateHeight();
    }

    private void updateHeight() {
        Window stage = getOwner();
        setHeight(stage.getScene().getHeight());
    }

    private void updateWidth() {
        Window stage = getOwner();
        setWidth(stage.getScene().getWidth());
    }

    private void updateY() {
        Window stage = getOwner();
        setY(stage.getY() + stage.getScene().getY());
    }

    private void updateX() {
        Window stage = getOwner();
        setX(stage.getX() + stage.getScene().getX());
    }


    private Animation transition = null;

    /**
     * play the hide animation for the dialog, as the java hide method is set to final
     * so it can not be overridden
     */
    public void hideWithAnimation() {
        if (transition == null || transition.getStatus().equals(Animation.Status.STOPPED)) {
            JFXAlertAnimation currentAnimation = getCurrentAnimation();
            Animation animation = currentAnimation.createHidingAnimation(getDialogPane().getContent(), getDialogPane());
            if (animation != null) {
                transition = animation;
                animation.setOnFinished(finish -> {
                    animateClosing = false;
                    hide();
                    transition = null;
                });
                animation.play();
            } else {
                animateClosing = false;
                transition = null;
                Platform.runLater(this::hide);
            }
        }
    }

    private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail).prepend(eventHandlerManager);
    }

    public void setContent(Node... content) {
        contentContainer.getChildren().setAll(content);
    }

    /**
     * indicates whether the dialog will close when clicking on the overlay or not
     */
    private BooleanProperty overlayClose = new SimpleBooleanProperty(true);

    public boolean isOverlayClose() {
        return overlayClose.get();
    }

    public BooleanProperty overlayCloseProperty() {
        return overlayClose;
    }

    public void setOverlayClose(boolean overlayClose) {
        this.overlayClose.set(overlayClose);
    }


    /**
     * specify the animation when showing / hiding the dialog
     * by default it's set to {@link JFXAlertAnimation#CENTER_ANIMATION}
     */
    private ObjectProperty<JFXAlertAnimation> animation = new SimpleObjectProperty<>
        (JFXAlertAnimation.CENTER_ANIMATION);

    public JFXAlertAnimation getAnimation() {
        return animation.get();
    }

    public ObjectProperty<JFXAlertAnimation> animationProperty() {
        return animation;
    }

    public void setAnimation(JFXAlertAnimation animation) {
        this.animation.set(animation);
    }

    public void setSize(double prefWidth, double prefHeight) {
        contentContainer.setPrefSize(prefWidth, prefHeight);
    }

    private BooleanProperty hideOnEscape = new SimpleBooleanProperty(this, "hideOnEscape", true);

    public final void setHideOnEscape(boolean value) {
        hideOnEscape.set(value);
    }

    public final boolean isHideOnEscape() {
        return hideOnEscape.get();
    }

    public final BooleanProperty hideOnEscapeProperty() {
        return hideOnEscape;
    }

}
