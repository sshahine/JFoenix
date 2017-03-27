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
package com.jfoenix.skins;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * @author Shadi Shaheen
 *
 */
public class JFXToggleButtonOldSkin extends ToggleButtonSkin {

	private final AnchorPane main = new AnchorPane();
	private Line line;
	private final int startX = 16;
	private final int endX = 52;
	private final int startY = 12;

	private Circle circle;
	private final int circleRadius = 8;
	private Circle innerCircle ;
	private StackPane circles = new StackPane();
	private final int strokeWidth = 2;
	private final Color unToggledColor = Color.valueOf("#5A5A5A");
	
	private Color toggledColor ;
	private JFXRippler rippler;
	
	private Timeline transition;
	private boolean invalid = true;


	public JFXToggleButtonOldSkin(JFXToggleButton toggleButton) {
		super(toggleButton);
		// hide the toggle button
		toggleButton.setStyle("-fx-background-color:TRANSPARENT");
		
		line = new Line(startX,startY,endX,startY);
		line.setStroke(unToggledColor);
		line.setStrokeWidth(1);

		circle = new Circle(startX-circleRadius, startY, circleRadius);
		circle.setFill(Color.TRANSPARENT);
		circle.setStroke(unToggledColor);
		circle.setStrokeWidth(strokeWidth);
		
		innerCircle = new Circle(startX-circleRadius, startY,0);
		innerCircle.setStrokeWidth(0);

		StackPane circlePane = new StackPane();
		circlePane.getChildren().add(circle);
		circlePane.getChildren().add(innerCircle);
		circlePane.setPadding(new Insets(15));		
		rippler = new JFXRippler(circlePane,RipplerMask.CIRCLE, RipplerPos.BACK);		
		
		
		circles.getChildren().add(rippler);
		
		main.getChildren().add(line);
		main.getChildren().add(circles);
		main.setCursor(Cursor.HAND);
		AnchorPane.setTopAnchor(circles, -12.0);
		AnchorPane.setLeftAnchor(circles, -15.0);
		
		getSkinnable().selectedProperty().addListener((o,oldVal,newVal) ->{
			rippler.setRipplerFill(newVal?unToggledColor:toggledColor);
			transition.setRate(newVal?1:-1);
			transition.play();
		});
		
		updateChildren();
	}

	@Override protected void updateChildren() {
		super.updateChildren();
		if (main != null) {
			getChildren().remove(0);			
			getChildren().add(main);
		}
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if(invalid){
			toggledColor = (Color) ((JFXToggleButton) getSkinnable()).getToggleColor();
			transition = getToggleTransition();
			innerCircle.setFill(toggledColor);
			innerCircle.setStroke(toggledColor);
			rippler.setRipplerFill(toggledColor);
			invalid = false;
		}
	}
	
	private Timeline getToggleTransition(){
		return  new Timeline(
				new KeyFrame(
						Duration.ZERO,
						new KeyValue(circles.translateXProperty(), 0 ,Interpolator.LINEAR ),
						new KeyValue(line.strokeProperty(), unToggledColor ,Interpolator.EASE_BOTH),
						new KeyValue(innerCircle.strokeWidthProperty(), 0 ,Interpolator.EASE_BOTH ),
						new KeyValue(innerCircle.radiusProperty(), 0 ,Interpolator.EASE_BOTH)
						),
						new KeyFrame(
								Duration.millis(30),       
								new KeyValue(circles.translateXProperty(), 0 ,Interpolator.LINEAR ),
								new KeyValue(line.strokeProperty(), unToggledColor ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(
										Duration.millis(70),       
										new KeyValue(circles.translateXProperty(), endX-startX + 2*circleRadius ,Interpolator.LINEAR),
										new KeyValue(line.strokeProperty(), toggledColor ,Interpolator.EASE_BOTH)
										),
										new KeyFrame(
												Duration.millis(100),
												new KeyValue(innerCircle.radiusProperty(), circleRadius ,Interpolator.EASE_BOTH),
												new KeyValue(innerCircle.strokeWidthProperty(), strokeWidth ,Interpolator.EASE_BOTH )
												)
				);
	}

}
