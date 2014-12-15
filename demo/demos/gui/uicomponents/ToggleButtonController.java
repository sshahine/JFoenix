package demos.gui.uicomponents;

import javax.annotation.PostConstruct;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;

@FXMLController(value = "/resources/fxml/ui/ToggleButton.fxml" , title = "Material Design Example")
public class ToggleButtonController {

	@PostConstruct
	public void init() throws FlowException, VetoException {
		
	}
	
	
	
}
