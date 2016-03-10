/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * @author Shadi Shaheen
 *
 */
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
		if(drawers.size() > 0) drawers.get(0).setContent(content);
		else this.getChildren().add(this.content);
	}


	private void addDrawer(JFXDrawer drawer){
		if (drawer == null) return;

		if(drawers.size() == 0){
			if(content!=null) drawer.setContent(content);
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
		if(!drawers.contains(drawer)) addDrawer(drawer);
		if(drawer.isShown()) drawer.hide();
		else{
			updateDrawerPosition(drawer);
			drawer.draw();	
		}
	}

	public void toggle(JFXDrawer drawer, boolean show){
		if(!drawers.contains(drawer)) addDrawer(drawer);
		if(!show){
			if(drawer.isShown()) drawer.hide();
		}else{
			if(!drawer.isShown()){
				updateDrawerPosition(drawer);
				drawer.draw();
			}
		}
	}


}
