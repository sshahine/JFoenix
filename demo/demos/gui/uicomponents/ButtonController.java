package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;

import javax.annotation.PostConstruct;

@FXMLController(value = "/resources/fxml/ui/Button.fxml" , title = "Material Design Example")
public class ButtonController {

	@PostConstruct
	public void init() throws FlowException, VetoException {
		
	}
	
}
