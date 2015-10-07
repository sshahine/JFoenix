/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
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
