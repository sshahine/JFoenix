/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
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
