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
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * @author Shadi Shaheen
 *
 */
public class JFXToggleButtonSkin extends ToggleButtonSkin {

	private final StackPane main = new StackPane();
	private Line line;
	private final int startX = 0;
	private final int endX = 22;
	private final int startY = 0;

	private Circle circle;
	private final int circleRadius = 10;
	private StackPane circles = new StackPane();
	
	private JFXRippler rippler;
	private Timeline transition;

	public JFXToggleButtonSkin(JFXToggleButton toggleButton) {
		super(toggleButton);
		// hide the toggle button		
		toggleButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		
		line = new Line(startX,startY,endX,startY);
		
		line.setStroke(toggleButton.getUnToggleLineColor());
		line.setStrokeWidth(14);
		line.setStrokeLineCap(StrokeLineCap.ROUND);

		circle = new Circle(startX-circleRadius, startY, circleRadius);
		circle.setFill(toggleButton.getUnToggleColor());
		circle.setSmooth(true);
		JFXDepthManager.setDepth(circle, 1);
		

		StackPane circlePane = new StackPane();
		circlePane.getChildren().add(circle);
		circlePane.setPadding(new Insets(14));		
		rippler = new JFXRippler(circlePane,RipplerMask.CIRCLE, RipplerPos.BACK);		
		rippler.setRipplerFill(toggleButton.getUnToggleLineColor());
		
		circles.getChildren().add(rippler);
		circles.setTranslateX(-(line.getLayoutBounds().getWidth()/2) + circleRadius);
		
		main.getChildren().add(line);
		main.getChildren().add(circles);
		main.setCursor(Cursor.HAND);
		
		getSkinnable().selectedProperty().addListener((o,oldVal,newVal) ->{
			rippler.setRipplerFill(newVal?toggleButton.getToggleColor():toggleButton.getUnToggleLineColor());
			transition.setRate(newVal?1:-1);
			transition.play();
		});
		
		getSkinnable().setGraphic(main);
		
		updateToggleTransition();
		
		toggleButton.toggleColorProperty().addListener((o,oldVal,newVal)-> {updateToggleTransition(); udpateCricle();});
		toggleButton.unToggleColorProperty().addListener((o,oldVal,newVal)-> {updateToggleTransition(); udpateCricle();});
		toggleButton.toggleLineColorProperty().addListener((o,oldVal,newVal)-> {updateToggleTransition(); updateLine();});
		toggleButton.unToggleLineColorProperty().addListener((o,oldVal,newVal)-> {updateToggleTransition(); updateLine();});
	}

	private void udpateCricle(){
		if(getSkinnable().isSelected())
			circle.setFill(((JFXToggleButton) getSkinnable()).getToggleColor());
		circle.setFill(((JFXToggleButton) getSkinnable()).getUnToggleColor());
	}
	private void updateLine(){
		if(getSkinnable().isSelected())
			line.setStroke(((JFXToggleButton) getSkinnable()).getToggleLineColor());
		line.setStroke(((JFXToggleButton) getSkinnable()).getUnToggleLineColor());
	}
	private void updateToggleTransition(){
		transition =  new Timeline(
				new KeyFrame(
						Duration.ZERO,
						new KeyValue(circles.translateXProperty(), -(line.getLayoutBounds().getWidth()/2) + circleRadius ,Interpolator.EASE_BOTH ),
						new KeyValue(line.strokeProperty(), ((JFXToggleButton) getSkinnable()).getUnToggleLineColor() ,Interpolator.EASE_BOTH),
						new KeyValue(circle.fillProperty(), ((JFXToggleButton) getSkinnable()).getUnToggleColor() ,Interpolator.EASE_BOTH)
						),
								new KeyFrame(
										Duration.millis(100),       
										new KeyValue(circles.translateXProperty(), (line.getLayoutBounds().getWidth()/2) - circleRadius ,Interpolator.EASE_BOTH),
										new KeyValue(line.strokeProperty(), ((JFXToggleButton) getSkinnable()).getToggleLineColor() ,Interpolator.EASE_BOTH),
										new KeyValue(circle.fillProperty(), ((JFXToggleButton) getSkinnable()).getToggleColor() ,Interpolator.EASE_BOTH)
										)
										
				);
	}

}
