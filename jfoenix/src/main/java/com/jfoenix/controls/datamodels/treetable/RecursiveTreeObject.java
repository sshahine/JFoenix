/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.controls.datamodels.treetable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableColumn;

/**
 * data model that is used in JFXTreeTableView, it's used to implement
 * the grouping feature.
 * <p>
 * <b>Note:</b> the data object used in JFXTreeTableView <b>must</b> extends this class
 *
 * @param <T> is the concrete object of the Tree table
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class RecursiveTreeObject<T> {

    /**
     * grouped children objects
     */
    ObservableList<T> children = FXCollections.observableArrayList();

    public ObservableList<T> getChildren() {
        return children;
    }

    public void setChildren(ObservableList<T> children) {
        this.children = children;
    }

    /**
     * Whether or not the object is grouped by a specified tree table column
     */
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

    /**
     * the value that must be shown when grouped
     */
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
