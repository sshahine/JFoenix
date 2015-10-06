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
package com.jfoenix.controls.cells.editors.base;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
/**
 * Provides the basis for an editable table cell using a text field. Sub-classes can provide formatters for display and a
 * commitHelper to control when editing is committed.
 *
 * @author Shadi Shaheen
 */
public class GenericEditableTreeTableCell<S, T> extends JFXTreeTableCell<S, T> {
	protected EditorNodeBuilder builder;
	protected Region editorNode;

	public GenericEditableTreeTableCell(EditorNodeBuilder builder) {
		this.builder = builder;
	}

	public GenericEditableTreeTableCell() {
		builder = new TextFieldEditorBuilder();
	}
	/**
	 * Any action attempting to commit an edit should call this method rather than commit the edit directly itself. This
	 * method will perform any validation and conversion required on the value. For text values that normally means this
	 * method just commits the edit but for numeric values, for example, it may first parse the given input. <p> The only
	 * situation that needs to be treated specially is when the field is losing focus. If you user hits enter to commit the
	 * cell with bad data we can happily cancel the commit and force them to enter a real value. If they click away from the
	 * cell though we want to give them their old value back.
	 *
	 * @param losingFocus true if the reason for the call was because the field is losing focus.
	 */
	protected void commitHelper( boolean losingFocus ) {
		if( editorNode == null ) return;
		try {
			builder.validateValue();
			commitEdit(((T) builder.getValue()));
		} catch (Exception ex) {
			//Most of the time we don't mind if there is a parse exception as it
			//indicates duff user data but in the case where we are losing focus
			//it means the user has clicked away with bad data in the cell. In that
			//situation we want to just cancel the editing and show them the old
			//value.
			if( losingFocus ) {
				cancelEdit();
			}
		}

	}
	/**
	 * Provides the string representation of the value of this cell when the cell is not being edited.
	 */
	protected Object getValue(){
		return getItem() == null ? "" : getItem();
	}

	@Override
	public void startEdit() {
		if(checkGroupedColumn()){
			super.startEdit();
			if (editorNode == null) {
				createEditorNode();
			}
			builder.startEdit();
			setGraphic(editorNode);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);			
		}

		//		StackPane pane = new StackPane();
		//		pane.setStyle("-fx-padding:-10 -8 -10 -8");
		//		pane.getChildren().add(textField);
		//		Platform.runLater(new Runnable() {
		//			@Override
		//			public void run() {
		//				textField.selectAll();
		//				textField.requestFocus();
		//			}
		//		});
	}


	@Override
	public void cancelEdit() {
		super.cancelEdit();
		builder.cancelEdit();
		builder.setValue(getValue());
		setContentDisplay(ContentDisplay.TEXT_ONLY);
		//Once the edit has been cancelled we no longer need the editor
		//so we mark it for cleanup here. Note though that you have to handle
		//this situation in the focus listener which gets fired at the end
		//of the editing.
		editorNode = null;
	}
	
	private boolean checkGroupedColumn(){
		boolean allowEdit = true;
		if(getTreeTableRow().getTreeItem()!=null){
			Object rowObject = getTreeTableRow().getTreeItem().getValue();
			if(rowObject instanceof RecursiveTreeObject && rowObject.getClass() == RecursiveTreeObject.class){
				allowEdit = false;
			}else{
				// check grouped columns in the tableview
				if(getTreeTableView() instanceof JFXTreeTableView && ((JFXTreeTableView)getTreeTableView()).getGroupOrder().contains(getTableColumn()))
					allowEdit = false;
			}
		}
		return allowEdit;
	}
	
	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing() && checkGroupedColumn()) {

				if (editorNode != null) {
					builder.setValue(getValue());
				}
				setGraphic(editorNode);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				builder.updateItem(item, empty);
				//				Platform.runLater(new Runnable() {
				//					@Override
				//					public void run() {
				//						textField.selectAll();
				//						textField.requestFocus();
				//					}
				//				});
			} else {
				Object value = getValue();
				if(value instanceof Node) {
					setGraphic((Node) value);
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				} else {
					setText(value.toString());
					setContentDisplay(ContentDisplay.TEXT_ONLY);
				}
			}
		}
	}

	private void createEditorNode() {	

		EventHandler<KeyEvent> keyEventsHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitHelper(false);
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				} else if (t.getCode() == KeyCode.TAB) {
					commitHelper(false);

					TreeTableColumn nextColumn = getNextColumn(!t.isShiftDown());
					if (nextColumn != null) {
						getTreeTableView().edit(getIndex(), nextColumn);
					}
				}
			}
		};

		ChangeListener<Boolean> focusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				//This focus listener fires at the end of cell editing when focus is lost
				//and when enter is pressed (because that causes the text field to lose focus).
				//The problem is that if enter is pressed then cancelEdit is called before this
				//listener runs and therefore the text field has been cleaned up. If the
				//text field is null we don't commit the edit. This has the useful side effect
				//of stopping the double commit.
				if (!newValue && editorNode != null) {
					commitHelper(true);
				}
			}
		};
		DoubleBinding minWidthBinding = Bindings.createDoubleBinding(()->{
			return this.getWidth() - this.getGraphicTextGap()*2 - this.getBaselineOffset() ;
		}, this.widthProperty(), this.graphicTextGapProperty());
		editorNode = builder.createNode(getValue(), minWidthBinding, keyEventsHandler, focusChangeListener);
	}
	/**
	 *
	 * @param forward true gets the column to the right, false the column to the left of the current column
	 * @return
	 */
	private TreeTableColumn<S, ?> getNextColumn(boolean forward) {
		List<TreeTableColumn<S, ?>> columns = new ArrayList<>();
		for (TreeTableColumn<S, ?> column : getTreeTableView().getColumns()) {
			columns.addAll(getLeaves(column));
		}
		//There is no other column that supports editing.
		if (columns.size() < 2) {
			return null;
		}
		int currentIndex = columns.indexOf(getTableColumn());
		int nextIndex = currentIndex;
		if (forward) {
			nextIndex++;
			if (nextIndex > columns.size() - 1) {
				nextIndex = 0;
			}
		} else {
			nextIndex--;
			if (nextIndex < 0) {
				nextIndex = columns.size() - 1;
			}
		}
		return columns.get(nextIndex);
	}


	private List<TreeTableColumn<S, ?>> getLeaves(TreeTableColumn<S, ?> root) {
		List<TreeTableColumn<S, ?>> columns = new ArrayList<>();
		if (root.getColumns().isEmpty()) {
			//We only want the leaves that are editable.
			if (root.isEditable()) {
				columns.add(root);
			}
			return columns;
		} else {
			for (TreeTableColumn<S, ?> column : root.getColumns()) {
				columns.addAll(getLeaves(column));
			}
			return columns;
		}
	}
}