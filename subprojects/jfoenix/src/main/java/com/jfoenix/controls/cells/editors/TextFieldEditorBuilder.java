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
package com.jfoenix.controls.cells.editors;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.cells.editors.base.EditorNodeBuilder;

public class TextFieldEditorBuilder implements EditorNodeBuilder<String> {

	private JFXTextField textField;

	@Override
	public void startEdit() {
		Platform.runLater(()->{
			textField.selectAll();
			textField.requestFocus();
		});
	}

	@Override
	public void cancelEdit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateItem(String item, boolean empty) {
		Platform.runLater(()->{
			textField.selectAll();
			textField.requestFocus();
		});
	}

	@Override
	public Region createNode(String value, DoubleBinding minWidthBinding, EventHandler<KeyEvent> keyEventsHandler, ChangeListener<Boolean> focusChangeListener) {
		StackPane pane = new StackPane();
		pane.setStyle("-fx-padding:-10 0 -10 0");
		textField = new JFXTextField(value);
		textField.setStyle("-fx-background-color:TRANSPARENT;");
		textField.minWidthProperty().bind(minWidthBinding);
		textField.setOnKeyPressed(keyEventsHandler);
		textField.focusedProperty().addListener(focusChangeListener);
		pane.getChildren().add(textField);
		return pane;
	}

	@Override
	public void setValue(String value) {
		textField.setText(value);
	}

	@Override
	public String getValue() {
		return textField.getText();
	}

	@Override
	public void validateValue() throws Exception {
		
	}


}
