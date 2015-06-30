package com.cctintl.c3dfx.controls.cells.editors.base;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import com.cctintl.c3dfx.controls.C3DTextField;
/**
 * Provides the basis for an editable table cell using a text field. Sub-classes can provide formatters for display and a
 * commitHelper to control when editing is committed.
 *
 * @author Shadi Shaheen
 */
public abstract class AbstractEditableTreeTableCell<S, T> extends TreeTableCell<S, T> {
	protected C3DTextField textField;
	public AbstractEditableTreeTableCell() {
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
	protected abstract void commitHelper(boolean losingFocus);
	/**
	 * Provides the string representation of the value of this cell when the cell is not being edited.
	 */
	protected abstract String getString();
	@Override
	public void startEdit() {
		super.startEdit();
		if (textField == null) {
			createTextField();
		}
		StackPane pane = new StackPane();
		pane.setStyle("-fx-padding:-10 -8 -10 -8");
		pane.getChildren().add(textField);
		setGraphic(pane);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textField.selectAll();
				textField.requestFocus();
			}
		});
	}
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getString());
		setContentDisplay(ContentDisplay.TEXT_ONLY);
		//Once the edit has been cancelled we no longer need the text field
		//so we mark it for cleanup here. Note though that you have to handle
		//this situation in the focus listener which gets fired at the end
		//of the editing.
		textField = null;
	}
	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setGraphic(textField);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						textField.selectAll();
						textField.requestFocus();
					}
				});
			} else {
				setText(getString());
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
		}
	}
	private void createTextField() {
		textField = new C3DTextField(getString());
		textField.setStyle("-fx-background-color:TRANSPARENT;");
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
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
						getTreeTableView().edit(getTreeTableView().getRow((TreeItem<S>) getItem()), nextColumn);
					}
				}
			}
		});
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				//This focus listener fires at the end of cell editing when focus is lost
				//and when enter is pressed (because that causes the text field to lose focus).
				//The problem is that if enter is pressed then cancelEdit is called before this
				//listener runs and therefore the text field has been cleaned up. If the
				//text field is null we don't commit the edit. This has the useful side effect
				//of stopping the double commit.
				if (!newValue && textField != null) {
					commitHelper(true);
				}
			}
		});
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