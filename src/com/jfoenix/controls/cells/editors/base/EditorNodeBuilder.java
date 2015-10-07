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

package com.jfoenix.controls.cells.editors.base;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * @author sshahine
 * this is editor builder to be used to edit treetableview/tableview cells
 * @param <T>
 */
public interface EditorNodeBuilder<T> {
	public void startEdit();
	public void cancelEdit();
	public void updateItem(T item, boolean empty);
	public Region createNode(T value, DoubleBinding minWidthBinding , EventHandler<KeyEvent> keyEventsHandler, ChangeListener<Boolean> focusChangeListener);
	public void setValue(T value);
	public T getValue();
	
	/*
	 * called before committing the new value
	 */
	public void validateValue() throws Exception;
}
