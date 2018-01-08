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

package com.jfoenix.android.skins;

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.scene.control.skin.TextAreaSkinAndroid;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.lang.reflect.Field;

/**
 * <h1>Material Design TextArea Skin for android</h1>
 * The JFXTextAreaSkinAndroid implements material design text area for android
 * when porting JFoenix to android using JavaFXPorts
 * <p>
 * <b>Note:</b> the implementation is a copy of the original {@link com.jfoenix.skins.JFXTextAreaSkin JFXTextAreaSkin}
 * however it extends the JavaFXPorts text area android skin.
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-01-25
 */
public class JFXTextAreaSkinAndroid extends TextAreaSkinAndroid {

    private boolean invalid = true;

    private StackPane line = new StackPane();
    private StackPane focusedLine = new StackPane();
    private Label errorLabel = new Label();
    private StackPane errorIcon = new StackPane();
    private HBox errorContainer = new HBox();
    private Pane promptContainer = new StackPane();

    private ScrollPane scrollPane;
    private Text promptText;

    private double initScale = 0.05;
    private Scale promptTextScale = new Scale(1, 1, 0, 0);
    private Scale scale = new Scale(initScale, 1);

    private Paint oldPromptTextFill;
    private BooleanBinding usePromptText = Bindings.createBooleanBinding(this::usePromptText,
        getSkinnable().textProperty(),
        getSkinnable().promptTextProperty());

    private final Rectangle errorContainerClip = new Rectangle();
    private final Scale errorClipScale = new Scale(1, 0, 0, 0);
    private Timeline errorHideTransition = new Timeline(new KeyFrame(Duration.millis(80), new
        KeyValue(errorContainer.opacityProperty(), 0, Interpolator.LINEAR)));
    private Timeline errorShowTransition = new Timeline(new KeyFrame(Duration.millis(80), new
        KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_OUT)));
    private Timeline scale1 = new Timeline();
    private Timeline scaleLess1 = new Timeline();

    private final ObjectProperty<Paint> animatedPromptTextFill = new SimpleObjectProperty<>(super.promptTextFill.get());

    JFXAnimationTimer focusTimer = new JFXAnimationTimer(
        new JFXKeyFrame(Duration.millis(1),
            JFXKeyValue.builder()
                .setTarget(focusedLine.opacityProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH)
                .setAnimateCondition(()->getSkinnable().isFocused()).build()),

        new JFXKeyFrame(Duration.millis(160),
            JFXKeyValue.builder()
                .setTarget(scale.xProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(animatedPromptTextFill)
                .setEndValueSupplier(()->((JFXTextArea) getSkinnable()).getFocusColor())
                .setInterpolator(Interpolator.EASE_BOTH)
                .setAnimateCondition(()->getSkinnable().isFocused()).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(()->promptText == null ? null : promptText.translateYProperty())
                .setEndValueSupplier(() -> -promptText.getLayoutBounds().getHeight() - 2)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(promptTextScale.xProperty())
                .setEndValue(0.85)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(promptTextScale.yProperty())
                .setEndValue(0.85)
                .setInterpolator(Interpolator.EASE_BOTH).build())
    );

    JFXAnimationTimer unfocusTimer = new JFXAnimationTimer(
        new JFXKeyFrame(Duration.millis(160),
            JFXKeyValue.builder()
                .setTargetSupplier(()->promptText == null ? null : promptText.translateYProperty())
                .setEndValue(0)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(promptTextScale.xProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(promptTextScale.yProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH).build())
    );

    public JFXTextAreaSkinAndroid(JFXTextArea textArea) {
        super(textArea);
        // init text area properties
        scrollPane = (ScrollPane) getChildren().get(0);
        textArea.setWrapText(true);

        // add style classes
        errorLabel.getStyleClass().add("error-label");
        line.getStyleClass().add("input-line");
        focusedLine.getStyleClass().add("input-focused-line");

        // draw lines
        line.setPrefHeight(1);
        line.setTranslateY(1 + 4 + 2); // translate = prefHeight + init_translation
        line.setManaged(false);
        line.setBackground(new Background(new BackgroundFill(((JFXTextArea) getSkinnable()).getUnFocusColor(),
            CornerRadii.EMPTY, Insets.EMPTY)));
        if (getSkinnable().isDisabled()) {
            line.setBorder(new Border(new BorderStroke(((JFXTextArea) getSkinnable()).getUnFocusColor(),
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(1))));
            line.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // focused line
        focusedLine.setPrefHeight(2);
        focusedLine.setTranslateY(4 + 2); // translate = prefHeight + init_translation(-1)
        focusedLine.setManaged(false);
        focusedLine.setBackground(new Background(new BackgroundFill(((JFXTextArea) getSkinnable()).getFocusColor(),
            CornerRadii.EMPTY, Insets.EMPTY)));
        focusedLine.setOpacity(0);
        focusedLine.getTransforms().add(scale);

        // error container
        errorContainer.getChildren().setAll(new StackPane(errorLabel), errorIcon);
        errorContainer.setAlignment(Pos.CENTER_LEFT);
        errorContainer.setManaged(false);
        errorContainer.setPadding(new Insets(4,0,0,0));
        errorContainer.setSpacing(8);
        errorContainer.setVisible(false);
        errorContainer.setOpacity(0);
        StackPane.setAlignment(errorLabel, Pos.TOP_LEFT);
        HBox.setHgrow(errorLabel.getParent(), Priority.ALWAYS);
        errorContainerClip.getTransforms().add(errorClipScale);
        errorContainer.setClip(textArea.isDisableAnimation() ? null : errorContainerClip);

        getChildren().addAll(line, focusedLine, promptContainer, errorContainer);

        textArea.activeValidatorProperty().addListener((o, oldVal, newVal) -> {
            if (scrollPane != null) {
                if (!((JFXTextArea) getSkinnable()).isDisableAnimation()) {
                    if (newVal != null) {
                        errorHideTransition.setOnFinished(finish -> {
                            showError(newVal);
                            final double w = getSkinnable().getWidth();
                            double errorContainerHeight = computeErrorHeight(computeErrorWidth(w));
                            if(errorLabel.isWrapText()){
                                // animate opacity + scale
                                if (errorContainerHeight < errorContainer.getHeight()) {
                                    // update animation frames
                                    scaleLess1.getKeyFrames().setAll(createSmallerScaleFrame(errorContainerHeight));
                                    scaleLess1.setOnFinished(event -> {
                                        updateErrorContainerSize(w, errorContainerHeight);
                                        errorClipScale.setY(1);
                                    });
                                    SequentialTransition transition = new SequentialTransition(scaleLess1,
                                        errorShowTransition);
                                    transition.play();
                                } else {
                                    errorClipScale.setY(oldVal == null ? 0 :
                                        errorContainer.getHeight() / errorContainerHeight);
                                    updateErrorContainerSize(w, errorContainerHeight);
                                    // update animation frames
                                    scale1.getKeyFrames().setAll(createScaleToOneFrames());
                                    // play animation
                                    ParallelTransition parallelTransition = new ParallelTransition();
                                    parallelTransition.getChildren().addAll(scale1, errorShowTransition);
                                    parallelTransition.play();
                                }
                            }else{
                                // animate opacity only
                                errorClipScale.setY(1);
                                updateErrorContainerSize(w, errorContainerHeight);
                                ParallelTransition parallelTransition = new ParallelTransition(errorShowTransition);
                                parallelTransition.play();
                            }
                        });
                        errorHideTransition.play();
                    } else {
                        errorHideTransition.setOnFinished(null);
                        if(errorLabel.isWrapText()){
                            // animate scale only
                            scaleLess1.getKeyFrames().setAll(new KeyFrame(Duration.millis(100),
                                new KeyValue(errorClipScale.yProperty(), 0, Interpolator.EASE_BOTH)));
                            scaleLess1.setOnFinished(event -> {
                                hideError();
                                errorClipScale.setY(0);
                            });
                            SequentialTransition transition = new SequentialTransition(scaleLess1);
                            transition.play();
                        }else{
                            errorClipScale.setY(0);
                        }
                        // animate opacity only
                        errorHideTransition.play();
                    }
                } else {
                    if (newVal != null) {
                        JFXUtilities.runInFXAndWait(() -> showError(newVal));
                    } else {
                        JFXUtilities.runInFXAndWait(this::hideError);
                    }
                }
            }
        });

        textArea.focusColorProperty().addListener(observable -> {
            if (textArea.getFocusColor() != null) {
                focusedLine.setBackground(new Background(new BackgroundFill(textArea.getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        textArea.unFocusColorProperty().addListener(observable -> {
            if (textArea.getUnFocusColor() != null) {
                line.setBackground(new Background(new BackgroundFill(textArea.getUnFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        // handle animation on focus gained/lost event
        textArea.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                focus();
            } else {
                unFocus();
            }
        });

        // handle text changing at runtime
        textArea.textProperty().addListener(observable -> {
            if (!getSkinnable().isFocused() && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
                final String text = textArea.getText();
                if (text == null || text.isEmpty()) {
                    animateFloatingLabel(false);
                } else {
                    animateFloatingLabel(true);
                }
            }
        });

        textArea.disabledProperty().addListener(observable -> {
            line.setBorder(textArea.isDisabled() ? new Border(new BorderStroke(((JFXTextArea) getSkinnable()).getUnFocusColor(),
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(line.getHeight()))) : Border.EMPTY);
            line.setBackground(new Background(new BackgroundFill(textArea.isDisabled() ? Color.TRANSPARENT : ((JFXTextArea) getSkinnable())
                .getUnFocusColor(),
                CornerRadii.EMPTY, Insets.EMPTY)));
        });

        // prevent setting prompt text fill to transparent when text field is focused (override java transparent color if the control was focused)
        promptTextFill.addListener(observable -> {
            oldPromptTextFill = promptTextFill.get();
            animatedPromptTextFill.set(promptTextFill.get());
        });

        registerChangeListener(textArea.disableAnimationProperty(), "DISABLE_ANIMATION");
        registerChangeListener(textArea.labelFloatProperty(), "LABEL_FLOAT");
    }

    @Override
    protected void handleControlPropertyChanged(String propertyReference) {
        if("LABEL_FLOAT".equals(propertyReference)){
            boolean isLabelFloat = ((JFXTextArea) getSkinnable()).isLabelFloat();
            if (isLabelFloat) {
                JFXUtilities.runInFX(this::createFloatingLabel);
            } else {
                if(promptText!=null){
                    // remove parent focus listener to prevent editor class cast exception
                    promptText.visibleProperty().bind(usePromptText);
                }
            }
            // update prompt text position
            if(isLabelFloat && getSkinnable().getText() != null)
                animateFloatingLabel(!getSkinnable().getText().isEmpty());
        } else if ("DISABLE_ANIMATION".equals(propertyReference)) {
            // remove error clip if animation is disabled
            errorContainer.setClip(((JFXTextArea) getSkinnable()).isDisableAnimation() ?
                null : errorContainerClip);
        } else {
            super.handleControlPropertyChanged(propertyReference);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);

        // change control properties if and only if animations are stopped
        if (!focusTimer.isRunning() && !unfocusTimer.isRunning()) {
            if (getSkinnable().isFocused() && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
                animatedPromptTextFill.set(((JFXTextArea) getSkinnable()).getFocusColor());
            }
        }

        if (invalid) {
            invalid = false;
            animatedPromptTextFill.set(promptTextFill.get());
            // set the default background of text area viewport to white
            Region viewPort = (Region) scrollPane.getChildrenUnmodifiable().get(0);
            viewPort.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
            // reapply css of scroll pane in case set by the user
            viewPort.applyCss();
            // create floating label
            focusTimer.setCacheNodes(promptContainer);
            unfocusTimer.setCacheNodes(promptContainer);
            createFloatingLabel();
            // to position the prompt node properly
            super.layoutChildren(x, y, w, h);
            // update validation container
            final ValidatorBase activeValidator = ((JFXTextArea) getSkinnable()).getActiveValidator();
            if (activeValidator != null) {
                showError(activeValidator);
                final double errorContainerWidth = w - errorIcon.prefWidth(-1);
                errorContainer.setOpacity(1);
                errorContainer.resize(w, computeErrorHeight(errorContainerWidth));
                errorContainerClip.setWidth(w);
                errorContainerClip.setHeight(errorContainer.getHeight());
                errorClipScale.setY(1);
            }
            // focus
            if (getSkinnable().isFocused()) {
                focus();
            }
        }

        final double height = h - focusedLine.prefHeight(-1);
        focusedLine.resizeRelocate(x, height, w, focusedLine.prefHeight(-1));
        line.resizeRelocate(x, height, w, line.prefHeight(-1));
        errorContainer.relocate(x, y);
        // resize error container if animation is disabled
        if (((JFXTextArea) getSkinnable()).isDisableAnimation()) {
            errorContainer.resize(w, computeErrorHeight(computeErrorWidth(w)));
        }
        errorContainer.setTranslateY(h + focusedLine.getHeight() + 4);
        scale.setPivotX(w / 2);
    }

    private double computeErrorWidth(double w) {
        return w - errorIcon.prefWidth(-1);
    }

    private double computeErrorHeight(double errorContainerWidth) {
        return errorLabel.prefHeight(errorContainerWidth)
               + errorContainer.snappedBottomInset()
               + errorContainer.snappedTopInset();
    }

    /**
     * update the size of error container and its clip
     * @param w
     * @param errorContainerHeight
     */
    private void updateErrorContainerSize(double w, double errorContainerHeight) {
        errorContainerClip.setWidth(w);
        errorContainerClip.setHeight(errorContainerHeight);
        errorContainer.resize(w, errorContainerHeight);
    }
    /**
     * creates error animation frames when moving from large -> small error container
     * @param errorContainerHeight
     * @return
     */
    private KeyFrame createSmallerScaleFrame(double errorContainerHeight) {
        return new KeyFrame(Duration.millis(100),
            new KeyValue(errorClipScale.yProperty(),
                errorContainerHeight / errorContainer.getHeight(),
                Interpolator.EASE_BOTH));
    }
    /**
     * creates error animation frames when moving from small -> large error container
     * @return
     */
    private KeyFrame createScaleToOneFrames() {
        return new KeyFrame(Duration.millis(100), new
            KeyValue(errorClipScale.yProperty(), 1, Interpolator.EASE_BOTH));
    }

    private void createFloatingLabel() {
        if (((JFXTextArea) getSkinnable()).isLabelFloat()) {
            if (promptText == null) {
                // get the prompt text node or create it
                boolean triggerFloatLabel = false;
                if (((Region) scrollPane.getContent()).getChildrenUnmodifiable().get(0) instanceof Text) {
                    promptText = (Text) ((Region) scrollPane.getContent()).getChildrenUnmodifiable().get(0);
                } else {
                    Field field;
                    try {
                        field = TextAreaSkin.class.getDeclaredField("promptNode");
                        field.setAccessible(true);
                        createPromptNode();
                        field.set(this, promptText);
                        // replace parent promptNode with promptText field
                        triggerFloatLabel = true;
                        oldPromptTextFill = promptTextFill.get();
                    } catch (NoSuchFieldException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // fixed issue text area is being resized when the content is excedeing its width
                promptText.wrappingWidthProperty().addListener((o, oldval, newVal) -> {
                    if (newVal.doubleValue() > getSkinnable().getWidth()) {
                        promptText.setWrappingWidth(getSkinnable().getWidth());
                    }
                });

                promptText.fillProperty().bind(animatedPromptTextFill);
                promptText.getTransforms().add(promptTextScale);
                promptContainer.getChildren().add(promptText);
                if (triggerFloatLabel) {
                    promptText.setTranslateY(-promptText.getBoundsInLocal().getHeight() - 2);
                    promptTextScale.setX(0.85);
                    promptTextScale.setY(0.85);
                }
            }

            // create prompt animations
//            promptTextUpTransition = new CachedTransition(promptContainer, new Timeline(
//                new KeyFrame(Duration.millis(1300),
//                    new KeyValue(promptText.translateYProperty(),
//                        -promptText.getLayoutBounds().getHeight() - 2,
//                        Interpolator.EASE_BOTH),
//                    new KeyValue(promptTextScale.xProperty(), 0.85, Interpolator.EASE_BOTH),
//                    new KeyValue(promptTextScale.yProperty(), 0.85, Interpolator.EASE_BOTH))))
//            {
//                private double finalHeight = -promptText.getLayoutBounds().getHeight() - 2;
//                {
//                    setDelay(Duration.millis(0));
//                    setCycleDuration(Duration.millis(240));
//                }
//
//                @Override
//                protected void stopping() {
//                    super.stopping();
//                    promptText.setTranslateY(finalHeight);
//                    promptTextScale.setX(0.85);
//                    promptTextScale.setY(0.85);
//                }
//            };
//
//            promptTextColorTransition = createColorTransition(((JFXTextArea) getSkinnable()).getFocusColor());
//
//            promptTextDownTransition = new CachedTransition(promptContainer, new Timeline(
//                new KeyFrame(Duration.millis(1300),
//                    new KeyValue(promptText.translateYProperty(), 0, Interpolator.EASE_BOTH),
//                    new KeyValue(promptTextScale.xProperty(), 1, Interpolator.EASE_BOTH),
//                    new KeyValue(promptTextScale.yProperty(), 1, Interpolator.EASE_BOTH))))
//            {
//                {
//                    setDelay(Duration.millis(0));
//                    setCycleDuration(Duration.millis(240));
//                }
//                @Override
//                protected void stopping() {
//                    super.stopping();
//                    promptText.setTranslateY(0);
//                    promptTextScale.setX(1);
//                    promptTextScale.setY(1);
//                }
//            };

            promptText.visibleProperty().unbind();
            promptText.visibleProperty().set(true);
        }
    }

    private void createPromptNode() {
        promptText = new Text();
        promptText.setManaged(false);
        promptText.getStyleClass().add("text");
        promptText.visibleProperty().bind(usePromptText);
        promptText.fontProperty().bind(getSkinnable().fontProperty());
        promptText.textProperty().bind(getSkinnable().promptTextProperty());
        promptText.fillProperty().bind(animatedPromptTextFill);
        promptText.setLayoutX(1);
    }

    private void focus() {
        // in case the method request layout is not called before focused
        // this bug is reported while editing treetableview cells
        if (scrollPane == null) {
            Platform.runLater(() -> focus());
        } else {
            unfocusTimer.stop();
            focusTimer.start();
        }
    }

    private void unFocus() {
        focusTimer.stop();
        scale.setX(initScale);
        focusedLine.setOpacity(0);
        if (oldPromptTextFill != null && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
            animatedPromptTextFill.set(oldPromptTextFill);
            if (usePromptText()) {
                unfocusTimer.start();
            }
        }
    }

    /**
     * this method is called when the text property is changed when the
     * field is not focused (changed in code)
     *
     * @param up
     */
    private void animateFloatingLabel(boolean up) {
        if (promptText == null) {
            Platform.runLater(() -> animateFloatingLabel(up));
        } else {
            if (up) {
                unfocusTimer.stop();
                focusTimer.start();
            } else if (!up) {
                focusTimer.stop();
                unfocusTimer.start();
            }
        }
    }

    private boolean usePromptText() {
        String txt = getSkinnable().getText();
        String promptTxt = getSkinnable().getPromptText();
        return (txt == null || txt.isEmpty()) && promptTxt != null &&
               !promptTxt.isEmpty() && !promptTextFill.get().equals(Color.TRANSPARENT);
    }

    private void showError(ValidatorBase validator) {
        // set text in error label
        errorLabel.setText(validator.getMessage());
        // show error icon
        Node icon = validator.getIcon();
        errorIcon.getChildren().clear();
        if (icon != null) {
            errorIcon.getChildren().add(icon);
            StackPane.setAlignment(icon, Pos.CENTER_RIGHT);
        }
        errorContainer.setVisible(true);
    }

    private void hideError() {
        // clear error label text
        errorLabel.setText(null);
        // clear error icon
        errorIcon.getChildren().clear();
        // hide error container
        errorContainer.setVisible(false);
    }
}
