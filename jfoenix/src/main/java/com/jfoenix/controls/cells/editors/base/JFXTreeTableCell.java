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
import javafx.scene.control.Skin;
import javafx.scene.control.TreeTableCell;

/**
 * overrides the cell skin to be able to use the {@link com.jfoenix.controls.JFXTreeTableView JFXTreeTableView}
 * features such as grouping
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableCell<S, T> extends TreeTableCell<S, T> {
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXTreeTableCellSkin<S, T>(this);
    }
}
