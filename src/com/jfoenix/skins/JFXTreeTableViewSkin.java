package com.jfoenix.skins;

import javafx.scene.control.TreeTableView;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;

public class JFXTreeTableViewSkin<S> extends TreeTableViewSkin<S> {

	public JFXTreeTableViewSkin(TreeTableView<S> treeTableView) {
		super(treeTableView);
	}
	
    protected TableHeaderRow createTableHeaderRow() {
    	JFXTableHeaderRow c3dHeaderRow = new JFXTableHeaderRow(this);
    	return c3dHeaderRow.getHeaderRow();
    }

	
}
