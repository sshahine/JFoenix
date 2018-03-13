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

package com.jfoenix.controls.cells.editors;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.cells.editors.base.EditorNodeBuilder;
import com.jfoenix.validation.DoubleValidator;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * <h1>Text field cell editor (numbers only) </h1>
 * this an example of the cell editor, it creates a JFXTextField node to
 * allow the user to edit the cell value
 * <p>
 *
 * @author eralpsahin
 * @version 1.0
 * @since 2017-01-27
 */
public class DoubleTextFieldEditorBuilder implements EditorNodeBuilder<Double> {

    private JFXTextField textField;

    @Override
    public void startEdit() {
        Platform.runLater(() -> {
            textField.selectAll();
            textField.requestFocus();
        });
    }

    @Override
    public void cancelEdit() {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateItem(Double item, boolean empty) {
        Platform.runLater(() -> {
            textField.selectAll();
            textField.requestFocus();
        });
    }

    @Override
    public Region createNode(Double value, EventHandler<KeyEvent> keyEventsHandler, ChangeListener<Boolean> focusChangeListener) {
        textField = new JFXTextField(value + "");
        textField.setOnKeyPressed(keyEventsHandler);
        textField.focusedProperty().addListener(focusChangeListener);
        DoubleValidator validator = new DoubleValidator();
        validator.setMessage("Value must be a rational number");
        textField.getValidators().add(validator);
        return textField;
    }

    @Override
    public void setValue(Double value) {
        textField.setText(value + "");
    }

    @Override
    public Double getValue() {
        return Double.parseDouble(textField.getText());
    }

    @Override
    public void validateValue() throws Exception {
        if (!textField.validate()) {
            throw new Exception();
        }
    }
}
