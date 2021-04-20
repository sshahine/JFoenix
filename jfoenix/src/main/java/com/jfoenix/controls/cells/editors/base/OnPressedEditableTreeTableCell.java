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

public class OnPressedEditableTreeTableCell<S, T> extends GenericEditableTreeTableCell<S, T> {
    public OnPressedEditableTreeTableCell() {
        init();
    }

    public OnPressedEditableTreeTableCell(EditorNodeBuilder builder) {
        super(builder);
        init();
    }

    public OnPressedEditableTreeTableCell(EditorNodeBuilder builder, Consumer<Exception> exConsumer) {
        super(builder, exConsumer);
        init();
    }

    private void init() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, click -> {
            if (getTreeTableView().getEditingCell() != null && !isEditing()) {
                OnPressedEditableTreeTableCell editingCell =
                    (OnPressedEditableTreeTableCell) getTreeTableView().getProperties().remove(OnPressedEditableTreeTableCell.class);
                if (editingCell != null) {
                    editingCell.commitHelper(true);
                }
            }
        });
        addEventFilter(MouseEvent.MOUSE_RELEASED, click -> {
            if (!isEmpty() && isEditable() && !isEditing() && getTableColumn().isEditable()) {
                getTreeTableView().edit(getIndex(), getTableColumn());
            }
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (isEditing()) {
            getTreeTableView().getProperties().put(OnPressedEditableTreeTableCell.class, this);
        }
    }

    @Override
    public void commitEdit(T newValue) {
        super.commitEdit(newValue);
        getTreeTableView().getProperties().remove(OnPressedEditableTreeTableCell.class);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        getTreeTableView().getProperties().remove(OnPressedEditableTreeTableCell.class);
    }
}
