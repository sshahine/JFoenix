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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.jfoenix.controls.JFXProgressBar;

public class ProgressBarDemo extends Application {

	private VBox pane;

	@Override
	public void start(Stage stage) throws Exception {

		pane = new VBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");

		ProgressBar bar = new ProgressBar();
		bar.setPrefWidth(500);

		ProgressBar cssBar = new ProgressBar();
		cssBar.setPrefWidth(500);
		cssBar.setProgress(-1.0f);

		JFXProgressBar jfxBar = new JFXProgressBar();
		jfxBar.setPrefWidth(500);

		JFXProgressBar jfxBarInf = new JFXProgressBar();
		jfxBarInf.setPrefWidth(500);
		jfxBarInf.setProgress(-1.0f);

		Timeline timeline = new Timeline(
									new KeyFrame(
											Duration.ZERO,
											new KeyValue(bar.progressProperty(), 0),
											new KeyValue(jfxBar.progressProperty(), 0)),
									new KeyFrame(
											Duration.seconds(2),
											new KeyValue(bar.progressProperty(), 1),
											new KeyValue(jfxBar.progressProperty(), 1)));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		pane.getChildren().addAll(bar, jfxBar, cssBar, jfxBarInf);

		StackPane main = new StackPane();
		main.getChildren().add(pane);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20, 0, 0, 20));

		final Scene scene = new Scene(main, 600, 200, Color.WHITE);
		scene.getStylesheets().add(ProgressBarDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setTitle("JFX ProgressBar Demo ");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
