package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import customui.components.C3DToggleButton;

public class ToggleButtonDemo extends Application {

	private VBox pane;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		pane = new VBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");
		
		ToggleButton button = new ToggleButton("JavaFx Toggle");
		pane.getChildren().add(button);
		
		pane.getChildren().add(new C3DToggleButton());
		
		StackPane main = new StackPane();
		main.getChildren().add(pane);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20,0,0,20));

		final Scene scene = new Scene(main, 600, 400, Color.WHITE);
		stage.setTitle("JavaFX TextField ;) ");
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}
	public static void main(String[] args) { launch(args); }

}
