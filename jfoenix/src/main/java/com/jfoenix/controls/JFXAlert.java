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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

    public JFXAlert() {
        this(null);
    }

    public JFXAlert(Stage stage) {
        // set the stage to transparent
        initStyle(StageStyle.TRANSPARENT);

        if(stage!=null) {
            initOwner(stage);
        }

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
        materialNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event->event.consume());

        // customize dialogPane
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getScene().setFill(Color.TRANSPARENT);
        dialogPane.setStyle("-fx-background-color: transparent;");

        if(stage!=null) {
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

            dialogPane.prefWidthProperty().bind(stage.getScene().widthProperty());
            dialogPane.prefHeightProperty().bind(stage.getScene().heightProperty());

            updateX(stage, dialogPane);
            updateY(stage, dialogPane);
            // bind dialog position to stage position
            stage.getScene().widthProperty().addListener(observable -> updateSize(dialogPane));
            stage.getScene().heightProperty().addListener(observable -> updateSize(dialogPane));
            stage.xProperty().addListener((observable, oldValue, newValue) -> updateX(stage, dialogPane));
            stage.yProperty().addListener((observable, oldValue, newValue) -> updateY(stage, dialogPane));
        }else{
            dialogPane.setContent(materialNode);
        }

        // handle animation
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWING, event -> {
            if (getAnimation() != null) {
                getAnimation().initAnimation(contentContainer.getParent(), dialogPane.getContent());
            }
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWN, event -> {
            if (getAnimation() != null) {
                Animation animation = getAnimation().createShowingAnimation(contentContainer.getParent(), dialogPane.getContent());
                if (animation != null) {
                    animation.play();
                }
            }
        });
    }

    private void updateY(Stage stage, DialogPane dialogPane) {
        if(dialogPane.getScene()!=null) {
            final Parent root = stage.getScene().getRoot();
            final Bounds screenBounds = root.localToScreen(root.getLayoutBounds());
            dialogPane.getScene().getWindow().setY(screenBounds.getMinY());
        }
    }

    private void updateX(Stage stage, DialogPane dialogPane) {
        if(dialogPane.getScene()!=null) {
            final Parent root = stage.getScene().getRoot();
            final Bounds screenBounds = root.localToScreen(root.getLayoutBounds());
            dialogPane.getScene().getWindow().setX(screenBounds.getMinX());
        }
    }


    private Animation transition = null;

    /**
     * play the hide animation for the dialog, as the java hide method is set to final
     * can not be overridden
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

    private void updateSize(DialogPane dialogPane) {
        if (dialogPane.getScene() != null) {
            dialogPane.getScene().getWindow().sizeToScene();
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

    public void setSize(double prefWidth, double prefHeight){
        contentContainer.setPrefSize(prefWidth, prefHeight);
    }
}
