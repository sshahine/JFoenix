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

import com.cctintl.c3dfx.demos.gui.sidemenu.SideMenuController;
import com.cctintl.c3dfx.demos.gui.uicomponents.DialogController;

import contact.AnimatedFlowContainer;

@FXMLController(value = "/resources/fxml/Main.fxml" , title = "Material Design Example")
public class MainController {

	@FXMLViewFlowContext
	private ViewFlowContext context;
	
	@FXML
	private StackPane content;
	
	@FXML
	private StackPane sideContent;

	private FlowHandler flowHandler;
	private FlowHandler sideMenuFlowHandler;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		
		context = new ViewFlowContext();
		// set the default controller 
		Flow innerFlow = new Flow(DialogController.class);
		
		flowHandler = innerFlow.createHandler(context);
		context.register("ContentFlowHandler", flowHandler);
		context.register("ContentFlow", innerFlow);
		content.getChildren().add(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
		
		// side controller will add links to the content flow
		Flow sideMenuFlow = new Flow(SideMenuController.class);
		sideMenuFlowHandler = sideMenuFlow.createHandler(context);
		sideContent.getChildren().add(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
		
		
	}
}
