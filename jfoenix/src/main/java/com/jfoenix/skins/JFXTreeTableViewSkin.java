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

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;
import javafx.scene.control.TreeTableView;

/**
 * @author Shadi Shaheen
 */
public class JFXTreeTableViewSkin<S> extends TreeTableViewSkin<S> {

    public JFXTreeTableViewSkin(TreeTableView<S> treeTableView) {
        super(treeTableView);
    }

    protected TableHeaderRow createTableHeaderRow() {
        JFXTableHeaderRow jfxHeaderRow = new JFXTableHeaderRow(this);
        return jfxHeaderRow.getHeaderRow();
    }


}
