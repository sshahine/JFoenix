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

import com.jfoenix.controls.JFXTextArea;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * <h1>Material Design TextArea Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-01-25
 */
public class JFXTextAreaSkin extends TextAreaSkin {

    private boolean invalid = true;

    private ScrollPane scrollPane;
    private Text promptText;

    private ValidationPane<JFXTextArea> errorContainer;
    private PromptLinesWrapper<JFXTextArea> linesWrapper;

    public JFXTextAreaSkin(JFXTextArea textArea) {
        super(textArea);
        // init text area properties
        scrollPane = (ScrollPane) getChildren().get(0);
        textArea.setWrapText(true);

        linesWrapper = new PromptLinesWrapper<>(
            textArea,
            super.promptTextFill,
            textArea.textProperty(),
            textArea.promptTextProperty(),
            () -> promptText);

        linesWrapper.init(() -> createPromptNode(), scrollPane);
        errorContainer = new ValidationPane<>(textArea);
        getChildren().addAll(linesWrapper.line, linesWrapper.focusedLine, linesWrapper.promptContainer, errorContainer);

        registerChangeListener(textArea.disableProperty(), "DISABLE_NODE");
        registerChangeListener(textArea.focusColorProperty(), "FOCUS_COLOR");
        registerChangeListener(textArea.unFocusColorProperty(), "UNFOCUS_COLOR");
        registerChangeListener(textArea.disableAnimationProperty(), "DISABLE_ANIMATION");
    }

    @Override
    protected void handleControlPropertyChanged(String propertyReference) {
        if ("DISABLE_NODE".equals(propertyReference)) {
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
        super.layoutChildren(x, y, w, h);

        final double height = getSkinnable().getHeight();
        linesWrapper.layoutLines(x, y, w, h, height, promptText == null ? 0 : promptText.getLayoutBounds().getHeight() + 3);
        linesWrapper.layoutPrompt(x, y, w, h);
        errorContainer.layoutPane(x, height + linesWrapper.focusedLine.getHeight(), w, h);
        linesWrapper.updateLabelFloatLayout();


        if (invalid) {
            invalid = false;
            // set the default background of text area viewport to white
            Region viewPort = (Region) scrollPane.getChildrenUnmodifiable().get(0);
            viewPort.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
            // reapply css of scroll pane in case set by the user
            viewPort.applyCss();
            errorContainer.invalid(w);
            // focus
            linesWrapper.invalid();
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
        promptText.setTranslateX(1);
        promptText.getTransforms().add(linesWrapper.promptTextScale);
        linesWrapper.promptContainer.getChildren().add(promptText);
        if (getSkinnable().isFocused() && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
            promptText.setTranslateY(-Math.floor(scrollPane.getHeight()));
            linesWrapper.promptTextScale.setX(0.85);
            linesWrapper.promptTextScale.setY(0.85);
        }

        try {
            reflectionFieldConsumer("promptNode", field -> {
                Object oldValue = field.get(this);
                if (oldValue != null) {
                    removeHighlight(Arrays.asList(((Node) oldValue)));
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
            field = TextAreaSkin.class.getDeclaredField(fieldName);
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
