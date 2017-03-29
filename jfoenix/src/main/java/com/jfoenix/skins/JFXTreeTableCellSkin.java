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
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import com.sun.javafx.scene.control.skin.TableCellSkinBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
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

        // getTreeItemLevel ignore the group nodes level
        treeColumn = treeTable.getTreeColumn() == null ? treeTable.getVisibleLeafColumn(0) : treeTable.getTreeColumn();
        if (tableColumn == treeColumn) {
            int nodeLevel = treeTable.getTreeItemLevel(treeItem);
            if (!treeTable.isShowRoot()) {
                nodeLevel--;
            }
            double indentPerLevel = 10;
            if (treeTableRow.getSkin() instanceof JFXTreeTableRowSkin) {
                indentPerLevel = ((JFXTreeTableRowSkin<?>) treeTableRow.getSkin()).getIndentationPerLevel();
            }
            leftPadding += nodeLevel * indentPerLevel;
        }

        if (tableColumn == treeColumn || ((JFXTreeTableColumn<S, T>) tableColumn).isGrouped()) {
            // add in the width of the disclosure node
            if (JFXTreeTableRowSkin.disclosureWidthMap != null && JFXTreeTableRowSkin.disclosureWidthMap.containsKey(
                treeTable)) {
                leftPadding += JFXTreeTableRowSkin.disclosureWidthMap.get(treeTable);
            }
        }
        // adding in the width of the graphic on the tree item
        leftPadding += treeItem.getGraphic() == null ? 0 : treeItem.getGraphic().prefWidth(height);

        return leftPadding;
    }

}
