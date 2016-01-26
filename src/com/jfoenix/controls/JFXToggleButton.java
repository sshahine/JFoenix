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

import com.jfoenix.skins.JFXToggleButtonSkin;
import com.sun.javafx.css.converters.PaintConverter;

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

/**
 * @author Shadi Shaheen
 * 
 * important CSS Selectors:
 * 
 * .jfx-toggle-button{
 * 		-fx-toggle-color: color-value;
 * 		-fx-untoggle-color: color-value;
 * 		-fx-toggle-line-color: color-value;
 * 		-fx-untoggle-line-color: color-value;
 *  }
 * 
 * To change the rippler color when toggled:
 * 
 * .jfx-toggle-button .jfx-rippler{
 * 		-fx-rippler-fill: color-value;
 * 	}
 * 
 * .jfx-toggle-button:selected .jfx-rippler{
 * 		-fx-rippler-fill: color-value;
 * 	}
 * 
 * 
 */
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
		toggleColor.addListener((o,oldVal,newVal)->{
			// update line color in case not set by the user
			toggleLineColor.set(((Color)getToggleColor()).desaturate().desaturate().brighter());
		});
	}
	

	/***************************************************************************
	 *                                                                         *
	 * styleable Properties                                                    *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableObjectProperty<Paint> toggleColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.TOGGLE_COLOR, JFXToggleButton.this, "toggleColor", Color.valueOf("#009688"));

	public Paint getToggleColor(){
		return toggleColor == null ? Color.valueOf("#009688") : toggleColor.get();
	}
	public StyleableObjectProperty<Paint> toggleColorProperty(){		
		return this.toggleColor;
	}
	public void setToggleColor(Paint color){
		this.toggleColor.set(color);
	}

	private StyleableObjectProperty<Paint> untoggleColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNTOGGLE_COLOR, JFXToggleButton.this, "unToggleColor", Color.valueOf("#FAFAFA"));

	public Paint getUnToggleColor(){
		return untoggleColor == null ? Color.valueOf("#FAFAFA") : untoggleColor.get();
	}
	public StyleableObjectProperty<Paint> unToggleColorProperty(){		
		return this.untoggleColor;
	}
	public void setUnToggleColor(Paint color){
		this.untoggleColor.set(color);
	}
	
	private StyleableObjectProperty<Paint> toggleLineColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.TOGGLE_LINE_COLOR, JFXToggleButton.this, "toggleLineColor", Color.valueOf("#77C2BB"));

	public Paint getToggleLineColor(){
		return toggleLineColor == null ? Color.valueOf("#77C2BB") : toggleLineColor.get();
	}
	public StyleableObjectProperty<Paint> toggleLineColorProperty(){		
		return this.toggleLineColor;
	}
	public void setToggleLineColor(Paint color){
		this.toggleLineColor.set(color);
	}
	
	private StyleableObjectProperty<Paint> untoggleLineColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNTOGGLE_LINE_COLOR, JFXToggleButton.this, "unToggleLineColor", Color.valueOf("#999999"));

	public Paint getUnToggleLineColor(){
		return untoggleLineColor == null ? Color.valueOf("#999999") : untoggleLineColor.get();
	}
	public StyleableObjectProperty<Paint> unToggleLineColorProperty(){		
		return this.untoggleLineColor;
	}
	public void setUnToggleLineColor(Paint color){
		this.untoggleLineColor.set(color);
	}


	private static class StyleableProperties {
		private static final CssMetaData< JFXToggleButton, Paint> TOGGLE_COLOR =
				new CssMetaData< JFXToggleButton, Paint>("-fx-toggle-color",
						PaintConverter.getInstance(), Color.valueOf("#009688")) {
			@Override
			public boolean isSettable(JFXToggleButton control) {
				return control.toggleColor == null || !control.toggleColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
				return control.toggleColorProperty();
			}
		};
		
		private static final CssMetaData< JFXToggleButton, Paint> UNTOGGLE_COLOR =
				new CssMetaData< JFXToggleButton, Paint>("-fx-untoggle-color",
						PaintConverter.getInstance(), Color.valueOf("#FAFAFA")) {
			@Override
			public boolean isSettable(JFXToggleButton control) {
				return control.untoggleColor == null || !control.untoggleColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
				return control.unToggleColorProperty();
			}
		};
		
		private static final CssMetaData< JFXToggleButton, Paint> TOGGLE_LINE_COLOR =
				new CssMetaData< JFXToggleButton, Paint>("-fx-toggle-line-color",
						PaintConverter.getInstance(), Color.valueOf("#77C2BB")) {
			@Override
			public boolean isSettable(JFXToggleButton control) {
				return control.toggleLineColor == null || !control.toggleLineColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
				return control.toggleLineColorProperty();
			}
		};
		
		private static final CssMetaData< JFXToggleButton, Paint> UNTOGGLE_LINE_COLOR =
				new CssMetaData< JFXToggleButton, Paint>("-fx-untoggle-line-color",
						PaintConverter.getInstance(), Color.valueOf("#999999")) {
			@Override
			public boolean isSettable(JFXToggleButton control) {
				return control.untoggleLineColor == null || !control.untoggleLineColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
				return control.unToggleLineColorProperty();
			}
		};
		
		

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					TOGGLE_COLOR,
					UNTOGGLE_COLOR,
					TOGGLE_LINE_COLOR,
					UNTOGGLE_LINE_COLOR
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
