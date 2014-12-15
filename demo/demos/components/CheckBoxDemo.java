package demos.components;

import com.cctintl.c3dfx.controls.C3DCheckBox;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CheckBoxDemo extends Application {

	public int i = 0;
	public int step = 1;
	
	@Override public void start(Stage stage) {

		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		
		CheckBox cb = new CheckBox("CheckBox");
		C3DCheckBox c3b = new C3DCheckBox("C3D CheckBox");
		C3DCheckBox customC3B = new C3DCheckBox("C3D CheckBox");
		customC3B.getStyleClass().add("custom-c3d-check-box");
		
		main.getChildren().add(cb);
		main.getChildren().add(c3b);
		main.getChildren().add(customC3B);
		
		
		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 600, 400);
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setTitle("JavaFX Ripple effect and shadows ");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}

	public static void main(String[] args) { launch(args); }
	
}
