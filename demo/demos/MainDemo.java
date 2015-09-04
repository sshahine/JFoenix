package demos;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import demos.gui.main.MainController;

public class MainDemo extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) throws Exception {
		Flow flow = new Flow(MainController.class);
		DefaultFlowContainer container = new DefaultFlowContainer();
		flow.createHandler().start(container);
		Scene scene = new Scene(container.getView(), 800, 800);
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/jfoenix-fonts.css").toExternalForm());
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/jfoenix-design.css").toExternalForm());
		scene.getStylesheets().add(MainDemo.class.getResource("/resources/css/jfoenix-main-demo.css").toExternalForm());
//		stage.initStyle(StageStyle.UNDECORATED);
//		stage.setFullScreen(true);
		stage.setScene(scene);
		stage.show();
	}

}