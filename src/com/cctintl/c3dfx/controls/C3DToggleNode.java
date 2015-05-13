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

import javafx.beans.DefaultProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;

import com.cctintl.c3dfx.skins.C3DToggleNodeSkin;

@DefaultProperty(value="graphic")
public class C3DToggleNode extends ToggleButton {

	public C3DToggleNode() {
		super();
		initialize();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DToggleNodeSkin(this);
	}
	
	private void initialize() {
		this.getStyleClass().add("c3d-toggle-node");        
	}
	
	
}
