package demos.gui.sidemenu;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javax.annotation.PostConstruct;
import demos.gui.uicomponents.ButtonController;
import demos.gui.uicomponents.CheckboxController;
import demos.gui.uicomponents.DialogController;
import demos.gui.uicomponents.IconsController;
import demos.gui.uicomponents.ListViewController;
import demos.gui.uicomponents.ProgressBarController;
import demos.gui.uicomponents.RadioButtonController;
import demos.gui.uicomponents.SliderController;
import demos.gui.uicomponents.TextFieldController;
import demos.gui.uicomponents.ToggleButtonController;

@FXMLController(value = "/resources/fxml/SideMenu.fxml" , title = "Material Design Example")
public class SideMenuController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	@ActionTrigger("buttons")
	private Label button;

	@FXML
	@ActionTrigger("checkbox")
	private Label checkbox;
	
	@FXML
	@ActionTrigger("dialogs")
	private Label dialogs;
	
	@FXML
	@ActionTrigger("icons")
	private Label icons;
	
	@FXML
	@ActionTrigger("listview")
	private Label listview;
	
	@FXML
	@ActionTrigger("progressbar")
	private Label progressbar;
	
	@FXML
	@ActionTrigger("radiobutton")
	private Label radiobutton;
	
	@FXML
	@ActionTrigger("slider")
	private Label slider;
	
	@FXML
	@ActionTrigger("textfield")
	private Label textfield;
	
	@FXML
	@ActionTrigger("togglebutton")
	private Label togglebutton;
	

	@PostConstruct
	public void init() throws FlowException, VetoException {
		FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
		Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
		bindNodeToController(button, ButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(checkbox, CheckboxController.class, contentFlow, contentFlowHandler);
		bindNodeToController(dialogs, DialogController.class, contentFlow, contentFlowHandler);
		bindNodeToController(icons, IconsController.class, contentFlow, contentFlowHandler);
		bindNodeToController(listview, ListViewController.class, contentFlow, contentFlowHandler);
		bindNodeToController(progressbar, ProgressBarController.class, contentFlow, contentFlowHandler);
		bindNodeToController(radiobutton, RadioButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(slider, SliderController.class, contentFlow, contentFlowHandler);
		bindNodeToController(textfield, TextFieldController.class, contentFlow, contentFlowHandler);
		bindNodeToController(togglebutton, ToggleButtonController.class, contentFlow, contentFlowHandler);
	}
	
	private void bindNodeToController(Node node,Class<?> controllerClass, Flow flow, FlowHandler flowHandler){
		flow.withGlobalLink(node.getId(), controllerClass);
		node.setOnMouseClicked((e)->{
			try {
				flowHandler.handle(node.getId());
			} catch (Exception e1) {
				e1.printStackTrace();
			}}
		);
	}
	
}
