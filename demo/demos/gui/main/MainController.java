package demos.gui.main;

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

import javax.annotation.PostConstruct;

import com.cctintl.jfx.controls.JFXDrawer;
import com.cctintl.jfx.controls.JFXHamburger;
import com.cctintl.jfx.controls.JFXPopup;
import com.cctintl.jfx.controls.JFXPopup.PopupHPosition;
import com.cctintl.jfx.controls.JFXPopup.PopupVPosition;
import com.cctintl.jfx.controls.JFXRippler;

import demos.datafx.AnimatedFlowContainer;
import demos.gui.sidemenu.SideMenuController;
import demos.gui.uicomponents.ButtonController;

@FXMLController(value = "/resources/fxml/Main.fxml", title = "Material Design Example")
public class MainController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML private StackPane root;
	@FXML private StackPane content;
	@FXML private StackPane sideContent;
	
	@FXML private StackPane titleBurgerContainer;
	@FXML private JFXHamburger titleBurger;
	
	@FXML private StackPane optionsBurger;	
	@FXML private JFXRippler optionsRippler;
	
	@FXML private JFXDrawer drawer;
	@FXML private JFXPopup toolbarPopup;
	@FXML private Label exit;

	private FlowHandler flowHandler;
	private FlowHandler sideMenuFlowHandler;

	private int counter = 0;

	@PostConstruct
	public void init() throws FlowException, VetoException {

		// init the title hamburger icon
		drawer.setOnDrawingAction((e) -> {
			titleBurger.getAnimation().setRate(1);
			titleBurger.getAnimation().setOnFinished((event) -> counter = 1);
			titleBurger.getAnimation().play();
		});
		drawer.setOnHidingAction((e) -> {
			titleBurger.getAnimation().setRate(-1);
			titleBurger.getAnimation().setOnFinished((event) -> counter = 0);
			titleBurger.getAnimation().play();
		});
		titleBurgerContainer.setOnMouseClicked((e)->{
			if (counter == 0)
				drawer.draw();
			else if (counter == 1)
				drawer.hide();
			counter = -1;
		});

		// init Popup 
		toolbarPopup.setPopupContainer(root);
		toolbarPopup.setSource(optionsRippler);
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
		context.register("ContentPane", content);
		content.getChildren().add(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));

		// side controller will add links to the content flow
		Flow sideMenuFlow = new Flow(SideMenuController.class);
		sideMenuFlowHandler = sideMenuFlow.createHandler(context);
		sideContent.getChildren().add(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));

	}
}
