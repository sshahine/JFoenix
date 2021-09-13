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

package com.jfoenix.skins;

import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.scene.control.skin.TreeTableCellSkin;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableCellSkin<S, T>  extends TreeTableCellSkin<S, T> {

    public JFXTreeTableCellSkin(TreeTableCell<S, T> treeTableCell) {
        super(treeTableCell);
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
            final S value = item == null ? null : item.getValue();
            boolean disclosureVisible = value != null
                                        && !item.isLeaf()
                                        && value instanceof RecursiveTreeObject
                                        && ((RecursiveTreeObject) value).getGroupedColumn() == getSkinnable().getTableColumn();
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
        double disclosureWidth = 0;
        Node disclosureNode = ((JFXTreeTableCell<S, T>) getSkinnable()).getDisclosureNode();
        if (disclosureNode.isVisible()) {
            Pos alignment = getSkinnable().getAlignment();
            alignment = alignment == null ? Pos.CENTER_LEFT : alignment;
            layoutInArea(disclosureNode, x + 8, y, w, h, 0, Insets.EMPTY, false, false, HPos.LEFT, VPos.CENTER);
            disclosureWidth = disclosureNode.getLayoutBounds().getWidth() + 18;
        }
        super.layoutChildren(x + disclosureWidth, y, w - disclosureWidth, h);
    }
}
