package com.cctintl.c3dfx.demos.components;

import com.cctintl.c3dfx.controls.C3DProgressBar;

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

public class ProgressBarDemo extends Application {

	private VBox pane;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		pane = new VBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");
		
		ProgressBar bar = new ProgressBar();
		ProgressBar cssBar = new ProgressBar();
		cssBar.setProgress(-1.0f);
		
		cssBar.getStyleClass().add("css-progress-bar");
		C3DProgressBar c3dBar = new C3DProgressBar();
		C3DProgressBar c3dBarInf = new C3DProgressBar();
		c3dBarInf.setProgress(-1.0f);
		
		Timeline task = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(bar.progressProperty(), 0),
						new KeyValue(c3dBar.progressProperty(), 0)
						),
						new KeyFrame(
								Duration.seconds(2), 
								new KeyValue(bar.progressProperty(), 1),
								new KeyValue(c3dBar.progressProperty(), 1)
								)
				);
		task.setCycleCount(5);
		task.playFromStart();
		
		pane.getChildren().add(bar);
		pane.getChildren().add(c3dBar);
		pane.getChildren().add(cssBar);
		pane.getChildren().add(c3dBarInf);
		
		
		StackPane main = new StackPane();
		main.getChildren().add(pane);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20,0,0,20));

		final Scene scene = new Scene(main, 600, 400, Color.WHITE);
		stage.setTitle("JavaFX TextField ;) ");
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}
	public static void main(String[] args) { launch(args); }

}
