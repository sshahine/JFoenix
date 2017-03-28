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

import com.jfoenix.controls.JFXTreeView.CellAnimation;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.lang.ref.WeakReference;

/**
 * JFXTreeCell is the animated material design implementation of a tree cell.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-02-15
 */
public class JFXTreeCell<T> extends TreeCell<T> {

    private HBox hbox;
    private StackPane selectedPane = new StackPane();

    protected JFXRippler cellRippler = new JFXRippler(new StackPane()) {
        @Override
        protected void initListeners() {
            ripplerPane.setOnMousePressed((event) -> createRipple(event.getX(), event.getY()));
        }
    };

    private WeakReference<TreeItem<T>> treeItemRef;

    private ChangeListener<Boolean> weakExpandListener = (ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
        JFXTreeView<T> jfxTreeView = (JFXTreeView<T>) getTreeView();
        jfxTreeView.clearAnimation();
        int currentRow = getTreeView().getRow(getTreeItem());
        jfxTreeView.animateRow = currentRow;
        jfxTreeView.expand = newValue;
        jfxTreeView.disableSiblings = false;

        VirtualFlow<?> vf = (VirtualFlow<?>) getTreeView().lookup(".virtual-flow");
        if (!newValue) {
            int index = currentRow + getTreeItem().getChildren().size() + 1;
            index = index > vf.getCellCount() ? vf.getCellCount() : index;
            jfxTreeView.height = (index - currentRow - 1) * vf.getCell(currentRow).getHeight();
        }
        jfxTreeView.layoutY = vf.getCell(currentRow).getLayoutY();
    };

    private InvalidationListener treeItemGraphicInvalidationListener = observable -> updateDisplay(getItem(),
                                                                                                   isEmpty());
    private InvalidationListener treeItemInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            TreeItem<T> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
            if (oldTreeItem != null) {
                oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
                oldTreeItem.expandedProperty().removeListener(weakExpandListener);
            }

            TreeItem<T> newTreeItem = getTreeItem();
            if (newTreeItem != null) {
                newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
                newTreeItem.expandedProperty().addListener(weakExpandListener);
                treeItemRef = new WeakReference<>(newTreeItem);
            }
        }
    };
    private WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(
        treeItemGraphicInvalidationListener);

    private ChangeListener<? super Status> weakAnimationListener = (o, oldVal, newVal) -> {
        if (newVal.equals(Status.STOPPED))
            clearCellAnimation();
    };

    private WeakReference<JFXTreeView<T>> treeViewRef;
    private InvalidationListener treeViewInvalidationListener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            JFXTreeView<T> oldTreeView = treeViewRef == null ? null : treeViewRef.get();
            if (oldTreeView != null)
                oldTreeView.trans.statusProperty().removeListener(weakAnimationListener);
            JFXTreeView<T> newTreeView = (JFXTreeView<T>) getTreeView();
            if (newTreeView != null) {
                newTreeView.trans.statusProperty().addListener(weakAnimationListener);
                treeViewRef = new WeakReference<>(newTreeView);
            }
        }
    };

    public JFXTreeCell() {
        selectedPane.setStyle("-fx-background-color:RED");
        selectedPane.setPrefWidth(3);
        selectedPane.setMouseTransparent(true);
        selectedProperty().addListener((o, oldVal, newVal) -> {
            selectedPane.setOpacity(newVal ? 1 : 0);
        });

        final WeakInvalidationListener weakTreeViewListener = new WeakInvalidationListener(treeViewInvalidationListener);
        treeViewProperty().addListener(weakTreeViewListener);
        final WeakInvalidationListener weakTreeItemListener = new WeakInvalidationListener(treeItemInvalidationListener);
        treeItemProperty().addListener(weakTreeItemListener);
        if (getTreeItem() != null) {
            getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
        }
    }

    @Override
    public void updateIndex(int i) {
        JFXTreeView<T> jfxTreeView = (JFXTreeView<T>) getTreeView();
        if (jfxTreeView.animateRow != -1 && i != -1) {
            int oldIndex = getIndex();
            if (oldIndex == -1 || (oldIndex > 0 && i > 0 && oldIndex != i)) {
                if (jfxTreeView.sibRow == -1) {
                    if (jfxTreeView.getTreeItem(i) != null && jfxTreeView.getTreeItem(jfxTreeView.animateRow) != null
                        && i > jfxTreeView.animateRow
                        && jfxTreeView.getTreeItem(i).getParent() == jfxTreeView.getTreeItem(jfxTreeView.animateRow)
                                                                                .getParent()) {
                        jfxTreeView.sibRow = i;
                        if (jfxTreeView.expand)
                            jfxTreeView.height = -(jfxTreeView.sibRow - jfxTreeView.animateRow - 1) * getHeight();
                    }
                }
                if (jfxTreeView.getTreeItem(i) != null && jfxTreeView.getTreeItem(i) == jfxTreeView.getTreeItem(
                    jfxTreeView.animateRow)) {
                    if (i * this.getHeight() != jfxTreeView.layoutY) {
                        jfxTreeView.disableSiblings = true;
                        for (int index : jfxTreeView.sibAnimationMap.keySet()) {
                            if (index > i) {
                                jfxTreeView.trans.getChildren()
                                                 .remove(jfxTreeView.sibAnimationMap.get(index).getAnimation());
                                jfxTreeView.sibAnimationMap.get(index).getCell().clearCellAnimation();
                            }
                        }
                        jfxTreeView.sibAnimationMap.clear();
                    }
                }
                if (i > jfxTreeView.animateRow) {
                    if (jfxTreeView.expand) {
                        // animate siblings
                        if (i >= jfxTreeView.sibRow && jfxTreeView.sibRow != -1)
                            animateSibling(i, jfxTreeView);
                            // animate children
                        else
                            animateChild(i, jfxTreeView);
                    } else {
                        // animate siblings
                        animateSibling(i, jfxTreeView);
                    }
                }
            }
        }
        super.updateIndex(i);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (!getChildren().contains(cellRippler)) {
            getChildren().add(0, cellRippler);
            getChildren().add(0, selectedPane);
        }
        if (isEmpty())
            cellRippler.resizeRelocate(0, 0, 0, 0);
        else
            cellRippler.resizeRelocate(0, 0, getWidth(), getHeight());

        selectedPane.resizeRelocate(0, 0, selectedPane.prefWidth(-1), getHeight());
        selectedPane.setOpacity(isSelected() ? 1 : 0);
        if (((JFXTreeView<T>) getTreeView()).trans.getChildren().isEmpty()) {
            clearCellAnimation();
            ((JFXTreeView<T>) getTreeView()).animateRow = -1;
        } else if (((JFXTreeView<T>) getTreeView()).trans.getStatus().equals(Status.STOPPED)) {
            ((JFXTreeView<T>) getTreeView()).trans.setOnFinished((finish) -> {
                ((JFXTreeView<T>) getTreeView()).trans.getChildren().clear();
                ((JFXTreeView<T>) getTreeView()).animateRow = -1;
            });
            ((JFXTreeView<T>) getTreeView()).trans.play();
        }
    }

    void updateDisplay(T item, boolean empty) {
        if (item == null || empty) {
            hbox = null;
            setText(null);
            setGraphic(null);
        } else {
            TreeItem<T> treeItem = getTreeItem();
            if (treeItem != null && treeItem.getGraphic() != null) {
                if (item instanceof Node) {
                    setText(null);
                    if (hbox == null) {
                        hbox = new HBox(3);
                    }
                    hbox.getChildren().setAll(treeItem.getGraphic(), (Node) item);
                    setGraphic(hbox);
                } else {
                    hbox = null;
                    setText(item.toString());
                    setGraphic(treeItem.getGraphic());
                }
            } else {
                hbox = null;
                if (item instanceof Node) {
                    setText(null);
                    setGraphic((Node) item);
                } else {
                    setText(item.toString());
                    setGraphic(null);
                }
            }
        }
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateDisplay(item, empty);
    }

    private void animateChild(int i, JFXTreeView<T> jfxTreeView) {
        Timeline createChildAnimation = createChildAnimation(this, i - jfxTreeView.animateRow - 1);
        if (jfxTreeView.childrenAnimationMap.containsKey(i)) {
            jfxTreeView.trans.getChildren().remove(jfxTreeView.childrenAnimationMap.get(i).getAnimation());
            jfxTreeView.childrenAnimationMap.get(i).getCell().clearCellAnimation();
        }
        jfxTreeView.childrenAnimationMap.put(i, new CellAnimation(this, createChildAnimation));
        jfxTreeView.trans.getChildren().add(createChildAnimation);
    }

    private void animateSibling(int i, JFXTreeView<T> jfxTreeView) {
        if (!jfxTreeView.disableSiblings) {
            Timeline createSibAnimation = createSibAnimation(this, i);
            jfxTreeView.sibAnimationMap.put(i, new CellAnimation(this, createSibAnimation));
            jfxTreeView.trans.getChildren().add(createSibAnimation);
        }
    }

    private void clearCellAnimation() {
        this.setOpacity(1);
        this.setTranslateY(0);
    }

    private Timeline createSibAnimation(TreeCell<?> cell, int index) {
        cell.setTranslateY(((JFXTreeView<T>) getTreeView()).height);
        return new Timeline(new KeyFrame(Duration.millis(120),
                                         new KeyValue(cell.translateYProperty(), 0, Interpolator.EASE_BOTH)));
    }

    private Timeline createChildAnimation(TreeCell<?> cell, int delay) {
        cell.setOpacity(0);
        cell.setTranslateY(-1);
        Timeline f1 = new Timeline(new KeyFrame(Duration.millis(120),
                                                new KeyValue(cell.opacityProperty(), 1, Interpolator.EASE_BOTH),
                                                new KeyValue(cell.translateYProperty(), 0, Interpolator.EASE_BOTH)));
        f1.setDelay(Duration.millis(20 + delay * 10));
        return f1;
    }
}
