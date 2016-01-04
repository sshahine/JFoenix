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
 * @author Shadi Shaheen
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
