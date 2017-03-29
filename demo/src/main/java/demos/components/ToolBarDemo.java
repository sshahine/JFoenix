package demos.components;

import com.jfoenix.controls.JFXToolbar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ToolBarDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        JFXToolbar jfxToolbar = new JFXToolbar();
        jfxToolbar.setLeftItems(new Label("Left"));
        jfxToolbar.setRightItems(new Label("Right"));

        StackPane main = new StackPane();
        main.getChildren().add(jfxToolbar);

        Scene scene = new Scene(main, 600, 400);
        scene.getStylesheets().add(ToolBarDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
