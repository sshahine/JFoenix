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
