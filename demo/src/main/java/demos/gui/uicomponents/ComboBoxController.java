package demos.gui.uicomponents;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.ViewController;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/ui/Combobox.fxml", title = "Material Design Example")
public class ComboBoxController {

    @FXML
    private JFXComboBox<String> jfxComboBox;
    @FXML
    private JFXComboBox<String> jfxEditableComboBox;

    @PostConstruct
    public void init() {

        jfxComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                ValidationFacade.validate(jfxComboBox);
            }
        });

        ChangeListener<? super Boolean> comboBoxFocus = (o, oldVal, newVal) -> {
            if (!newVal) {
                ValidationFacade.validate(jfxEditableComboBox);
            }
        };
        jfxEditableComboBox.focusedProperty().addListener(comboBoxFocus);
        jfxEditableComboBox.getEditor().focusedProperty().addListener(comboBoxFocus);
    }

}
