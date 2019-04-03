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
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.function.Function;

/**
 * JFXAlertAnimation object is used to to create showing/hiding animation for
 * {@link JFXAlert} control
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-05-26
 */
public interface JFXAlertAnimation {

    Function<Transition, Transition> inverseAnimation = transition -> {
        transition.jumpTo(transition.getCycleDuration());
        transition.setRate(-1);
        return transition;
    };

    void initAnimation(Node contentContainer, Node overlay);

    Animation createShowingAnimation(Node contentContainer, Node overlay);

    Animation createHidingAnimation(Node contentContainer, Node overlay);

    JFXAlertAnimation LEFT_ANIMATION = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {
            overlay.setOpacity(0);
            contentContainer.setTranslateX(-(contentContainer.getLayoutX()
                                             + contentContainer.getLayoutBounds().getMaxX()));
        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return new HorizontalTransition(true, contentContainer, overlay);
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return inverseAnimation.apply(new HorizontalTransition(true, contentContainer, overlay));
        }
    };

    JFXAlertAnimation RIGHT_ANIMATION = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {
            overlay.setOpacity(0);
            contentContainer.setTranslateX(contentContainer.getLayoutX()
                                           + contentContainer.getLayoutBounds().getMaxX());
        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return new HorizontalTransition(false, contentContainer, overlay);
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return inverseAnimation.apply(new HorizontalTransition(false, contentContainer, overlay));
        }
    };

    JFXAlertAnimation TOP_ANIMATION = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {
            overlay.setOpacity(0);
            contentContainer.setTranslateY(-(contentContainer.getLayoutY()
                                             + contentContainer.getLayoutBounds().getMaxY()));
        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return new VerticalTransition(true, contentContainer, overlay);
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return inverseAnimation.apply(new VerticalTransition(true, contentContainer, overlay));
        }
    };

    JFXAlertAnimation BOTTOM_ANIMATION = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {
            overlay.setOpacity(0);
            contentContainer.setTranslateY(contentContainer.getLayoutY()
                                           + contentContainer.getLayoutBounds().getMaxY());
        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return new VerticalTransition(false, contentContainer, overlay);
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return inverseAnimation.apply(new VerticalTransition(false, contentContainer, overlay));
        }
    };

    JFXAlertAnimation CENTER_ANIMATION = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {
            overlay.setOpacity(0);
            contentContainer.setScaleX(0);
            contentContainer.setScaleY(0);
        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return new CenterTransition(contentContainer, overlay);
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return inverseAnimation.apply(new CenterTransition(contentContainer, overlay));
        }
    };

    JFXAlertAnimation NO_ANIMATION = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {

        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return null;
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return null;
        }
    };

    JFXAlertAnimation SMOOTH = new JFXAlertAnimation() {
        @Override
        public void initAnimation(Node contentContainer, Node overlay) {
            overlay.setOpacity(0);
            contentContainer.setScaleX(.80);
            contentContainer.setScaleY(.80);
        }

        @Override
        public Animation createShowingAnimation(Node contentContainer, Node overlay) {
            return new CachedTransition(contentContainer, new Timeline(
                new KeyFrame(Duration.millis(1000),
                    new KeyValue(contentContainer.scaleXProperty(), 1, Interpolator.EASE_OUT),
                    new KeyValue(contentContainer.scaleYProperty(), 1, Interpolator.EASE_OUT),
                    new KeyValue(overlay.opacityProperty(), 1, Interpolator.EASE_BOTH)
                ))) {
                {
                    setCycleDuration(Duration.millis(160));
                    setDelay(Duration.seconds(0));
                }
            };
        }

        @Override
        public Animation createHidingAnimation(Node contentContainer, Node overlay) {
            return new CachedTransition(contentContainer, new Timeline(
                new KeyFrame(Duration.millis(1000),
                    new KeyValue(overlay.opacityProperty(), 0, Interpolator.EASE_BOTH)
                ))) {
                {
                    setCycleDuration(Duration.millis(160));
                    setDelay(Duration.seconds(0));
                }
            };
        }
    };
}

