/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validaciondecampo;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;
/**
 *
 * @author Victor Espino
 * @version 1.0
 * @since 2019-08-10
 */
public class StringLengthValidator extends ValidatorBase{

    int StringLengh;

    /**
     * Basic constructor with Default message this way:
     * "Max length is " + StringLengh +" character(s) "
     * @param StringLengh 
     * Length of the string in the input field to validate.
     */
    public StringLengthValidator(int StringLengh) {
        super("Max length is " + StringLengh +" character(s) ");
        this.StringLengh = StringLengh;
    }
    
    
    /**
     * The displayed message shown will be concatenated by the message with StringLength
     * this way "message" + StringLength.
     * 
     * @param StringLength
     * Length of the string in the input field to validate.
     * @param message 
     * Message to show.
     */
    public StringLengthValidator(int StringLength,String message) {
    
        this.StringLengh = StringLength;
        setMessage(message + StringLength);                        
    }
    /**
     * The displayed message will be personalized, 
     * but still need to indicate the StringLength to validate.
     * @param StringLength
     * Length of the string in the input field to validate.
     * @param message 
     * Message to show.
     */
    public StringLengthValidator(String message,int StringLength){        
        super(message);
        this.StringLengh = StringLength;
        
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void eval() {

        if(srcControl.get() instanceof TextInputControl){
            evalTextInputField();
        }

    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
            String text = textField.getText();
            try {
                hasErrors.set(false);
                if (!text.isEmpty()) {
                    if(text.length()>StringLengh){
                        throw new Exception("String length exceded.");
                    }
                }
            } catch (Exception e) {
                hasErrors.set(true);
            }

    }
    
}
