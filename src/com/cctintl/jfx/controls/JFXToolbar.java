/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.cctintl.jfx.controls;


import com.cctintl.jfx.effects.JFXDepthManager;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class JFXToolbar extends BorderPane {
	
	private HBox leftBox = new HBox();
	private HBox rightBox = new HBox();

	public JFXToolbar() {
		initialize();
		this.setLeft(leftBox);
		leftBox.getStyleClass().add("tool-bar-left-box");
		this.setRight(rightBox);
		rightBox.getStyleClass().add("tool-bar-right-box");
		JFXDepthManager.setDepth(this, 1);
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
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "jfx-tool-bar";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
	}
	

}
