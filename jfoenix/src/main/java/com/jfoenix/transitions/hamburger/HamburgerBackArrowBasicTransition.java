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

package com.jfoenix.transitions.hamburger;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.CachedTransition;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * transform {@link JFXHamburger} into left arrow
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class HamburgerBackArrowBasicTransition extends CachedTransition implements HamburgerTransition {

    public HamburgerBackArrowBasicTransition() {
        super(null, null);
    }

    public HamburgerBackArrowBasicTransition(JFXHamburger burger) {
        super(burger, createTimeline(burger));
        timeline.bind(Bindings.createObjectBinding(() -> createTimeline(burger),
            burger.widthProperty(),
            burger.heightProperty(),
            ((Region) burger.getChildren().get(0)).widthProperty(),
            ((Region) burger.getChildren().get(0)).heightProperty()));
        // reduce the number to increase the shifting , increase number to reduce shifting
        setCycleDuration(Duration.seconds(0.3));
        setDelay(Duration.seconds(0));
    }

    private static Timeline createTimeline(JFXHamburger burger) {
        double burgerWidth = burger.getChildren().get(0).getLayoutBounds().getWidth();
        double burgerHeight = burger.getChildren().get(2).getBoundsInParent().getMaxY() - burger.getChildren()
            .get(0)
            .getBoundsInParent()
            .getMinY();

        double hypotenuse = Math.sqrt(Math.pow(burgerHeight / 2 - burger.getChildren()
            .get(0)
            .getLayoutBounds()
            .getHeight() / 2, 2) + Math.pow(burgerWidth / 2,
            2));
        double angle = Math.toDegrees(Math.asin((burgerHeight / 2 - burger.getChildren()
            .get(0)
            .getLayoutBounds()
            .getHeight() / 2) / hypotenuse));

        double burgerDiagonal = Math.sqrt(Math.pow(burger.getChildren().get(0).getLayoutBounds().getHeight(),
            2) + Math.pow(burger.getChildren()
            .get(0)
            .getBoundsInParent()
            .getWidth() / 2, 2));
        double theta = (90 - angle) + Math.toDegrees(Math.atan((burger.getChildren()
            .get(0)
            .getLayoutBounds()
            .getHeight()) / (burger.getChildren()
            .get(0)
            .getBoundsInParent()
            .getWidth() / 2)));
        double hOffset = Math.cos(Math.toRadians(theta)) * burgerDiagonal / 2;
        double transY = burger.getChildren().get(0).getLayoutBounds().getHeight() / 2 + burger.getSpacing() - hOffset;
        double transX = burgerWidth / 2 - Math.sin(Math.toRadians(theta)) * (burgerDiagonal / 2);

        return new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(burger.rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).translateYProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).translateXProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).scaleXProperty(), 1, Interpolator.EASE_BOTH),

                new KeyValue(burger.getChildren().get(2).rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).translateYProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).translateXProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).scaleXProperty(), 1, Interpolator.EASE_BOTH)

            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(burger.rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).rotateProperty(), -angle, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).translateYProperty(), transY, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).translateXProperty(),
                    -transX,
                    Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).scaleXProperty(), 0.5, Interpolator.EASE_BOTH),

                new KeyValue(burger.getChildren().get(2).rotateProperty(), angle, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).translateYProperty(),
                    -transY,
                    Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).translateXProperty(),
                    -transX,
                    Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).scaleXProperty(), 0.5, Interpolator.EASE_BOTH)
            )
        );
    }

    public Transition getAnimation(JFXHamburger burger) {
        return new HamburgerBackArrowBasicTransition(burger);
    }

}
