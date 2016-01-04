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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * @author Shadi Shaheen
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
