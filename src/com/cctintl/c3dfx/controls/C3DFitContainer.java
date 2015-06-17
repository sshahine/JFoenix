package com.cctintl.c3dfx.controls;

import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author sshahine
 * this is a container that force children to be used in the min size
 *
 */

public class C3DFitContainer extends HBox {
	
	private VBox vContainer = new VBox();
	private StackPane stackContainer = new StackPane();
	
	public C3DFitContainer() {
		this.setPickOnBounds(false);
		vContainer.setPickOnBounds(false);
		stackContainer.setPickOnBounds(false);
		vContainer.getChildren().add(stackContainer);			
		this.getChildren().add(vContainer);
		this.maxWidthProperty().bind(vContainer.widthProperty());
		vContainer.maxHeightProperty().bind(stackContainer.heightProperty());
		this.maxHeightProperty().bind(stackContainer.heightProperty());
	}
	
	
}
