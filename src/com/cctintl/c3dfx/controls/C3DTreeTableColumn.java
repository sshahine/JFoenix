package com.cctintl.c3dfx.controls;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class C3DTreeTableColumn<S,T> extends TreeTableColumn<S,T> {

	public C3DTreeTableColumn(String text) {
		super(text);
		this.setCellFactory(new Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>() {
			@Override
			public TreeTableCell<S, T> call(TreeTableColumn<S, T> param) {
				return new C3DTreeTableCell<S, T>();
			}
		});
	}
}
