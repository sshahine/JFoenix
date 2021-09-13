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

package com.jfoenix.controls.cells.editors;

import com.jfoenix.utils.JFXUtilities;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.base.ValidatorBase;

/**
 * <h1>Text field cell editor (numbers only) </h1>
 * this an example of the cell editor, it creates a JFXTextField node to
 * allow the user to edit the cell value
 * <p>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class IntegerTextFieldEditorBuilder extends TextFieldEditorBase<Integer> {

    public IntegerTextFieldEditorBuilder(ValidatorBase... validators) {
        super(JFXUtilities.concat(
            new ValidatorBase[] {new IntegerValidator()},
            validators,
            len -> new ValidatorBase[len]));
    }

    @Override
    public Integer getValue() {
        return Integer.valueOf(textField.getText());
    }
}
