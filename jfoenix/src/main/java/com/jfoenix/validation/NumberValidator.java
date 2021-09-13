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
import javafx.beans.DefaultProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * An example of Number field validation, that is applied on text input controls
 * such as {@link TextField} and {@link TextArea}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "icon")
public class NumberValidator extends ValidatorBase {

    private NumberStringConverter numberStringConverter = new NumberStringConverter(){
        @Override
        public Number fromString(String string) {
            try {
                if (string == null) {
                    return null;
                }
                string = string.trim();
                if (string.length() < 1) {
                    return null;
                }
                // Create and configure the parser to be used
                NumberFormat parser = getNumberFormat();
                ParsePosition parsePosition = new ParsePosition(0);
                Number result = parser.parse(string, parsePosition);
                final int index = parsePosition.getIndex();
                if (index == 0 || index < string.length()) {
                    throw new ParseException("Unparseable number: \"" + string + "\"", parsePosition.getErrorIndex());
                }
                return result;
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    public NumberValidator() { }

    public NumberValidator(String message) {
        super(message);
    }

    public NumberValidator(NumberStringConverter numberStringConverter) {
        this.numberStringConverter = numberStringConverter;
    }

    public NumberValidator(String message, NumberStringConverter numberStringConverter) {
        super(message);
        this.numberStringConverter = numberStringConverter;
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
        try {
            hasErrors.set(false);
            if (!text.isEmpty())
                numberStringConverter.fromString(text);
        } catch (Exception e) {
            hasErrors.set(true);
        }
    }

    public NumberStringConverter getNumberStringConverter() {
        return numberStringConverter;
    }

    public void setNumberStringConverter(NumberStringConverter numberStringConverter) {
        this.numberStringConverter = numberStringConverter;
    }
}
