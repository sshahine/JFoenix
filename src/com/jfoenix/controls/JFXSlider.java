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

import com.jfoenix.converters.IndicatorPositionConverter;
import com.jfoenix.skins.JFXSliderSkin;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;

/**
 * JFXSlider is the material design implementation of a slider. 
 * 
 * @author Bashir Elias & Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXSlider extends Slider {

	/**
     * {@inheritDoc}
     */
	public JFXSlider() {
		super(0, 100, 50);
		initialize();
	}

	/**
     * {@inheritDoc}
     */
	public JFXSlider(double min, double max, double value) {
		super(min, max, value);
		initialize();
	}

	/**
     * {@inheritDoc}
     */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXSliderSkin(this);
	}
	
	private void initialize() {
		getStyleClass().add(DEFAULT_STYLE_CLASS);
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	 /**
     * Initialize the style class to 'jfx-slider'.
     *
     * This is the selector class from which CSS can be used to style
     * this control.
     */
	private static final String DEFAULT_STYLE_CLASS = "jfx-slider";
	
	public static enum IndicatorPosition {
		LEFT, RIGHT
	};

	/**
	 * indicates the position of the slider indicator, can be 
	 * either LEFT or RIGHT 
	 */
	private StyleableObjectProperty<IndicatorPosition> indicatorPosition = new SimpleStyleableObjectProperty<IndicatorPosition>(StyleableProperties.INDICATOR_POSITION, JFXSlider.this, "indicatorPosition",
			IndicatorPosition.LEFT);

	public IndicatorPosition getIndicatorPosition() {
		return indicatorPosition == null ? IndicatorPosition.LEFT : indicatorPosition.get();
	}

	public StyleableObjectProperty<IndicatorPosition> indicatorPositionProperty() {
		return this.indicatorPosition;
	}

	public void setIndicatorPosition(IndicatorPosition pos) {
		this.indicatorPosition.set(pos);
	}

	private static class StyleableProperties {
		private static final CssMetaData<JFXSlider, IndicatorPosition> INDICATOR_POSITION = new CssMetaData<JFXSlider, IndicatorPosition>("-fx-indicator-position", IndicatorPositionConverter.getInstance(),
				IndicatorPosition.LEFT) {
			@Override
			public boolean isSettable(JFXSlider control) {
				return control.indicatorPosition == null || !control.indicatorPosition.isBound();
			}

			@Override
			public StyleableProperty<IndicatorPosition> getStyleableProperty(JFXSlider control) {
				return control.indicatorPositionProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables, INDICATOR_POSITION);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		if (STYLEABLES == null) {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
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
