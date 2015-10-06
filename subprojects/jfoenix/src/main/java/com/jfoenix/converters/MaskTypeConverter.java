/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jfoenix.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.sun.javafx.css.StyleConverterImpl;

public final class MaskTypeConverter extends StyleConverterImpl<String , RipplerMask> {

    // lazy, thread-safe instatiation
    private static class Holder {
        static final MaskTypeConverter INSTANCE = new MaskTypeConverter();
    }
    public static StyleConverter<String, RipplerMask> getInstance() {
        return Holder.INSTANCE;
    }
    private MaskTypeConverter() {
        super();
    }

    @Override
    public RipplerMask convert(ParsedValue<String,RipplerMask> value, Font not_used) {
        String string = value.getValue();
        try {
            return RipplerMask.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return RipplerMask.RECT;
        }
    }

    @Override
    public String toString() {
        return "MaskTypeConverter";
    }
}