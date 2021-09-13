/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.skins;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.base.IFXLabelFloatControl;
import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.beans.property.DoubleProperty;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * <h1>Material Design Text input control Skin, used for both JFXTextField/JFXPasswordField</h1>
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-01-25
 */
public class JFXTextFieldSkin<T extends JFXTextField & IFXLabelFloatControl> extends TextFieldSkin {

    private boolean invalid = true;

    private Text promptText;
    private Pane textPane;
    private Node textNode;
    private DoubleProperty textTranslateX;

    private ValidationPane<T> errorContainer;
    private PromptLinesWrapper<T> linesWrapper;

    public JFXTextFieldSkin(T textField) {
        super(textField);
        textPane = (Pane) this.getChildren().get(0);
        // get parent fields
        reflectionFieldConsumer("textNode", field -> textNode = (Node) field.get(this));
        reflectionFieldConsumer("textTranslateX", field -> textTranslateX = (DoubleProperty) field.get(this));

        linesWrapper = new PromptLinesWrapper<T>(
            textField,
            super.promptTextFill,
            textField.textProperty(),
            textField.promptTextProperty(),
            () -> promptText);

        linesWrapper.init(() -> createPromptNode(), textPane);

        reflectionFieldConsumer("usePromptText", field -> field.set(this, linesWrapper.usePromptText));

        errorContainer = new ValidationPane<>(textField);

        getChildren().addAll(linesWrapper.line, linesWrapper.focusedLine, linesWrapper.promptContainer, errorContainer);

        updateGraphic(textField.getLeadingGraphic(), "leading");
        updateGraphic(textField.getTrailingGraphic(), "trailing");

        registerChangeListener(textField.disableProperty(), "DISABLE_NODE");
        registerChangeListener(textField.focusColorProperty(), "FOCUS_COLOR");
        registerChangeListener(textField.unFocusColorProperty(), "UNFOCUS_COLOR");
        registerChangeListener(textField.disableAnimationProperty(), "DISABLE_ANIMATION");
        registerChangeListener(textField.leadingGraphicProperty(), "LEADING_GRAPHIC");
        registerChangeListener(textField.trailingGraphicProperty(), "TRAILING_GRAPHIC");
    }

    @Override
    protected void handleControlPropertyChanged(String propertyReference) {
        if ("LEADING_GRAPHIC".equals(propertyReference)) {
            updateGraphic(((JFXTextField) getSkinnable()).getLeadingGraphic(), "leading");
        } else if ("TRAILING_GRAPHIC".equals(propertyReference)) {
            updateGraphic(((JFXTextField) getSkinnable()).getTrailingGraphic(), "trailing");
        } else if ("DISABLE_NODE".equals(propertyReference)) {
            linesWrapper.updateDisabled();
        } else if ("FOCUS_COLOR".equals(propertyReference)) {
            linesWrapper.updateFocusColor();
        } else if ("UNFOCUS_COLOR".equals(propertyReference)) {
            linesWrapper.updateUnfocusColor();
        } else if ("DISABLE_ANIMATION".equals(propertyReference)) {
            // remove error clip if animation is disabled
            errorContainer.updateClip();
        } else {
            super.handleControlPropertyChanged(propertyReference);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double height = getSkinnable().getHeight();
        final Node leadingGraphic = ((JFXTextField) getSkinnable()).getLeadingGraphic();
        final Node trailingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        final double leadingW = leadingGraphic == null ? 0.0 : snapSize(leadingGraphic.prefWidth(height));
        final double trailingW = trailingGraphic == null ? 0.0 : snapSize(trailingGraphic.prefWidth(height));

        final double textX = snapPosition(x) + leadingW;
        final double textW = w - snapSize(leadingW) - snapSize(trailingW);

        super.layoutChildren(textX, y, textW, h);
        linesWrapper.layoutLines(x, y, w, h, height, Math.floor(h));
        linesWrapper.layoutPrompt(textX, y, textW, h);
        errorContainer.layoutPane(x, height + linesWrapper.focusedLine.getHeight(), w, h);

        // draw leading/trailing graphics
        if (leadingGraphic != null) {
            leadingGraphic.resizeRelocate(snappedLeftInset(), 0, leadingW, height);
        }

        if (trailingGraphic != null) {
            trailingGraphic.resizeRelocate(w - trailingW + snappedLeftInset(), 0, trailingW, height);
        }

        if (getSkinnable().getWidth() > 0) {
            updateTextPos();
        }

        linesWrapper.updateLabelFloatLayout();

        if (invalid) {
            invalid = false;
            // update validation container
            errorContainer.invalid(w);
            // focus
            linesWrapper.invalid();
        }
    }

    private final void updateGraphic(Node graphic, String id) {
        Node old = getSkinnable().lookup("#" + id);
        getChildren().remove(old);
        if (graphic != null) {
            graphic.setId(id);
            graphic.setManaged(false);
            // add tab events handler as there is a bug in javafx traversing engine
            Set<Control> controls = JFXNodeUtils.getAllChildren(graphic, Control.class);
            controls.forEach(control -> {
                control.addEventHandler(KeyEvent.KEY_PRESSED, JFXNodeUtils.TRAVERSE_HANDLER);
                control.addEventHandler(KeyEvent.KEY_TYPED, Event::consume);
            });
            getChildren().add(graphic);
        }
    }

    private void updateTextPos() {
        double textWidth = textNode.getLayoutBounds().getWidth();
        final double promptWidth = promptText == null ? 0 : promptText.getLayoutBounds().getWidth();
        switch (getHAlignment()) {
            case CENTER:
                linesWrapper.promptTextScale.setPivotX(promptWidth / 2);
                double midPoint = textRight.get() / 2;
                double newX = midPoint - textWidth / 2;
                if (newX + textWidth <= textRight.get()) {
                    textTranslateX.set(newX);
                }
                break;
            case LEFT:
                linesWrapper.promptTextScale.setPivotX(0);
                break;
            case RIGHT:
                linesWrapper.promptTextScale.setPivotX(promptWidth);
                break;
        }

    }

    private void createPromptNode() {
        if (promptText != null || !linesWrapper.usePromptText.get()) {
            return;
        }

        promptText = new Text();
        promptText.setManaged(false);
        promptText.getStyleClass().add("text");
        promptText.visibleProperty().bind(linesWrapper.usePromptText);
        promptText.fontProperty().bind(getSkinnable().fontProperty());
        promptText.textProperty().bind(getSkinnable().promptTextProperty());
        promptText.fillProperty().bind(linesWrapper.animatedPromptTextFill);
        promptText.setLayoutX(1);
        promptText.getTransforms().add(linesWrapper.promptTextScale);
        linesWrapper.promptContainer.getChildren().add(promptText);
        if (getSkinnable().isFocused() && ((IFXLabelFloatControl) getSkinnable()).isLabelFloat()) {
            promptText.setTranslateY(-Math.floor(textPane.getHeight()));
            linesWrapper.promptTextScale.setX(0.85);
            linesWrapper.promptTextScale.setY(0.85);
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

    @Override
    protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double w = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
        Node leadingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        Node trailingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        final double leadingW = leadingGraphic == null ? 0.0 : snapSize(leadingGraphic.prefWidth(h));
        final double trailingW = trailingGraphic == null ? 0.0 : snapSize(trailingGraphic.prefWidth(h));
        return w + trailingW + leadingW;
    }

    @Override
    protected double computePrefHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double h = super.computePrefHeight(w, topInset, rightInset, bottomInset, leftInset);
        Node leadingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        Node trailingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        final double leadingH = leadingGraphic == null ? 0.0 : snapSize(leadingGraphic.prefHeight(w));
        final double trailingH = trailingGraphic == null ? 0.0 : snapSize(trailingGraphic.prefHeight(w));
        return Math.max(Math.max(h, leadingH), trailingH);
    }

    @Override
    protected double computeMinWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double w = super.computeMinWidth(h, topInset, rightInset, bottomInset, leftInset);
        Node leadingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        Node trailingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        final double leadingW = leadingGraphic == null ? 0.0 : snapSize(leadingGraphic.minWidth(h));
        final double trailingW = trailingGraphic == null ? 0.0 : snapSize(trailingGraphic.minWidth(h));
        return w + trailingW + leadingW;
    }

    @Override
    protected double computeMinHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double h = super.computeMinHeight(w, topInset, rightInset, bottomInset, leftInset);
        Node leadingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        Node trailingGraphic = ((JFXTextField) getSkinnable()).getTrailingGraphic();
        final double leadingH = leadingGraphic == null ? 0.0 : snapSize(leadingGraphic.minHeight(w));
        final double trailingH = trailingGraphic == null ? 0.0 : snapSize(trailingGraphic.minHeight(w));
        return Math.max(Math.max(h, leadingH), trailingH);
    }
}
