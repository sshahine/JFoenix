package demos.components;

import com.jfoenix.controls.JFXColorPicker;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ColorPickerDemo extends Application {


    @Override
    public void start(Stage stage) {

        FlowPane main = new FlowPane();
        main.setVgap(20);
        main.setHgap(20);

        javafx.scene.control.ColorPicker picker = new javafx.scene.control.ColorPicker(Color.RED);
        picker.getStyleClass().add("button");
        //      picker.getStyleClass().add("split-button");
        main.getChildren().add(picker);

        main.getChildren().add(new JFXColorPicker());

        StackPane pane = new StackPane();
        pane.getChildren().add(main);
        StackPane.setMargin(main, new Insets(100));
        pane.setStyle("-fx-background-color:WHITE");

        final Scene scene = new Scene(pane, 800, 200);
        //      scene.getStylesheets().add(ButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JFX Button Demo");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
