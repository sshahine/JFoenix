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
package com.jfoenix.controls;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * @author sshahine
 * this is a container for Nodes list, its used mainly with JFXNodesList component
 *
 */

public class TODELETE_JFXNodesListContainer extends StackPane {
	
	public TODELETE_JFXNodesListContainer(Node... nodes){
		super(nodes);
		this.setPickOnBounds(false);
	}
	
	public TODELETE_JFXNodesListContainer() {
		this.setPickOnBounds(false);
	}
	
	
}

//public class C3DFitContainer extends HBox {
//	
//	private VBox vContainer = new VBox();
//	private StackPane stackContainer = new StackPane();
//	
//	public C3DFitContainer() {
//		this.setPickOnBounds(false);
//		vContainer.setPickOnBounds(false);
//		stackContainer.setPickOnBounds(false);
//		vContainer.getChildren().add(stackContainer);			
//		this.getChildren().add(vContainer);
//		this.maxWidthProperty().bind(vContainer.widthProperty());
//		vContainer.maxHeightProperty().bind(stackContainer.heightProperty());
//		this.maxHeightProperty().bind(stackContainer.heightProperty());
//	}
//}
