package com.cctintl.c3dfx.skins;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;

import com.cctintl.c3dfx.controls.behavior.C3DTreeTableCellBehavior;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import com.sun.javafx.scene.control.skin.TableCellSkinBase;
import com.sun.javafx.scene.control.skin.TreeTableCellSkin;

public class C3DTreeTableCellSkin<S,T> extends TableCellSkinBase<TreeTableCell<S,T>, TreeTableCellBehavior<S,T>> {
    
//    private final TreeTableCellSkinWrapper<S, T> javaSkin;
	 private final TreeTableColumn<S,T> tableColumn;
    
    public C3DTreeTableCellSkin(TreeTableCell<S,T> treeTableCell) {
        super(treeTableCell, new C3DTreeTableCellBehavior<S,T>(treeTableCell));
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

//    @Override protected double leftLabelPadding() {
//    	return javaSkin.tempLeftLabelPadding();
//    }
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
    class  TreeTableCellSkinWrapper<S,t> extends TreeTableCellSkin<S, T>{

		public TreeTableCellSkinWrapper(TreeTableCell<S, T> treeTableCell) {
			super(treeTableCell);
		}
		
		public double tempLeftLabelPadding(){
			return super.leftLabelPadding();
		}
		
		public double tempComputePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset){
			return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
		}

		public BooleanProperty columnVisibleProperty() {
	        return super.columnVisibleProperty();
	    }

		public ReadOnlyDoubleProperty columnWidthProperty() {
	        return super.columnWidthProperty();
	    }
		
    }
    
}
