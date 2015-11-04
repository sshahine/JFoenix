package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;

@FXMLController(value = "/resources/fxml/ui/Popup.fxml" , title = "Material Design Example")
public class PopupController {

	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML private StackPane root;

	@FXML private JFXRippler rippler1;
	@FXML private JFXRippler rippler2;
	@FXML private JFXRippler rippler3;
	@FXML private JFXRippler rippler4;

	@FXML private JFXHamburger burger1;
	@FXML private JFXHamburger burger2;
	@FXML private JFXHamburger burger3;
	@FXML private JFXHamburger burger4;

	@FXML private JFXPopup popup;

	@PostConstruct
	public void init() throws FlowException, VetoException {

		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

		popup.setPopupContainer(root);

		burger1.setOnMouseClicked((e)->{
			popup.setSource(rippler1);
			popup.show(PopupVPosition.TOP, PopupHPosition.LEFT);
		});

		burger2.setOnMouseClicked((e)->{
			popup.setSource(rippler2);
			popup.show(PopupVPosition.TOP, PopupHPosition.RIGHT);
		});

		burger3.setOnMouseClicked((e)->{
			popup.setSource(rippler3);
			popup.show(PopupVPosition.BOTTOM, PopupHPosition.LEFT);
		});

		burger4.setOnMouseClicked((e)->{
			popup.setSource(rippler4);
			popup.show(PopupVPosition.BOTTOM, PopupHPosition.RIGHT);
		});
	}
}
