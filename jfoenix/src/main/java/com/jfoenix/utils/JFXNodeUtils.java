package com.jfoenix.utils;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Locale;

public class JFXNodeUtils {


    public static void updateBackground(Background newBackground, Region nodeToUpdate) {
        updateBackground(newBackground, nodeToUpdate, Color.BLACK);
    }

    public static void updateBackground(Background newBackground, Region nodeToUpdate, Paint fill) {
        if (newBackground != null && !newBackground.getFills().isEmpty()) {
            final BackgroundFill[] fills = new BackgroundFill[newBackground.getFills().size()];
            for (int i = 0; i < newBackground.getFills().size(); i++) {
                BackgroundFill bf = newBackground.getFills().get(i);
                fills[i] = new BackgroundFill(fill,bf.getRadii(),bf.getInsets());
            }
            nodeToUpdate.setBackground(new Background(fills));
        }
    }

    public static String colorToHex(Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255)).toUpperCase();
        } else {
            return null;
        }
    }
}
