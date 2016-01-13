package demos.components;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextAreaDemo extends Application {

	
	@Override public void start(Stage stage) {

		VBox main = new VBox();
		main.setSpacing(50);
		
		main.getChildren().add(new TextArea());
		JFXTextArea prompt = new JFXTextArea();
		prompt.setPromptText("TEST SDFSDLFSDd");
		prompt.setLabelFloat(true);
		
		RequiredFieldValidator validator = new RequiredFieldValidator();
		// NOTE adding error class to text area is causing the cursor to disapper
		validator.setErrorStyleClass("");
		validator.setMessage("Input Requireas x");
		validator.setIcon(new Icon(AwesomeIcon.WARNING,"1em",";","error"));		
		prompt.getValidators().add(validator);
		prompt.focusedProperty().addListener((o,oldVal,newVal)->{
			if(!newVal) prompt.validate();
		});
		
		
		main.getChildren().add(prompt);
		
		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 800, 600);
		scene.getStylesheets().add(ButtonDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setTitle("JFX Button Demo");
		stage.setScene(scene);
		stage.show();
		
	}

	public static void main(String[] args) { launch(args); }
	
}
