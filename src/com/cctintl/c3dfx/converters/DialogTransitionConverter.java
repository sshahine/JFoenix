package com.cctintl.c3dfx.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.cctintl.c3dfx.controls.C3DDialog.C3DDialogAnimation;
import com.sun.javafx.css.StyleConverterImpl;

public class DialogTransitionConverter  extends StyleConverterImpl<String , C3DDialogAnimation> {

    // lazy, thread-safe instatiation
    private static class Holder {
        static final DialogTransitionConverter INSTANCE = new DialogTransitionConverter();
    }
    public static StyleConverter<String, C3DDialogAnimation> getInstance() {
        return Holder.INSTANCE;
    }
    private DialogTransitionConverter() {
        super();
    }

    @Override
    public C3DDialogAnimation convert(ParsedValue<String,C3DDialogAnimation> value, Font not_used) {
        String string = value.getValue();
        try {
            return C3DDialogAnimation.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return C3DDialogAnimation.CENTER;
        }
    }
    @Override
    public String toString() {
        return "DialogTransitionConverter";
    }
}