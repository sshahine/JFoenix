package demos.gui.uicomponents;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/ui/TextField.fxml", title = "Material Design Example")
public class TextFieldController {

    @FXML
    private JFXTextField validatedText;
    @FXML
    private JFXPasswordField validatedPassowrd;
    @FXML
    private JFXTextArea jfxTextArea;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        validatedText.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validatedText.validate();
            }
        });
        validatedPassowrd.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validatedPassowrd.validate();
            }
        });
        jfxTextArea.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                jfxTextArea.validate();
            }
        });
    }

}
