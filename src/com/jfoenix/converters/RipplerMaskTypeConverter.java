/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.sun.javafx.css.StyleConverterImpl;

/**
 * @author Shadi Shaheen
 *
 */
public final class RipplerMaskTypeConverter extends StyleConverterImpl<String , RipplerMask> {
    // lazy, thread-safe instatiation
    private static class Holder {
        static final RipplerMaskTypeConverter INSTANCE = new RipplerMaskTypeConverter();
    }
    public static StyleConverter<String, RipplerMask> getInstance() {
        return Holder.INSTANCE;
    }
    private RipplerMaskTypeConverter() {
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
        return "RipplerMaskTypeConverter";
    }
}