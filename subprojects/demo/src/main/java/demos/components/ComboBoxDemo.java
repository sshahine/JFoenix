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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXComboBox;

public class ComboBoxDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		JFXComboBox<Label> c = new JFXComboBox<>();
		c.getItems().add(new Label("Java 1.8"));
		c.getItems().add(new Label("Java 1.7"));
		c.getItems().add(new Label("Java 1.6"));
		c.getItems().add(new Label("Java 1.5"));
		c.setEditable(true);
		c.setPromptText("Select Java Version");
		
		HBox pane = new HBox(100);
		HBox.setMargin(c, new Insets(20));
		pane.setStyle("-fx-background-color:WHITE");
		pane.getChildren().add(c);
		
		final Scene scene = new Scene(pane, 300, 300);
		scene.getStylesheets().add(ComboBoxDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

		primaryStage.setTitle("JFX ComboBox Demo");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();		
	}

	public static void main(String[] args) { launch(args); }
}
