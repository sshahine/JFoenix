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
