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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package demos.components;


import java.util.ArrayList;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXButton.ButtonType;


public class NodesListDemo extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			JFXButton ssbutton1 = new JFXButton();			
			Label sslabel = new Label("R1");
			sslabel.setStyle("-fx-text-fill:WHITE");			
			ssbutton1.setGraphic(sslabel);			
			ssbutton1.setButtonType(ButtonType.RAISED);
			ssbutton1.getStyleClass().addAll("animated-option-button","animated-option-sub-button2");
			
			JFXButton ssbutton2 = new JFXButton("R2");
			ssbutton2.setTooltip(new Tooltip("Button 2"));
			ssbutton2.setButtonType(ButtonType.RAISED);
			ssbutton2.getStyleClass().addAll("animated-option-button","animated-option-sub-button2");
			
			JFXButton ssbutton3 = new JFXButton("R3");
			ssbutton3.setButtonType(ButtonType.RAISED);
			ssbutton3.getStyleClass().addAll("animated-option-button","animated-option-sub-button2");
			
			
			JFXNodesList nodesList3 = new JFXNodesList();
			nodesList3.setSpacing(10);
			// init nodes
			nodesList3.addAnimatedNode(ssbutton1, (expanded)->{ return new ArrayList<KeyValue>(){{ add(new KeyValue(sslabel.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));}};});
			nodesList3.addAnimatedNode(ssbutton2);
			nodesList3.addAnimatedNode(ssbutton3);
			
			
			
			JFXButton sbutton1 = new JFXButton();			
			Label slabel = new Label("B1");
			slabel.setStyle("-fx-text-fill:WHITE");			
			sbutton1.setGraphic(slabel);			
			sbutton1.setButtonType(ButtonType.RAISED);
			sbutton1.getStyleClass().addAll("animated-option-button","animated-option-sub-button");
			
			JFXButton sbutton2 = new JFXButton("B2");
			sbutton2.setTooltip(new Tooltip("Button 2"));
			sbutton2.setButtonType(ButtonType.RAISED);
			sbutton2.getStyleClass().addAll("animated-option-button","animated-option-sub-button");
			
			JFXButton sbutton3 = new JFXButton("B3");
			sbutton3.setButtonType(ButtonType.RAISED);
			sbutton3.getStyleClass().addAll("animated-option-button","animated-option-sub-button");
			
			
			JFXNodesList nodesList2 = new JFXNodesList();
			nodesList2.setSpacing(10);
			// init nodes
			nodesList2.addAnimatedNode(sbutton1, (expanded)->{ return new ArrayList<KeyValue>(){{ add(new KeyValue(slabel.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));}};});
			nodesList2.addAnimatedNode(nodesList3);
			nodesList2.addAnimatedNode(sbutton2);
			nodesList2.addAnimatedNode(sbutton3);
			nodesList2.setRotate(90);
			
			
			JFXButton button1 = new JFXButton();			
			Label label = new Label("G1");
			button1.setGraphic(label);			
			label.setStyle("-fx-text-fill:WHITE");			
			button1.setButtonType(ButtonType.RAISED);
			button1.getStyleClass().add("animated-option-button");
			
			JFXButton button2 = new JFXButton("G2");
			button2.setTooltip(new Tooltip("Button 2"));
			button2.setButtonType(ButtonType.RAISED);
			button2.getStyleClass().add("animated-option-button");
			
			JFXButton button3 = new JFXButton("G3");
			button3.setButtonType(ButtonType.RAISED);
			button3.getStyleClass().add("animated-option-button");
			
			
			JFXNodesList nodesList = new JFXNodesList();
			nodesList.setSpacing(10);
			nodesList.addAnimatedNode(button1, (expanded)->{ return new ArrayList<KeyValue>(){{ add(new KeyValue(label.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));}};});
			nodesList.addAnimatedNode(button2);
			nodesList.addAnimatedNode(nodesList2);
			nodesList.addAnimatedNode(button3);
			nodesList.setRotate(180);
			
			
			StackPane main = new StackPane();
			main.setPadding(new Insets(10));
			
			JFXButton e = new JFXButton("Click Me");
			e.setTranslateY(-50);
			e.setTranslateX(-100);
			main.getChildren().add(e);
			main.getChildren().add(nodesList);
			
			Scene scene = new Scene(main, 600, 600);
			scene.getStylesheets().add(NodesListDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	class User {
		String userName;
		String age;
		public User(String userName, String age) {
			this.userName = userName;
			this.age = age;
		}

		public String toString(){
			return userName + " : " + age;
		}

	}
}