package com.cctintl.c3dfx.controls;

import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableCell;

import com.cctintl.c3dfx.skins.C3DTreeTableCellSkin;

public class C3DTreeTableCell<S, T> extends TreeTableCell<S, T>{

	@Override protected Skin<?> createDefaultSkin() {
		return new C3DTreeTableCellSkin<S, T>(this);
	}

}
