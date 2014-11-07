package com.aquafx_project.controls.skin.styles;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.sun.javafx.css.StyleConverterImpl;

/**
 * 
 * Converts the CSS for -fx-aqua-icon items into a MacOSDefaultIcons. Used by
 * {@link com.aquafx_project.controls.skin.AquaButtonSkin AquaButtonSkin}.
 * 
 * @author claudinezillmann
 * 
 */
public final class MacOSDefaultIconConverter extends StyleConverterImpl<String, MacOSDefaultIcons> {

    private static class Holder {
        static MacOSDefaultIconConverter ICON_INSTANCE = new MacOSDefaultIconConverter();
    }

    // lazy, thread-safe instantiation
    public static StyleConverter<String, MacOSDefaultIcons> getInstance() {
        return Holder.ICON_INSTANCE;
    }

    private MacOSDefaultIconConverter() {
        super();
    }

    @Override public MacOSDefaultIcons convert(ParsedValue<String, MacOSDefaultIcons> value, Font font) {
        String str = value.getValue();
        if (str == null || str.isEmpty() || "null".equals(str)) {
            return null;
        }
        try {
            return MacOSDefaultIcons.valueOf(str);
        } catch (final IllegalArgumentException e) {
            // TODO: use logger here
            System.err.println("not a Mac Icon: " + value);
            return MacOSDefaultIcons.RIGHT;
        }
    }

    @Override public String toString() {
        return "MacOSDefaultIconConverter";
    }
}