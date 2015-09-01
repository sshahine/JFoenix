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

package com.cctintl.jfx.skins;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.cctintl.jfx.controls.JFXRippler;
import com.cctintl.jfx.controls.JFXToggleNode;
import com.cctintl.jfx.controls.JFXRippler.RipplerPos;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;

public class C3DToggleNodeSkin extends ToggleButtonSkin {

	private final StackPane main = new StackPane();
	private JFXRippler rippler;
	private boolean invalid = true;


	public C3DToggleNodeSkin(JFXToggleNode toggleNode) {
		super(toggleNode);
		if(toggleNode.getBackground().getFills().get(0).getFill().toString().equals("0xffffffba"))
			toggleNode.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		toggleNode.setText(null);
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {			
		if(invalid){
			rippler = new JFXRippler(getSkinnable().getGraphic(),RipplerPos.FRONT);
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
