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

package com.jfoenix.skins;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;

public class JFXToggleNodeSkin extends ToggleButtonSkin {

	private final StackPane main = new StackPane();
	private JFXRippler rippler;
	private boolean invalid = true;


	public JFXToggleNodeSkin(JFXToggleNode toggleNode) {
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
