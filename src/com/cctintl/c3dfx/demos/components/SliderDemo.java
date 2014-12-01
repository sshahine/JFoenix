package com.cctintl.c3dfx.demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DSlider;

public class SliderDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			C3DSlider c3dSlider = new C3DSlider();
			c3dSlider.setMinWidth(700);
			c3dSlider.setMinHeight(500);
			//c3dSlider.setOrientation(Orientation.VERTICAL);
			c3dSlider.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, null)));

			Slider slider = new Slider();

			HBox hbox = new HBox();
			VBox vbox = new VBox();
			vbox.getChildren().add(c3dSlider);
			vbox.getChildren().add(slider);
			vbox.setSpacing(100);
			hbox.getChildren().add(vbox);
			hbox.setSpacing(50);
			hbox.setPadding(new Insets(250, 10, 10, 100));

			Scene scene = new Scene(new Group());
			((Group) scene.getRoot()).getChildren().add(hbox);
			//scene.getStylesheets().add(SliderDemo.class.getResource("/resources/css/c3dobjects.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setWidth(900);
			primaryStage.setHeight(900);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
