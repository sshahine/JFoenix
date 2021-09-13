/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.animation.alert;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.transitions.CachedTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Horizontal translate animation for {@link JFXAlert} control
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-05-26
 */
class HorizontalTransition extends CachedTransition {
    public HorizontalTransition(boolean leftDirection, Node contentContainer, Node overlay) {
        super(contentContainer, new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(contentContainer.translateXProperty(),
                    (contentContainer.getLayoutX() + contentContainer.getLayoutBounds().getMaxX())
                    * (leftDirection? -1 : 1), Interpolator.LINEAR),
                new KeyValue(overlay.opacityProperty(), 0, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(overlay.opacityProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(contentContainer.translateXProperty(), 0, Interpolator.EASE_OUT)
            )));
        // reduce the number to increase the shifting , increase number to reduce shifting
        setCycleDuration(Duration.seconds(0.4));
        setDelay(Duration.seconds(0));
    }
}
