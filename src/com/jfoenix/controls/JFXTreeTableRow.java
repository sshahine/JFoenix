package com.jfoenix.controls;

import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableRow;

import com.jfoenix.skins.JFXTreeTableRowSkin;

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
    	return new JFXTreeTableRowSkin<T>(this);
    }
	
}
