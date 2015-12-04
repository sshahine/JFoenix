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

import com.jfoenix.skins.JFXPasswordFieldSkin;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * @author sshahine
 *
 */

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
				pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);
				return false;
			}
		}
		activeValidator.set(null);
		pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
		return true;
	}

	
	
	/***************************************************************************
	 *                                                                         *
	 * styleable Properties                                                    *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableBooleanProperty labelFloat = new SimpleStyleableBooleanProperty(StyleableProperties.LABEL_FLOAT, JFXPasswordField.this, "lableFloat", false);
	
	public final StyleableBooleanProperty labelFloatProperty() {
		return this.labelFloat;
	}

	public final boolean isLabelFloat() {
		return this.labelFloatProperty().get();
	}

	public final void setLabelFloat(final boolean labelFloat) {
		this.labelFloatProperty().set(labelFloat);
	}
	
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
		
		private static final CssMetaData<JFXPasswordField, Boolean> LABEL_FLOAT = new CssMetaData<JFXPasswordField, Boolean>("-fx-label-float", BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(JFXPasswordField control) {
				return control.labelFloat == null || !control.labelFloat.isBound();
			}

			@Override
			public StyleableBooleanProperty getStyleableProperty(JFXPasswordField control) {
				return control.labelFloatProperty();
			}
		};


		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables, UNFOCUS_COLOR, FOCUS_COLOR, LABEL_FLOAT);
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
	
	private static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");
	
}
