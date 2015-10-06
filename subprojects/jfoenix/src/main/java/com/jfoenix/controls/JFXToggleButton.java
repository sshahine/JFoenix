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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
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
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.jfoenix.skins.JFXToggleButtonSkin;
import com.sun.javafx.css.converters.PaintConverter;

public class JFXToggleButton extends ToggleButton {

	private static final String DEFAULT_STYLE_CLASS = "jfx-toggle-button";
	
	public JFXToggleButton() {
		super();
		initialize();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXToggleButtonSkin(this);
	}
	
	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}
	

	/***************************************************************************
	 *                                                                         *
	 * styleable Properties                                                    *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableObjectProperty<Paint> toggleColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.TOGGLE_COLOR, JFXToggleButton.this, "toggleColor", Color.valueOf("#0F9D58"));

	public Paint getToggleColor(){
		return toggleColor == null ? Color.valueOf("#0F9D58") : toggleColor.get();
	}
	public StyleableObjectProperty<Paint> toggleColorProperty(){		
		return this.toggleColor;
	}
	public void setToggleColor(Paint color){
		this.toggleColor.set(color);
	}


	private static class StyleableProperties {
		private static final CssMetaData< JFXToggleButton, Paint> TOGGLE_COLOR =
				new CssMetaData< JFXToggleButton, Paint>("-fx-toggle-color",
						PaintConverter.getInstance(), Color.valueOf("#0F9D58")) {
			@Override
			public boolean isSettable(JFXToggleButton control) {
				return control.toggleColor == null || !control.toggleColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
				return control.toggleColorProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					TOGGLE_COLOR
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
