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

package com.jfoenix.controls;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfoenix.skins.JFXDatePickerSkin;
import com.sun.javafx.css.converters.PaintConverter;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * @author sshahine
 *
 */

public class JFXDatePicker extends DatePicker {
	
	public JFXDatePicker() {
		super();		
		initialize();
	}
	
    public JFXDatePicker(LocalDate localDate) {
       super(localDate);
       initialize();
    }
	
	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
	}
    
    @Override protected Skin<?> createDefaultSkin() {
        return new JFXDatePickerSkin(this);
    }
    
    
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	private static final String DEFAULT_STYLE_CLASS = "jfx-date-picker";
    
	
	private StyleableObjectProperty<Paint> defaultColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.DEFAULT_COLOR, JFXDatePicker.this, "defaultColor", Color.valueOf("#009688"));

	public Paint getDefaultColor(){
		return defaultColor == null ? Color.valueOf("#009688") : defaultColor.get();
	}
	public StyleableObjectProperty<Paint> defaultColorProperty(){		
		return this.defaultColor;
	}
	public void setDefaultColor(Paint color){
		this.defaultColor.set(color);
	}
    
	private static class StyleableProperties {
		private static final CssMetaData< JFXDatePicker, Paint> DEFAULT_COLOR =
				new CssMetaData< JFXDatePicker, Paint>("-fx-default-color",
						PaintConverter.getInstance(), Color.valueOf("#5A5A5A")) {
			@Override
			public boolean isSettable(JFXDatePicker control) {
				return control.defaultColor == null || !control.defaultColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXDatePicker control) {
				return control.defaultColorProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					DEFAULT_COLOR);
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
