package demos.components;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.effects.JFXDepthManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class RipplerDemo extends Application {

    private static final String FX_BACKGROUND_COLOR_WHITE = "-fx-background-color:WHITE;";
    private static int counter = 0;
    private static int step = 1;

    @Override
    public void start(Stage stage) {

        //TODO drop shadow changes the width and height thus need to be considered
        FlowPane main = new FlowPane();
        main.setVgap(20);
        main.setHgap(20);

        Label label = new Label("Click Me");
        label.setStyle(FX_BACKGROUND_COLOR_WHITE);
        label.setPadding(new Insets(20));
        JFXRippler rippler = new JFXRippler(label);
        rippler.setEnabled(false);
        main.getChildren().add(rippler);

        label.setOnMousePressed((e) -> {
            if (counter == 5) {
                step = -1;
            } else if (counter == 0) {
                step = 1;
            }
            JFXDepthManager.setDepth(label, counter += step % JFXDepthManager.getLevels());
        });

        Label l1 = new Label("TEST");
        l1.setStyle(FX_BACKGROUND_COLOR_WHITE);
        l1.setPadding(new Insets(20));
        JFXRippler rippler1 = new JFXRippler(l1);
        main.getChildren().add(rippler1);
        JFXDepthManager.setDepth(rippler1, 1);

        Label l2 = new Label("TEST1");
        l2.setStyle(FX_BACKGROUND_COLOR_WHITE);
        l2.setPadding(new Insets(20));
        JFXRippler rippler2 = new JFXRippler(l2);
        main.getChildren().add(rippler2);
        JFXDepthManager.setDepth(rippler2, 2);


        Label l3 = new Label("TEST2");
        l3.setStyle(FX_BACKGROUND_COLOR_WHITE);
        l3.setPadding(new Insets(20));
        JFXRippler rippler3 = new JFXRippler(l3);
        main.getChildren().add(rippler3);
        JFXDepthManager.setDepth(rippler3, 3);

        Label l4 = new Label("TEST3");
        l4.setStyle(FX_BACKGROUND_COLOR_WHITE);
        l4.setPadding(new Insets(20));
        JFXRippler rippler4 = new JFXRippler(l4);
        main.getChildren().add(rippler4);
        JFXDepthManager.setDepth(rippler4, 4);

        Label l5 = new Label("TEST4");
        l5.setStyle(FX_BACKGROUND_COLOR_WHITE);
        l5.setPadding(new Insets(20));
        JFXRippler rippler5 = new JFXRippler(l5);
        main.getChildren().add(rippler5);
        JFXDepthManager.setDepth(rippler5, 5);

        StackPane pane = new StackPane();
        pane.getChildren().add(main);
        StackPane.setMargin(main, new Insets(100));
        pane.setStyle("-fx-background-color:WHITE");

        final Scene scene = new Scene(pane, 600, 400);

        stage.setTitle("JavaFX Ripple effect and shadows ");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
