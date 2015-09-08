package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSlider.IndicatorPosition;

public class SliderDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			JFXSlider hor_left = new JFXSlider();
			hor_left.setMinWidth(500);

			JFXSlider hor_right = new JFXSlider();
			hor_left.setMinWidth(500);
			hor_left.setIndicatorPosition(IndicatorPosition.RIGHT);

			JFXSlider ver_left = new JFXSlider();
			ver_left.setMinHeight(500);
			ver_left.setOrientation(Orientation.VERTICAL);

			JFXSlider ver_right = new JFXSlider();
			ver_right.setMinHeight(500);
			ver_right.setOrientation(Orientation.VERTICAL);
			ver_right.setIndicatorPosition(IndicatorPosition.RIGHT);

			HBox hbox = new HBox();
			hbox.setSpacing(450);
			hbox.getChildren().addAll(ver_right, ver_left);

			VBox vbox = new VBox();
			vbox.getChildren().addAll(hor_right, hor_left, hbox);
			vbox.setSpacing(100);
			vbox.setPadding(new Insets(100, 50, 50, 150));

			Scene scene = new Scene(new Group());
			((Group) scene.getRoot()).getChildren().add(vbox);
			scene.getStylesheets().add(SliderDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setWidth(900);
			primaryStage.setHeight(900);
			primaryStage.show();
			primaryStage.setTitle("JFX Slider Demo");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
