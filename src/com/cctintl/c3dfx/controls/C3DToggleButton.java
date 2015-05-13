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
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.cctintl.c3dfx.skins.C3DToggleButtonSkin;
import com.sun.javafx.css.converters.PaintConverter;

public class C3DToggleButton extends ToggleButton {

	public C3DToggleButton() {
		super();
		initialize();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DToggleButtonSkin(this);
	}
	
	private void initialize() {
		this.getStyleClass().add("c3d-toggle-button");        
	}
	

	/***************************************************************************
	 *                                                                         *
	 * styleable Properties                                                    *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableObjectProperty<Paint> toggleColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.TOGGLE_COLOR, C3DToggleButton.this, "toggleColor", Color.valueOf("#0F9D58"));

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
		private static final CssMetaData< C3DToggleButton, Paint> TOGGLE_COLOR =
				new CssMetaData< C3DToggleButton, Paint>("-fx-toggle-color",
						PaintConverter.getInstance(), Color.valueOf("#0F9D58")) {
			@Override
			public boolean isSettable(C3DToggleButton control) {
				return control.toggleColor == null || !control.toggleColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DToggleButton control) {
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
