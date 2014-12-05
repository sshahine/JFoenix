package com.cctintl.c3dfx.controls;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;

import com.cctintl.c3dfx.skins.C3DProgressBarSkin;

public class C3DProgressBar extends ProgressBar {

	private static final String DEFAULT_STYLE_CLASS = "c3d-progress-bar";

	public C3DProgressBar() {
		super();
		initialize();
	}

	public C3DProgressBar(double progress) {
		super(progress);
		initialize();
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DProgressBarSkin(this);
	}

	private void initialize() {
		setPrefWidth(200);
		getStyleClass().add(DEFAULT_STYLE_CLASS);
	}

}
