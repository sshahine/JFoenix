package demos.components;

import com.jfoenix.controls.JFXComboBox;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ComboBoxDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		JFXComboBox<Label> c = new JFXComboBox<>();
		c.getItems().add(new Label("Java 1.8"));
		c.getItems().add(new Label("Java 1.7"));
		c.getItems().add(new Label("Java 1.6"));
		c.getItems().add(new Label("Java 1.5"));
		c.setEditable(true);
		c.setPromptText("Select Java Version");
		
		HBox pane = new HBox(100);
		HBox.setMargin(c, new Insets(20));
		pane.setStyle("-fx-background-color:WHITE");
		pane.getChildren().add(c);
		
		final Scene scene = new Scene(pane, 300, 300);
		scene.getStylesheets().add(ComboBoxDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

		primaryStage.setTitle("JFX ComboBox Demo");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();		
	}

	public static void main(String[] args) { launch(args); }
}
