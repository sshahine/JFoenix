package com.cctintl.c3dfx.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.cctintl.c3dfx.controls.C3DDialog.C3DDialogTransition;
import com.sun.javafx.css.StyleConverterImpl;

public class DialogTransitionConverter  extends StyleConverterImpl<String , C3DDialogTransition> {

    // lazy, thread-safe instatiation
    private static class Holder {
        static final DialogTransitionConverter INSTANCE = new DialogTransitionConverter();
    }
    public static StyleConverter<String, C3DDialogTransition> getInstance() {
        return Holder.INSTANCE;
    }
    private DialogTransitionConverter() {
        super();
    }

    @Override
    public C3DDialogTransition convert(ParsedValue<String,C3DDialogTransition> value, Font not_used) {
        String string = value.getValue();
        try {
            return C3DDialogTransition.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return C3DDialogTransition.CENTER;
        }
    }
    @Override
    public String toString() {
        return "DialogTransitionConverter";
    }
}