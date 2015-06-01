package com.cctintl.c3dfx.skins;

import javafx.scene.control.TreeTableView;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;

public class C3DTreeTableViewSkin<S> extends TreeTableViewSkin<S> {

	public C3DTreeTableViewSkin(TreeTableView<S> treeTableView) {
		super(treeTableView);
	}
	
    protected TableHeaderRow createTableHeaderRow() {
    	C3DTableHeaderRow c3dHeaderRow = new C3DTableHeaderRow(this);
    	return c3dHeaderRow.getHeaderRow();
    }

	
}
