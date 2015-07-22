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

package com.cctintl.c3dfx.skins;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.C3DRippler.RipplerPos;
import com.cctintl.c3dfx.controls.C3DToggleNode;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;

public class C3DToggleNodeSkin extends ToggleButtonSkin {

	private final StackPane main = new StackPane();
	private C3DRippler rippler;
	private boolean invalid = true;


	public C3DToggleNodeSkin(C3DToggleNode toggleNode) {
		super(toggleNode);
		if(toggleNode.getBackground().getFills().get(0).getFill().toString().equals("0xffffffba"))
			toggleNode.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		toggleNode.setText(null);
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {			
		if(invalid){
			rippler = new C3DRippler(getSkinnable().getGraphic(),RipplerPos.FRONT);
			getSkinnable().selectedProperty().addListener((o,oldVal,newVal)->{
				rippler.toggle();
			});
			main.getChildren().add(rippler);
			getSkinnable().layoutBoundsProperty().addListener((o,oldVal,newVal)->{
				main.resize(newVal.getWidth(), newVal.getHeight());
			});				
			getChildren().add(main);
			main.resize(getSkinnable().layoutBoundsProperty().get().getWidth(), getSkinnable().layoutBoundsProperty().get().getHeight());
			invalid = false;
		}
	}
}
