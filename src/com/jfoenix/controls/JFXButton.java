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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;

import com.jfoenix.converters.ButtonTypeConverter;
import com.jfoenix.skins.JFXButtonSkin;

/**
 * @author sshahine
 * JFXButton contains ripple effect , this effect is set according to text fill of the button
 * or the text fill of graphic node if it was set to Label. Priority is set to the text fill 
 * of the button.
 * 
 */
public class JFXButton extends Button {
	
	
	public JFXButton() {
		super();
		initialize();		
	}	
		
	public JFXButton(String text){
		super(text);
		initialize();
	}
	public JFXButton(String text, Node graphic){
		super(text, graphic);
		initialize();
	}

    private void initialize() {
    	this.getStyleClass().add(DEFAULT_STYLE_CLASS);
//    	if(this.getText().isEmpty()) this.setText("Button");
    }
    
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXButtonSkin(this);
	}
	
	
	private ObjectProperty<Paint> ripplerFill = new SimpleObjectProperty<>(null);
	
	public final ObjectProperty<Paint> ripplerFillProperty() {
		return this.ripplerFill;
	}

	public final Paint getRipplerFill() {
		return this.ripplerFillProperty().get();
	}

	public final void setRipplerFill(final Paint ripplerFill) {
		this.ripplerFillProperty().set(ripplerFill);
	}
	
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	private static final String DEFAULT_STYLE_CLASS = "jfx-button";
	
	
	public static enum ButtonType{FLAT, RAISED};
	
	private StyleableObjectProperty<ButtonType> buttonType = new SimpleStyleableObjectProperty<ButtonType>(StyleableProperties.BUTTON_TYPE, JFXButton.this, "buttonType", ButtonType.FLAT );

	public ButtonType getButtonType(){
		return buttonType == null ? ButtonType.FLAT : buttonType.get();
	}
	public StyleableObjectProperty<ButtonType> buttonTypeProperty(){		
		return this.buttonType;
	}
	public void setButtonType(ButtonType type){
		this.buttonType.set(type);
	}


	private static class StyleableProperties {
		private static final CssMetaData< JFXButton, ButtonType> BUTTON_TYPE =
				new CssMetaData< JFXButton, ButtonType>("-fx-button-type",
						ButtonTypeConverter.getInstance(), ButtonType.FLAT) {
			@Override
			public boolean isSettable(JFXButton control) {
				return control.buttonType == null || !control.buttonType.isBound();
			}
			@Override
			public StyleableProperty<ButtonType> getStyleableProperty(JFXButton control) {
				return control.buttonTypeProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					BUTTON_TYPE
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
