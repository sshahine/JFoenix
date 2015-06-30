package com.cctintl.c3dfx.controls.cells.editors;

import com.cctintl.c3dfx.controls.cells.editors.base.AbstractEditableTreeTableCell;

public class EditableTreeTableCell<S extends Object, T extends String> extends AbstractEditableTreeTableCell<S, T> {
	public EditableTreeTableCell() {
	}
	@Override
	protected String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
	@Override
	protected void commitHelper( boolean losingFocus ) {
		commitEdit(((T) textField.getText()));
	}
	
}