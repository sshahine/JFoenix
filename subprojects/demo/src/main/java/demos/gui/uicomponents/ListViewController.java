/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

@FXMLController(value = "/resources/fxml/ui/ListView.fxml" , title = "Material Design Example")
public class ListViewController {

	
	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML private JFXListView<?> list1;
	@FXML private JFXListView<?> list2;
	@FXML private JFXListView<?> subList;
	
	@FXML private JFXButton button3D;
	@FXML private JFXButton collapse;
	@FXML private JFXButton expand;
	
	private int counter = 0;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
		
		button3D.setOnMouseClicked((e)->{
			int val = ++counter%2;
			list1.depthProperty().set(val);
			list2.depthProperty().set(val);
		});
		
		expand.setOnMouseClicked((e)->list2.expandedProperty().set(true));
		collapse.setOnMouseClicked((e)->list2.expandedProperty().set(false));
		list1.depthProperty().set(1);
	}
	
	
	
}
