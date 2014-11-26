package demos;

import demos.gui.main.MainController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainDemo extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) throws Exception {
		Flow flow = new Flow(MainController.class);
		DefaultFlowContainer container = new DefaultFlowContainer();
		flow.createHandler().start(container);
		Scene scene = new Scene(container.getView(), 800, 800);
		scene.getStylesheets().add(MainDemo.class.getResource("../resources/css/c3dobjects.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

}