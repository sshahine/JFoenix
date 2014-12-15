package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DTextField;

@FXMLController(value = "/resources/fxml/ui/TextField.fxml", title = "Material Design Example")
public class TextFieldController {

	@FXML
	private C3DTextField validatedText;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		validatedText.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) validatedText.validate();
		});
	}

}
