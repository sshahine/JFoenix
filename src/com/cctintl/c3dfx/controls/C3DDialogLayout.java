package com.cctintl.c3dfx.controls;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class C3DDialogLayout extends StackPane {

	private VBox layout = new VBox();
	private StackPane heading = new StackPane();
	private StackPane body = new StackPane();
	private StackPane actions = new StackPane();
	
	public C3DDialogLayout() {
		super();
		initialize();
		layout.getChildren().add(heading);
		heading.getStyleClass().add("c3d-layout-title");
		layout.getChildren().add(body);
		body.getStyleClass().add("c3d-layout-body");
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
		this.heading.getChildren().addAll(heading);
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
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-dialog-layout";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
	}
	
}
