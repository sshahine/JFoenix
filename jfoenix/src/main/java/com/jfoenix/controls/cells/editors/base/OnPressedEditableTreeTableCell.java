/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
