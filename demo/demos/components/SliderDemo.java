package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DSlider;
import com.cctintl.c3dfx.controls.C3DSlider.IndicatorPosition;

public class SliderDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			C3DSlider hor_left = new C3DSlider();
			hor_left.setMinWidth(500);

			C3DSlider hor_right = new C3DSlider();
			hor_left.setMinWidth(500);
			hor_left.setIndicatorPosition(IndicatorPosition.RIGHT);

			C3DSlider ver_left = new C3DSlider();
			ver_left.setMinHeight(500);
			ver_left.setOrientation(Orientation.VERTICAL);

			C3DSlider ver_right = new C3DSlider();
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
