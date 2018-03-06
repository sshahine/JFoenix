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

package com.jfoenix.controls.cells.editors.base;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Generic Editable Tree Table cell</h1>
 * Provides the base for an editable table cell using a text field. Sub-classes can provide formatters for display and a
 * commitHelper to control when editing is committed.
 * <p>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class GenericEditableTreeTableCell<S, T> extends JFXTreeTableCell<S, T> {
    protected EditorNodeBuilder builder;
    protected Region editorNode;


    /**
     * constructor that takes a custom builder to edit the cell
     *
     * @param builder
     */
    public GenericEditableTreeTableCell(EditorNodeBuilder builder) {
        this.builder = builder;
    }

    /**
     * constructor that creates the default {@link com.jfoenix.controls.cells.editors.TextFieldEditorBuilder TextField}
     * editor node to edit the cell
     */
    public GenericEditableTreeTableCell() {
        builder = new TextFieldEditorBuilder();
    }

    /**
     * Any action attempting to commit an edit should call this method rather than commit the edit directly itself. This
     * method will perform any validation and conversion required on the value. For text values that normally means this
     * method just commits the edit but for numeric values, for example, it may first parse the given input. <p> The
     * only situation that needs to be treated specially is when the field is losing focus. If you user hits enter to
     * commit the cell with bad data we can happily cancel the commit and force them to enter a real value. If they
     * click away from the cell though we want to give them their old value back.
     *
     * @param losingFocus true if the reason for the call was because the field is losing focus.
     */
    protected void commitHelper(boolean losingFocus) {
        if (editorNode == null) {
            return;
        }
        try {
            builder.validateValue();
            commitEdit((T) builder.getValue());
        } catch (Exception ex) {
            //Most of the time we don't mind if there is a parse exception as it
            //indicates duff user data but in the case where we are losing focus
            //it means the user has clicked away with bad data in the cell. In that
            //situation we want to just cancel the editing and show them the old
            //value.
            if (losingFocus) {
                cancelEdit();
            }
        }

    }

    /**
     * Provides the string representation of the value of this cell when the cell is not being edited.
     */
    protected Object getValue() {
        return getItem() == null ? "" : getItem();
    }

    @Override
    public void startEdit() {
        if (isEditable() && checkGroupedColumn()) {
            super.startEdit();
            if (editorNode == null) {
                createEditorNode();
            }else{
                // set current value if the editor is already created
                builder.setValue(getValue());
            }
            builder.startEdit();
            setGraphic(editorNode);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        builder.cancelEdit();
        builder.setValue(getValue());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        //Once the edit has been cancelled we no longer need the editor
        //so we mark it for cleanup here. Note though that you have to handle
        //this situation in the focus listener which gets fired at the end
        //of the editing.
        editorNode = null;
    }

    /**
     * only allows editing for items that are not grouped
     *
     * @return whether the item is grouped or not
     */
    private boolean checkGroupedColumn() {
        boolean allowEdit = true;
        if (getTreeTableRow().getTreeItem() != null) {
            Object rowObject = getTreeTableRow().getTreeItem().getValue();
            if (rowObject instanceof RecursiveTreeObject && rowObject.getClass() == RecursiveTreeObject.class) {
                allowEdit = false;
            } else {
                // check grouped columns in the tableview
                if (getTableColumn() instanceof JFXTreeTableColumn && ((JFXTreeTableColumn) getTableColumn()).isGrouped()) {
                    // make sure that the object is a direct child to a group node
                    if (getTreeTableRow().getTreeItem().getParent() != null &&
                        getTreeTableRow().getTreeItem().getParent().getValue().getClass() == RecursiveTreeObject.class) {
                        allowEdit = false;
                    }
                }
            }
        }
        return allowEdit;
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing() && checkGroupedColumn()) {
                if (editorNode != null) {
                    builder.setValue(getValue());
                }
                setGraphic(editorNode);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                builder.updateItem(item, empty);
            } else {
                Object value = getValue();
                if (value instanceof Node) {
                    setGraphic((Node) value);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(value.toString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }
    }

    private void createEditorNode() {
        EventHandler<KeyEvent> keyEventsHandler = t -> {
            if (t.getCode() == KeyCode.ENTER) {
                commitHelper(false);
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            } else if (t.getCode() == KeyCode.TAB) {
                commitHelper(false);

                TreeTableColumn nextColumn = getNextColumn(!t.isShiftDown());
                if (nextColumn != null) {
                    getTreeTableView().edit(getIndex(), nextColumn);
                }
            }
        };

        ChangeListener<Boolean> focusChangeListener = (observable, oldValue, newValue) -> {
            //This focus listener fires at the end of cell editing when focus is lost
            //and when enter is pressed (because that causes the text field to lose focus).
            //The problem is that if enter is pressed then cancelEdit is called before this
            //listener runs and therefore the text field has been cleaned up. If the
            //text field is null we don't commit the edit. This has the useful side effect
            //of stopping the double commit.
            if (!newValue && editorNode != null) {
                commitHelper(true);
            }
        };
        editorNode = builder.createNode(getValue(), keyEventsHandler, focusChangeListener);
    }

    /**
     * @param forward true gets the column to the right, false the column to the left of the current column
     * @return
     */
    private TreeTableColumn<S, ?> getNextColumn(boolean forward) {
        List<TreeTableColumn<S, ?>> columns = new ArrayList<>();
        for (TreeTableColumn<S, ?> column : getTreeTableView().getColumns()) {
            columns.addAll(getLeaves(column));
        }
        //There is no other column that supports editing.
        if (columns.size() < 2) {
            return null;
        }
        int nextIndex = columns.indexOf(getTableColumn());
        if (forward) {
            nextIndex++;
            if (nextIndex > columns.size() - 1) {
                nextIndex = 0;
            }
        } else {
            nextIndex--;
            if (nextIndex < 0) {
                nextIndex = columns.size() - 1;
            }
        }
        return columns.get(nextIndex);
    }


    private List<TreeTableColumn<S, ?>> getLeaves(TreeTableColumn<S, ?> root) {
        List<TreeTableColumn<S, ?>> columns = new ArrayList<>();
        if (root.getColumns().isEmpty()) {
            //We only want the leaves that are editable.
            if (root.isEditable()) {
                columns.add(root);
            }
            return columns;
        } else {
            for (TreeTableColumn<S, ?> column : root.getColumns()) {
                columns.addAll(getLeaves(column));
            }
            return columns;
        }
    }
}
