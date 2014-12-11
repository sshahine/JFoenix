package com.cctintl.c3dfx.controls;

import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;

import com.cctintl.c3dfx.skins.C3DTabPaneSkin;

public class C3DTabPane extends TabPane {

	private static final String DEFAULT_STYLE_CLASS = "c3d-tab-pane";

	public C3DTabPane() {
		super();
		initialize();
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DTabPaneSkin(this);
	}

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
	}
}
