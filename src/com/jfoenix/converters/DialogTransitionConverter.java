/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.sun.javafx.css.StyleConverterImpl;

public class DialogTransitionConverter  extends StyleConverterImpl<String , DialogTransition> {

    // lazy, thread-safe instatiation
    private static class Holder {
        static final DialogTransitionConverter INSTANCE = new DialogTransitionConverter();
    }
    public static StyleConverter<String, DialogTransition> getInstance() {
        return Holder.INSTANCE;
    }
    private DialogTransitionConverter() {
        super();
    }

    @Override
    public DialogTransition convert(ParsedValue<String,DialogTransition> value, Font not_used) {
        String string = value.getValue();
        try {
            return DialogTransition.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return DialogTransition.CENTER;
        }
    }
    @Override
    public String toString() {
        return "DialogTransitionConverter";
    }
}