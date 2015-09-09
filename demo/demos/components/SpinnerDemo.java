package demos.components;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXSpinner;

import demos.MainDemo;

public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		final Scene scene = new Scene(new JFXSpinner(), 100, 100);
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("JFX Spinner Demo");
		stage.show();
	}

	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}
}
