package com.aquafx_project.controls.skin.effects;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Defines the Mac OS X specific very light shadow for some controls. 
 * This Effect is a dropshadow.
 * 
 * @author claudinezillmann
 *
 */

public class Shadow extends DropShadow {

    public Shadow(boolean pillShadow) {
        setColor(Color.rgb(172, 172, 184));
        setBlurType(BlurType.ONE_PASS_BOX);
        setRadius(2.0);
        setSpread(0.1);
        setOffsetX(0.0);
        setOffsetY(0.8);

        if (pillShadow) {
            setOffsetX(1.0);
            setOffsetY(0.5);
            setWidth(1.0);
        }
    }
}
