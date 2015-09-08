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

package com.jfoenix.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.jfoenix.skins.JFXPasswordFieldSkin;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.css.converters.PaintConverter;



public class JFXPasswordField extends PasswordField {
	
	public JFXPasswordField() {
		super();
		initialize();
	}

	private void initialize() {
		this.getStyleClass().add("jfx-password-field");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXPasswordFieldSkin(this);
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	private ReadOnlyObjectWrapper<ValidatorBase> activeValidator = new ReadOnlyObjectWrapper<ValidatorBase>();

	public ValidatorBase getActiveValidator() {
		return activeValidator == null ? null : activeValidator.get();
	}

	public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty() {
		return this.activeValidator.getReadOnlyProperty();
	}

	private ObservableList<ValidatorBase> validators = FXCollections.observableArrayList();

	public ObservableList<ValidatorBase> getValidators() {
		return validators;
	}

	public void setValidators(ValidatorBase... validators) {
		this.validators.addAll(validators);
	}

	public boolean validate() {
		for (ValidatorBase validator : validators) {
			if (validator.getSrcControl() == null)
				validator.setSrcControl(this);
			validator.validate();
			if (validator.getHasErrors()) {
				activeValidator.set(validator);
				return false;
			}
		}
		activeValidator.set(null);
		return true;
	}

	
	
	/***************************************************************************
	 *                                                                         *
	 * styleable Properties                                                    *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableObjectProperty<Paint> unFocusColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNFOCUS_COLOR, JFXPasswordField.this, "unFocusColor", Color.rgb(77, 77, 77));

	public Paint getUnFocusColor() {
		return unFocusColor == null ? Color.rgb(77, 77, 77) : unFocusColor.get();
	}

	public StyleableObjectProperty<Paint> unFocusColorProperty() {
		return this.unFocusColor;
	}

	public void setUnFocusColor(Paint color) {
		this.unFocusColor.set(color);
	}

	private StyleableObjectProperty<Paint> focusColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.FOCUS_COLOR, JFXPasswordField.this, "focusColor", Color.valueOf("#4059A9"));

	public Paint getFocusColor() {
		return focusColor == null ? Color.valueOf("#4059A9") : focusColor.get();
	}

	public StyleableObjectProperty<Paint> focusColorProperty() {
		return this.focusColor;
	}

	public void setFocusColor(Paint color) {
		this.focusColor.set(color);
	}

	private static class StyleableProperties {
		private static final CssMetaData<JFXPasswordField, Paint> UNFOCUS_COLOR = new CssMetaData<JFXPasswordField, Paint>("-fx-unfocus-color", PaintConverter.getInstance(), Color.rgb(77, 77, 77)) {
			@Override
			public boolean isSettable(JFXPasswordField control) {
				return control.unFocusColor == null || !control.unFocusColor.isBound();
			}

			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXPasswordField control) {
				return control.unFocusColorProperty();
			}
		};
		private static final CssMetaData<JFXPasswordField, Paint> FOCUS_COLOR = new CssMetaData<JFXPasswordField, Paint>("-fx-focus-color", PaintConverter.getInstance(), Color.valueOf("#4059A9")) {
			@Override
			public boolean isSettable(JFXPasswordField control) {
				return control.focusColor == null || !control.focusColor.isBound();
			}

			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXPasswordField control) {
				return control.focusColorProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables, UNFOCUS_COLOR, FOCUS_COLOR);
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
