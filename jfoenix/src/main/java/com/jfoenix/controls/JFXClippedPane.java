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

import com.jfoenix.utils.JFXNodeUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * JFXClippedPane is a StackPane that clips its content if exceeding the pane bounds.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-06-02
 */
public class JFXClippedPane extends StackPane {

    private Region clip = new Region();

    public JFXClippedPane(){
        super();
        init();
    }

    public JFXClippedPane(Node... children) {
        super(children);
        init();
    }

    private void init() {
        setClip(clip);
        clip.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(2), Insets.EMPTY)));
        backgroundProperty().addListener(observable -> JFXNodeUtils.updateBackground(getBackground(), clip));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        clip.resizeRelocate(0,0,getWidth(), getHeight());
    }
}
