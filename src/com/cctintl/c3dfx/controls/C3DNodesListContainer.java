package com.cctintl.c3dfx.controls;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * @author sshahine
 * this is a container for Nodes list, its used mainly with C3DNodesList component
 *
 */

public class C3DNodesListContainer extends StackPane {
	
	public C3DNodesListContainer(Node... nodes){
		super(nodes);
		this.setPickOnBounds(false);
	}
	
	public C3DNodesListContainer() {
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
//	
//	
//}
