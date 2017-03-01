package demos.gui.sidemenu;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXListView;

import demos.gui.uicomponents.ButtonController;
import demos.gui.uicomponents.CheckboxController;
import demos.gui.uicomponents.ComboBoxController;
import demos.gui.uicomponents.DialogController;
import demos.gui.uicomponents.IconsController;
import demos.gui.uicomponents.ListViewController;
import demos.gui.uicomponents.MasonryPaneController;
import demos.gui.uicomponents.PickersController;
import demos.gui.uicomponents.PopupController;
import demos.gui.uicomponents.ProgressBarController;
import demos.gui.uicomponents.RadioButtonController;
import demos.gui.uicomponents.SVGLoaderController;
import demos.gui.uicomponents.ScrollPaneController;
import demos.gui.uicomponents.SliderController;
import demos.gui.uicomponents.SpinnerController;
import demos.gui.uicomponents.TextFieldController;
import demos.gui.uicomponents.ToggleButtonController;
import demos.gui.uicomponents.TreeTableViewController;
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

@FXMLController(value = "/resources/fxml/SideMenu.fxml", title = "Material Design Example")
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
	@ActionTrigger("combobox")
	private Label combobox;

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
	@ActionTrigger("treetableview")
	private Label treetableview;
	
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
	@ActionTrigger("spinner")
	private Label spinner;

	@FXML
	@ActionTrigger("textfield")
	private Label textfield;

	@FXML
	@ActionTrigger("togglebutton")
	private Label togglebutton;

	@FXML
	@ActionTrigger("popup")
	private Label popup;
	
	@FXML
	@ActionTrigger("svgLoader")
	private Label svgLoader;
	
	@FXML
	@ActionTrigger("pickers")
	private Label pickers;
	
	@FXML
	@ActionTrigger("masonry")
	private Label masonry;
	
	@FXML
	@ActionTrigger("scrollpane")
	private Label scrollpane;
	
	@FXML
	private JFXListView<Label> sideList;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		FlowHandler contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
		sideList.propagateMouseEventsToParent();
		sideList.getSelectionModel().selectedItemProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null){
				try {
					contentFlowHandler.handle(newVal.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		});
		Flow contentFlow = (Flow) context.getRegisteredObject("ContentFlow");
		bindNodeToController(button, ButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(checkbox, CheckboxController.class, contentFlow, contentFlowHandler);
		bindNodeToController(combobox, ComboBoxController.class, contentFlow, contentFlowHandler);
		bindNodeToController(dialogs, DialogController.class, contentFlow, contentFlowHandler);
		bindNodeToController(icons, IconsController.class, contentFlow, contentFlowHandler);
		bindNodeToController(listview, ListViewController.class, contentFlow, contentFlowHandler);
		bindNodeToController(treetableview, TreeTableViewController.class, contentFlow, contentFlowHandler);
		bindNodeToController(progressbar, ProgressBarController.class, contentFlow, contentFlowHandler);
		bindNodeToController(radiobutton, RadioButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(slider, SliderController.class, contentFlow, contentFlowHandler);
		bindNodeToController(spinner, SpinnerController.class, contentFlow, contentFlowHandler);
		bindNodeToController(textfield, TextFieldController.class, contentFlow, contentFlowHandler);
		bindNodeToController(togglebutton, ToggleButtonController.class, contentFlow, contentFlowHandler);
		bindNodeToController(popup, PopupController.class, contentFlow, contentFlowHandler);
		bindNodeToController(svgLoader, SVGLoaderController.class, contentFlow, contentFlowHandler);
		bindNodeToController(pickers, PickersController.class, contentFlow, contentFlowHandler);
		bindNodeToController(masonry, MasonryPaneController.class, contentFlow, contentFlowHandler);
		bindNodeToController(scrollpane, ScrollPaneController.class, contentFlow, contentFlowHandler);
	}

	private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
		flow.withGlobalLink(node.getId(), controllerClass);
	}

}
