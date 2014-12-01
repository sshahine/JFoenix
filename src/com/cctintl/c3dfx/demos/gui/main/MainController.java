package com.cctintl.c3dfx.demos.gui.main;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DDrawer;
import com.cctintl.c3dfx.controls.C3DHamburger;
import com.cctintl.c3dfx.demos.gui.sidemenu.SideMenuController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ButtonController;
import com.cctintl.c3dfx.demos.gui.uicomponents.RadioButtonController;
import com.cctintl.c3dfx.demos.gui.uicomponents.SliderController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ListViewController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ProgressBarController;
import com.cctintl.c3dfx.demos.gui.uicomponents.TextFieldController;
import com.cctintl.c3dfx.demos.gui.uicomponents.ToggleButtonController;

import contact.AnimatedFlowContainer;

@FXMLController(value = "/resources/fxml/Main.fxml", title = "Material Design Example")
public class MainController {

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	private StackPane content;

	@FXML
	private StackPane sideContent;
	
	@FXML private C3DHamburger titleBurger;

	@FXML private C3DDrawer drawer;
	
	private FlowHandler flowHandler;
	private FlowHandler sideMenuFlowHandler;

	private int counter = 0 ;
	@PostConstruct
	public void init() throws FlowException, VetoException {

		drawer.setOnDrawingAction((e)->{
			titleBurger.getAnimation().setRate(1);
			titleBurger.getAnimation().setOnFinished((event)->counter = 1);
			titleBurger.getAnimation().play();
		});
		drawer.setOnHidingAction((e)->{
			titleBurger.getAnimation().setRate(-1);
			titleBurger.getAnimation().setOnFinished((event)->counter = 0);
			titleBurger.getAnimation().play();
		});
				
		titleBurger.setOnMouseClicked((e)->{
			if(counter == 0) drawer.draw();
			else if(counter == 1) drawer.hide();
			counter = -1;
		});	
		
		
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
