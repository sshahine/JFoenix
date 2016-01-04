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
package com.jfoenix.controls.datamodels.treetable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableColumn;

/**
 * @author Shadi Shaheen
 *
 * @param <T> is the concrete object of the Tree table
 */
public class RecursiveTreeObject<T> {

	ObservableList<T> children = FXCollections.observableArrayList();
	
	public ObservableList<T> getChildren(){
		return children;
	}	
	
	ObjectProperty<TreeTableColumn<T, ?>> groupedColumn = new SimpleObjectProperty<>();

	public final ObjectProperty<TreeTableColumn<T, ?>> groupedColumnProperty() {
		return this.groupedColumn;
	}

	public final TreeTableColumn<T, ?> getGroupedColumn() {
		return this.groupedColumnProperty().get();
	}

	public final void setGroupedColumn(final TreeTableColumn<T, ?> groupedColumn) {
		this.groupedColumnProperty().set(groupedColumn);
	}
	
	
	ObjectProperty<Object> groupedValue = new SimpleObjectProperty<>();

	public final ObjectProperty<Object> groupedValueProperty() {
		return this.groupedValue;
	}

	public final java.lang.Object getGroupedValue() {
		return this.groupedValueProperty().get();
	}

	public final void setGroupedValue(final java.lang.Object groupedValue) {
		this.groupedValueProperty().set(groupedValue);
	}
	
	
}
