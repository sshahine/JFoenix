package customui.validation;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextInputControl;
import customui.validation.base.ValidatorBase;

public class RequiredFieldValidator extends ValidatorBase {

	public RequiredFieldValidator() {
		message = new SimpleStringProperty("Input Required!");
	}
	
	@Override
	protected void eval() {
		if(srcControl.get() instanceof TextInputControl)
			evalTextInputField();
	}
	
	protected void evalTextInputField(){
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() == null || textField.getText().equals(""))
			hasErrors.set(true);
		else
			hasErrors.set(false);
	}
}
