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

package com.cctintl.c3dfx.controls;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class C3DDialogLayout extends StackPane {

	private VBox layout = new VBox();
	private StackPane heading = new StackPane();
	private StackPane body = new StackPane();
	private FlowPane actions = new FlowPane();
	
	public C3DDialogLayout() {
		super();
		initialize();
		layout.getChildren().add(heading);
		heading.getStyleClass().add("c3d-layout-heading");
		heading.getStyleClass().add("title");
		layout.getChildren().add(body);
		body.getStyleClass().add("c3d-layout-body");
		body.prefHeightProperty().bind(this.prefHeightProperty());
		body.prefWidthProperty().bind(this.prefWidthProperty());
		layout.getChildren().add(actions);
		actions.getStyleClass().add("c3d-layout-actions");
		this.getChildren().add(layout);
	}

	/***************************************************************************
	 *                                                                         *
	 * Setters / Getters                                                       *
	 *                                                                         *
	 **************************************************************************/
	
	public ObservableList<Node> getHeading() {
		return heading.getChildren();
	}

	public void setHeading(Node... titleContent) {
		this.heading.getChildren().addAll(titleContent);
	}

	public ObservableList<Node> getBody() {
		return body.getChildren();
	}

	public void setBody(Node... body) {
		this.body.getChildren().addAll(body);
	}

	public ObservableList<Node> getActions() {
		return actions.getChildren();
	}

	public void setActions(Node... actions) {
		this.actions.getChildren().addAll(actions);
	}
	
	public void setActions(List<? extends Node> actions) {
		this.actions.getChildren().addAll(actions);
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-dialog-layout";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.setPadding(new Insets(24,24,16,24));
		this.setStyle("-fx-text-fill: rgba(0, 0, 0, 0.87);");
		heading.setStyle("-fx-font-weight: BOLD;-fx-alignment: center-left;");
		heading.setPadding(new Insets(5,0,5,0));
		body.setStyle("-fx-pref-width: 400px;-fx-wrap-text: true;");
		actions.setStyle("-fx-alignment: center-right ;");
		actions.setPadding(new Insets(10,0,0,0));
	}
	
}
