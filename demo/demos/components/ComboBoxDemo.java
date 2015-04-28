package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DComboBox;

public class ComboBoxDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		C3DComboBox<Label> c = new C3DComboBox<>();
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
		
		C3DButton submitButton = new C3DButton("Submit");
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
