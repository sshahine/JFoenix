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

package com.jfoenix.converters;

import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton.ButtonType;
import com.sun.javafx.css.StyleConverterImpl;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

/**
 * Converts the CSS for -fx-button-type items into ButtonType.
 * it's used in JFXButton
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class ButtonTypeConverter extends StyleConverterImpl<String, ButtonType> {

    private ButtonTypeConverter() {
        super();
    }

    // lazy, thread-safe instantiation
    private static class Holder {
        static final ButtonTypeConverter INSTANCE = new ButtonTypeConverter();

        private Holder() {
            throw new IllegalAccessError("Holder class");
        }
    }

    public static StyleConverter<String, ButtonType> getInstance() {
        return Holder.INSTANCE;
    }


    @Override
    public ButtonType convert(ParsedValue<String, ButtonType> value, Font notUsedFont) {
        String string = value.getValue();
        try {
            return ButtonType.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            Logger.getLogger(ButtonTypeConverter.class.getName()).info(String.format("Invalid button type value '%s'", string));
            return ButtonType.FLAT;
        }
    }

    @Override
    public String toString() {
        return "ButtonTypeConverter";
    }
}
