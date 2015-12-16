/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.skins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class JFXColorPickerSkin extends ComboBoxPopupControl<Color> {

	private Label displayNode; 
	private Pane pickerColorBox;
	private StackPane pickerColorClip;
	private JFXColorPalette popupContent;    
	StyleableBooleanProperty colorLabelVisible = new SimpleStyleableBooleanProperty(StyleableProperties.COLOR_LABEL_VISIBLE,JFXColorPickerSkin.this,"colorLabelVisible",true);
	
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
		pickerColorBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#fafafa"), new CornerRadii(3), Insets.EMPTY)));
		pickerColorClip = new StackPane();
		pickerColorClip.backgroundProperty().bind(Bindings.createObjectBinding(()->{					
			return new Background(new BackgroundFill(Color.WHITE, 
					pickerColorBox.backgroundProperty().get()!=null?pickerColorBox.getBackground().getFills().get(0).getRadii() : new CornerRadii(3),
							pickerColorBox.backgroundProperty().get()!=null?pickerColorBox.getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
		}, pickerColorBox.backgroundProperty()));	
		pickerColorBox.setClip(pickerColorClip);
		JFXButton button = new JFXButton("");
		button.ripplerFillProperty().bind(displayNode.textFillProperty());
		button.minWidthProperty().bind(pickerColorBox.widthProperty());
		button.minHeightProperty().bind(pickerColorBox.heightProperty());
		button.addEventHandler(MouseEvent.ANY, (event)->{
			if(!event.isConsumed()){
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
		colorLabelVisible.addListener(invalidate->{
			if (displayNode != null) {
				if (colorLabelVisible.get()) {
					displayNode.setText(colorDisplayName(((ColorPicker)getSkinnable()).getValue()));
				} else {
					displayNode.setText("");
				}
			}
		});
	}


	@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
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
			String displayName = formatHexString(c);
			return displayName;
		}
		return null;
	}

	static String tooltipString(Color c) {
		if (c != null) {
			String tooltipStr = formatHexString(c);
			return tooltipStr;
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

	@Override protected Node getPopupContent() {
		if (popupContent == null) {
			popupContent = new JFXColorPalette((ColorPicker)getSkinnable());
			popupContent.setPopupControl(getPopup());
		}
		return popupContent;
	}

	@Override protected void focusLost() { }

	@Override public void show() {
		super.show();	
		final ColorPicker colorPicker = (ColorPicker)getSkinnable();
		popupContent.updateSelection(colorPicker.getValue());
	}

	@Override protected void handleControlPropertyChanged(String p) {
		super.handleControlPropertyChanged(p);
		if ("SHOWING".equals(p)) {
			if (getSkinnable().isShowing()) show();
			else if (!popupContent.isCustomColorDialogShowing()) hide();
		} else if ("VALUE".equals(p)) {
			// change the selected color
			updateColor();
		}
	}
	@Override public Node getDisplayNode() {
		return displayNode;
	}

	private void updateColor() {
		final ColorPicker colorPicker = (ColorPicker)getSkinnable();
		// update picker box color
		Circle ColorCircle = new Circle();
		ColorCircle.setFill(colorPicker.getValue());
		ColorCircle.setLayoutX(pickerColorBox.getWidth()/4); 
		ColorCircle.setLayoutY(pickerColorBox.getHeight()/2);
		pickerColorBox.getChildren().add(ColorCircle);
		Timeline animateColor = new Timeline(new KeyFrame(Duration.millis(240), new KeyValue(ColorCircle.radiusProperty(), 200, Interpolator.EASE_BOTH)));
		animateColor.setOnFinished((finish)->{
			pickerColorBox.setBackground(new Background(new BackgroundFill(ColorCircle.getFill(), pickerColorBox.getBackground().getFills().get(0).getRadii(), pickerColorBox.getBackground().getFills().get(0).getInsets())));
			pickerColorBox.getChildren().remove(ColorCircle);
		});
		animateColor.play();
		// update label color
		displayNode.setTextFill(colorPicker.getValue().grayscale().getRed() < 0.5?Color.valueOf("rgba(255, 255, 255, 0.87)") : Color.valueOf("rgba(0, 0, 0, 0.87)"));
		if (colorLabelVisible.get()) displayNode.setText(colorDisplayName(colorPicker.getValue()));
		else displayNode.setText("");
	}
	public void syncWithAutoUpdate() {
		if (!getPopup().isShowing() && getSkinnable().isShowing()) {
			// Popup was dismissed. Maybe user clicked outside or typed ESCAPE.
			// Make sure JFXColorPickerUI button is in sync.
			getSkinnable().hide();
		}
	}

	@Override protected void layoutChildren(final double x, final double y,
			final double w, final double h) {
		pickerColorBox.resizeRelocate(x-1, y-1, w + 2, h + 2);
		pickerColorClip.resize(w+2, h+2);
		super.layoutChildren(x,y,w,h);
	}

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling   											       *
	 *                                                                         *
	 **************************************************************************/

	private static class StyleableProperties {
		private static final CssMetaData<ColorPicker,Boolean> COLOR_LABEL_VISIBLE = 
				new CssMetaData<ColorPicker,Boolean>("-fx-color-label-visible",
						BooleanConverter.getInstance(), Boolean.TRUE) {

			@Override public boolean isSettable(ColorPicker n) {
				final JFXColorPickerSkin skin = (JFXColorPickerSkin) n.getSkin();
				return skin.colorLabelVisible == null || !skin.colorLabelVisible.isBound();
			}

			@Override public StyleableProperty<Boolean> getStyleableProperty(ColorPicker n) {
				final JFXColorPickerSkin skin = (JFXColorPickerSkin) n.getSkin();
				return (StyleableProperty<Boolean>)(WritableValue<Boolean>)skin.colorLabelVisible;
			}
		};
		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(ComboBoxBaseSkin.getClassCssMetaData());
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
