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

import javafx.beans.DefaultProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import com.jfoenix.skins.JFXToggleNodeSkin;
import com.sun.javafx.css.converters.ColorConverter;

/**
 * @author sshahine
 *
 *	JFX Toggle Node , allows any node set as its graphic to be toggled
 *  not that JFXToggleNode background color MUST match the unselected 
 *  color property, else the toggle animation will not be consistent.
 *  Notice that the default value for unselected color is set to 
 *  transparent color.
 * 
 */
@DefaultProperty(value="graphic")
public class JFXToggleNode extends ToggleButton {

	public JFXToggleNode() {
		super();
		initialize();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXToggleNodeSkin(this);
	}
	
	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}
	

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	private static final String DEFAULT_STYLE_CLASS = "jfx-toggle-node";
	
	private StyleableObjectProperty<Color> selectedColor = new SimpleStyleableObjectProperty<Color>(StyleableProperties.SELECTED_COLOR, JFXToggleNode.this, "selectedColor", Color.rgb(0, 0, 0, 0.2));

	public final StyleableObjectProperty<Color> selectedColorProperty() {
		return this.selectedColor;
	}
	public final Color getSelectedColor() {
		return selectedColor == null ? Color.rgb(0, 0, 0, 0.2) : this.selectedColorProperty().get();
	}
	public final void setSelectedColor(final Color selectedColor) {
		this.selectedColorProperty().set(selectedColor);
	}
	
	private StyleableObjectProperty<Color> unSelectedColor = new SimpleStyleableObjectProperty<Color>(StyleableProperties.UNSELECTED_COLOR, JFXToggleNode.this, "unSelectedCOlor", Color.TRANSPARENT);
	public final StyleableObjectProperty<Color> unSelectedColorProperty() {
		return this.unSelectedColor;
	}
	public final Color getUnSelectedColor() {
		return unSelectedColor == null ? Color.TRANSPARENT : this.unSelectedColorProperty().get();
	}
	public final void setUnSelectedColor(final Color unSelectedColor) {
		this.unSelectedColorProperty().set(unSelectedColor);
	}
	

	private static class StyleableProperties {
		private static final CssMetaData< JFXToggleNode, Color> SELECTED_COLOR =
				new CssMetaData< JFXToggleNode, Color>("-fx-selected-color",
						ColorConverter.getInstance(), Color.rgb(255, 255, 255, 0.87)) {
			@Override
			public boolean isSettable(JFXToggleNode control) {
				return control.selectedColor == null || !control.selectedColor.isBound();
			}
			@Override
			public StyleableProperty<Color> getStyleableProperty(JFXToggleNode control) {
				return control.selectedColorProperty();
			}
		};
		
		private static final CssMetaData< JFXToggleNode, Color> UNSELECTED_COLOR =
				new CssMetaData< JFXToggleNode, Color>("-fx-unselected-color",
						ColorConverter.getInstance(), Color.TRANSPARENT) {
			@Override
			public boolean isSettable(JFXToggleNode control) {
				return control.unSelectedColor == null || !control.unSelectedColor.isBound();
			}
			@Override
			public StyleableProperty<Color> getStyleableProperty(JFXToggleNode control) {
				return control.unSelectedColorProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					SELECTED_COLOR,
					UNSELECTED_COLOR
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
