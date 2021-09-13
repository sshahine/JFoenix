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

package com.jfoenix.validation;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

/**
 * @author Victor Espino
 * @version 1.0
 * @since 2019-08-10
 */
public class StringLengthValidator extends ValidatorBase {

    int StringLength;

    /**
     * Basic constructor with Default message this way:
     * "Max length is " + StringLength +" character(s) "
     *
     * @param StringLengh Length of the string in the input field to validate.
     */
    public StringLengthValidator(int StringLengh) {
        super("Max length is " + StringLengh + " character(s) ");
        this.StringLength = StringLengh + 1;
    }


    /**
     * The displayed message shown will be concatenated by the message with StringLength
     * this way "message" + StringLength.
     *
     * @param StringLength Length of the string in the input field to validate.
     * @param message      Message to show.
     */
    public StringLengthValidator(int StringLength, String message) {
        this.StringLength = StringLength + 1;
        setMessage(message + StringLength);
    }

    /**
     * The displayed message will be personalized,
     * but still need to indicate the StringLength to validate.
     *
     * @param StringLength Length of the string in the input field to validate.
     * @param message      Message to show.
     */
    public StringLengthValidator(String message, int StringLength) {
        super(message);
        this.StringLength = StringLength + 1;
    }

    public void changeStringLength(int newLength) {
        this.StringLength = newLength + 1;
    }

    public int getStringLength() {
        return StringLength - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        String text = textField.getText();
        hasErrors.set(false);

        if (!text.isEmpty()) {
            if (text.length() > StringLength - 1) {
                hasErrors.set(true);
                //  textField.textProperty().set(text.substring(0, 19));

            }
        }
    }
}
