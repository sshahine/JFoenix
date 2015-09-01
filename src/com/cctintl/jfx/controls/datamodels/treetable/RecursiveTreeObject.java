package com.cctintl.jfx.controls.datamodels.treetable;

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
