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
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.transitions.JFXFillTransition;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;

public class JFXToggleNodeSkin extends ToggleButtonSkin {

	private final StackPane main = new StackPane();
	private JFXRippler rippler;
	private boolean invalid = true;
	private final CornerRadii defaultRadii = new CornerRadii(3);
	private JFXFillTransition ft;

	public JFXToggleNodeSkin(JFXToggleNode toggleNode) {
		super(toggleNode);
		toggleNode.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, defaultRadii, null)));
		toggleNode.setText(null);
		main.setPickOnBounds(false);
		getSkinnable().setPickOnBounds(false);

		StackPane toggleNodeContainer = new StackPane();
		toggleNodeContainer.getChildren().add(getSkinnable().getGraphic());
		toggleNodeContainer.prefWidthProperty().bind(getSkinnable().widthProperty());
		toggleNodeContainer.prefHeightProperty().bind(getSkinnable().heightProperty());
		
		rippler = new JFXRippler(toggleNodeContainer,RipplerPos.FRONT){
			@Override protected Node getMask(){
				StackPane mask = new StackPane(); 
				mask.shapeProperty().bind(main.shapeProperty());				
				mask.backgroundProperty().bind(Bindings.createObjectBinding(()->{					
					return new Background(new BackgroundFill(Color.WHITE, 
							main.backgroundProperty().get()!=null?main.getBackground().getFills().get(0).getRadii() : CornerRadii.EMPTY,
									main.backgroundProperty().get()!=null?main.getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
				}, main.backgroundProperty()));				
				mask.resize(main.getWidth(), main.getHeight());
				return mask;				
			}
			@Override protected void initListeners(){
				ripplerPane.setOnMousePressed((event) -> {
					createRipple(event.getX(),event.getY());
				});
			}
		};			
		main.getChildren().add(rippler);
		
		getSkinnable().setPickOnBounds(false);
		main.setPickOnBounds(false);
		
		main.borderProperty().bind(getSkinnable().borderProperty());		
		ObjectBinding<Background> backgroundBinding = Bindings.createObjectBinding(()->{
			CornerRadii radii = toggleNode.getBackground()==null ? null : toggleNode.getBackground().getFills().get(0).getRadii();
			Insets insets = toggleNode.getBackground()==null ? null : Insets.EMPTY;
			return new Background(new BackgroundFill(toggleNode.isSelected()? toggleNode.getSelectedColor() : toggleNode.getUnSelectedColor(), radii, insets));			
		}, toggleNode.unSelectedColorProperty(), toggleNode.backgroundProperty());
		main.backgroundProperty().bind(backgroundBinding);
				
		
		// listener to change background color 
		getSkinnable().selectedProperty().addListener((o,oldVal,newVal)->{
			if(ft==null){
//				unSelectedColor = toggleNode.getBackground()==null? Color.TRANSPARENT : (Color) toggleNode.getBackground().getFills().get(0).getFill();
//				if(selectedColor==null) selectedColor = new Color(((Color)rippler.getRipplerFill()).getRed(), ((Color)rippler.getRipplerFill()).getGreen(), ((Color)rippler.getRipplerFill()).getBlue(),0.2);
				ft = new JFXFillTransition(Duration.millis(320), main);
				ft.toValueProperty().bind(toggleNode.selectedColorProperty());
				ft.fromValueProperty().bind(toggleNode.unSelectedColorProperty());
			}
			main.backgroundProperty().unbind();
			ft.stop();
			ft.setRate(newVal?1:-1);
			ft.play();
			
		});
	}
	
	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (main != null) getChildren().add(0,main);			
		for(int i = 1 ; i < getChildren().size(); i++)
			getChildren().get(i).setMouseTransparent(true);
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {			
		if(invalid){
//			getSkinnable().layoutBoundsProperty().addListener((o,oldVal,newVal)-> main.resize(newVal.getWidth(), newVal.getHeight()));
//			main.resize(getSkinnable().layoutBoundsProperty().get().getWidth(), getSkinnable().layoutBoundsProperty().get().getHeight());
			
			invalid = false;
		}
		double shift = 1;		
		main.resizeRelocate(getSkinnable().getLayoutBounds().getMinX()-shift, getSkinnable().getLayoutBounds().getMinY()-shift, getSkinnable().getWidth()+(2*shift), getSkinnable().getHeight()+(2*shift));
	}
	
}
