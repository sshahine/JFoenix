package com.aquafx_project.nativestuff;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class NsImageIconLoader {

    public static Image load(NsImageIcon icon) {
        System.setProperty("java.awt.headless", "false");
        java.awt.Image awtImage = Toolkit.getDefaultToolkit().getImage(
                "NSImage://" + icon.getName());
        BufferedImage bufferedImage = null;
        if (awtImage instanceof BufferedImage) {
            bufferedImage = (BufferedImage) awtImage;
        } else {
            bufferedImage = new BufferedImage(awtImage.getWidth(null),
                    awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.createGraphics();
            g.drawImage(awtImage, 0, 0, null);
            g.dispose();
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
