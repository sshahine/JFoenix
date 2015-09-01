package com.cctintl.jfx.controls;

import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableRow;

import com.cctintl.jfx.skins.C3DTreeTableRowSkin;

/**
 * @author sshahine
 *
 * @param <T>
 */

public class JFXTreeTableRow<T> extends TreeTableRow<T> {
	
	public JFXTreeTableRow() {
		super();		
	}
	
    @Override protected Skin<?> createDefaultSkin() {
    	return new C3DTreeTableRowSkin<T>(this);
    }
	
}
