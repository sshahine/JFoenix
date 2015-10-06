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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jfoenix.controls.datamodels.treetable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableColumn;

/**
 * @author sshahine
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
