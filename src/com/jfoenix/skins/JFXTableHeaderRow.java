/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.skins;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

public class JFXTableHeaderRow {

	private TableViewSkinBase tableSkin;
	private TableHeaderRow headerRow;
	
	public JFXTableHeaderRow(final TableViewSkinBase skin) {
		this.tableSkin = skin;
		headerRow = new TableHeaderRow(skin){
			 // protected to allow subclasses to provide a custom root header
		    protected NestedTableColumnHeader createRootHeader() {
		        return new JFXNestedTableColumnHeader(tableSkin, null);
		    }
		};
	}
    
	public TableHeaderRow getHeaderRow(){
		return headerRow;
	}
	
}
