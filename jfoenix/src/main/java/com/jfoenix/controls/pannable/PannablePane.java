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

package com.jfoenix.controls.pannable;

import com.jfoenix.controls.pannable.base.IPannablePane;
import com.jfoenix.controls.pannable.gestures.PanningGestures;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/**
 * Simple pannable pane implementation
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2020-04-30
 */
public class PannablePane extends Pane implements IPannablePane {

    private Scale scale = new Scale(1, 1, 0, 0);

    public PannablePane() {
        getTransforms().add(scale);
        scale.yProperty().bind(scale.xProperty());
    }

    @Override
    public double getScale() {
        return scale.getX();
    }

    @Override
    public void setScale(double scale) {
        this.scale.setX(scale);
    }

    @Override
    public DoubleProperty scaleProperty() {
        return scale.xProperty();
    }

    public static PannablePane wrap(Node... children) {
        PannablePane canvas = new PannablePane();
        PanningGestures.attachViewPortGestures(canvas);
        canvas.getChildren().setAll(children);
        return canvas;
    }
}

