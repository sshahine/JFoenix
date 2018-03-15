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

package com.jfoenix.controls;

import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
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
    private final DoubleProperty percentageWidth = new SimpleDoubleProperty(1);
    
    public JFXTreeTableColumn() {
        init();
        treeTableViewProperty().addListener(new ChangeListener<TreeTableView<S>>() {

            @Override
            public void changed(ObservableValue<? extends TreeTableView<S>> observable, TreeTableView<S> oldValue, TreeTableView<S> newValue) {
                if (prefWidthProperty().isBound()) {
                    prefWidthProperty().unbind();
                }

                prefWidthProperty().bind(newValue.widthProperty().multiply(percentageWidth));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public JFXTreeTableColumn(String text) {
        super(text);
        init();
        treeTableViewProperty().addListener(new ChangeListener<TreeTableView<S>>() {

            @Override
            public void changed(ObservableValue<? extends TreeTableView<S>> observable, TreeTableView<S> oldValue, TreeTableView<S> newValue) {
                if (prefWidthProperty().isBound()) {
                    prefWidthProperty().unbind();
                }

                prefWidthProperty().bind(newValue.widthProperty().multiply(percentageWidth));
            }
        });
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
    
    public final DoubleProperty percentageWidthProperty() {
        return this.percentageWidth;
    }

    public final double getPercentageWidth() {
        return this.percentageWidthProperty().get();
    }

    public final void setPercentageWidth(double value) throws IllegalArgumentException {
        if (value >= 0 && value <= 1) {
            this.percentageWidthProperty().set(value);
        } else {
            throw new IllegalArgumentException(String.format("The provided percentage width is not between 0.0 and 1.0. Value is: %1$s", value));
        }
    }

}
