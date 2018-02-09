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

import com.jfoenix.svg.SVGGlyph;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class JFXDefaultChip<T> extends JFXChip<T>{

    protected final HBox root;

    public JFXDefaultChip(JFXChipView<T> view, T item) {
        super(view, item);
        JFXButton closeButton = new JFXButton(null, new SVGGlyph());
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction((event) -> view.getChips().remove(item));

        String tagString = null;
        if (getItem() instanceof String) {
            tagString = (String) getItem();
        } else {
            tagString = view.getConverter().toString(getItem());
        }
        Label label = new Label(tagString);
        label.setWrapText(true);
        root = new HBox(label, closeButton);
        getChildren().setAll(root);
        label.setMaxWidth(100);
    }
}
