package demos.gui.main;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;

import demos.datafx.AnimatedFlowContainer;
import demos.gui.sidemenu.SideMenuController;
import demos.gui.uicomponents.ButtonController;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

@FXMLController(value = "/resources/fxml/Main.fxml", title = "Material Design Example")
public class MainController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML private StackPane root;
	
	@FXML private StackPane titleBurgerContainer;
	@FXML private JFXHamburger titleBurger;
	
	@FXML private StackPane optionsBurger;	
	@FXML private JFXRippler optionsRippler;
	
	@FXML private JFXDrawer drawer;
	@FXML private JFXPopup toolbarPopup;
	@FXML private Label exit;

	private FlowHandler flowHandler;
	private FlowHandler sideMenuFlowHandler;

	@PostConstruct
	public void init() throws FlowException, VetoException {

		// init the title hamburger icon
		drawer.setOnDrawerOpening((e) -> {
			titleBurger.getAnimation().setRate(1);
			titleBurger.getAnimation().play();
		});
		drawer.setOnDrawerClosing((e) -> {
			titleBurger.getAnimation().setRate(-1);
			titleBurger.getAnimation().play();
		});
		titleBurgerContainer.setOnMouseClicked((e)->{
			if (drawer.isHidden() || drawer.isHidding()) drawer.open();
			else drawer.close();
		});

		// init Popup 
		toolbarPopup.setPopupContainer(root);		
		toolbarPopup.setSource(optionsRippler);
		root.getChildren().remove(toolbarPopup);
		
		optionsBurger.setOnMouseClicked((e) -> {
			toolbarPopup.show(PopupVPosition.TOP, PopupHPosition.RIGHT, -12, 15);
		});

		// close application
		exit.setOnMouseClicked((e) -> {
			Platform.exit();
		});

		// create the inner flow and content
		context = new ViewFlowContext();
		// set the default controller 
		Flow innerFlow = new Flow(ButtonController.class);

		flowHandler = innerFlow.createHandler(context);
		context.register("ContentFlowHandler", flowHandler);
		context.register("ContentFlow", innerFlow);
		drawer.setContent(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
		context.register("ContentPane", drawer.getContent().get(0));

		// side controller will add links to the content flow
		Flow sideMenuFlow = new Flow(SideMenuController.class);
		sideMenuFlowHandler = sideMenuFlow.createHandler(context);
		drawer.setSidePane(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
	}
}
