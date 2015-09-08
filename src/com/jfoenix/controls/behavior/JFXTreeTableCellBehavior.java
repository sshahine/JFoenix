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
