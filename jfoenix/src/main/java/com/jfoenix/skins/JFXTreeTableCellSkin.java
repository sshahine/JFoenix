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

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.behavior.JFXTreeTableCellBehavior;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import com.sun.javafx.scene.control.skin.TableCellSkinBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableCellSkin<S, T> extends TableCellSkinBase<TreeTableCell<S, T>, TreeTableCellBehavior<S, T>> {

    private final TreeTableColumn<S, T> tableColumn;

    public JFXTreeTableCellSkin(TreeTableCell<S, T> treeTableCell) {
        super(treeTableCell, new JFXTreeTableCellBehavior<>(treeTableCell));
        tableColumn = treeTableCell.getTableColumn();
        super.init(treeTableCell);
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        updateDisclosureNode();
    }

    private void updateDisclosureNode() {
        Node disclosureNode = ((JFXTreeTableCell<S, T>) getSkinnable()).getDisclosureNode();
        if (disclosureNode != null) {
            TreeItem<S> item = getSkinnable().getTreeTableRow().getTreeItem();
            boolean disclosureVisible = item != null && !item.isLeaf()
                                        && item.getValue() != null
                                        && ((RecursiveTreeObject) item.getValue()).getGroupedColumn() == tableColumn;
            disclosureNode.setVisible(disclosureVisible);

            if (!disclosureVisible) {
                getChildren().remove(disclosureNode);
            } else if (disclosureNode.getParent() == null) {
                getChildren().add(disclosureNode);
                disclosureNode.toFront();
            } else {
                disclosureNode.toBack();
            }
            if (disclosureNode.getScene() != null) {
                disclosureNode.applyCss();
            }
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        updateDisclosureNode();
        Node disclosureNode = ((JFXTreeTableCell<S, T>) getSkinnable()).getDisclosureNode();
        if (disclosureNode.isVisible()) {
            Pos alighnment = getSkinnable().getAlignment();
            alighnment = alighnment == null ? Pos.CENTER_LEFT : alighnment;
            layoutInArea(disclosureNode, x + 8, y, w, h, 0, Insets.EMPTY, false, false, HPos.LEFT, VPos.CENTER);
        }
        super.layoutChildren(x, y, w, h);
    }

    @Override
    protected BooleanProperty columnVisibleProperty() {
        return tableColumn.visibleProperty();
    }

    @Override
    protected ReadOnlyDoubleProperty columnWidthProperty() {
        return tableColumn.widthProperty();
    }

    // compute the padding of disclosure node
    @Override
    protected double leftLabelPadding() {
        double leftPadding = super.leftLabelPadding();
        final double height = getCellSize();
        TreeTableColumn<S, T> tableColumn = getSkinnable().getTableColumn();
        if (tableColumn == null) {
            return leftPadding;
        }
        TreeTableView<S> treeTable = getSkinnable().getTreeTableView();
        if (treeTable == null) {
            return leftPadding;
        }
        int columnIndex = treeTable.getVisibleLeafIndex(tableColumn);

        TreeTableColumn<S, ?> treeColumn = treeTable.getTreeColumn();
        if (!(treeTable instanceof JFXTreeTableView)) {
            if ((treeColumn == null && columnIndex != 0) || (treeColumn != null && !tableColumn.equals(treeColumn))) {
                return leftPadding;
            }
        }

        TreeTableRow<S> treeTableRow = getSkinnable().getTreeTableRow();
        if (treeTableRow == null) {
            return leftPadding;
        }
        TreeItem<S> treeItem = getSkinnable().getTreeTableRow().getTreeItem();
        if (treeItem == null) {
            return leftPadding;
        }

        Node disclosureNode = ((JFXTreeTableCell<S, T>) getSkinnable()).getDisclosureNode();
        if (((JFXTreeTableColumn) tableColumn).isGrouped()) {
            leftPadding += disclosureNode.prefWidth(-1) + 18;
        }

        // adding in the width of the graphic on the tree item
        leftPadding += treeItem.getGraphic() == null ? 0 : treeItem.getGraphic().prefWidth(height);

        return leftPadding;
    }

}
