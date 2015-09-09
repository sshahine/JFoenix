package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXToggleNode;

import de.jensd.fx.fontawesome.Icon;

public class ToggleButtonDemo extends Application {

	private VBox pane;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		pane = new VBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:#EEE; -fx-padding: 40;");
		
		ToggleButton button = new ToggleButton("JavaFx Toggle");
		pane.getChildren().add(button);
		
		JFXToggleButton toggleButton = new JFXToggleButton();
		pane.getChildren().add(toggleButton);		
		
		JFXToggleNode node = new JFXToggleNode();		
		Icon value = new Icon("HEART");
		value.setPadding(new Insets(10));
		node.setGraphic(value);
		
		pane.getChildren().add(node);
		

		final Scene scene = new Scene(pane, 600, 400, Color.valueOf("#EEE"));
		stage.setTitle("JFX Toggle Button Demo ");
		scene.getStylesheets().add(ToggleButtonDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}
	public static void main(String[] args) { launch(args); }

}
