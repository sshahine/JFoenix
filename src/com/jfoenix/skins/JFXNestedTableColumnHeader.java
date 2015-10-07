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

import javafx.scene.control.TableColumnBase;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

public class JFXNestedTableColumnHeader extends NestedTableColumnHeader {


	public JFXNestedTableColumnHeader(TableViewSkinBase skin, TableColumnBase tc) {
		super(skin, tc);
	}

    // protected to allow subclasses to customise the column header types
    protected TableColumnHeader createTableColumnHeader(TableColumnBase col) {
        return col.getColumns().isEmpty() ?
                new JFXTableColumnHeader(getTableViewSkin(), col) :
                new NestedTableColumnHeader(getTableViewSkin(), col);
    }
    
    
}
