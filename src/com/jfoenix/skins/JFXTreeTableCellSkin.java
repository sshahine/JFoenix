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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

/**
 * @author Shadi Shaheen
 *
 */
public class JFXTreeTableCellSkin<S,T> extends TableCellSkinBase<TreeTableCell<S,T>, TreeTableCellBehavior<S,T>> {
    
	 private final TreeTableColumn<S,T> tableColumn;
    
    public JFXTreeTableCellSkin(TreeTableCell<S,T> treeTableCell) {
        super(treeTableCell, new JFXTreeTableCellBehavior<S,T>(treeTableCell));        
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
        final double height = getCellSize();
        TreeTableColumn<S,T> tableColumn = getSkinnable().getTableColumn();
        if (tableColumn == null) return leftPadding;
        TreeTableView<S> treeTable = getSkinnable().getTreeTableView();
        if (treeTable == null) return leftPadding;
        int columnIndex = treeTable.getVisibleLeafIndex(tableColumn);

        TreeTableColumn<S,?> treeColumn = treeTable.getTreeColumn();
        if(!(treeTable instanceof JFXTreeTableView)){
	        if ((treeColumn == null && columnIndex != 0) || (treeColumn != null && ! tableColumn.equals(treeColumn))) {
	            return leftPadding;
	        }
        }else if(!((JFXTreeTableColumn<S,T>)tableColumn).isGrouped()){
        	return leftPadding;
        }

        if (getSkinnable().getTreeTableRow() == null) return leftPadding;
        TreeItem<S> treeItem = getSkinnable().getTreeTableRow().getTreeItem();
        if (treeItem == null)  return leftPadding;
        
        // add in the width of the disclosure node
        leftPadding += JFXTreeTableRowSkin.disclosureWidthMap.containsKey(treeTable) ? JFXTreeTableRowSkin.disclosureWidthMap.get(treeTable) : 0;
        // adding in the width of the graphic on the tree item
        leftPadding += treeItem.getGraphic() == null ? 0 : treeItem.getGraphic().prefWidth(height);
        
        return leftPadding;
    }
    
    /**
     * @author Shadi Shaheen
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
