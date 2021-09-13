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

package com.jfoenix.controls.cells.editors.base;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * <h1>Editor Builder</h1>
 * this a builder interface to create editors for treetableview/tableview cells
 * <p>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public interface EditorNodeBuilder<T> {
    /**
     * This method is called when the editor start editing the cell
     *
     * @return Nothing
     */
    void startEdit();

    /**
     * This method is called when the editor cancel editing the cell
     *
     * @return Nothing
     */
    void cancelEdit();

    /**
     * This method is called when the editor updates the visuals of the cell
     *
     * @param item the new item for the cell
     * @param empty whether or not this cell holds a value
     * @return Nothing
     */
    void updateItem(T item, boolean empty);

    /**
     * This method is will create the editor node to be displayed when
     * editing the cell
     *
     * @param value               current value of the cell
     * @param keyEventsHandler    keyboard events handler for the cell
     * @param focusChangeListener focus change listener for the cell
     * @return the editor node
     */
    Region createNode(T value, EventHandler<KeyEvent> keyEventsHandler, ChangeListener<Boolean> focusChangeListener);

    /**
     * This method is used to update the editor node to corresponde with
     * the new value of the cell
     *
     * @param value the new value of the cell
     * @return Nothing
     */
    void setValue(T value);

    /**
     * This method is used to get the current value from the editor node
     *
     * @return T the value of the editor node
     */
    T getValue();

    /**
     * This method will be called before committing the new value of the cell
     *
     * @return Nothing
     * @throws Exception
     */
    void validateValue() throws Exception;

    /**
     * set ui nodes to null for memory efficiency
     */
    void nullEditorNode();
}
