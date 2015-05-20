package com.cctintl.c3dfx.skins;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

public class C3DTableHeaderRow {

	private TableViewSkinBase tableSkin;
	private TableHeaderRow headerRow;
	
	public C3DTableHeaderRow(final TableViewSkinBase skin) {
		this.tableSkin = skin;
		headerRow = new TableHeaderRow(skin){
			 // protected to allow subclasses to provide a custom root header
		    protected NestedTableColumnHeader createRootHeader() {
		        return new C3DNestedTableColumnHeader(tableSkin, null);
		    }
		};
	}
    
	public TableHeaderRow getHeaderRow(){
		return headerRow;
	}
	
}
