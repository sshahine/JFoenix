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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXRadioButton;

public class RadioButtonDemo extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			final ToggleGroup group = new ToggleGroup();

			JFXRadioButton javaRadio = new JFXRadioButton("JavaFX");
			javaRadio.setPadding(new Insets(10));
			javaRadio.setToggleGroup(group);

			JFXRadioButton jfxRadio = new JFXRadioButton("JFoenix");
			jfxRadio.setPadding(new Insets(10));
			jfxRadio.setToggleGroup(group);

			HBox hbox = new HBox();
			VBox vbox = new VBox();
			vbox.getChildren().add(javaRadio);
			vbox.getChildren().add(jfxRadio);
			vbox.setSpacing(10);
			hbox.getChildren().add(vbox);
			hbox.setSpacing(50);
			hbox.setPadding(new Insets(40, 10, 10, 120));
			
			Scene scene = new Scene(hbox);
			primaryStage.setScene(scene);
			primaryStage.setWidth(500);
			primaryStage.setHeight(400);
			primaryStage.setTitle("JFX RadioButton Demo ");			
			scene.getStylesheets().add(RadioButtonDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
