package demos.components;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class ButtonDemo extends Application {

    @Override
    public void start(Stage stage) {
        FlowPane main = new FlowPane();
        main.setVgap(20);
        main.setHgap(20);

        main.getChildren().add(new Button("Java Button"));
        JFXButton jfoenixButton = new JFXButton("JFoenix Button");
        main.getChildren().add(jfoenixButton);

        JFXButton button = new JFXButton("RAISED BUTTON");
        button.getStyleClass().add("button-raised");
        main.getChildren().add(button);

        JFXButton button1 = new JFXButton("DISABLED");
        button1.setDisable(true);
        main.getChildren().add(button1);

        StackPane pane = new StackPane();
        pane.getChildren().add(main);
        StackPane.setMargin(main, new Insets(100));
        pane.setStyle("-fx-background-color:WHITE");

        final Scene scene = new Scene(pane, 800, 200);
        scene.getStylesheets().add(ButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JFX Button Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
