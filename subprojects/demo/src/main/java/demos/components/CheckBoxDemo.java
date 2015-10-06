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
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXCheckBox;

public class CheckBoxDemo extends Application {

	public int i = 0;
	public int step = 1;
	
	@Override public void start(Stage stage) {

		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		
		CheckBox cb = new CheckBox("CheckBox");
		JFXCheckBox jfxCheckBox = new JFXCheckBox("JFX CheckBox");
		JFXCheckBox customJFXCheckBox = new JFXCheckBox("JFX CheckBox");
		customJFXCheckBox.getStyleClass().add("custom-jfx-check-box");
		
		main.getChildren().add(cb);
		main.getChildren().add(jfxCheckBox);
		main.getChildren().add(customJFXCheckBox);
				
		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 600, 200);
		scene.getStylesheets().add(CheckBoxDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setTitle("JFX CheckBox Demo ");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}

	public static void main(String[] args) { launch(args); }
	
}
