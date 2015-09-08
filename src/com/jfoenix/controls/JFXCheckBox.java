/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.jfoenix.skins.JFXCheckBoxSkin;
import com.sun.javafx.css.converters.PaintConverter;

public class JFXCheckBox extends CheckBox {

	public JFXCheckBox(String text){
		super(text);
		initialize();
	}

	public JFXCheckBox(){
		super();
		initialize();
	}

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);  
//		if(this.getText().isEmpty()) this.setText("CheckBox");
	}

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXCheckBoxSkin(this);
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	private static final String DEFAULT_STYLE_CLASS = "jfx-check-box";
	
	
	private StyleableObjectProperty<Paint> checkedColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.CHECKED_COLOR, JFXCheckBox.this, "checkedColor", Color.valueOf("#0F9D58"));

	public Paint getCheckedColor(){
		return checkedColor == null ? Color.valueOf("#0F9D58") : checkedColor.get();
	}
	public StyleableObjectProperty<Paint> checkedColorProperty(){		
		return this.checkedColor;
	}
	public void setCheckedColor(Paint color){
		this.checkedColor.set(color);
	}


	private StyleableObjectProperty<Paint> unCheckedColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNCHECKED_COLOR, JFXCheckBox.this, "unCheckedColor", Color.valueOf("#5A5A5A"));

	public Paint getUnCheckedColor(){
		return unCheckedColor == null ? Color.valueOf("#5A5A5A") : unCheckedColor.get();
	}
	public StyleableObjectProperty<Paint> unCheckedColorProperty(){		
		return this.unCheckedColor;
	}
	public void setUnCheckedColor(Paint color){
		this.unCheckedColor.set(color);
	}


	private static class StyleableProperties {
		private static final CssMetaData< JFXCheckBox, Paint> CHECKED_COLOR =
				new CssMetaData< JFXCheckBox, Paint>("-fx-checked-color",
						PaintConverter.getInstance(), Color.valueOf("#0F9D58")) {
			@Override
			public boolean isSettable(JFXCheckBox control) {
				return control.checkedColor == null || !control.checkedColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXCheckBox control) {
				return control.checkedColorProperty();
			}
		};
		private static final CssMetaData< JFXCheckBox, Paint> UNCHECKED_COLOR =
				new CssMetaData< JFXCheckBox, Paint>("-fx-unchecked-color",
						PaintConverter.getInstance(), Color.valueOf("#5A5A5A")) {
			@Override
			public boolean isSettable(JFXCheckBox control) {
				return control.unCheckedColor == null || !control.unCheckedColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXCheckBox control) {
				return control.unCheckedColorProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					CHECKED_COLOR,
					UNCHECKED_COLOR
					);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		if(STYLEABLES == null){
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.CHILD_STYLEABLES;
	}



}