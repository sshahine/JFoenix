package com.cctintl.c3dfx.controls;

import javafx.scene.control.Skin;
import javafx.scene.control.Slider;

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
}
