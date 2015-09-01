package com.cctintl.jfx.skins;

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
