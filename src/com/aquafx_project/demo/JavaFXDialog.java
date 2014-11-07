package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class JavaFXDialog extends Application {

    @Override public void start(Stage stage) throws Exception {
        stage.setTitle("Title");
        
        HBox box = new HBox();

        Button button = new Button("Button");
        box.getChildren().add(button);
        box.setPadding(new Insets(50));

        Scene scene = new Scene(box);
        stage.setScene(scene);
        
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}