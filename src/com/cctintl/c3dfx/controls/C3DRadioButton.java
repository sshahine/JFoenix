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

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;

import com.cctintl.c3dfx.skins.C3DRadioButtonSkin;

public class C3DRadioButton extends RadioButton {

	private static final String DEFAULT_STYLE_CLASS = "c3d-radio-button";

	public C3DRadioButton(String text) {
		super(text);
		initialize();
	}

	public C3DRadioButton() {
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
