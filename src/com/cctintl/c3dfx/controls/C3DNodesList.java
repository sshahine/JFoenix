package com.cctintl.c3dfx.controls;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

public class C3DNodesList extends VBox {
	
	HashMap<Node, Callback<Boolean, ArrayList<KeyValue>>> animationsMap = new HashMap<>();
	private boolean expanded = false;
	
	public void addAnimatedNode(Node node, Callback<Boolean, ArrayList<KeyValue>> animationCallBack ){
		this.getChildren().add(node);
		this.rotateProperty().addListener((o,oldVal,newVal)-> node.setRotate(newVal.doubleValue() % 180 == 0 ? newVal.doubleValue() : -newVal.doubleValue()));
		animationsMap.put(node, animationCallBack);
	}
	
	public void animateList(){
		expanded = !expanded;
		Timeline animateTimeline = new Timeline();
		double duration = 120/this.getChildren().size();
		
		for(int i = 1; i < this.getChildren().size();i++){
			Node child = this.getChildren().get(i);
			ArrayList<KeyValue> keyValues = animationsMap.get(child).call(expanded);
			animateTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i*duration), keyValues.toArray(new KeyValue[keyValues.size()])));
		}
		// add 1st element animation
		ArrayList<KeyValue> keyValues = animationsMap.get(this.getChildren().get(0)).call(expanded);
		animateTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(160), keyValues.toArray(new KeyValue[keyValues.size()])));
		animateTimeline.play();
	}
		
}
