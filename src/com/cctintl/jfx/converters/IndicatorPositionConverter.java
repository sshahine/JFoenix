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

package com.cctintl.jfx.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.cctintl.jfx.controls.JFXSlider.IndicatorPosition;
import com.sun.javafx.css.StyleConverterImpl;

public class IndicatorPositionConverter extends StyleConverterImpl<String, IndicatorPosition> {

	// lazy, thread-safe instatiation
	private static class Holder {
		static final IndicatorPositionConverter INSTANCE = new IndicatorPositionConverter();
	}

	public static StyleConverter<String, IndicatorPosition> getInstance() {
		return Holder.INSTANCE;
	}

	private IndicatorPositionConverter() {
		super();
	}

	@Override
	public IndicatorPosition convert(ParsedValue<String, IndicatorPosition> value, Font not_used) {
		String string = value.getValue().toUpperCase();
		try {
			return IndicatorPosition.valueOf(string);
		} catch (IllegalArgumentException | NullPointerException exception) {
			return IndicatorPosition.LEFT;
		}
	}

	@Override
	public String toString() {
		return "IndicatorPositionConverter";
	}

}
