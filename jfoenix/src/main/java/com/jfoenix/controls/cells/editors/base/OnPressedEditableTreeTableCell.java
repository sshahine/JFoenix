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
