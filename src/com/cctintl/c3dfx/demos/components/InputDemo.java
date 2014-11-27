package com.cctintl.c3dfx.demos.components;

import com.cctintl.c3dfx.controls.C3DTextField;
import com.cctintl.c3dfx.validation.RequiredFieldValidator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class InputDemo extends Application {

	private VBox pane;
	
	@Override
	public void start(Stage stage) throws Exception {

		pane = new VBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");
		
		pane.getChildren().add(new TextField());
		
		C3DTextField field = new C3DTextField();
		field.setPromptText("Type Something");
		pane.getChildren().add(field);
				
		
		C3DTextField disabledField = new C3DTextField();
		disabledField.setPromptText("I'm disabled..");
		disabledField.setDisable(true);
		pane.getChildren().add(disabledField);
		
		
		C3DTextField validationField = new C3DTextField();
		validationField.setPromptText("With Validation..");
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validationField.getValidators().add(validator);
		validationField.focusedProperty().addListener((o,oldVal,newVal)->{
			if(!newVal) validationField.validate();
		});
		pane.getChildren().add(validationField);
		
		
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
