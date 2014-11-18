package demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import customui.components.Drawer;
import customui.components.C3DHamburger;

public class DrawerDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		FlowPane content = new FlowPane();
		C3DHamburger h1 = new C3DHamburger();
		FlowPane.setMargin(h1, new Insets(300,0,0,400));
		content.getChildren().add(h1);

		Drawer drawer = new Drawer(content, 250);
				
		// create animation		
		h1.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			drawer.getTransition().setRate(1);
			drawer.getTransition().play();			
		});

		StackPane pane = new StackPane();
		pane.getChildren().add(drawer);
		pane.setStyle("-fx-background-color:WHITE");		
		final Scene scene = new Scene(pane, 800, 800);
		scene.getStylesheets().add(DrawerDemo.class.getResource("css/styles.css").toExternalForm());

		primaryStage.setTitle("JavaFX Drawer");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();		
	}

	public static void main(String[] args) { launch(args); }
	

}
