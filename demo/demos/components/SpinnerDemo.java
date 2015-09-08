package demos.components;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXSpinner;

import demos.MainDemo;

public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		final Group group = new Group();
		final Scene scene = new Scene(group, 800, 800);
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("JFX Spinner Demo");
		stage.show();
		JFXSpinner spinner = new JFXSpinner();
		group.getChildren().add(spinner);
	}

	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}
}
