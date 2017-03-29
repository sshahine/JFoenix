package demos.gui.uicomponents;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/ui/Pickers.fxml", title = "Material Design Example")
public class PickersController {

    @FXML
    private StackPane root;
    @FXML
    private JFXDatePicker dateOverlay;
    @FXML
    private JFXTimePicker timeOverlay;

    @PostConstruct
    public void init() {
        dateOverlay.setDialogParent(root);
        timeOverlay.setDialogParent(root);
    }
}
