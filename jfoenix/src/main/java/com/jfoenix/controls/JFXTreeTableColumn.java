/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.controls;

import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * JFXTreeTableColumn is used by {@Link JFXTreeTableView}, it supports grouping functionality
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableColumn<S, T> extends TreeTableColumn<S, T> {

    /**
     * {@inheritDoc}
     */
    public JFXTreeTableColumn() {
        init();
    }

    /**
     * {@inheritDoc}
     */
    public JFXTreeTableColumn(String text) {
        super(text);
        init();
    }

    private void init() {
        this.setCellFactory(new Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>() {
            @Override
            public TreeTableCell<S, T> call(TreeTableColumn<S, T> param) {
                return new JFXTreeTableCell<S, T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        if (item == getItem()) {
                            return;
                        }
                        super.updateItem(item, empty);
                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else if (item instanceof Node) {
                            super.setText(null);
                            super.setGraphic((Node) item);
                        } else {
                            super.setText(item.toString());
                            super.setGraphic(null);
                        }
                    }
                };
            }
        });

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Group");
        item1.setOnAction((action) -> {
            ((JFXTreeTableView) getTreeTableView()).group(this);
        });
        MenuItem item2 = new MenuItem("UnGroup");
        item2.setOnAction((action) -> {
            ((JFXTreeTableView) getTreeTableView()).unGroup(this);
        });
        contextMenu.getItems().addAll(item1, item2);
        setContextMenu(contextMenu);
    }

    /**
     * validates the value of the tree item,
     * this method also hides the column value for the grouped nodes
     *
     * @param param tree item
     * @return true if the value is valid else false
     */
    public final boolean validateValue(CellDataFeatures<S, T> param) {
        Object rowObject = param.getValue().getValue();
        return !((rowObject instanceof RecursiveTreeObject && rowObject.getClass() == RecursiveTreeObject.class)
                 || (param.getTreeTableView() instanceof JFXTreeTableView
                     && ((JFXTreeTableView<?>) param.getTreeTableView()).getGroupOrder().contains(this)
                     // make sure the node is a direct child to a group node
                     && param.getValue().getParent() != null
                     && param.getValue().getParent().getValue() != null
                     && param.getValue().getParent().getValue().getClass() == RecursiveTreeObject.class
                 ));
    }

    /**
     * @param param tree item
     * @return the data represented by the tree item
     */
    public final ObservableValue<T> getComputedValue(CellDataFeatures<S, T> param) {
        Object rowObject = param.getValue().getValue();
        if (rowObject instanceof RecursiveTreeObject) {
            RecursiveTreeObject<?> item = (RecursiveTreeObject<?>) rowObject;
            if (item.getGroupedColumn() == this) {
                return new ReadOnlyObjectWrapper(item.getGroupedValue());
            }
        }
        return null;
    }

    /**
     * @return true if the column is grouped else false
     */
    public boolean isGrouped() {
        return getTreeTableView() instanceof JFXTreeTableView && ((JFXTreeTableView<?>) getTreeTableView()).getGroupOrder()
            .contains(
                this);
    }

}
