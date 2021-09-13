/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.controls.cells.editors.base;

import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

public class OnPressedEditableTableCell<S, T> extends GenericEditableTableCell<S, T> {
    public OnPressedEditableTableCell() {
        init();
    }

    public OnPressedEditableTableCell(EditorNodeBuilder builder) {
        super(builder);
        init();
    }

    public OnPressedEditableTableCell(EditorNodeBuilder builder, Consumer<Exception> exConsumer) {
        super(builder, exConsumer);
        init();
    }

    private void init() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, click -> {
            if (getTableView().getEditingCell() != null && !isEditing()) {
                OnPressedEditableTableCell editingCell =
                    (OnPressedEditableTableCell) getTableView().getProperties().remove(OnPressedEditableTableCell.class);
                if (editingCell != null) {
                    editingCell.commitHelper(true);
                }
            }
        });
        addEventFilter(MouseEvent.MOUSE_RELEASED, click -> {
            if (!isEmpty() && isEditable() && !isEditing() && getTableColumn().isEditable()) {
                getTableView().edit(getIndex(), getTableColumn());
            }
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (isEditing()) {
            getTableView().getProperties().put(OnPressedEditableTableCell.class, this);
        }
    }

    @Override
    public void commitEdit(T newValue) {
        super.commitEdit(newValue);
        getTableView().getProperties().remove(OnPressedEditableTableCell.class);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        getTableView().getProperties().remove(OnPressedEditableTableCell.class);
    }
}
