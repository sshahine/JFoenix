package com.cctintl.c3dfx.demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DDialog;
import com.cctintl.c3dfx.controls.C3DDialog.C3DDialogTransition;

@FXMLController(value = "/resources/fxml/ui/Dialog.fxml" , title = "Material Design Example")
public class DialogController {

	@FXMLViewFlowContext
	private ViewFlowContext context;
	
	@FXML private C3DButton centerButton;
	
	@FXML private C3DButton topButton;
	
	@FXML private C3DButton rightButton;
	
	@FXML private C3DButton bottomButton;
	
	@FXML private C3DButton leftButton;
	
	@FXML private C3DButton acceptButton;
	
	@FXML
	private C3DDialog dialog;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		centerButton.setOnMouseClicked((e)->{
			dialog.setTransitionType(C3DDialogTransition.CENTER);
			dialog.show((Pane) context.getRegisteredObject("ContentPane"));
		});
		
		topButton.setOnMouseClicked((e)->{
			dialog.setTransitionType(C3DDialogTransition.TOP);
			dialog.show((Pane) context.getRegisteredObject("ContentPane"));
		});
		
		rightButton.setOnMouseClicked((e)->{
			dialog.setTransitionType(C3DDialogTransition.RIGHT);
			dialog.show((Pane) context.getRegisteredObject("ContentPane"));
		});
		
		bottomButton.setOnMouseClicked((e)->{
			dialog.setTransitionType(C3DDialogTransition.BOTTOM);
			dialog.show((Pane) context.getRegisteredObject("ContentPane"));
		});
		
		leftButton.setOnMouseClicked((e)->{
			dialog.setTransitionType(C3DDialogTransition.LEFT);
			dialog.show((Pane) context.getRegisteredObject("ContentPane"));
		});
		
		acceptButton.setOnMouseClicked((e)->{
			dialog.close();
		});
	}
	
}
