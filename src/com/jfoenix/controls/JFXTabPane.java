/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import com.jfoenix.skins.JFXTabPaneSkin;

public class JFXTabPane extends TabPane {

	private static final String DEFAULT_STYLE_CLASS = "jfx-tab-pane";

	public JFXTabPane() {
		super();
		initialize();
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXTabPaneSkin(this);
	}

	private void initialize() {
		this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
	}
	
	public void propagateMouseEventsToParent(){
		this.addEventHandler(MouseEvent.ANY, (e)->{
			e.consume();
			this.getParent().fireEvent(e);
		});
	}
}
