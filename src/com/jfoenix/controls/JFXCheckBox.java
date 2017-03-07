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

import com.jfoenix.skins.JFXCheckBoxSkin;
import com.sun.javafx.css.converters.PaintConverter;
import javafx.css.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXCheckBox is the material design implementation of a checkbox. 
 * it shows ripple effect and a custom selection animation.
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXCheckBox extends CheckBox {

	/**
	 * {@inheritDoc}
	 */
	public JFXCheckBox(String text){
		super(text);
		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public JFXCheckBox(){
		super();
		initialize();
		// init in scene builder workaround ( TODO : remove when JFoenix is well integrated in scenebuilder by gluon )		
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for(int i = 0 ; i < stackTraceElements.length && i < 15; i++){
			if(stackTraceElements[i].getClassName().toLowerCase().contains(".scenebuilder.kit.fxom.")){
				this.setText("CheckBox");
				break;
			}
		}
	}

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);  
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXCheckBoxSkin(this);
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	/**
	 * Initialize the style class to 'jfx-check-box'.
	 *
	 * This is the selector class from which CSS can be used to style
	 * this control.
	 */
	private static final String DEFAULT_STYLE_CLASS = "jfx-check-box";

	/**
	 * checkbox color property when selected
	 */
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

	/**
	 * checkbox color property when not selected
	 */
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
				new CssMetaData< JFXCheckBox, Paint>("-jfx-checked-color",
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
				new CssMetaData< JFXCheckBox, Paint>("-jfx-unchecked-color",
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