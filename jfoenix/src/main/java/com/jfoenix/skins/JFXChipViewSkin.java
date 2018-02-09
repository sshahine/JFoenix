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

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXChip;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXDefaultChip;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.List;

/**
 * JFXChipArea is the material design implementation of chip Input.
 * An easy way to manage chips in a text area component with an x to
 * omit the chip.
 *
 * @author Shadi Shaheen & Gerard Moubarak
 * @version 1.0.0
 * @since 2018-02-01
 */
public class JFXChipViewSkin<T> extends SkinBase<JFXChipView<T>> {

    private static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");

    private CustomFlowPane root;
    private JFXChipView control;
    private TextArea inputField;
    private ChipsAutoComplete<T> autoCompletePopup;

    private boolean moveToNewLine = false;
    private boolean editorOnNewLine = true;
    private double availableWidth;
    private double requiredWidth;

    private final ListChangeListener<T> chipsChangeListeners = change -> {
        while (change.next()) {
            for (T item : change.getRemoved()) {
                for (int i = root.getChildren().size() - 2; i >= 0; i--) {
                    Node child = root.getChildren().get(i);
                    if (child instanceof JFXChip) {
                        if (((JFXChip) child).getItem() == item) {
                            root.getChildren().remove(i);
                            break;
                        }
                    }
                }
            }
            for (T item : change.getAddedSubList()) {
                createChip(item);
            }
        }
    };

    public JFXChipViewSkin(JFXChipView<T> control) {
        super(control);
        this.control = control;
        root = new CustomFlowPane();
        root.getStyleClass().add("chips-pane");
        setupEditor();
        getChildren().add(root);

        // init auto complete
        autoCompletePopup = (ChipsAutoComplete<T>) getSkinnable().getAutoCompletePopup();
        autoCompletePopup.setSelectionHandler(event -> {
            getSkinnable().getChips().add(event.getObject());
            inputField.clear();
        });
        // add position listener to auto complete
        autoCompletePopup.setShift(root.getVgap() * 2);
        root.vgapProperty().addListener((observable -> autoCompletePopup.setShift(root.getVgap() * 2)));

        // create initial chips
        for (T item : control.getChips()) {
            createChip(item);
        }
        control.getChips().addListener(new WeakListChangeListener<>(chipsChangeListeners));
    }

    private void setupEditor() {
        inputField = new TextArea();
        inputField.setManaged(false);
        inputField.getStyleClass().add("editor");
        final StringConverter<T> sc = control.getConverter();
        if (inputField instanceof TextInputControl) {
            TextInputControl editor = (TextInputControl) inputField;
            editor.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                if (event.getCode() != KeyCode.ENTER) {
                    getSkinnable().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
                }
            });
            if (editor instanceof TextArea) {
                ((TextArea) editor).setWrapText(true);
                editor.textProperty().addListener(observable -> {
                    // 13 is the default scroll bar width
                    requiredWidth = editor.snappedLeftInset() + computeTextContentWidth(editor) + editor.snappedRightInset() + 13;
                    if (availableWidth < requiredWidth && !editorOnNewLine) {
                        moveToNewLine = true;
                        root.updateEditorPosition();
                        root.requestLayout();
                    } else if (availableWidth > requiredWidth && editorOnNewLine) {
                        moveToNewLine = false;
                        root.updateEditorPosition();
                        root.requestLayout();
                    }
                });
            }
            editor.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (!editor.getText().trim().isEmpty()) {
                        try {
                            getSkinnable().getChips().add(sc.fromString(editor.getText()));
                            editor.clear();
                            autoCompletePopup.hide();
                        } catch (Exception ex) {
                            getSkinnable().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);
                        }
                    }
                    event.consume();
                }
                if (event.getCode() == KeyCode.BACK_SPACE) {
                    ObservableList<T> chips = getSkinnable().getChips();
                    int size = chips.size();
                    if ((size > 0) && editor.getText().isEmpty()) {
                        chips.remove(size - 1);
                        if (autoCompletePopup.isShowing()) {
                            autoCompletePopup.hide();
                        }
                    }
                }
            });
            editor.textProperty().addListener(observable -> {
                autoCompletePopup.filter(item -> getSkinnable().getPredicate().test(item, inputField.getText()));
                if (autoCompletePopup.getFilteredSuggestions().isEmpty()) {
                    autoCompletePopup.hide();
                } else {
                    autoCompletePopup.show(editor);
                }
            });
        }
        root.getChildren().add(inputField);
    }

    // these methods are called inside the chips items change listener
    private void createChip(T item) {
        JFXChip<T> chip;
        if (getSkinnable().getChipFactory() != null) {
            chip = getSkinnable().getChipFactory().apply(getSkinnable(), item);
        } else {
            chip = new JFXDefaultChip<T>(getSkinnable(), item);
        }
        int size = root.getChildren().size();
        root.getChildren().add(size - 1, chip);
    }

    private double computeTextContentWidth(TextInputControl editor) {
        Text text = new Text(editor.getText());
        text.setFont(editor.getFont());
        text.applyCss();
        return text.getLayoutBounds().getWidth();
    }


    private class CustomFlowPane extends FlowPane {
        double initOffset = 8;
        double childHeight = 0;

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            updateEditorPosition();
        }

        private VPos getRowValignmentInternal() {
            VPos localPos = getRowValignment();
            return localPos == null ? VPos.CENTER : localPos;
        }

        private HPos getColumnHalignmentInternal() {
            HPos localPos = getColumnHalignment();
            return localPos == null ? HPos.LEFT : localPos;
        }

        public void updateEditorPosition() {
            final Insets insets = getInsets();
            final double width = getWidth();
            final double height = getHeight();
            final double top = insets.getTop();
            final double left = insets.getLeft();
            final double bottom = insets.getBottom();
            final double right = insets.getRight();
            final double insideWidth = width - left - right;
            final double insideHeight = height - top - bottom;
            final double newLineEditorX = right + initOffset;

            final List<Node> managedChildren = getManagedChildren();
            final int mangedChildrenSize = managedChildren.size();
            if (mangedChildrenSize > 0) {
                Region lastChild = (Region) managedChildren.get(mangedChildrenSize - 1);
                childHeight = lastChild.getHeight();
                double contentHeight = lastChild.getHeight() + lastChild.getLayoutY();
                availableWidth = insideWidth - lastChild.getBoundsInParent().getMaxX();
                double minWidth = inputField.getMinWidth();
                minWidth = minWidth < 0 ? 100 : minWidth;
                minWidth = Math.max(minWidth, requiredWidth);

                if (availableWidth > requiredWidth) {
                    moveToNewLine = false;
                }

                if (availableWidth < minWidth || moveToNewLine) {
                    layoutInArea(inputField,
                        newLineEditorX, contentHeight + root.getVgap(),
                        insideWidth - initOffset, insideHeight - lastChild.getHeight() - lastChild.getLayoutY(),
                        0, getColumnHalignmentInternal(), VPos.TOP);
                    editorOnNewLine = true;
                } else {
                    double controlInsets = 0;
                    if (inputField instanceof TextArea) {
                        controlInsets = inputField.snappedTopInset();
                    }
                    layoutInArea(inputField,
                        lastChild.getBoundsInParent().getMaxX() + root.getHgap(),
                        lastChild.getLayoutY() + controlInsets,
                        availableWidth - root.getHgap(),
                        lastChild.getHeight(),
                        0, getColumnHalignmentInternal(), getRowValignmentInternal());
                    editorOnNewLine = false;
                }
            } else {
                layoutInArea(inputField, newLineEditorX, top, insideWidth - initOffset, height, 0, getColumnHalignmentInternal(), VPos.TOP);
                editorOnNewLine = true;
            }
        }

    }

    public static class ChipsAutoComplete<T> extends JFXAutoCompletePopup<T> {

        public ChipsAutoComplete() {
            getStyleClass().add("jfx-chips-popup");
        }

        private double shift = 0;

        private Text text;

        void setShift(double shift) {
            this.shift = shift;
        }

        public void show(Node node) {
            if (text == null) {
                text = (Text) node.lookup(".text");
            }
            node = text;
            if (!isShowing()) {
                if (node.getScene() == null || node.getScene().getWindow() == null) {
                    throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
                }
                Window parent = node.getScene().getWindow();
                this.show(parent, parent.getX() +
                                  node.localToScene(0, 0).getX() +
                                  node.getScene().getX(),
                    parent.getY() + node.localToScene(0, 0).getY() +
                    node.getScene().getY() + node.getLayoutBounds().getHeight() + shift);
                ((JFXAutoCompletePopupSkin<T>) getSkin()).animate();
            } else {
                // if already showing update location if needed
                Window parent = node.getScene().getWindow();
                this.show(parent, parent.getX() +
                                  node.localToScene(0, 0).getX() +
                                  node.getScene().getX(),
                    parent.getY() + node.localToScene(0, 0).getY() +
                    node.getScene().getY() + node.getLayoutBounds().getHeight() + shift);
            }
        }
    }

}
