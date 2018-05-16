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

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.behavior.JFXColorPickerBehavior;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.scene.control.skin.ComboBoxBaseSkin;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Pane colorBox;
    private Region pickerColorClip;
    private JFXColorPalette popupContent;
    StyleableBooleanProperty colorLabelVisible = new SimpleStyleableBooleanProperty(StyleableProperties.COLOR_LABEL_VISIBLE,
        JFXColorPickerSkin.this,
        "colorLabelVisible",
        true);

    public JFXColorPickerSkin(final ColorPicker colorPicker) {
        super(colorPicker, new JFXColorPickerBehavior(colorPicker));

        // create displayNode
        displayNode = new Label("");
        displayNode.getStyleClass().add("color-label");
        displayNode.setManaged(false);
        displayNode.setMouseTransparent(true);

        // label graphic
        colorBox = new Pane();
        colorBox.getStyleClass().add("color-box");
        colorBox.setManaged(false);

        pickerColorClip = new Region();
        pickerColorClip.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
            return new Background(new BackgroundFill(Color.WHITE,
                colorBox.backgroundProperty()
                    .get() != null ? colorBox.getBackground()
                    .getFills()
                    .get(0)
                    .getRadii() : new CornerRadii(
                    3),
                colorBox.backgroundProperty()
                    .get() != null ? colorBox.getBackground()
                    .getFills()
                    .get(0)
                    .getInsets() : Insets.EMPTY));
        }, colorBox.backgroundProperty()));
        colorBox.setClip(pickerColorClip);

        colorBox.getChildren().add(displayNode);
        updateColor();
        final JFXRippler rippler = new JFXRippler(colorBox, JFXRippler.RipplerMask.FIT);
        rippler.ripplerFillProperty().bind(displayNode.textFillProperty());
        getChildren().setAll(rippler);
        JFXDepthManager.setDepth(getSkinnable(), 1);
        getSkinnable().setPickOnBounds(false);
        // to improve the performance on 1st click
        getPopupContent();

        colorPicker.focusedProperty().addListener(observable -> {
            if (colorPicker.isFocused()) {
                if (!getSkinnable().isPressed()) {
                    rippler.setOverlayVisible(true);
                }
            } else {
                rippler.setOverlayVisible(false);
            }
        });

        // add listeners
        registerChangeListener(colorPicker.valueProperty(), "VALUE");
        colorLabelVisible.addListener(invalidate -> {
            if (displayNode != null) {
                if (colorLabelVisible.get()) {
                    displayNode.setText(JFXNodeUtils.colorToHex(getSkinnable().getValue()));
                } else {
                    displayNode.setText("");
                }
            }
        });
    }


    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double width = 100;
        width = Math.max(width, super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset));
        return width + rightInset + leftInset;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        double hInsets = snappedLeftInset() + snappedRightInset();
        double vInsets = snappedTopInset() + snappedBottomInset();
        double width = w + hInsets;
        double height = h + vInsets;
        colorBox.resizeRelocate(0, 0, width, height);
        pickerColorClip.resizeRelocate(0,0, width, height);
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
        Color color = colorPicker.getValue();
        // update picker box color
        Color circleColor = color == null ? Color.WHITE : color;
        Circle colorCircle = new Circle();
        colorCircle.setFill(circleColor);
        colorCircle.setLayoutX(colorBox.getWidth() / 4);
        colorCircle.setLayoutY(colorBox.getHeight() / 2);
        colorBox.getChildren().add(colorCircle);
        Timeline animateColor = new Timeline(new KeyFrame(Duration.millis(240),
            new KeyValue(colorCircle.radiusProperty(),
                200,
                Interpolator.EASE_BOTH)));
        animateColor.setOnFinished((finish) -> {
            JFXNodeUtils.updateBackground(colorBox.getBackground(), colorBox, colorCircle.getFill());
            colorBox.getChildren().remove(colorCircle);
        });
        animateColor.play();
        // update label color
        displayNode.setTextFill(circleColor.grayscale().getRed() < 0.5 ? Color.valueOf(
            "rgba(255, 255, 255, 0.87)") : Color.valueOf("rgba(0, 0, 0, 0.87)"));
        if (colorLabelVisible.get()) {
            displayNode.setText(JFXNodeUtils.colorToHex(circleColor));
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
