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
import com.jfoenix.controls.IFXTextInputControl;
import com.jfoenix.skins.JFXTextFieldSkin;
import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.sun.javafx.scene.control.skin.TextFieldSkinAndroid;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.lang.reflect.Field;

/**
 * <h1>Material Design TextField Skin for android</h1>
 * The JFXTextFieldSkinAndroid implements material design text field for android
 * when porting JFoenix to android using JavaFXPorts
 * <p>
 * <b>Note:</b> the implementation is a copy of the original {@link JFXTextFieldSkin}
 * however it extends the JavaFXPorts text field android skin.
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-01-25
 */
public class JFXTextFieldSkinAndroid<T extends TextField & IFXTextInputControl> extends TextFieldSkinAndroid {

    private boolean invalid = true;

    private StackPane line = new StackPane();
    private StackPane focusedLine = new StackPane();
    private Label errorLabel = new Label();
    private StackPane errorIcon = new StackPane();
    private HBox errorContainer = new HBox();
    private StackPane promptContainer = new StackPane();

    private Text promptText;
    private Pane textPane;
    private Node textNode;
    private DoubleProperty textTranslateX;

    private double initScale = 0.05;
    private final Scale promptTextScale = new Scale(1, 1, 0, 0);
    private final Scale scale = new Scale(initScale, 1);

    //    private Paint oldPromptTextFill;
    private ObservableBooleanValue usePromptText = Bindings.createBooleanBinding(this::usePromptText,
        getSkinnable().textProperty(),
        getSkinnable().promptTextProperty(),
        ((IFXTextInputControl) getSkinnable()).labelFloatProperty(),
        promptTextFill);

    private final Rectangle errorContainerClip = new Rectangle();
    private final Scale errorClipScale = new Scale(1, 0, 0, 0);
    private Timeline errorHideTransition = new Timeline(new KeyFrame(Duration.millis(80),
        new KeyValue(errorContainer.opacityProperty(), 0, Interpolator.LINEAR)));
    private Timeline errorShowTransition = new Timeline(new KeyFrame(Duration.millis(80),
        new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_OUT)));
    private Timeline scale1 = new Timeline();
    private Timeline scaleLess1 = new Timeline();

    private final ObjectProperty<Paint> animatedPromptTextFill = new SimpleObjectProperty<>(super.promptTextFill.get());

    JFXAnimationTimer focusTimer = new JFXAnimationTimer(
        new JFXKeyFrame(Duration.millis(1),
            JFXKeyValue.builder()
                .setTarget(focusedLine.opacityProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH)
                .setAnimateCondition(() -> getSkinnable().isFocused()).build()),

        new JFXKeyFrame(Duration.millis(160),
            JFXKeyValue.builder()
                .setTarget(scale.xProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(animatedPromptTextFill)
                .setEndValueSupplier(() -> ((IFXTextInputControl) getSkinnable()).getFocusColor())
                .setInterpolator(Interpolator.EASE_BOTH)
                .setAnimateCondition(() -> getSkinnable().isFocused() && ((IFXTextInputControl) getSkinnable()).isLabelFloat()).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> promptText == null ? null : promptText.translateYProperty())
                .setEndValueSupplier(() -> -textPane.getHeight())
                .setAnimateCondition(() -> ((IFXTextInputControl) getSkinnable()).isLabelFloat())
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(promptTextScale.xProperty())
                .setEndValue(0.85)
                .setAnimateCondition(() -> ((IFXTextInputControl) getSkinnable()).isLabelFloat())
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTarget(promptTextScale.yProperty())
                .setEndValue(0.85)
                .setAnimateCondition(() -> ((IFXTextInputControl) getSkinnable()).isLabelFloat())
                .setInterpolator(Interpolator.EASE_BOTH).build())
    );


    JFXAnimationTimer unfocusTimer = new JFXAnimationTimer(
        new JFXKeyFrame(Duration.millis(160),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> promptText == null ? null : promptText.translateYProperty())
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


    public JFXTextFieldSkinAndroid(T textField) {
        super(textField);

        textPane = (Pane) this.getChildren().get(0);

        // get parent fields
        reflectionFieldConsumer("textNode", field -> textNode = (Node) field.get(this));
        reflectionFieldConsumer("usePromptText", field -> field.set(this, usePromptText));
        reflectionFieldConsumer("textTranslateX", field -> textTranslateX = (DoubleProperty) field.get(this));

        if (usePromptText.get()) {
            createPromptNode();
        }
        usePromptText.addListener(observable -> {
            createPromptNode();
            textField.requestLayout();
        });

        // add style classes
        errorLabel.getStyleClass().add("error-label");
        line.getStyleClass().add("input-line");
        focusedLine.getStyleClass().add("input-focused-line");

        // draw lines
        line.setManaged(false);
        line.setBackground(new Background(
            new BackgroundFill(textField.getUnFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        if (getSkinnable().isDisabled()) {
            line.setBorder(new Border(new BorderStroke(
                textField.getUnFocusColor(),
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(1))));
            line.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        }
        line.setCache(true);
        line.setCacheHint(CacheHint.SPEED);

        // focused line
        focusedLine.setManaged(false);
        focusedLine.setBackground(new Background(
            new BackgroundFill(textField.getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        focusedLine.setOpacity(0);
        focusedLine.getTransforms().add(scale);
        focusedLine.setCacheHint(CacheHint.SPEED);
        focusedLine.setCache(true);


        // error container
        errorContainer.getChildren().setAll(new StackPane(errorLabel), errorIcon);
        errorContainer.setAlignment(Pos.CENTER_LEFT);
        errorContainer.setSpacing(8);
        errorContainer.setPadding(new Insets(4, 0, 0, 0));
        errorContainer.setVisible(false);
        errorContainer.setOpacity(0);
        errorContainer.setManaged(false);
        StackPane.setAlignment(errorLabel, Pos.TOP_LEFT);
        HBox.setHgrow(errorLabel.getParent(), Priority.ALWAYS);
        errorContainerClip.getTransforms().add(errorClipScale);
        errorContainer.setClip(textField.isDisableAnimation() ? null : errorContainerClip);

        getChildren().addAll(line, focusedLine, promptContainer, errorContainer);

        focusTimer.setCacheNodes(textPane);
        unfocusTimer.setCacheNodes(textPane);

        textField.activeValidatorProperty().addListener((ObservableValue<? extends ValidatorBase> o, ValidatorBase oldVal, ValidatorBase newVal) -> {
            if (textPane != null) {
                if (!((IFXTextInputControl) getSkinnable()).isDisableAnimation()) {
                    if (newVal != null) {
                        errorHideTransition.setOnFinished(finish -> {
                            showError(newVal);
                            final double w = getSkinnable().getWidth();
                            double errorContainerHeight = computeErrorHeight(computeErrorWidth(w));
                            if (errorLabel.isWrapText()) {
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
                            } else {
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
                        if (errorLabel.isWrapText()) {
                            // animate scale only
                            scaleLess1.getKeyFrames().setAll(new KeyFrame(Duration.millis(100),
                                new KeyValue(errorClipScale.yProperty(), 0, Interpolator.EASE_BOTH)));
                            scaleLess1.setOnFinished(event -> {
                                hideError();
                                errorClipScale.setY(0);
                            });
                            SequentialTransition transition = new SequentialTransition(scaleLess1);
                            transition.play();
                        } else {
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

        // handle animation on focus gained/lost event
        textField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                focus();
            } else {
                unFocus();
            }
        });

        promptTextFill.addListener(observable -> {
            if (!textField.isLabelFloat() || (textField.isLabelFloat() && textField.isFocused())) {
                animatedPromptTextFill.set(promptTextFill.get());
            }
        });

        registerChangeListener(textField.focusColorProperty(), "FOCUS_COLOR");
        registerChangeListener(textField.unFocusColorProperty(), "UNFOCUS_COLOR");
        registerChangeListener(textField.disableAnimationProperty(), "DISABLE_ANIMATION");
    }

    @Override
    protected void handleControlPropertyChanged(String propertyReference) {
        if ("FOCUS_COLOR".equals(propertyReference)) {
            Paint paint = ((IFXTextInputControl) getSkinnable()).getFocusColor();
            focusedLine.setBackground(paint == null ? Background.EMPTY
                : new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
        } else if ("UNFOCUS_COLOR".equals(propertyReference)) {
            Paint paint = ((IFXTextInputControl) getSkinnable()).getUnFocusColor();
            line.setBackground(paint == null ? Background.EMPTY
                : new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
        } else if ("DISABLE_ANIMATION".equals(propertyReference)) {
            // remove error clip if animation is disabled
            errorContainer.setClip(((IFXTextInputControl) getSkinnable()).isDisableAnimation() ?
                null : errorContainerClip);
        } else {
            super.handleControlPropertyChanged(propertyReference);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);

        final double height = getSkinnable().getHeight();
        final double focusedLineHeight = focusedLine.prefHeight(-1);
        focusedLine.resizeRelocate(x, height, w, focusedLineHeight);
        line.resizeRelocate(x, height, w, line.prefHeight(-1));
        errorContainer.relocate(x, height + focusedLineHeight);
        // resize error container if animation is disabled
        if (((IFXTextInputControl) getSkinnable()).isDisableAnimation() || isErrorVisible()) {
            errorContainer.resize(w, computeErrorHeight(computeErrorWidth(w)));
        }
        scale.setPivotX(w / 2);

        if (getSkinnable().getWidth() > 0) {
            updateTextPos();
        }

        updateLabelFloat(!invalid);

        if (invalid) {
            invalid = false;
            // update validation container
            final ValidatorBase activeValidator = ((IFXTextInputControl) getSkinnable()).getActiveValidator();
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
    }

    private void updateTextPos(){
        double textWidth = textNode.getLayoutBounds().getWidth();
        final double promptWidth = promptText == null ? 0 : promptText.getLayoutBounds().getWidth();
        switch (getHAlignment()){
            case CENTER:
                promptTextScale.setPivotX(promptWidth / 2);
                double midPoint = textRight.get() / 2;
                double newX = midPoint - textWidth / 2;
                if (newX + textWidth <= textRight.get()) {
                    textTranslateX.set(newX);
                }
                break;
            case LEFT:
                promptTextScale.setPivotX(0);
                break;
            case RIGHT:
                promptTextScale.setPivotX(promptWidth);
                break;
        }

    }

    private void updateLabelFloat(boolean animation) {
        if (((IFXTextInputControl) getSkinnable()).isLabelFloat()) {
            if (getSkinnable().isFocused()) {
                animateFloatingLabel(true, animation);
            } else {
                final String text = getSkinnable().getText();
                animateFloatingLabel(!(text == null || text.isEmpty()), animation);
            }
        }
    }

    private boolean isErrorVisible() {
        return errorContainer.isVisible()
               && errorShowTransition.getStatus().equals(Animation.Status.STOPPED)
               && errorHideTransition.getStatus().equals(Animation.Status.STOPPED);
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
     *
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
     *
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
     *
     * @return
     */
    private KeyFrame createScaleToOneFrames() {
        return new KeyFrame(Duration.millis(100), new
            KeyValue(errorClipScale.yProperty(), 1, Interpolator.EASE_BOTH));
    }

    private void createPromptNode() {
        if (promptText != null || !usePromptText.get()) {
            return;
        }

        promptText = new Text();
        promptText.setManaged(false);
        promptText.getStyleClass().add("text");
        promptText.visibleProperty().bind(usePromptText);
        promptText.fontProperty().bind(getSkinnable().fontProperty());
        promptText.textProperty().bind(getSkinnable().promptTextProperty());
        promptText.fillProperty().bind(animatedPromptTextFill);
        promptText.setLayoutX(1);
        promptText.getTransforms().add(promptTextScale);
        promptContainer.getChildren().add(promptText);
        if (getSkinnable().isFocused()) {
            promptText.setTranslateY(-textPane.getHeight());
            promptTextScale.setX(0.85);
            promptTextScale.setY(0.85);
        }
        try {
            reflectionFieldConsumer("promptNode", field -> {
                Object oldValue = field.get(this);
                if (oldValue != null) {
                    textPane.getChildren().remove(oldValue);
                }
                field.set(this, promptText);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focus() {
        unfocusTimer.stop();
        runTimer(focusTimer, true);
    }

    private void unFocus() {
        focusTimer.stop();
        scale.setX(initScale);
        focusedLine.setOpacity(0);
        if (((IFXTextInputControl) getSkinnable()).isLabelFloat()) {
            animatedPromptTextFill.set(promptTextFill.get());
            if (getSkinnable().getText().isEmpty()) {
                if (!unfocusTimer.isRunning()) {
                    unfocusTimer.start();
                }
            }
        }
    }

    /**
     * this method is called when the text property is changed when the
     * field is not focused (changed in code)
     *
     * @param up
     */
    private void animateFloatingLabel(boolean up, boolean animation) {
        if (up) {
            if (promptTextScale.getX() != 0.85) {
                unfocusTimer.stop();
                runTimer(focusTimer, animation);
            }
        } else {
            if (promptTextScale.getX() != 1) {
                focusTimer.stop();
                runTimer(unfocusTimer, animation);
            }
        }
    }

    private void runTimer(JFXAnimationTimer timer, boolean animation) {
        if (animation) {
            if (!timer.isRunning()) {
                timer.start();
            }
        } else {
            timer.applyEndValues();
        }
    }

    private boolean usePromptText() {
        String txt = getSkinnable().getText();
        String promptTxt = getSkinnable().getPromptText();
        boolean isLabelFloat = ((IFXTextInputControl) getSkinnable()).isLabelFloat();
        return isLabelFloat || ((txt == null || txt.isEmpty()) &&
                                promptTxt != null && !promptTxt.isEmpty() &&
                                !promptTextFill.get().equals(Color.TRANSPARENT));
    }

    private void showError(ValidatorBase validator) {
        // set text in error label
        errorLabel.setText(validator.getMessage());
        // show error icon
        Node icon = validator.getIcon();
        errorIcon.getChildren().clear();
        if (icon != null) {
            errorIcon.getChildren().setAll(icon);
            StackPane.setAlignment(icon, Pos.CENTER_RIGHT);
        }
        errorContainer.setVisible(true);
    }

    private void hideError() {
        // clear error label text
        errorLabel.setText(null);
        // clear error icon
        errorIcon.getChildren().clear();
        // reset the height of the text field
        // hide error container
        errorContainer.setVisible(false);
    }

    private <T> void reflectionFieldConsumer(String fieldName, CheckedConsumer<Field> consumer) {
        Field field = null;
        try {
            field = TextFieldSkin.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            consumer.accept(field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }
}
