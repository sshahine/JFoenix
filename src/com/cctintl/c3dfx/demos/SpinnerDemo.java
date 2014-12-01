package com.cctintl.c3dfx.demos;

import com.cctintl.c3dfx.controls.C3DSpinner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		final Group group = new Group();
		final Scene scene = new Scene(group, 800, 800);
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/c3dobjects.css").toExternalForm());
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
