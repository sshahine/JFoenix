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

import com.jfoenix.controls.JFXSlider.IndicatorPosition;
import com.sun.javafx.css.StyleConverterImpl;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

/**
 * Converts the CSS for -fx-indicator-position items into IndicatorPosition.
 * it's used in JFXSlider.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class IndicatorPositionConverter extends StyleConverterImpl<String, IndicatorPosition> {
    // lazy, thread-safe instatiation
    private static class Holder {
        static final IndicatorPositionConverter INSTANCE = new IndicatorPositionConverter();
    }

    public static StyleConverter<String, IndicatorPosition> getInstance() {
        return Holder.INSTANCE;
    }

    private IndicatorPositionConverter() {
    }

    @Override
    public IndicatorPosition convert(ParsedValue<String, IndicatorPosition> value, Font not_used) {
        String string = value.getValue().toUpperCase();
        try {
            return IndicatorPosition.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return IndicatorPosition.LEFT;
        }
    }

    @Override
    public String toString() {
        return "IndicatorPositionConverter";
    }

}
