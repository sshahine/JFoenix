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

import javafx.beans.DefaultProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;

import com.cctintl.jfx.skins.JFXToggleNodeSkin;

@DefaultProperty(value="graphic")
public class JFXToggleNode extends ToggleButton {

	public JFXToggleNode() {
		super();
		initialize();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXToggleNodeSkin(this);
	}
	
	private void initialize() {
		this.getStyleClass().add("jfx-toggle-node");        
	}
	
	
}
