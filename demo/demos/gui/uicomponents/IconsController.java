package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXHamburger;

@FXMLController(value = "/resources/fxml/ui/Icons.fxml" , title = "Material Design Example")
public class IconsController {

	@FXML private JFXHamburger burger1;
	@FXML private JFXHamburger burger2;
	@FXML private JFXHamburger burger3;
	@FXML private JFXHamburger burger4;

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
		bindAction(burger1);
		bindAction(burger2);
		bindAction(burger3);
		bindAction(burger4);
	}

	private void bindAction(JFXHamburger burger){
		burger.setOnMouseClicked((e)->{
			burger.getAnimation().setRate(burger.getAnimation().getRate()*-1);
			burger.getAnimation().play();
		});
	}

}
