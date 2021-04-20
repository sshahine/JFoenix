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

package com.jfoenix.controls.base;

import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.paint.Paint;

/**
 * this class is created for internal use only, to remove duplication between text input controls
 * skins
 *
 * Created by sshahine on 7/14/2017.
 */
public interface IFXLabelFloatControl extends IFXValidatableControl, IFXStaticControl {

    StyleableBooleanProperty labelFloatProperty();

    boolean isLabelFloat();

    void setLabelFloat(boolean labelFloat);

    Paint getUnFocusColor();

    StyleableObjectProperty<Paint> unFocusColorProperty();

    void setUnFocusColor(Paint color);

    Paint getFocusColor();

    StyleableObjectProperty<Paint> focusColorProperty();

    void setFocusColor(Paint color);
}
