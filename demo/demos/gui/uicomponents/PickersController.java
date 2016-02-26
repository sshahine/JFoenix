package demos.gui.uicomponents;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXDatePicker;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

@FXMLController(value = "/resources/fxml/ui/Pickers.fxml" , title = "Material Design Example")
public class PickersController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML private StackPane root;
	@FXML private JFXDatePicker dateOverlay;
	@FXML private JFXDatePicker timeOverlay;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
		
		dateOverlay.setDialogParent(root);
		timeOverlay.setDialogParent(root);
	}
}
