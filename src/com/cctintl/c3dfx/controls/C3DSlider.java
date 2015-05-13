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

package com.cctintl.c3dfx.controls;

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
import javafx.scene.control.Slider;

import com.cctintl.c3dfx.converters.IndicatorPositionConverter;
import com.cctintl.c3dfx.skins.C3DSliderSkin;

public class C3DSlider extends Slider {

	private static final String DEFAULT_STYLE_CLASS = "c3d-slider";

	public C3DSlider() {
		super(0, 100, 50);
		initialize();
	}

	public C3DSlider(double min, double max, double value) {
		super(min, max, value);
		initialize();
	}

	private void initialize() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DSliderSkin(this);
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * */

	public static enum IndicatorPosition {
		LEFT, RIGHT
	};

	private StyleableObjectProperty<IndicatorPosition> indicatorPosition = new SimpleStyleableObjectProperty<IndicatorPosition>(StyleableProperties.INDICATOR_POSITION, C3DSlider.this, "indicatorPosition",
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
		private static final CssMetaData<C3DSlider, IndicatorPosition> INDICATOR_POSITION = new CssMetaData<C3DSlider, IndicatorPosition>("-fx-indicator-position", IndicatorPositionConverter.getInstance(),
				IndicatorPosition.LEFT) {
			@Override
			public boolean isSettable(C3DSlider control) {
				return control.indicatorPosition == null || !control.indicatorPosition.isBound();
			}

			@Override
			public StyleableProperty<IndicatorPosition> getStyleableProperty(C3DSlider control) {
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
