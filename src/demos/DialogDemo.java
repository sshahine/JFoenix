package demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import customui.components.C3DButton;
import customui.components.C3DDialog;

public class DialogDemo extends Application {


	
	@Override
	public void start(Stage stage) throws Exception {

		HBox pane = new HBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");
		C3DButton button = new C3DButton("Dialogs");
		button.setOnMouseClicked((e)-> new C3DDialog((Pane) stage.getScene().getRoot()));
		pane.getChildren().add(button);
		
				
		StackPane main = new StackPane();
		main.getChildren().add(pane);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20,0,0,20));

		final Scene scene = new Scene(main, 600, 400, Color.WHITE);
		stage.setTitle("JavaFX Dialogs ;) ");
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args) { launch(args); }






}
