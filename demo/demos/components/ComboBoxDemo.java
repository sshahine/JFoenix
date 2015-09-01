package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.cctintl.jfx.controls.JFXButton;
import com.cctintl.jfx.controls.JFXComboBox;

public class ComboBoxDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		JFXComboBox<Label> c = new JFXComboBox<>();
		c.getItems().add(new Label("JAVA1"));
		c.getItems().add(new Label("JAVA2"));
		c.getItems().add(new Label("JAVA3"));
		c.getItems().add(new Label("JAVA4"));
		c.setEditable(true);
		c.setPromptText("Select Java Type");
		
		HBox pane = new HBox(100);
		HBox.setMargin(c, new Insets(20));
		pane.setStyle("-fx-background-color:WHITE");
		pane.getChildren().add(c);
		
		JFXButton submitButton = new JFXButton("Submit");
		HBox.setMargin(submitButton, new Insets(20));
		pane.getChildren().add(submitButton);

		final Scene scene = new Scene(pane, 800, 800);
		scene.getStylesheets().add(DrawerDemo.class.getResource("css/styles.css").toExternalForm());

		primaryStage.setTitle("JavaFX MenuButton Demo");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();		
	}

	public static void main(String[] args) { launch(args); }
}
