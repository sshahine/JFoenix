/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jfoenix.controls.behavior;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;

public class JFXTreeTableCellBehavior<S,T> extends TreeTableCellBehavior<S,T>{

	public JFXTreeTableCellBehavior(TreeTableCell<S, T> control) {
		super(control);
	}

	@Override protected boolean handleDisclosureNode(double x, double y) {
        final TreeItem<S> treeItem = getControl().getTreeTableRow().getTreeItem();

//        final TreeTableView<S> treeTableView = getControl().getTreeTableView();
//        final TreeTableColumn<S,T> column = getTableColumn();
//        final TreeTableColumn<S,?> treeColumn = treeTableView.getTreeColumn() == null ?
//                treeTableView.getVisibleLeafColumn(0) : treeTableView.getTreeColumn();

         if(!treeItem.isLeaf()){
//        if (column == treeColumn) {
            final Node disclosureNode = getControl().getTreeTableRow().getDisclosureNode();
            if (disclosureNode != null) {  
                if (disclosureNode.getBoundsInParent().contains(x+disclosureNode.getTranslateX(), y)) {
                    if (treeItem != null) {
                        treeItem.setExpanded(!treeItem.isExpanded());
                    }
                    return true;
                }
            }
        }
            
        return false;
    }
	
	
}
