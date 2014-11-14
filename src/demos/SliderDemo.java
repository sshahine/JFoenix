package demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import customui.components.C3DSlider;

public class SliderDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			C3DSlider slider = new C3DSlider();
			slider.setMinWidth(700);
			// slider.setMinHeight(500);
			// slider.setOrientation(Orientation.VERTICAL);
			// slider.setMin(0);
			// slider.setMax(200);
			// slider.setValue(50);

			HBox hbox = new HBox();
			hbox.getChildren().addAll(slider);
			hbox.setSpacing(10);
			hbox.setPadding(new Insets(100, 100, 10, 20));
			Scene scene = new Scene(new Group());
			((Group) scene.getRoot()).getChildren().add(hbox);
			primaryStage.setScene(scene);
			primaryStage.setWidth(900);
			primaryStage.setHeight(700);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
