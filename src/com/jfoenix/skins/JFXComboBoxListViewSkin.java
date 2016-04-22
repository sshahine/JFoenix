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
package com.jfoenix.skins;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.converters.base.NodeConverter;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * @author Shadi Shaheen
 *
 */

public class JFXComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/    

	private StackPane customPane;

	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/   

	public JFXComboBoxListViewSkin(final JFXComboBox<T> comboBox) {

		super(comboBox);
		// create my custom pane 
		customPane = new StackPane();
		getSkinnable().backgroundProperty().addListener((o,oldVal,newVal)-> customPane.setBackground(newVal));
		customPane.getStyleClass().add("combo-box-button-container");
		customPane.backgroundProperty().bindBidirectional(getSkinnable().backgroundProperty());
		customPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		customPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT,  BorderStrokeStyle.NONE,BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, null, new BorderWidths(0, 0, 1, 0), null)));
		getChildren().add(0,customPane);
		arrowButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

		if(comboBox.isEditable()){
			customPane.getChildren().add(comboBox.getJFXEditor());
			comboBox.getJFXEditor().paddingProperty().addListener((o,oldVal,newVal)-> comboBox.getJFXEditor().setTranslateY(newVal.getBottom()));
			comboBox.getJFXEditor().setStyle("-fx-focus-color:TRANSPARENT;-fx-unfocus-color:TRANSPARENT;-fx-background-color:TRANSPARENT;");
		}

		// create custom button cell to display a custom node upon selection
		comboBox.setButtonCell(new ListCell<T>() {
			@Override public void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				updateDisplayText(this, item, empty);
			}
		});
		
		ListView<T> view = comboBox.getButtonCell().getListView();
		view.widthProperty().addListener(new WeakChangeListener<>((o,oldVal,newVal)-> view.setPrefWidth(newVal.doubleValue())));
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/  

	@Override protected TextField getEditor() {
		// return null when called from parent listeners
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		boolean parentListenerCall = caller.getMethodName().contains("lambda") && caller.getClassName().equals(this.getClass().getSuperclass().getSuperclass().getName());
		if(parentListenerCall) return null;		
		return getSkinnable().isEditable() ? ((JFXComboBox<T>)getSkinnable()).getJFXEditor() : null;
	}


	@Override protected void layoutChildren(final double x, final double y,
			final double w, final double h) {
		customPane.resizeRelocate(x, y, w , h);
		super.layoutChildren(x,y,w,h);		 
	}

	private boolean updateDisplayText(ListCell<T> cell, T item, boolean empty) {
		if (empty) {
			// create empty cell
			if (cell == null) return true;
			cell.setGraphic(null);
			cell.setText(null);
			return true;
		} else if (item instanceof Node) {
			Node currentNode = cell.getGraphic();
			Node newNode = (Node) item;
			/*
			 *  create a node from the selected node of the listview
			 *  using JFXComboBox {@link #nodeConverterProperty() NodeConverter}) 
			 */
			NodeConverter<T> nc = ((JFXComboBox<T>)getSkinnable()).getNodeConverter();
			Node node = nc == null? null : nc.toNode(item);
			if (currentNode == null || ! currentNode.equals(newNode)) {
				cell.setText(null);
				cell.setGraphic(node==null? newNode : node);
			}
			return newNode == null;
		} else {
			// run item through StringConverter if it isn't null
			StringConverter<T> c = ((JFXComboBox<T>)getSkinnable()).getConverter();
			String s = item == null ? getSkinnable().getPromptText() : (c == null ? item.toString() : c.toString(item));
			cell.setText(s);
			cell.setGraphic(null);
			return s == null || s.isEmpty();
		}
	}
}
