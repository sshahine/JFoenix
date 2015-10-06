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
package com.jfoenix.controls;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;


/**
 * @author sshahine
 *
 * @param <S>
 * @param <T>
 */

public class JFXTreeTableColumn<S, T> extends TreeTableColumn<S, T> {

	public JFXTreeTableColumn() {
		super();
		init();
	}
	
	public JFXTreeTableColumn(String text){
		super(text);
		init();
	}
	
	private void init(){
		this.setCellFactory(new Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>() {
			@Override
			public TreeTableCell<S, T> call(TreeTableColumn<S, T> param) {
				return new JFXTreeTableCell<S, T>(){
					 @Override protected void updateItem(T item, boolean empty) {
		                    if (item == getItem()) return;
		                    super.updateItem(item, empty);
		                    if (item == null) {
		                        super.setText(null);
		                        super.setGraphic(null);
		                    } else if (item instanceof Node) {
		                        super.setText(null);
		                        super.setGraphic((Node)item);
		                    } else {
		                        super.setText(item.toString());
		                        super.setGraphic(null);
		                    }
		                }
				};
			}
		});
	}
	
	public final boolean validateValue(CellDataFeatures<S, T> param){
		Object rowObject = param.getValue().getValue();
		if((rowObject instanceof RecursiveTreeObject && rowObject.getClass() == RecursiveTreeObject.class)
		|| (param.getTreeTableView() instanceof JFXTreeTableView && ((JFXTreeTableView<?>)param.getTreeTableView()).getGroupOrder().contains(this)))
			return false;
		return true;
	}
	
	public final ObservableValue<T> getComputedValue(CellDataFeatures<S, T> param){
		Object rowObject = param.getValue().getValue();
		if(rowObject instanceof RecursiveTreeObject){
			RecursiveTreeObject<?> item = (RecursiveTreeObject<?>) rowObject;
			if(item.getGroupedColumn() == this)
				return new ReadOnlyObjectWrapper(item.getGroupedValue());
		}
		return null;
	}
	
}
