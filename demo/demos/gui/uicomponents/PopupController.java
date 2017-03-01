package demos.gui.uicomponents;

import java.io.IOException;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;

import demos.gui.main.MainController.InputController;

import com.jfoenix.controls.JFXRippler;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

@FXMLController(value = "/resources/fxml/ui/Popup.fxml" , title = "Material Design Example")
public class PopupController {

	@FXML private StackPane root;

	@FXML private JFXRippler rippler1;
	@FXML private JFXRippler rippler2;
	@FXML private JFXRippler rippler3;
	@FXML private JFXRippler rippler4;

	@FXML private JFXHamburger burger1;
	@FXML private JFXHamburger burger2;
	@FXML private JFXHamburger burger3;
	@FXML private JFXHamburger burger4;
	@FXML private JFXHamburger burger5;

	private JFXPopup popup;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		try {
			popup = new JFXPopup(FXMLLoader.load(getClass().getResource("/resources/fxml/ui/popup/DemoPopup.fxml")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		burger1.setOnMouseClicked((e)->{
			popup.show(rippler1, PopupVPosition.TOP, PopupHPosition.LEFT);
		});

		burger2.setOnMouseClicked((e)->{
			popup.show(rippler2, PopupVPosition.TOP, PopupHPosition.RIGHT);
		});

		burger3.setOnMouseClicked((e)->{
			popup.show(rippler3, PopupVPosition.BOTTOM, PopupHPosition.LEFT);
		});

		burger4.setOnMouseClicked((e)->{
			popup.show(rippler4, PopupVPosition.BOTTOM, PopupHPosition.RIGHT);
		});
	}
}
