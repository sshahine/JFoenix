package demos.components;

import com.jfoenix.controls.JFXRadioButton;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RadioButtonDemo extends Application {
    @Override
    public void start(Stage primaryStage) {
        final ToggleGroup group = new ToggleGroup();

        JFXRadioButton javaRadio = new JFXRadioButton("JavaFX");
        javaRadio.setPadding(new Insets(10));
        javaRadio.setToggleGroup(group);

        JFXRadioButton jfxRadio = new JFXRadioButton("JFoenix");
        jfxRadio.setPadding(new Insets(10));
        jfxRadio.setToggleGroup(group);


        VBox vbox = new VBox();
        vbox.getChildren().add(javaRadio);
        vbox.getChildren().add(jfxRadio);
        vbox.setSpacing(10);
        
        HBox hbox = new HBox();
        hbox.getChildren().add(vbox);
        hbox.setSpacing(50);
        hbox.setPadding(new Insets(40, 10, 10, 120));

        Scene scene = new Scene(hbox);
        primaryStage.setScene(scene);
        primaryStage.setWidth(500);
        primaryStage.setHeight(400);
        primaryStage.setTitle("JFX RadioButton Demo ");
        scene.getStylesheets()
            .add(RadioButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
