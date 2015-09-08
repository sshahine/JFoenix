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

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;

import com.jfoenix.skins.JFXRadioButtonSkin;

public class JFXRadioButton extends RadioButton {

	private static final String DEFAULT_STYLE_CLASS = "jfx-radio-button";

	public JFXRadioButton(String text) {
		super(text);
		initialize();
	}

	public JFXRadioButton() {
		super();
		initialize();
	}

	private void initialize() {
    	this.getStyleClass().add(DEFAULT_STYLE_CLASS);    	
//    	if(this.getText().isEmpty()) this.setText("RadioButton");
    }
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXRadioButtonSkin(this);
	}

}
