package com.jfoenix.controls.cells.editors.base;

import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableCell;

import com.jfoenix.skins.JFXTreeTableCellSkin;

/**
 * @author sshahine
 *
 * @param <S>
 * @param <T>
 */

public class JFXTreeTableCell<S, T> extends TreeTableCell<S, T> {
	
    @Override protected Skin<?> createDefaultSkin() {
        return new JFXTreeTableCellSkin<S,T>(this);
    }
    
}
