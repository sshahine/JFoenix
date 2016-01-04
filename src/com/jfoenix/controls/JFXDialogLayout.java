/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author Shadi Shaheen
 *
 */
public class JFXDialogLayout extends StackPane {

	private VBox layout = new VBox();
	private StackPane heading = new StackPane();
	private StackPane body = new StackPane();
	private FlowPane actions = new FlowPane();
	
	public JFXDialogLayout() {
		super();
		initialize();
		layout.getChildren().add(heading);
		heading.getStyleClass().add("jfx-layout-heading");
		heading.getStyleClass().add("title");
		layout.getChildren().add(body);
		body.getStyleClass().add("jfx-layout-body");
		body.prefHeightProperty().bind(this.prefHeightProperty());
		body.prefWidthProperty().bind(this.prefWidthProperty());
		layout.getChildren().add(actions);
		actions.getStyleClass().add("jfx-layout-actions");
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

	private static final String DEFAULT_STYLE_CLASS = "jfx-dialog-layout";

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
