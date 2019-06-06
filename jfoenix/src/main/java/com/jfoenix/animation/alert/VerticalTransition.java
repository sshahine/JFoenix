/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
 * Vertical translate animation for {@link JFXAlert} control
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-05-26
 */
public class VerticalTransition extends CachedTransition {
    public VerticalTransition(boolean topDirection, Node contentContainer, Node overlay) {
        super(contentContainer, new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(contentContainer.translateYProperty(),
                    (contentContainer.getLayoutY() + contentContainer.getLayoutBounds().getMaxY())
                    * (topDirection? -1 : 1), Interpolator.LINEAR),
                new KeyValue(overlay.opacityProperty(), 0, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(overlay.opacityProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(contentContainer.translateYProperty(), 0, Interpolator.EASE_OUT)
            )));
        // reduce the number to increase the shifting , increase number to reduce shifting
        setCycleDuration(Duration.seconds(0.4));
        setDelay(Duration.seconds(0));
    }
}
