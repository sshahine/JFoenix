package com.cctintl.c3dfx.controls;

import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableRow;
import com.cctintl.c3dfx.skins.C3DTreeTableRowSkin;

/**
 * @author sshahine
 *
 * @param <T>
 */

public class C3DTreeTableRow<T> extends TreeTableRow<T> {
	
	public C3DTreeTableRow() {
		super();		
	}
	
    @Override protected Skin<?> createDefaultSkin() {
    	return new C3DTreeTableRowSkin<T>(this);
    }
	
}
