package com.cctintl.c3dfx.controls;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

/**
 * @author sshahine
 *
 * @param <S>
 */

public class C3DTreeTableView<S> extends TreeTableView<S> {

	public C3DTreeTableView(TreeItem<S> root) {
		super(root);
		this.setRowFactory(new Callback<TreeTableView<S>, TreeTableRow<S>>() {
			@Override
			public TreeTableRow<S> call(TreeTableView<S> param) {
				return new C3DTreeTableRow<S>();
			}
		});				
	}
	
	public C3DTreeTableView() {
		super();
	}
	
}
