package customui.converters;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

import com.sun.javafx.css.StyleConverterImpl;

import customui.components.C3DButton.ButtonType;

public class ButtonTypeConverter  extends StyleConverterImpl<String , ButtonType> {

    // lazy, thread-safe instatiation
    private static class Holder {
        static final ButtonTypeConverter INSTANCE = new ButtonTypeConverter();
    }
    public static StyleConverter<String, ButtonType> getInstance() {
        return Holder.INSTANCE;
    }
    private ButtonTypeConverter() {
        super();
    }

    @Override
    public ButtonType convert(ParsedValue<String,ButtonType> value, Font not_used) {
        String string = value.getValue();
        try {
            return ButtonType.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return ButtonType.FLAT;
        }
    }

    @Override
    public String toString() {
        return "ButtonTypeConverter";
    }
}