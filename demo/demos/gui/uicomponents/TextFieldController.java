package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

@FXMLController(value = "/resources/fxml/ui/TextField.fxml", title = "Material Design Example")
public class TextFieldController {

	@FXML
	private JFXTextField validatedText;
	
	@FXML private JFXPasswordField validatedPassowrd;
	@FXML private JFXTextArea jfxTextArea;
	

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
		
		validatedText.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) validatedText.validate();
		});
		validatedPassowrd.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) validatedPassowrd.validate();
		});
		jfxTextArea.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) jfxTextArea.validate();
		});
	}

}
