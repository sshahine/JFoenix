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

import com.jfoenix.skins.JFXTreeTableCellSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.StackPane;

/**
 * overrides the cell skin to be able to use the {@link com.jfoenix.controls.JFXTreeTableView JFXTreeTableView}
 * features such as grouping
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableCell<S, T> extends TreeTableCell<S, T> {

    // Disclosure Node
    private ObjectProperty<Node> disclosureNode = new SimpleObjectProperty<Node>(this, "disclosureNode");

    public final void setDisclosureNode(Node value) { disclosureNodeProperty().set(value); }

    /**
     * Returns the current disclosure node set in this cell.
     */
    public final Node getDisclosureNode() {
        if (disclosureNode.get() == null) {
            final StackPane disclosureNode = new StackPane();
            disclosureNode.getStyleClass().setAll("tree-disclosure-node");
            disclosureNode.setMouseTransparent(true);

            final StackPane disclosureNodeArrow = new StackPane();
            disclosureNodeArrow.getStyleClass().setAll("arrow");
            disclosureNode.getChildren().add(disclosureNodeArrow);
            setDisclosureNode(disclosureNode);
        }
        return disclosureNode.get();
    }

    public final ObjectProperty<Node> disclosureNodeProperty() { return disclosureNode; }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXTreeTableCellSkin<>(this);
    }
}
