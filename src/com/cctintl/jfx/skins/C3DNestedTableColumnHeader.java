package com.cctintl.jfx.skins;

import javafx.scene.control.TableColumnBase;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

public class C3DNestedTableColumnHeader extends NestedTableColumnHeader {


	public C3DNestedTableColumnHeader(TableViewSkinBase skin, TableColumnBase tc) {
		super(skin, tc);
	}

    // protected to allow subclasses to customise the column header types
    protected TableColumnHeader createTableColumnHeader(TableColumnBase col) {
        return col.getColumns().isEmpty() ?
                new C3DTableColumnHeader(getTableViewSkin(), col) :
                new NestedTableColumnHeader(getTableViewSkin(), col);
    }
    
    
}
