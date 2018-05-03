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

package com.jfoenix.controls;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.css.PseudoClass;
import javafx.scene.control.TreeTableRow;

/**
 * JFXTreeTableRow is the row object used in {@link JFXTreeTableView}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableRow<T> extends TreeTableRow<T> {

    private static final PseudoClass groupedClass = PseudoClass.getPseudoClass("grouped");

    /**
     * {@inheritDoc}
     */
    public JFXTreeTableRow() {
        // allow custom skin to grouped rows
        itemProperty().addListener(observable -> {
            T item = getItem();
            pseudoClassStateChanged(groupedClass, item != null
                                                  && item instanceof RecursiveTreeObject
                                                  && item.getClass() == RecursiveTreeObject.class);
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setDisclosureNode(null);
    }
}
