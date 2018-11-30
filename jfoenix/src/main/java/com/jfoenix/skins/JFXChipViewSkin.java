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
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.FocusTraversalInputMap;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.traversal.Direction;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    private JFXChipView<T> control;
    private FakeFocusTextArea editor;
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

    private final ScrollPane scrollPane;
    private ChipViewBehaviorBase<T> behavior;

    public JFXChipViewSkin(JFXChipView<T> control) {
        super(control);
        this.control = control;
        this.behavior = new JFXChipViewSkin.ChipViewBehaviorBase<>(control);
        root = new CustomFlowPane();
        root.getStyleClass().add("chips-pane");
        setupEditor();

        scrollPane = new ScrollPane(root) {
            @Override
            public void requestFocus() {
                if (getSkinnable() != null) {
                    getSkinnable().requestFocus();
                }
            }
        };
        scrollPane.setFitToWidth(true);

        getChildren().add(scrollPane);

        // init auto complete
        autoCompletePopup = (ChipsAutoComplete<T>) getSkinnable().getAutoCompletePopup();
        autoCompletePopup.setSelectionHandler(event -> {
            T selectedItem = event.getObject();
            if (getSkinnable().getSelectionHandler() != null) {
                selectedItem = getSkinnable().getSelectionHandler().apply(selectedItem);
            }
            getSkinnable().getChips().add(selectedItem);
            editor.clear();
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

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        scrollPane.resizeRelocate(contentX, contentY, contentWidth, contentHeight);
    }

    private void setupEditor() {
        editor = new FakeFocusTextArea();
        editor.setManaged(false);
        editor.getStyleClass().add("editor");
        editor.setWrapText(true);
        editor.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() != KeyCode.ENTER) {
                getSkinnable().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
            }
        });
        editor.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ENTER:
                    if (!editor.getText().trim().isEmpty()) {
                        try {
                            final StringConverter<T> sc = control.getConverter();
                            final T item = sc.fromString(editor.getText());
                            if (item != null) {
                                getSkinnable().getChips().add(item);
                            }
                            editor.clear();
                            autoCompletePopup.hide();
                        } catch (Exception ex) {
                            getSkinnable().pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);
                        }
                    }
                    event.consume();
                    break;

                case TAB:
                    if (editor.getText().trim().isEmpty()) {
                        if (event.isShiftDown()) {
                            behavior.traverse(getSkinnable(), Direction.PREVIOUS);
                        } else {
                            behavior.traverse(editor, Direction.NEXT);
                        }
                    }
                    event.consume();
                    break;

                case BACK_SPACE:
                    ObservableList<T> chips = getSkinnable().getChips();
                    int size = chips.size();
                    if ((size > 0) && editor.getText().isEmpty()) {
                        chips.remove(size - 1);
                        if (autoCompletePopup.isShowing()) {
                            autoCompletePopup.hide();
                        }
                    }
                    break;

                case SPACE:
                    if (event.isControlDown()) {
                        if (!autoCompletePopup.getFilteredSuggestions().isEmpty()) {
                            autoCompletePopup.show(editor);
                        }
                    }
                    break;
            }
        });

        editor.textProperty().addListener(observable -> {
            // update editor position
            // 13 is the default scroll bar width
            requiredWidth = editor.snappedLeftInset() + computeTextContentWidth(editor) + editor.snappedRightInset() + 13;
            if (availableWidth < requiredWidth && !editorOnNewLine && !moveToNewLine) {
                moveToNewLine = true;
                root.requestLayout();
            } else if (availableWidth > requiredWidth && editorOnNewLine && moveToNewLine) {
                moveToNewLine = false;
                root.requestLayout();
            }
            // show popup
            autoCompletePopup.filter(item -> getSkinnable().getPredicate().test(item, editor.getText()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(editor);
            }
        });
        root.getChildren().add(editor);

        // add control listeners
        control.focusedProperty().addListener((obj, oldVal, newVal) -> {
            if (editor != null) {
                editor.setFakeFocus(newVal);
            }
        });
        control.addEventFilter(KeyEvent.ANY, ke -> {
            if (editor != null) {
                if (ke.getTarget().equals(editor)) {
                    return;
                }
                // only pass event
                if (ke.getTarget().equals(control)) {
                    switch (ke.getCode()) {
                        case ESCAPE:
                        case F10:
                            // Allow to bubble up.
                            break;
                        default:
                            editor.fireEvent(ke.copyFor(editor, editor));
                            ke.consume();
                    }
                }
            }
        });
    }

    // these methods are called inside the chips items change listener
    private void createChip(T item) {
        JFXChip<T> chip = null;
        try {
            if (getSkinnable().getChipFactory() != null) {
                chip = getSkinnable().getChipFactory().apply(getSkinnable(), item);
            } else {
                chip = new JFXDefaultChip<T>(getSkinnable(), item);
            }
        } catch (Exception e) {
            throw new RuntimeException("can't create chip for item '" + item +
                                       "' make sure to override the string converter and return null if text input is not valid.", e);
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

        {
            addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ensureVisible(editor));
        }

        private void ensureVisible(Node node) {
            double height = scrollPane.getContent().getBoundsInLocal().getHeight();
            double y = node.getBoundsInParent().getMaxY();
            // scrolling values range from 0 to 1
            scrollPane.setVvalue(y / height);
            // just for usability
            if (getSkinnable().isFocused()) {
                node.requestFocus();
            }
        }


        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            updateEditorPosition();
        }

        @Override
        protected double computePrefHeight(double forWidth) {
            editor.setManaged(true);
            double height = super.computePrefHeight(forWidth);
            editor.setManaged(false);
            return height;
        }

        private VPos getRowVAlignmentInternal() {
            VPos localPos = getRowValignment();
            return localPos == null ? VPos.CENTER : localPos;
        }

        private HPos getColumnHAlignmentInternal() {
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
            final double editorVInsets = editor.snappedTopInset() + editor.snappedBottomInset();

            final List<Node> managedChildren = getManagedChildren();
            final int mangedChildrenSize = managedChildren.size();
            if (mangedChildrenSize > 0) {
                Region lastChild = (Region) managedChildren.get(mangedChildrenSize - 1);
                double contentHeight = lastChild.getHeight() + lastChild.getLayoutY();
                availableWidth = insideWidth - lastChild.getBoundsInParent().getMaxX();
                double minWidth = editor.getMinWidth();
                minWidth = minWidth < 0 ? 100 : minWidth;
                minWidth = Math.max(minWidth, requiredWidth);

                if (availableWidth > requiredWidth) {
                    moveToNewLine = false;
                }

                if (availableWidth < minWidth || moveToNewLine) {
                    layoutInArea(editor,
                        newLineEditorX,
                        contentHeight + root.getVgap(),
                        insideWidth - initOffset,
                        editor.prefHeight(-1),
                        0, getColumnHAlignmentInternal(), VPos.TOP);
                    editorOnNewLine = true;
                    ensureVisible(editor);
                } else {
                    layoutInArea(editor,
                        lastChild.getBoundsInParent().getMaxX() + root.getHgap(),
                        lastChild.getLayoutY(),
                        availableWidth - root.getHgap(),
                        lastChild.getHeight() + editorVInsets,
                        0, getColumnHAlignmentInternal(), getRowVAlignmentInternal());
                    editorOnNewLine = false;
                }
            } else {
                layoutInArea(editor,
                    newLineEditorX,
                    top,
                    insideWidth - initOffset,
                    editor.prefHeight(-1)
                    , 0, getColumnHAlignmentInternal(), VPos.TOP);
                editorOnNewLine = true;
                ensureVisible(editor);
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

    final class FakeFocusTextArea extends TextArea {
        @Override
        public void requestFocus() {
            if (getSkinnable() != null) {
                getSkinnable().requestFocus();
            }
        }

        public void setFakeFocus(boolean b) {
            setFocused(b);
        }

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            switch (attribute) {
                case FOCUS_ITEM:
                    // keep focus on parent control
                    return getSkinnable();
                default:
                    return super.queryAccessibleAttribute(attribute, parameters);
            }
        }
    }

    public void dispose() {
        super.dispose();
        if (this.behavior != null) {
            this.behavior.dispose();
        }
    }

    final static class ChipViewBehaviorBase<T> extends BehaviorBase<JFXChipView<T>> {
        public ChipViewBehaviorBase(JFXChipView<T> control) {
            super(control);
        }

        @Override
        public InputMap<JFXChipView<T>> getInputMap() {
            return new InputMap<>(getNode());
        }

        public void traverse(Node node, Direction dir) {
            FocusTraversalInputMap.traverse(node, dir);
        }
    }
}
