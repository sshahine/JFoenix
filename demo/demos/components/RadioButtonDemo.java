package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DRadioButton;

public class RadioButtonDemo extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			final ToggleGroup group = new ToggleGroup();

			C3DRadioButton c3dRadio = new C3DRadioButton("C3D Radio");
			c3dRadio.setId("c3d_radio");
			c3dRadio.setPadding(new Insets(10));
			c3dRadio.setToggleGroup(group);

			C3DRadioButton fxRadio = new C3DRadioButton("JavaFX Radio");
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

			scene.getStylesheets().add(InputDemo.class.getResource("/resources/css/c3dobjects.css").toExternalForm());

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
