package demos.gui.main;

import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.io.IOException;

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

	private JFXPopup toolbarPopup;
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

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/ui/popup/MainPopup.fxml"));
			loader.setController(new InputController());
			toolbarPopup = new JFXPopup(loader.load());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		optionsBurger.setOnMouseClicked((e) -> {
			toolbarPopup.show(optionsBurger, PopupVPosition.TOP, PopupHPosition.RIGHT, -12, 15);
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

	public class InputController{
		@FXML private JFXListView<?> toolbarPopupList;
		// close application
		@FXML private void submit() {
			if(toolbarPopupList.getSelectionModel().getSelectedIndex() == 1) Platform.exit();
		}
	}
}
