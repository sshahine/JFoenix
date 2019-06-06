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
import com.jfoenix.validation.base.ValidatorBase;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * <h1>Text field cell editor</h1>
 * this an example of the cell editor, it creates a JFXTextField node to
 * allow the user to edit the cell value
 * <p>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public abstract class TextFieldEditorBase<T> implements EditorNodeBuilder<T> {

    protected JFXTextField textField;
    protected final ValidatorBase[] validators;

    public TextFieldEditorBase(ValidatorBase... validators) {
        this.validators = validators;
    }

    @Override
    public void startEdit() {
        Platform.runLater(() -> {
            textField.selectAll();
            textField.requestFocus();
        });
    }

    @Override
    public void cancelEdit() {

    }

    @Override
    public void updateItem(T item, boolean empty) {
        Platform.runLater(() -> {
            textField.selectAll();
            textField.requestFocus();
        });
    }

    @Override
    public Region createNode(T value, EventHandler<KeyEvent> keyEventsHandler, ChangeListener<Boolean> focusChangeListener) {
        textField = value == null ? new JFXTextField() : new JFXTextField(String.valueOf(value));
        textField.setOnKeyPressed(keyEventsHandler);
        textField.getValidators().addAll(validators);
        textField.focusedProperty().addListener(focusChangeListener);
        return textField;
    }

    @Override
    public void setValue(T value) {
        textField.setText(value == null ? null : String.valueOf(value));
    }

    @Override
    public void validateValue() throws Exception {
        if (!textField.validate()) {
            throw new Exception("Invalid value");
        }
    }

    @Override
    public void nullEditorNode() {
        textField = null;
    }
}
