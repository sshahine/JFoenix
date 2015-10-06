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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.effects.JFXDepthManager;

public class RipplerDemo extends Application {

	public int i = 0;
	public int step = 1;
	
	@Override public void start(Stage stage) {

		//TODO drop shadow changes the width and hegith thus need to be considered
		
		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		
		Label l = new Label("Click Me");
		l.setStyle("-fx-background-color:WHITE;");
		l.setPadding(new Insets(20));
		JFXRippler lrippler = new JFXRippler(l);
		lrippler.setEnabled(false);
		main.getChildren().add(lrippler);
		
		l.setOnMousePressed((e) -> {
			if(i == 5) step = -1;	
			else if (i == 0) step = 1;
			JFXDepthManager.setDepth(l, i+=step % JFXDepthManager.getLevels());
		});
		
		Label l1 = new Label("TEST");
		l1.setStyle("-fx-background-color:WHITE;");
		l1.setPadding(new Insets(20));
		JFXRippler rippler1 = new JFXRippler(l1);
		main.getChildren().add(rippler1);		
		JFXDepthManager.setDepth(rippler1, 1);
		
		Label l2 = new Label("TEST1");
		l2.setStyle("-fx-background-color:WHITE;");
		l2.setPadding(new Insets(20));
		JFXRippler rippler2 = new JFXRippler(l2);
		main.getChildren().add(rippler2);		
		JFXDepthManager.setDepth(rippler2, 2);

		
		Label l3 = new Label("TEST2");
		l3.setStyle("-fx-background-color:WHITE;");
		l3.setPadding(new Insets(20));
		JFXRippler rippler3 = new JFXRippler(l3);
		main.getChildren().add(rippler3);
		JFXDepthManager.setDepth(rippler3, 3);
		
		Label l4 = new Label("TEST3");
		l4.setStyle("-fx-background-color:WHITE;");
		l4.setPadding(new Insets(20));
		JFXRippler rippler4 = new JFXRippler(l4);
		main.getChildren().add(rippler4);
		JFXDepthManager.setDepth(rippler4, 4);
		
		Label l5 = new Label("TEST4");
		l5.setStyle("-fx-background-color:WHITE;");
		l5.setPadding(new Insets(20));
		JFXRippler rippler5 = new JFXRippler(l5);
		main.getChildren().add(rippler5);
		JFXDepthManager.setDepth(rippler5, 5);

		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 600, 400);

		stage.setTitle("JavaFX Ripple effect and shadows ");
		stage.setScene(scene);
		stage.setResizable(false);

		stage.show();

	}

	public static void main(String[] args) { launch(args); }
}
