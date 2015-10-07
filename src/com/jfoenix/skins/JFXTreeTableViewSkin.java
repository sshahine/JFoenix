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
