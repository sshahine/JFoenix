package demos.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.cctintl.jfx.controls.JFXProgressBar;

public class ProgressBarDemo extends Application {

	private VBox pane;

	@Override
	public void start(Stage stage) throws Exception {

		pane = new VBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");

		ProgressBar bar = new ProgressBar();
		bar.setPrefWidth(500);

		ProgressBar cssBar = new ProgressBar();
		cssBar.setPrefWidth(500);
		cssBar.setProgress(-1.0f);

		JFXProgressBar c3dBar = new JFXProgressBar();
		c3dBar.setPrefWidth(500);

		JFXProgressBar c3dBarInf = new JFXProgressBar();
		c3dBarInf.setPrefWidth(500);
		c3dBarInf.setProgress(-1.0f);

		Timeline timeline = new Timeline(
									new KeyFrame(
											Duration.ZERO,
											new KeyValue(bar.progressProperty(), 0),
											new KeyValue(c3dBar.progressProperty(), 0)),
									new KeyFrame(
											Duration.seconds(2),
											new KeyValue(bar.progressProperty(), 1),
											new KeyValue(c3dBar.progressProperty(), 1)));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		pane.getChildren().addAll(bar, c3dBar, cssBar, c3dBarInf);

		StackPane main = new StackPane();
		main.getChildren().add(pane);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20, 0, 0, 20));

		final Scene scene = new Scene(main, 600, 400, Color.WHITE);
		stage.setTitle("JavaFX TextField ;) ");
		scene.getStylesheets().add(SliderDemo.class.getResource("/resources/css/c3dobjects.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
