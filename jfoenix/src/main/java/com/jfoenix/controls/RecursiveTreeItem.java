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

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/**
 * RecursiveTreeItem is used along with RecursiveTreeObject
 * to build the data model for the TreeTableView.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class RecursiveTreeItem<T extends RecursiveTreeObject<T>> extends TreeItem<T> {

    private Callback<RecursiveTreeObject<T>, ObservableList<T>> childrenFactory;

    /**
     * predicate used to filter nodes
     */
    private ObjectProperty<Predicate<TreeItem<T>>> predicate = new SimpleObjectProperty<>((TreeItem<T> t) -> true);

    /**
     * map data value to tree item
     */
    private HashMap<T, TreeItem<T>> itemsMap;

    /**
     * list of original items
     */
    ObservableList<TreeItem<T>> originalItems;

    /**
     * list of filtered items
     */
    FilteredList<TreeItem<T>> filteredItems;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * creates empty recursive tree item
     *
     * @param func is the callback used to retrieve the children of the current tree item
     */
    public RecursiveTreeItem(Callback<RecursiveTreeObject<T>, ObservableList<T>> func) {
        this(null, null, func);
    }

    /**
     * creates recursive tree item for a specified value
     *
     * @param value of the tree item
     * @param func  is the callback used to retrieve the children of the current tree item
     */
    public RecursiveTreeItem(final T value, Callback<RecursiveTreeObject<T>, ObservableList<T>> func) {
        this(value, null, func);
    }

    /**
     * creates recursive tree item for a specified value and a graphic node
     *
     * @param value   of the tree item
     * @param graphic node
     * @param func    is the callback used to retrieve the children of the current tree item
     */
    public RecursiveTreeItem(final T value, Node graphic, Callback<RecursiveTreeObject<T>, ObservableList<T>> func) {
        super(value, graphic);
        this.childrenFactory = func;
        init(value);
    }

    /**
     * creates recursive tree item from a data list
     *
     * @param dataList of values
     * @param func     is the callback used to retrieve the children of the current tree item
     */
    public RecursiveTreeItem(ObservableList<T> dataList, Callback<RecursiveTreeObject<T>, ObservableList<T>> func) {
        RecursiveTreeObject<T> root = new RecursiveTreeObject<>();
        root.setChildren(dataList);
        this.childrenFactory = func;
        init(root);
    }

    private void init(RecursiveTreeObject<T> value) {

        if (value != null) {
            addChildrenListener(value);
        }
        valueProperty().addListener(observable -> {
            if (getValue() != null) {
                addChildrenListener(getValue());
            }
        });

        predicate.addListener(observable -> {
            filteredItems.setPredicate(child -> {
                // Set the predicate of child items to force filtering
                if (child instanceof RecursiveTreeItem) {
                    if (!((RecursiveTreeItem) child).originalItems.isEmpty()) {
                        RecursiveTreeItem<T> filterableChild = (RecursiveTreeItem<T>) child;
                        filterableChild.setPredicate(predicate.get());
                    }
                }
                // If there is no predicate, keep this tree item
                if (predicate.get() == null) {
                    return true;
                }
                // If there are children, keep this tree item
                if (child.getChildren().size() > 0) {
                    return true;
                }
                // If its a group node keep this item if it has children
                if (child.getValue() instanceof RecursiveTreeObject &&
                    child.getValue().getClass() == RecursiveTreeObject.class) {
                    return child.getChildren().size() != 0;
                }
                // Otherwise ask the TreeItemPredicate
                return predicate.get().test(child);
            });
        });

        this.filteredItems.predicateProperty().addListener(observable ->
            JFXUtilities.runInFXAndWait(() -> {
                getChildren().clear();
                getChildren().setAll(filteredItems);
            }));
    }


    private void addChildrenListener(RecursiveTreeObject<T> value) {
        final ObservableList<T> children = childrenFactory.call(value);
        originalItems = FXCollections.observableArrayList();
        itemsMap = new HashMap<>();

        for (T child : children) {
            final RecursiveTreeItem<T> treeItem = new RecursiveTreeItem<>(child, getGraphic(), childrenFactory);
            originalItems.add(treeItem);
            itemsMap.put(child, treeItem);
        }

        filteredItems = new FilteredList<>(originalItems, (TreeItem<T> t) -> true);

        this.getChildren().addAll(originalItems);

        children.addListener((ListChangeListener<T>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    List<RecursiveTreeItem<T>> addedItems = new ArrayList<>();
                    for (T newChild : change.getAddedSubList()) {
                        final RecursiveTreeItem<T> newTreeItem = new RecursiveTreeItem<>(newChild, getGraphic(), childrenFactory);
                        addedItems.add(newTreeItem);
                        itemsMap.put(newChild, newTreeItem);
                    }
                    getChildren().addAll(addedItems);
                    originalItems.addAll(addedItems);
                }
                if (change.wasRemoved()) {
                    List<TreeItem<T>> removedItems = new ArrayList<>();
                    change.getRemoved().forEach(t -> {
                        final TreeItem<T> treeItem = itemsMap.remove(t);
                        if (treeItem != null) {
                            // remove the items from the current/original items list
                            removedItems.add(treeItem);
                        }
                    });
                    if (originalItems.size() == removedItems.size()) {
                        originalItems.clear();
                        getChildren().clear();
                    } else {
                        getChildren().removeAll(removedItems);
                        originalItems.removeAll(removedItems);
                    }
                }
            }
        });

    }

    public final ObjectProperty<Predicate<TreeItem<T>>> predicateProperty() {
        return this.predicate;
    }

    public final Predicate<TreeItem<T>> getPredicate() {
        return this.predicateProperty().get();
    }

    public final void setPredicate(final Predicate<TreeItem<T>> predicate) {
        this.predicateProperty().set(predicate);
    }

    public TreeItem<T> getTreeItem(T value) {
        return itemsMap.get(value);
    }
}
