package application.gui;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;

@FXMLController("/application/Main.fxml")
public class MainController {
	@FXML
	private Label resultLabel;

	@FXML
	@ActionTrigger("myAction")
	private Button actionButton;

	private int clickCount = 0;

	@PostConstruct
	public void init() {
		resultLabel.setText("Button was clicked " + clickCount + " times");
	}

	@ActionMethod("myAction")
	public void onAction() {
		clickCount++;
		resultLabel.setText("Button was clicked " + clickCount + " times");
	}
}

