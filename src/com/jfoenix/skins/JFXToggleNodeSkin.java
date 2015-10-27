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

package com.jfoenix.skins;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
		main.setPickOnBounds(false);
		getSkinnable().setPickOnBounds(false);
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {			
		if(invalid){
			StackPane toggleNodeContainer = new StackPane();
			toggleNodeContainer.getChildren().add(getSkinnable().getGraphic());
			toggleNodeContainer.prefWidthProperty().bind(getSkinnable().widthProperty());
			toggleNodeContainer.prefHeightProperty().bind(getSkinnable().heightProperty());
			
			rippler = new JFXRippler(toggleNodeContainer,RipplerPos.FRONT){
				@Override protected Node getMask(){
					StackPane mask = new StackPane(); 
					mask.shapeProperty().bind(getSkinnable().shapeProperty());				
					mask.backgroundProperty().bind(Bindings.createObjectBinding(()->{					
						return new Background(new BackgroundFill(Color.WHITE, 
								getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getRadii() : CornerRadii.EMPTY,
							    getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
					}, getSkinnable().backgroundProperty()));				
					mask.resize(getSkinnable().getWidth(), getSkinnable().getHeight());
					return mask;
				}
			};
			
			getSkinnable().selectedProperty().addListener((o,oldVal,newVal)-> rippler.toggle());
			main.getChildren().add(rippler);
			getSkinnable().layoutBoundsProperty().addListener((o,oldVal,newVal)-> main.resize(newVal.getWidth(), newVal.getHeight()));
			getChildren().add(main);
			main.resize(getSkinnable().layoutBoundsProperty().get().getWidth(), getSkinnable().layoutBoundsProperty().get().getHeight());
			invalid = false;
		}
		
		double shift = 1;
		main.resizeRelocate(getSkinnable().getLayoutBounds().getMinX()-shift, getSkinnable().getLayoutBounds().getMinY()-shift, getSkinnable().getWidth()+(2*shift), getSkinnable().getHeight()+(2*shift));
			
	}
}
