package com.aquafx_project.nativestuff;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class NsIconDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane pane = new FlowPane();
        for (NsImageIcon nsImageIcon : NsImageIcon.values()) {
            pane.getChildren().add(
                    new ImageView(NsImageIconLoader.load(nsImageIcon)));
        }

        Scene myScene = new Scene(pane);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
