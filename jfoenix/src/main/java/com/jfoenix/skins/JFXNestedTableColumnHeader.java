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

package com.jfoenix.skins;

import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.TableColumnBase;

/**
 * @author Shadi Shaheen
 */
public class JFXNestedTableColumnHeader extends NestedTableColumnHeader {


    public JFXNestedTableColumnHeader(TableViewSkinBase skin, TableColumnBase tc) {
        super(skin, tc);
    }

    // protected to allow subclasses to customise the column header types
    protected TableColumnHeader createTableColumnHeader(TableColumnBase col) {
        return col.getColumns().isEmpty() ?
            new JFXTableColumnHeader(getTableViewSkin(), col) :
            new NestedTableColumnHeader(getTableViewSkin(), col);
    }


}
