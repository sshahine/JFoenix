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
