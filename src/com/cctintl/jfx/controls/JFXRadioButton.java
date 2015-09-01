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

package com.cctintl.jfx.controls;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;

import com.cctintl.jfx.skins.C3DRadioButtonSkin;

public class JFXRadioButton extends RadioButton {

	private static final String DEFAULT_STYLE_CLASS = "c3d-radio-button";

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
		return new C3DRadioButtonSkin(this);
	}

}
