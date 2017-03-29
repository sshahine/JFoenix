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
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.transitions.CachedTransition;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * transform {@link JFXHamburger} into close icon v.2
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class HamburgerSlideCloseTransition extends CachedTransition implements HamburgerTransition {

    public HamburgerSlideCloseTransition() {
        super(null, null);
    }

    public HamburgerSlideCloseTransition(JFXHamburger burger) {
        super(burger, createTimeline(burger));
        timeline.bind(Bindings.createObjectBinding(() -> createTimeline(burger),
            ((Region) burger.getChildren().get(0)).widthProperty(),
            ((Region) burger.getChildren().get(0)).heightProperty()));
        setCycleDuration(Duration.seconds(0.3));
        setDelay(Duration.seconds(0));

        setOnFinished((finish) -> {
            if (this.getRate() == 1) {
                burger.getChildren().get(1).setVisible(false);
            }
        });
    }

    @Override
    protected void starting() {
        super.starting();

        if (node.getParent() instanceof JFXRippler) {
            JFXRippler rippler = (JFXRippler) node.getParent();
            BorderPane p = new BorderPane(node);
            p.setMaxWidth(((JFXHamburger) node).getWidth());
            p.setMinWidth(((JFXHamburger) node).getWidth());
            p.addEventHandler(MouseEvent.ANY, (event) -> {
                if (!event.isConsumed()) {
                    event.consume();
                    node.fireEvent(event);
                }
            });
            rippler.setControl(p);
        }

        if (this.getRate() == -1) {
            ((JFXHamburger) node).getChildren().get(1).setVisible(true);
        }
    }


    private static Timeline createTimeline(JFXHamburger burger) {
        double burgerWidth = burger.getChildren().get(0).getLayoutBounds().getWidth();
        double burgerHeight = burger.getChildren().get(2).getBoundsInParent().getMaxY() - burger.getChildren()
            .get(0)
            .getBoundsInParent()
            .getMinY();

        double hypotenuse = Math.sqrt(Math.pow(burgerHeight, 2) + Math.pow(burgerWidth, 2));
        double angle = Math.toDegrees(Math.asin(burgerWidth / hypotenuse)) + 90;

        return new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(burger.rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).translateYProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).translateYProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(1).opacityProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(1).translateXProperty(), 0, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(burger.rotateProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).rotateProperty(), angle, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(0).translateYProperty(),
                    (burgerHeight / 2) - burger.getChildren()
                        .get(0)
                        .getBoundsInLocal()
                        .getHeight() / 2,
                    Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).rotateProperty(), -angle, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(2).translateYProperty(),
                    -((burgerHeight / 2) - burger.getChildren()
                        .get(2)
                        .getBoundsInLocal()
                        .getHeight() / 2),
                    Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(1).opacityProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(burger.getChildren().get(1).translateXProperty(),
                    -burger.getWidth() / 1.1,
                    Interpolator.EASE_BOTH)
            )
        );
    }

    public Transition getAnimation(JFXHamburger burger) {
        return new HamburgerSlideCloseTransition(burger);
    }
}
