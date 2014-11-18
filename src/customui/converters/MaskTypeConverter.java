package customui.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.sun.javafx.css.StyleConverterImpl;

import customui.components.C3DRippler.RipplerMask;

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