package demos.components;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.cctintl.jfx.controls.JFXToolbar;

public class ToolBarDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			JFXToolbar c3dToolbar = new JFXToolbar();
			c3dToolbar.setLeftItems(new Label("Left"));
			c3dToolbar.setRightItems(new Label("Right"));
			StackPane main = new StackPane();
			
			main.getChildren().add(c3dToolbar);
			Scene scene = new Scene(main, 600, 400);
			scene.getStylesheets().add(ToolBarDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
