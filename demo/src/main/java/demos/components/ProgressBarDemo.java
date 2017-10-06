package demos.components;

import com.jfoenix.controls.JFXProgressBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgressBarDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        final VBox pane = new VBox();
        pane.setSpacing(30);
        pane.setStyle("-fx-background-color:WHITE");

        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(500);

        ProgressBar cssBar = new ProgressBar();
        cssBar.setPrefWidth(500);
        cssBar.setProgress(-1.0f);

        JFXProgressBar jfxBar = new JFXProgressBar();
        jfxBar.setPrefWidth(500);

        JFXProgressBar jfxBarInf = new JFXProgressBar();
        jfxBarInf.setPrefWidth(500);
        jfxBarInf.setProgress(-1.0f);

        Timeline timeline = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(bar.progressProperty(), 0),
                new KeyValue(jfxBar.secondaryProgressProperty(), 0),
                new KeyValue(jfxBar.progressProperty(), 0)),
            new KeyFrame(
                Duration.seconds(1),
                new KeyValue(jfxBar.secondaryProgressProperty(), 1)),
            new KeyFrame(
                Duration.seconds(2),
                new KeyValue(bar.progressProperty(), 1),
                new KeyValue(jfxBar.progressProperty(), 1)));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        pane.getChildren().addAll(bar, jfxBar, cssBar, jfxBarInf);

        StackPane main = new StackPane();
        main.getChildren().add(pane);
        main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane.setMargin(pane, new Insets(20, 0, 0, 20));

        final Scene scene = new Scene(main, 600, 200, Color.WHITE);
        scene.getStylesheets().add(ProgressBarDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JFX ProgressBar Demo ");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
