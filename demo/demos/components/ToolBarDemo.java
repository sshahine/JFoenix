package demos.components;


import com.cctintl.c3dfx.controls.C3DToolbar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ToolBarDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			C3DToolbar c3dToolbar = new C3DToolbar();
			c3dToolbar.setLeftItems(new Label("Left"));
			c3dToolbar.setRightItems(new Label("Right"));
			c3dToolbar.setContent(new Label("Content"));
			StackPane main = new StackPane();
			
			main.getChildren().add(c3dToolbar);
			Scene scene = new Scene(main, 600, 400);
			scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
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
