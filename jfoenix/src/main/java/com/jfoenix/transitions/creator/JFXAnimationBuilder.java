package com.jfoenix.transitions.creator;

import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public class JFXAnimationBuilder {

  public static <N> Timeline buildTimeline(JFXAnimationCreator<N> creator) {

    Timeline timeline = new Timeline();
    JFXAnimationCreatorConfig animationConfig =
        creator
            .getCreatorConfigBuilderFunction()
            .apply(JFXAnimationCreatorConfig.builder())
            .build();

    creator
        .getAnimationValues()
        .forEach(
            (percent, animationValues) -> {

              // calc the percentage duration of total duration.
              Duration percentageDuration = animationConfig.getDuration().multiply((percent / 100));

              // Create the key values.
              KeyValue[] keyValues =
                  animationValues
                      .stream()
                      .flatMap(
                          animationValue ->
                              animationValue.toKeyValues(animationConfig.getInterpolator()))
                      .toArray(KeyValue[]::new);

              // Reduce the onFinish events to one consumer.
              Consumer<ActionEvent> onFinish =
                  animationValues
                      .stream()
                      .map(animationValue -> (Consumer<ActionEvent>) animationValue::handleOnFinish)
                      .reduce(action -> {}, Consumer::andThen);

              KeyFrame keyFrame = new KeyFrame(percentageDuration, onFinish::accept, keyValues);
              timeline.getKeyFrames().add(keyFrame);
            });

    timeline.setAutoReverse(animationConfig.isAutoReverse());
    timeline.setCycleCount(animationConfig.getCycleCount());
    timeline.setDelay(animationConfig.getDelay());
    timeline.setRate(animationConfig.getRate());
    timeline.setOnFinished(animationConfig::handleOnFinish);

    return timeline;
  }

  public static <N> JFXAnimationTimer buildAnimationTimer(JFXAnimationCreator<N> creator) {

    JFXAnimationTimer animationTimer = new JFXAnimationTimer();
    JFXAnimationCreatorConfig animationConfig =
        creator
            .getCreatorConfigBuilderFunction()
            .apply(JFXAnimationCreatorConfig.builder())
            .build();

    creator
        .getAnimationValues()
        .forEach(
            (percent, animationValues) -> {

              // calc the percentage duration of total duration.
              Duration percentageDuration = animationConfig.getDuration().multiply((percent / 100));

              // Create the key values.
              JFXKeyValue<?>[] keyValues =
                  animationValues
                      .stream()
                      .flatMap(
                          animationValue ->
                              animationValue.toJFXKeyValues(animationConfig.getInterpolator()))
                      .toArray(JFXKeyValue<?>[]::new);

              JFXKeyFrame keyFrame = new JFXKeyFrame(percentageDuration, keyValues);
              try {
                animationTimer.addKeyFrame(keyFrame);
              } catch (Exception e) {
                // Nothing happens cause timer can't run at this point.
              }
            });

    animationTimer.setOnFinished(() -> animationConfig.handleOnFinish(new ActionEvent()));

    return animationTimer;
  }
}
