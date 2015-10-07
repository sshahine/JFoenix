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
