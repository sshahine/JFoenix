package com.cctintl.c3dfx.demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DHamburger;

@FXMLController(value = "/resources/fxml/ui/Icons.fxml" , title = "Material Design Example")
public class IconsController {

	@FXML private C3DHamburger burger1;
	@FXML private C3DHamburger burger2;
	@FXML private C3DHamburger burger3;
	@FXML private C3DHamburger burger4;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		bindAction(burger1);
		bindAction(burger2);
		bindAction(burger3);
		bindAction(burger4);
	}
	
	private void bindAction(C3DHamburger burger){
		burger.setOnMouseClicked((e)->{
			burger.getAnimation().setRate(burger.getAnimation().getRate()*-1);
			burger.getAnimation().play();
		});
	}
	
}
