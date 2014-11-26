package demos;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import customui.components.C3DSpinner;

public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		final Group group = new Group();
		final Scene scene = new Scene(group, 800, 800);
		stage.setScene(scene);
		stage.setTitle("C3D Spinner");
		stage.show();
		C3DSpinner spinner = new C3DSpinner();
		group.getChildren().add(spinner);
	}

	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}
}
