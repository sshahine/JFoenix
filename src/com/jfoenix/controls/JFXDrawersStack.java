package com.jfoenix.controls;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class JFXDrawersStack extends StackPane {

	ArrayList<JFXDrawer> drawers = new ArrayList<>();
	Node content ;
	
	public void setContent(Node content){
		this.content = content;
	}
	
	boolean holding = false;
	
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
		if(drawer.isDrawn()) drawer.hide();
		else{
			updateDrawerPosition(drawer);
			drawer.draw();	
		}
	}
	
	
}
