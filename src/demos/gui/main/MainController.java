package demos.gui.main;

import javax.annotation.PostConstruct;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;

@FXMLController(value = "/resources/fxml/Main.fxml" , title = "Material Design Example")
public class MainController {

	@PostConstruct
	public void init() throws FlowException, VetoException {
		
	}
}
