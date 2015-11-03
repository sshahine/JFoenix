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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.skins;

import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.behavior.JFXTreeTableCellBehavior;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import com.sun.javafx.scene.control.skin.TableCellSkinBase;

public class JFXTreeTableCellSkin<S,T> extends TableCellSkinBase<TreeTableCell<S,T>, TreeTableCellBehavior<S,T>> {
    
//    private final TreeTableCellSkinWrapper<S, T> javaSkin;
	 private final TreeTableColumn<S,T> tableColumn;
    
    public JFXTreeTableCellSkin(TreeTableCell<S,T> treeTableCell) {
        super(treeTableCell, new JFXTreeTableCellBehavior<S,T>(treeTableCell));
//        javaSkin = new TreeTableCellSkinWrapper<>(treeTableCell);
        tableColumn = treeTableCell.getTableColumn();
        super.init(treeTableCell);         
    }

    @Override protected BooleanProperty columnVisibleProperty() {
        return tableColumn.visibleProperty();
    }

    @Override protected ReadOnlyDoubleProperty columnWidthProperty() {
        return tableColumn.widthProperty();
    }
    
    // compute the padding of disclosure node
    @Override protected double leftLabelPadding() {
        double leftPadding = super.leftLabelPadding();
        
        // RT-27167: we must take into account the disclosure node and the
        // indentation (which is not taken into account by the LabeledSkinBase.
        final double height = getCellSize();

        TreeTableCell<S,T> cell = getSkinnable();

        TreeTableColumn<S,T> tableColumn = cell.getTableColumn();
        if (tableColumn == null) return leftPadding;

        // check if this column is the TreeTableView treeColumn (i.e. the 
        // column showing the disclosure node and graphic).
        TreeTableView<S> treeTable = cell.getTreeTableView();
        if (treeTable == null) return leftPadding;

        int columnIndex = treeTable.getVisibleLeafIndex(tableColumn);

        TreeTableColumn<S,?> treeColumn = treeTable.getTreeColumn();
        if(!(treeTable instanceof JFXTreeTableView)){
	        if ((treeColumn == null && columnIndex != 0) || (treeColumn != null && ! tableColumn.equals(treeColumn))) {
	            return leftPadding;
	        }
        }else if(!((JFXTreeTableColumn)tableColumn).isGrouped()){
        	return leftPadding;
        }

        TreeTableRow<S> treeTableRow = cell.getTreeTableRow();
        if (treeTableRow == null) return leftPadding;

        TreeItem<S> treeItem = treeTableRow.getTreeItem();
        if (treeItem == null) return leftPadding;
        
//        int nodeLevel = treeTable.getTreeItemLevel(treeItem);
//        if (! treeTable.isShowRoot()) nodeLevel--;
//        double indentPerLevel = 10;
//        if (treeTableRow.getSkin() instanceof TreeTableRowSkin) {
//            indentPerLevel = ((TreeTableRowSkin<?>)treeTableRow.getSkin()).getIndentationPerLevel();
//        }
//        leftPadding += nodeLevel * indentPerLevel;

        // add in the width of the disclosure node, if one exists
        Map<Control, Double> mdwp = JFXTreeTableRowSkin.maxDisclosureWidthMap;
        leftPadding += mdwp.containsKey(treeTable) ? mdwp.get(treeTable) : 0;

        // adding in the width of the graphic on the tree item
        Node graphic = treeItem.getGraphic();
        leftPadding += graphic == null ? 0 : graphic.prefWidth(height);
        
        
        return leftPadding;
    }
//
//    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
//    	return javaSkin.tempComputePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
//    }
    
    /**
     * @author sshahine
     * this class is used to exploit the method of the parent class
     * 
     * @param <S>
     * @param <t>
     */
//    class  TreeTableCellSkinWrapper<S,t> extends TreeTableCellSkin<S, T>{
//
//		public TreeTableCellSkinWrapper(TreeTableCell<S, T> treeTableCell) {
//			super(treeTableCell);
//		}
//		
//		public double tempLeftLabelPadding(){
//			return super.leftLabelPadding();
//		}
//		
//		public double tempComputePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset){
//			return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
//		}
//
//		public BooleanProperty columnVisibleProperty() {
//	        return super.columnVisibleProperty();
//	    }
//
//		public ReadOnlyDoubleProperty columnWidthProperty() {
//	        return super.columnWidthProperty();
//	    }
//		
//    }
    
}
