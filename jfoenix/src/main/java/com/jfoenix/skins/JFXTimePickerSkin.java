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

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import com.jfoenix.svg.SVGGlyph;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.time.LocalTime;

/**
 * <h1>Material Design Time Picker Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-03-01
 */
public class JFXTimePickerSkin extends JFXGenericPickerSkin<LocalTime> {

    private JFXTimePicker jfxTimePicker;
    // displayNode is the same as editorNode
    private TextField displayNode;
    private JFXTimePickerContent content;
    private JFXDialog dialog;

    public JFXTimePickerSkin(JFXTimePicker timePicker) {
        super(timePicker);

        this.jfxTimePicker = timePicker;

        // add focus listener on editor node
        timePicker.focusedProperty().addListener(observable -> {
            if (getEditor() != null && !timePicker.isFocused()) {
                reflectSetTextFromTextFieldIntoComboBoxValue();
            }
        });

        updateArrow(timePicker);
        ((JFXTextField) getEditor()).setFocusColor(timePicker.getDefaultColor());

        registerChangeListener(timePicker.defaultColorProperty(), obs -> updateArrow(timePicker));
        registerChangeListener(timePicker.converterProperty(), obs -> reflectUpdateDisplayNode());
        registerChangeListener(timePicker.editorProperty(), obs -> reflectUpdateDisplayNode());
        registerChangeListener(timePicker.showingProperty(), obs -> {
            if (jfxTimePicker.isShowing()) {
                show();
            } else {
                hide();
            }
        });
        registerChangeListener(timePicker.valueProperty(), obs -> {
            reflectUpdateDisplayNode();
            jfxTimePicker.fireEvent(new ActionEvent());
        });
    }

    private void updateArrow(JFXTimePicker picker) {
        ((Region) arrowButton.getChildren().get(0)).setBackground(new Background(
            new BackgroundFill(picker.getDefaultColor(), null, null)));
    }

    @Override
    protected Node getPopupContent() {
        if (content == null) {
            content = new JFXTimePickerContent((JFXTimePicker) getSkinnable());
        }
        return content;
    }

    @Override
    public void show() {
        if (!jfxTimePicker.isOverLay()) {
            super.show();
        }
        if (content != null) {
            content.init();
            content.clearFocus();
        }
        if (jfxTimePicker.isOverLay()) {
            if (dialog == null) {
                StackPane dialogParent = jfxTimePicker.getDialogParent();
                if (dialogParent == null) {
                    dialogParent = (StackPane) getSkinnable().getScene().getRoot();
                }
                dialog = new JFXDialog(dialogParent, (Region) getPopupContent(),
                    DialogTransition.CENTER, true);
                arrowButton.setOnMouseClicked((click) -> {
                    if (jfxTimePicker.isOverLay()) {
                        StackPane parent = jfxTimePicker.getDialogParent();
                        if (parent == null) {
                            parent = (StackPane) getSkinnable().getScene().getRoot();
                        }
                        dialog.show(parent);
                    }
                });
            }
        }
    }

    @Override
    protected TextField getEditor() {
        return ((JFXTimePicker) getSkinnable()).getEditor();
    }

    @Override
    protected StringConverter<LocalTime> getConverter() {
        return ((JFXTimePicker) getSkinnable()).getConverter();
    }

    @Override
    public Node getDisplayNode() {
        if (displayNode == null) {
            displayNode = reflectGetEditableInputNode();
            displayNode.getStyleClass().add("time-picker-display-node");
            reflectUpdateDisplayNode();
        }
        displayNode.setEditable(jfxTimePicker.isEditable());
        return displayNode;
    }


}

