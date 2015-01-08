package com.cctintl.c3dfx.controls;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class C3DToolbar extends StackPane {
	
	private BorderPane toolBar = new  BorderPane();
	private BorderPane container = new BorderPane();
	private HBox leftBox = new HBox();
	private HBox rightBox = new HBox();

	public C3DToolbar() {
		initialize();
		toolBar.setLeft(leftBox);
		toolBar.setRight(rightBox);
		DepthManager.setDepth(toolBar, 1);
		container.setTop(toolBar);
		this.getChildren().add(container);
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Setters / Getters                                                       *
	 *                                                                         *
	 **************************************************************************/
	
	public void setLeftItems(Node... nodes){
		this.leftBox.getChildren().addAll(nodes);
	}
	
	public ObservableList<Node> getLeftItems(){
		return this.leftBox.getChildren();
	}
	
	public void setRightItems(Node... nodes){
		this.rightBox.getChildren().addAll(nodes);
	}
	
	public ObservableList<Node> getRightItems(){
		return this.rightBox.getChildren();
	}
	
	public void setContent(Node node){
		this.container.setCenter(node);
	}

	public Node getContent(){
		return this.container;
	}
	
	public void setBottom(Node node){
		this.toolBar.setBottom(node);
	}
	
	public Node getBottom(){
		return this.toolBar.getBottom();
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-tool-bar";

	private void initialize() {
		toolBar.getStyleClass().add(DEFAULT_STYLE_CLASS);
	}
	

}
