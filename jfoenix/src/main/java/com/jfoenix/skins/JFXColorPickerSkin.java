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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.behavior.JFXColorPickerBehavior;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.scene.control.skin.ComboBoxBaseSkin;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.WritableValue;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Shadi Shaheen
 */
public class JFXColorPickerSkin extends ComboBoxPopupControl<Color> {

    private Label displayNode;
    private Pane pickerColorBox;
    private StackPane pickerColorClip;
    private JFXColorPalette popupContent;
    StyleableBooleanProperty colorLabelVisible = new SimpleStyleableBooleanProperty(StyleableProperties.COLOR_LABEL_VISIBLE,
        JFXColorPickerSkin.this,
        "colorLabelVisible",
        true);

    public JFXColorPickerSkin(final ColorPicker colorPicker) {

        super(colorPicker, new JFXColorPickerBehavior(colorPicker));
        // create displayNode
        displayNode = new Label();
        displayNode.getStyleClass().add("color-picker-label");
        displayNode.setManaged(false);
        displayNode.setMouseTransparent(true);

        // label graphic
        pickerColorBox = new Pane();
        pickerColorBox.getStyleClass().add("picker-color");
        pickerColorBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#fafafa"),
            new CornerRadii(3),
            Insets.EMPTY)));
        pickerColorClip = new StackPane();
        pickerColorClip.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
            return new Background(new BackgroundFill(Color.WHITE,
                pickerColorBox.backgroundProperty()
                    .get() != null ? pickerColorBox.getBackground()
                    .getFills()
                    .get(0)
                    .getRadii() : new CornerRadii(
                    3),
                pickerColorBox.backgroundProperty()
                    .get() != null ? pickerColorBox.getBackground()
                    .getFills()
                    .get(0)
                    .getInsets() : Insets.EMPTY));
        }, pickerColorBox.backgroundProperty()));
        pickerColorBox.setClip(pickerColorClip);
        JFXButton button = new JFXButton("");
        button.ripplerFillProperty().bind(displayNode.textFillProperty());
        button.minWidthProperty().bind(pickerColorBox.widthProperty());
        button.minHeightProperty().bind(pickerColorBox.heightProperty());
        button.addEventHandler(MouseEvent.ANY, (event) -> {
            if (!event.isConsumed()) {
                event.consume();
                getSkinnable().fireEvent(event);
            }
        });

        pickerColorBox.getChildren().add(button);
        updateColor();
        getChildren().add(pickerColorBox);
        getChildren().remove(arrowButton);
        JFXDepthManager.setDepth(getSkinnable(), 1);
        // to improve the performance on 1st click
        getPopupContent();

        // add listeners
        registerChangeListener(colorPicker.valueProperty(), "VALUE");
        colorLabelVisible.addListener(invalidate -> {
            if (displayNode != null) {
                if (colorLabelVisible.get()) {
                    displayNode.setText(colorDisplayName(getSkinnable().getValue()));
                } else {
                    displayNode.setText("");
                }
            }
        });
    }


    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (!colorLabelVisible.get()) {
            return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
        String displayNodeText = displayNode.getText();
        double width = 0;
        displayNode.setText("#00000000");
        width = Math.max(width, super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset));
        displayNode.setText(displayNodeText);
        return width;
    }

    static String colorDisplayName(Color c) {
        if (c != null) {
            return formatHexString(c);
        }
        return null;
    }

    static String tooltipString(Color c) {
        if (c != null) {
            return formatHexString(c);
        }
        return null;
    }

    static String formatHexString(Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255)).toUpperCase();
        } else {
            return null;
        }
    }

    @Override
    protected Node getPopupContent() {
        if (popupContent == null) {
            popupContent = new JFXColorPalette((ColorPicker) getSkinnable());
            popupContent.setPopupControl(getPopup());
        }
        return popupContent;
    }

    @Override
    protected void focusLost() {
    }

    @Override
    public void show() {
        super.show();
        final ColorPicker colorPicker = (ColorPicker) getSkinnable();
        popupContent.updateSelection(colorPicker.getValue());
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("SHOWING".equals(p)) {
            if (getSkinnable().isShowing()) {
                show();
            } else if (!popupContent.isCustomColorDialogShowing()) {
                hide();
            }
        } else if ("VALUE".equals(p)) {
            // change the selected color
            updateColor();
        }
    }

    @Override
    public Node getDisplayNode() {
        return displayNode;
    }

    private void updateColor() {
        final ColorPicker colorPicker = (ColorPicker) getSkinnable();
        // update picker box color
        Circle ColorCircle = new Circle();
        ColorCircle.setFill(colorPicker.getValue());
        ColorCircle.setLayoutX(pickerColorBox.getWidth() / 4);
        ColorCircle.setLayoutY(pickerColorBox.getHeight() / 2);
        pickerColorBox.getChildren().add(ColorCircle);
        Timeline animateColor = new Timeline(new KeyFrame(Duration.millis(240),
            new KeyValue(ColorCircle.radiusProperty(),
                200,
                Interpolator.EASE_BOTH)));
        animateColor.setOnFinished((finish) -> {
            pickerColorBox.setBackground(new Background(new BackgroundFill(ColorCircle.getFill(),
                pickerColorBox.getBackground()
                    .getFills()
                    .get(0)
                    .getRadii(),
                pickerColorBox.getBackground()
                    .getFills()
                    .get(0)
                    .getInsets())));
            pickerColorBox.getChildren().remove(ColorCircle);
        });
        animateColor.play();
        // update label color
        displayNode.setTextFill(colorPicker.getValue().grayscale().getRed() < 0.5 ? Color.valueOf(
            "rgba(255, 255, 255, 0.87)") : Color.valueOf("rgba(0, 0, 0, 0.87)"));
        if (colorLabelVisible.get()) {
            displayNode.setText(colorDisplayName(colorPicker.getValue()));
        } else {
            displayNode.setText("");
        }
    }

    public void syncWithAutoUpdate() {
        if (!getPopup().isShowing() && getSkinnable().isShowing()) {
            // Popup was dismissed. Maybe user clicked outside or typed ESCAPE.
            // Make sure JFXColorPickerUI button is in sync.
            getSkinnable().hide();
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        pickerColorBox.resizeRelocate(x - 1, y - 1, w + 2, h + 2);
        pickerColorClip.resize(w + 2, h + 2);
        super.layoutChildren(x, y, w, h);
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling   											       *
     *                                                                         *
     **************************************************************************/

    private static class StyleableProperties {
        private static final CssMetaData<ColorPicker, Boolean> COLOR_LABEL_VISIBLE =
            new CssMetaData<ColorPicker, Boolean>("-fx-color-label-visible",
                BooleanConverter.getInstance(), Boolean.TRUE) {

                @Override
                public boolean isSettable(ColorPicker n) {
                    final JFXColorPickerSkin skin = (JFXColorPickerSkin) n.getSkin();
                    return skin.colorLabelVisible == null || !skin.colorLabelVisible.isBound();
                }

                @Override
                public StyleableProperty<Boolean> getStyleableProperty(ColorPicker n) {
                    final JFXColorPickerSkin skin = (JFXColorPickerSkin) n.getSkin();
                    return skin.colorLabelVisible;
                }
            };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(ComboBoxBaseSkin.getClassCssMetaData());
            styleables.add(COLOR_LABEL_VISIBLE);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }


    protected TextField getEditor() {
        return null;
    }

    protected javafx.util.StringConverter<Color> getConverter() {
        return null;
    }

}
