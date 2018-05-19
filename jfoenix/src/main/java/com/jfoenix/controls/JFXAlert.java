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
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.event.EventHandlerManager;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

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
    private StackPane overlay;
    private InvalidationListener widthListener;
    private InvalidationListener heightListener;
    private InvalidationListener xListener;
    private InvalidationListener yListener;

    public JFXAlert() {
        this(null);
    }

    public JFXAlert(Stage stage) {
        // create custom dialog pane
        setDialogPane(new DialogPane() {
            {
                getButtonTypes().add(ButtonType.CLOSE);
                Node closeButton = this.lookupButton(ButtonType.CLOSE);
                closeButton.managedProperty().bind(closeButton.visibleProperty());
                closeButton.setVisible(false);
            }

            @Override
            protected Node createButtonBar() {
                return null;
            }
        });

        // init style for the content container
        contentContainer = new StackPane();
        contentContainer.getStyleClass().add("jfx-alert-content-container");

        // set dialog pane content
        final Node materialNode = JFXDepthManager.createMaterialNode(contentContainer, 2);
        materialNode.setPickOnBounds(false);
        materialNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> event.consume());

        // customize dialogPane
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getScene().setFill(Color.TRANSPARENT);
        dialogPane.setStyle("-fx-background-color: transparent;");

        if (stage != null) {
            // set the stage to transparent
            initStyle(StageStyle.TRANSPARENT);
            initOwner(stage);

            // init style for overlay
            overlay = new StackPane(materialNode) {
                public String getUserAgentStylesheet() {
                    return getClass().getResource("/css/controls/jfx-alert.css").toExternalForm();
                }
            };
            overlay.getStyleClass().add("jfx-alert-overlay");
            overlay.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (this.isOverlayClose()) {
                    hideWithAnimation();
                }
            });

            dialogPane.setContent(overlay);
            // bind dialog position to stage position
            widthListener = observable -> updateWidth();
            heightListener = observable -> updateHeight();
            xListener = observable -> updateX();
            yListener = observable -> updateY();
        } else {
            dialogPane.setContent(materialNode);
        }

        // handle animation / owner stage layout changes
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWING, event -> {
            if (getAnimation() != null) {
                addLayoutListeners();
                overlay.setOpacity(0);
                getAnimation().initAnimation(contentContainer.getParent(), dialogPane.getContent());
            }
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWN, event -> {
            if (getOwner() != null) {
                updateLayout();
            }
            if (getAnimation() != null) {
                Animation animation = getAnimation().createShowingAnimation(contentContainer.getParent(), dialogPane.getContent());
                if (animation != null) {
                    animation.play();
                }
            }
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_HIDDEN, event -> removeLayoutListeners());
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
        if(transition==null || transition.getStatus().equals(Animation.Status.STOPPED)){
            if (getAnimation() != null) {
                Animation animation = getAnimation().createHidingAnimation(contentContainer.getParent(), getDialogPane().getContent());
                if (animation != null) {
                    transition = animation;
                    animation.setOnFinished(finish -> {
                        this.hide();
                        this.transition = null;
                    });
                    animation.play();
                } else {
                    Platform.runLater(this::hide);
                }
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
}
