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

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class JFXDrawersStack extends StackPane {

	private ArrayList<JFXDrawer> drawers = new ArrayList<>();
	private Node content ;
	private Rectangle clip = new Rectangle();
	boolean holding = false;
	
	public JFXDrawersStack() {
		super();
		clip.widthProperty().bind(this.widthProperty());
		clip.heightProperty().bind(this.heightProperty());
		this.setClip(clip);
	}
	
	public void setContent(Node content){
		this.content = content;
	}
	
	
	public void addDrawer(JFXDrawer drawer){
		if(drawers.size() == 0){
			drawer.setContent(content);
			this.getChildren().add(drawer);
		}else {
			drawer.setContent(drawers.get(drawers.size()-1));
			this.getChildren().add(drawer);
		}
		
		drawer.sidePane.addEventHandler(MouseEvent.MOUSE_PRESSED, (event)->{
			holding = true;
			new Thread(()->{
				try {
					Thread.sleep(300);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(holding){
					holding = false;
					if(drawers.indexOf(drawer) < drawers.size()-1)
						Platform.runLater(()->drawer.bringToFront((param)->{
							updateDrawerPosition(drawer);
							return param;
						}));
				}
			}).start();
		});
		
		drawer.sidePane.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event)-> holding = false);
		drawer.sidePane.addEventHandler(MouseEvent.MOUSE_RELEASED, (event)-> holding = false);
		
		drawers.add(drawer);
		
	}

	private void updateDrawerPosition(JFXDrawer drawer){
		int index = drawers.indexOf(drawer);
		if(index + 1 < drawers.size()){
			if(index - 1 >= 0) drawers.get(index+1).setContent(drawers.get(index-1));
			else if(index == 0) drawers.get(index+1).setContent(content);
		}
		if(index < drawers.size() - 1){
			drawer.setContent(drawers.get(drawers.size()-1));
			drawers.remove(drawer);
			drawers.add(drawer);
			this.getChildren().add(drawer);
		}
	}
	
	public void toggle(JFXDrawer drawer){
		if(drawer.isShown()) drawer.hide();
		else{
			updateDrawerPosition(drawer);
			drawer.draw();	
		}
	}
	
	
}
