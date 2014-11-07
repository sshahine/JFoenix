package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.aquafx_project.AquaFx;

public class ProgressBarDemo extends Application {

    @Override public void start(Stage stage) {
        stage.setTitle("Progress");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(20));
        Scene scene = new Scene(root);

        final ProgressBar p = new ProgressBar(0.6);
        root.getChildren().add(p);

        Button b = new Button("Disable");
        b.setOnAction(new EventHandler<ActionEvent>() {

            @Override public void handle(ActionEvent event) {
                if (p.isDisabled()) {
                    p.setDisable(false);
                } else {
                    p.setDisable(true);
                }
            }
        });
        root.getChildren().add(b);

        Button bI = new Button("Indeterminate");
        bI.setOnAction(new EventHandler<ActionEvent>() {

            @Override public void handle(ActionEvent event) {
                if (p.isIndeterminate()) {
                    p.setProgress(0.6);
                } else {
                    p.setProgress(-1);
                }
            }
        });

        root.getChildren().add(bI);

        AquaFx.style();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}