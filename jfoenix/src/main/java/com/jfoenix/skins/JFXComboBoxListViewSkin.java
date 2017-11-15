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

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Material Design ComboBox Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-01-25
 */

public class JFXComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private boolean invalid = true;

    private StackPane customPane;
    private StackPane line = new StackPane();
    private StackPane focusedLine = new StackPane();
    private Text promptText = new Text();

    private double initScale = 0.05;
    private Scale scale = new Scale(initScale, 1);

    private Scale promptTextScale = new Scale(1, 1, 0, 0);


    protected final ObjectProperty<Paint> promptTextFill = new StyleableObjectProperty<Paint>(Color.GRAY) {
        @Override public Object getBean() {
            return JFXComboBoxListViewSkin.this;
        }

        @Override public String getName() {
            return "promptTextFill";
        }

        @Override public CssMetaData<JFXComboBox,Paint> getCssMetaData() {
            return StyleableProperties.PROMPT_TEXT_FILL;
        }
    };

    private Paint oldPromptTextFill = promptTextFill.get();

    protected final ObjectProperty<Paint> animatedPromptTextFill = new SimpleObjectProperty<>(promptTextFill.get());

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
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : animatedPromptTextFill)
                .setEndValueSupplier(()->((JFXComboBox) getSkinnable()).getFocusColor())
                .setInterpolator(Interpolator.EASE_BOTH)
                .setAnimateCondition(()->getSkinnable().isFocused()).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : promptText.translateYProperty())
                .setEndValueSupplier(() -> -customPane.getHeight() + 6.05)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : promptTextScale.xProperty())
                .setEndValue(0.85)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : promptTextScale.yProperty())
                .setEndValue(0.85)
                .setInterpolator(Interpolator.EASE_BOTH).build())
    );

    JFXAnimationTimer unfocusTimer = new JFXAnimationTimer(
        new JFXKeyFrame(Duration.millis(160),
            JFXKeyValue.builder()
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : promptText.translateYProperty())
                .setEndValue(0)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : promptTextScale.xProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH).build(),
            JFXKeyValue.builder()
                .setTargetSupplier(()-> !((JFXComboBox<T>) getSkinnable()).isLabelFloat() ? null : promptTextScale.yProperty())
                .setEndValue(1)
                .setInterpolator(Interpolator.EASE_BOTH).build())
    );

    private BooleanBinding usePromptText = Bindings.createBooleanBinding(() -> usePromptText(),
        ((JFXComboBox<?>) getSkinnable()).valueProperty(),
        getSkinnable().promptTextProperty());


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JFXComboBoxListViewSkin(final JFXComboBox<T> comboBox) {
        super(comboBox);

        // create my custom pane for the prompt node
        promptText.textProperty().bind(comboBox.promptTextProperty());
        promptText.fillProperty().bind(animatedPromptTextFill);
        promptText.getStyleClass().addAll("text", "prompt-text");
        promptText.getTransforms().add(promptTextScale);
        if (!comboBox.isLabelFloat()) {
            promptText.visibleProperty().bind(usePromptText);
        }

        customPane = new StackPane();
        customPane.setMouseTransparent(true);
        customPane.getStyleClass().add("combo-box-button-container");
        customPane.getChildren().add(promptText);
        getChildren().add(0, customPane);
        StackPane.setAlignment(promptText, Pos.CENTER_LEFT);

        // add lines
        line.getStyleClass().add("input-line");
        focusedLine.getStyleClass().add("input-focused-line");

        getChildren().add(line);
        getChildren().add(focusedLine);
        line.setPrefHeight(1);
        line.setTranslateY(1); // translate = prefHeight + init_translation
        line.setManaged(false);
        line.setBackground(new Background(new BackgroundFill(((JFXComboBox<?>) getSkinnable()).getUnFocusColor(),
            CornerRadii.EMPTY, Insets.EMPTY)));
        if (getSkinnable().isDisabled()) {
            line.setBorder(new Border(new BorderStroke(((JFXComboBox<?>) getSkinnable()).getUnFocusColor(),
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(1))));
            line.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                CornerRadii.EMPTY, Insets.EMPTY)));
        }
        // focused line
        focusedLine.setPrefHeight(2);
        focusedLine.setTranslateY(0); // translate = prefHeight + init_translation(-1)
        focusedLine.setBackground(new Background(new BackgroundFill(((JFXComboBox<?>) getSkinnable()).getFocusColor(),
            CornerRadii.EMPTY, Insets.EMPTY)));
        focusedLine.setOpacity(0);
        focusedLine.getTransforms().add(scale);
        focusedLine.setManaged(false);

        if (comboBox.isEditable()) {
            comboBox.getEditor().setStyle("-fx-background-color:TRANSPARENT;-fx-padding: 4 0 4 0");
            comboBox.getEditor().promptTextProperty().unbind();
            comboBox.getEditor().setPromptText(null);
            comboBox.getEditor().textProperty().addListener((o, oldVal, newVal) -> {
                usePromptText.invalidate();
                comboBox.setValue(getConverter().fromString(newVal));
            });
        }

        comboBox.labelFloatProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                promptText.visibleProperty().unbind();
            } else {
                promptText.visibleProperty().bind(usePromptText);
            }
        });

        comboBox.focusColorProperty().addListener(observable -> {
            if (comboBox.getFocusColor() != null) {
                focusedLine.setBackground(new Background(new BackgroundFill(comboBox.getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        comboBox.unFocusColorProperty().addListener(observable -> {
            if (comboBox.getUnFocusColor() != null) {
                line.setBackground(new Background(new BackgroundFill(comboBox.getUnFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        comboBox.disabledProperty().addListener(observable -> {
            line.setBorder(comboBox.isDisabled() ? new Border(new BorderStroke(((JFXComboBox<?>) getSkinnable()).getUnFocusColor(),
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(line.getHeight()))) : Border.EMPTY);
            line.setBackground(new Background(new BackgroundFill(comboBox.isDisabled() ? Color.TRANSPARENT : ((JFXComboBox<?>) getSkinnable())
                .getUnFocusColor(),
                CornerRadii.EMPTY, Insets.EMPTY)));
        });

        // handle animation on focus gained/lost event
        comboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                focus();
            } else {
                unFocus();
            }
        });

        promptTextFill.addListener(observable -> {
            oldPromptTextFill = promptTextFill.get();
            animatedPromptTextFill.set(promptTextFill.get());
        });

        // handle animation on value changed
        comboBox.valueProperty().addListener(observable -> {
            if (!getSkinnable().isFocused() && ((JFXComboBox<?>) getSkinnable()).isLabelFloat()) {
                T value = comboBox.getValue();
                if (value == null || value.toString().isEmpty()) {
                    animateFloatingLabel(false);
                } else {
                    animateFloatingLabel(true);
                }
            }
        });
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        customPane.resizeRelocate(x, y, w, h);
        if (invalid) {
            invalid = false;
            // create floating label
            // set initial prompt text fill using javafx prompt node fill
            animatedPromptTextFill.set(promptTextFill.get());
//            focusTimer.setCacheNodes(promptText);
//            unfocusTimer.setCacheNodes(promptText);
//            if(!getSkinnable().isEditable()){
//                final Text javaPromptText = (Text) ((ListCell<T>) super.getDisplayNode()).lookup(".text");
//                if(javaPromptText!=null) animatedPromptTextFill.set(javaPromptText.getFill());
//            }
            if(getSkinnable().getValue()!=null)
                animateFloatingLabel(true);
        }
        focusedLine.resizeRelocate(x, getSkinnable().getHeight(), w, focusedLine.prefHeight(-1));
        line.resizeRelocate(x, getSkinnable().getHeight(), w, line.prefHeight(-1));
        scale.setPivotX(w / 2);
    }


    private void focus() {
        // create the focus animations
        unfocusTimer.stop();
        focusTimer.start();
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

    private void unFocus() {
        focusTimer.stop();
        scale.setX(initScale);
        focusedLine.setOpacity(0);
        if (((JFXComboBox<?>) getSkinnable()).isLabelFloat() && oldPromptTextFill != null) {
            animatedPromptTextFill.set(oldPromptTextFill);
            if (usePromptText()) {
                unfocusTimer.start();
            }
        }
    }

    private boolean usePromptText() {
        Object txt = ((JFXComboBox<?>) getSkinnable()).getValue();
        String promptTxt = getSkinnable().getPromptText();
        boolean isLabelFloat = ((JFXComboBox<?>) getSkinnable()).isLabelFloat();
        return (txt == null || txt.toString().isEmpty()) && promptTxt != null
               && !promptTxt.isEmpty() && (!promptTextFill.get().equals(Color.TRANSPARENT) || isLabelFloat);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXComboBox,Paint> PROMPT_TEXT_FILL =
            new CssMetaData<JFXComboBox,Paint>("-fx-prompt-text-fill",
                PaintConverter.getInstance(), Color.GRAY) {

                @Override
                public boolean isSettable(JFXComboBox n) {
                    final JFXComboBoxListViewSkin<?> skin = (JFXComboBoxListViewSkin<?>) n.getSkin();
                    return skin.promptTextFill == null || !skin.promptTextFill.isBound();
                }

                @Override @SuppressWarnings("unchecked")
                public StyleableProperty<Paint> getStyleableProperty(JFXComboBox n) {
                    final JFXComboBoxListViewSkin<?> skin = (JFXComboBoxListViewSkin<?>) n.getSkin();
                    return (StyleableProperty<Paint>)skin.promptTextFill;
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<CssMetaData<? extends Styleable, ?>>(ComboBoxListViewSkin.getClassCssMetaData());
            styleables.add(PROMPT_TEXT_FILL);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }


    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }


}
