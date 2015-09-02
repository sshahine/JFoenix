package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.cctintl.jfx.controls.JFXRadioButton;

public class RadioButtonDemo extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			final ToggleGroup group = new ToggleGroup();

			JFXRadioButton c3dRadio = new JFXRadioButton("C3D Radio");
			c3dRadio.setId("c3d_radio");
			c3dRadio.setPadding(new Insets(10));
			c3dRadio.setToggleGroup(group);

			JFXRadioButton fxRadio = new JFXRadioButton("JavaFX Radio");
			fxRadio.setId("javafx_radio");
			fxRadio.setPadding(new Insets(10));
			fxRadio.setToggleGroup(group);

			HBox hbox = new HBox();
			VBox vbox = new VBox();
			vbox.getChildren().add(c3dRadio);
			vbox.getChildren().add(fxRadio);
			vbox.setSpacing(10);
			hbox.getChildren().add(vbox);
			hbox.setSpacing(50);
			hbox.setPadding(new Insets(40, 10, 10, 120));
			Scene scene = new Scene(new Group());
			((Group) scene.getRoot()).getChildren().add(hbox);
			primaryStage.setScene(scene);
			primaryStage.setWidth(500);
			primaryStage.setHeight(400);

			scene.getStylesheets().add(InputDemo.class.getResource("/resources/css/jfx-components.css").toExternalForm());

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
