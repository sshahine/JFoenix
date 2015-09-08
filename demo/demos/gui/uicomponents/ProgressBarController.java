package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXProgressBar;

@FXMLController(value = "/resources/fxml/ui/ProgressBar.fxml", title = "Material Design Example")
public class ProgressBarController {

	@FXML
	private JFXProgressBar progress1;
	@FXML
	private JFXProgressBar progress2;

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

		Timeline task = new Timeline(
							new KeyFrame(
									Duration.ZERO,
									new KeyValue(progress1.progressProperty(), 0),
									new KeyValue(progress2.progressProperty(), 0)),
							new KeyFrame(
									Duration.seconds(2),
									new KeyValue(progress1.progressProperty(), 1),
									new KeyValue(progress2.progressProperty(), 1)));
		task.setCycleCount(Timeline.INDEFINITE);
		task.play();
	}

}
