package com.cctintl.c3dfx.controls;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;

import com.cctintl.c3dfx.skins.C3DRadioButtonSkin;

public class C3DRadioButton extends RadioButton {

	private static final String DEFAULT_STYLE_CLASS = "c3d-radio-button";

	public C3DRadioButton(String text) {
		super(text);
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
	}

	public C3DRadioButton() {
		super();
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DRadioButtonSkin(this);
	}

}
