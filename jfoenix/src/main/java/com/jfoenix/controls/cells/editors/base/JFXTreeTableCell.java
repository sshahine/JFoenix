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
