package demos.gui.uicomponents;

import com.jfoenix.controls.JFXProgressBar;
import io.datafx.controller.ViewController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/ui/ProgressBar.fxml", title = "Material Design Example")
public class ProgressBarController {

    @FXML
    private JFXProgressBar progress1;
    @FXML
    private JFXProgressBar progress2;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        Timeline task = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(progress1.progressProperty(), 0),
                new KeyValue(progress2.progressProperty(), 0),
                new KeyValue(progress2.secondaryProgressProperty(), 0.5)),
            new KeyFrame(
                Duration.seconds(1),
                new KeyValue(progress2.secondaryProgressProperty(), 1)),
            new KeyFrame(
                Duration.seconds(2),
                new KeyValue(progress1.progressProperty(), 1),
                new KeyValue(progress2.progressProperty(), 1)));
        task.setCycleCount(Timeline.INDEFINITE);
        task.play();
    }

}
