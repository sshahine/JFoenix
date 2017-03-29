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

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.scene.control.skin.TreeTableRowSkin;
import javafx.animation.*;
import javafx.animation.Animation.Status;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TreeTableRow;
import javafx.util.Duration;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Shadi Shaheen
 */
public class JFXTreeTableRowSkin<T> extends TreeTableRowSkin<T> {

    static Map<Control, Double> disclosureWidthMap = null;

    // this vairable is used to hold the expanded/collapsed row index
    private static int expandedIndex = -1;
    // this variable indicates whether an expand/collapse operation is triggered
    private boolean expandTriggered = false;


    private ChangeListener<Boolean> expandedListener = (o, oldVal, newVal) -> {
        if (getSkinnable().getTreeItem() != null && !getSkinnable().getTreeItem().isLeaf()) {
            expandedIndex = getSkinnable().getIndex();
            expandTriggered = true;
        }
    };
    private Timeline collapsedAnimation;
    private Animation expandedAnimation;

    public JFXTreeTableRowSkin(TreeTableRow<T> control) {
        super(control);

        if (disclosureWidthMap == null) {
            try {
                Field declaredField = getClass().getSuperclass()
                    .getSuperclass()
                    .getDeclaredField("maxDisclosureWidthMap");
                declaredField.setAccessible(true);
                disclosureWidthMap = (Map<Control, Double>) declaredField.get(this);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                ex.printStackTrace();
            }
        }
        getSkinnable().indexProperty().addListener((o, oldVal, newVal) -> {
            if (newVal.intValue() != -1) {
                if (newVal.intValue() == expandedIndex) {
                    expandTriggered = true;
                    expandedIndex = -1;
                } else {
                    expandTriggered = false;
                }
            }
        });

        // clear disclosure node indentation after grouping/ungrouping
        getSkinnable().getTreeTableView().rootProperty().addListener((o, oldVal, newVal) -> {
            disclosureWidthMap.remove(getSkinnable().getTreeTableView());
        });
    }


    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        // allow custom skin to grouped rows
        getSkinnable().getStyleClass().remove("tree-table-row-group");
        if (getSkinnable().getTreeItem() != null && getSkinnable().getTreeItem()
            .getValue() instanceof RecursiveTreeObject && getSkinnable()
            .getTreeItem()
            .getValue()
            .getClass() == RecursiveTreeObject.class) {
            getSkinnable().getStyleClass().add("tree-table-row-group");
        }

        if (getSkinnable().getIndex() > -1 && getSkinnable().getTreeTableView()
            .getTreeItem(getSkinnable().getIndex()) != null) {
            super.layoutChildren(x, y, w, h);

            // disclosure row case
            if (getSkinnable().getTreeItem() != null && !getSkinnable().getTreeItem().isLeaf()) {
                Node arrow = ((Parent) getDisclosureNode()).getChildrenUnmodifiable().get(0);
                // relocating the disclosure node according to the grouping column
                final Parent arrowParent = arrow.getParent();
                if (((RecursiveTreeObject<?>) getSkinnable().getItem()).getGroupedColumn() != null) {
                    Node col = getChildren().get(getSkinnable().getTreeTableView()
                        .getTreeItemLevel(getSkinnable().getTreeItem()) + 1);
                    if (getSkinnable().getItem() instanceof RecursiveTreeObject) {
                        int index = getSkinnable().getTreeTableView()
                            .getColumns()
                            .indexOf(((RecursiveTreeObject<?>) getSkinnable().getItem()).getGroupedColumn());
                        //						getSkinnable().getTreeTableView().getColumns().get(index).getText();
                        col = getChildren().get(index + 1); // index + 2 , if the rippler was added
                    }
                    arrowParent.setTranslateX(col.getBoundsInParent().getMinX());
                    arrowParent.setLayoutX(0);
                } else {
                    arrowParent.setTranslateX(0);
                    arrowParent.setLayoutX(0);
                }

                // add disclosure node animation
                if (expandedAnimation == null || !(expandedAnimation.getStatus() == Status.RUNNING)) {
                    expandedAnimation = new Timeline(new KeyFrame(Duration.millis(160),
                        new KeyValue(arrow.rotateProperty(),
                            90,
                            Interpolator.EASE_BOTH)));
                    expandedAnimation.setOnFinished((finish) -> arrow.setRotate(90));
                }
                if (collapsedAnimation == null || !(collapsedAnimation.getStatus() == Status.RUNNING)) {
                    collapsedAnimation = new Timeline(new KeyFrame(Duration.millis(160),
                        new KeyValue(arrow.rotateProperty(),
                            0,
                            Interpolator.EASE_BOTH)));
                    collapsedAnimation.setOnFinished((finish) -> arrow.setRotate(0));
                }
                getSkinnable().getTreeItem().expandedProperty().removeListener(expandedListener);
                getSkinnable().getTreeItem().expandedProperty().addListener(expandedListener);

                if (expandTriggered) {
                    if (getSkinnable().getTreeTableView().getTreeItem(getSkinnable().getIndex()).isExpanded()) {
                        arrow.setRotate(0);
                        expandedAnimation.play();
                    } else {
                        arrow.setRotate(90);
                        collapsedAnimation.play();
                    }
                    expandTriggered = false;
                } else {
                    if (getSkinnable().getTreeTableView().getTreeItem(getSkinnable().getIndex()).isExpanded()) {
                        if (expandedAnimation.getStatus() != Status.RUNNING) {
                            arrow.setRotate(90);
                        }
                    } else {
                        if (collapsedAnimation.getStatus() != Status.RUNNING) {
                            arrow.setRotate(0);
                        }
                    }
                }
            }
        }

    }

    @Override
    protected double getIndentationPerLevel() {
        return super.getIndentationPerLevel();
    }
}
