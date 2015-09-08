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

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;

import com.jfoenix.skins.JFXProgressBarSkin;

public class JFXProgressBar extends ProgressBar {

	private static final String DEFAULT_STYLE_CLASS = "jfx-progress-bar";

	public JFXProgressBar() {
		super();
		initialize();
	}

	public JFXProgressBar(double progress) {
		super(progress);
		initialize();
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXProgressBarSkin(this);
	}

	private void initialize() {
		setPrefWidth(200);
		getStyleClass().add(DEFAULT_STYLE_CLASS);
	}

}
