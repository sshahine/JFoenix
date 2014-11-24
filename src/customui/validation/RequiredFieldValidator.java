package customui.validation;

import javafx.scene.control.TextInputControl;
import customui.validation.base.ValidatorBase;
import de.jensd.fx.fontawesome.Icon;

public class RequiredFieldValidator extends ValidatorBase {

	public RequiredFieldValidator() {
		setMessage("Input Required!");
		setAwsomeIcon(new Icon("WARNING"));
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
