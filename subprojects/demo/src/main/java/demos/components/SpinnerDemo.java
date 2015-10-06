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
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXSpinner;

import demos.MainDemo;

public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		StackPane pane = new StackPane();
		
		JFXSpinner root = new JFXSpinner();
		
		pane.getChildren().add(root);
		
		final Scene scene = new Scene(pane, 300, 300);
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("JFX Spinner Demo");
		stage.show();
	}

	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}
}
