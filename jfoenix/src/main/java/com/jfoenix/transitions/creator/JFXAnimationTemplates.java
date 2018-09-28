package com.jfoenix.transitions.creator;

import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public class JFXAnimationTemplates {

  public static <N> Timeline buildTimeline(JFXAnimationTemplate<N> creator) {

    Timeline timeline = new Timeline();
    JFXAnimationTemplateConfig creatorConfig = creator.buildAndGetTemplateConfig();

    creator
        .buildAndGetAnimationValues()
        .forEach(
            (percent, animationValues) -> {

              // calc the percentage duration of total duration.
              Duration percentageDuration = creatorConfig.getDuration().multiply((percent / 100));

              // Create the key values.
              KeyValue[] keyValues =
                  animationValues
                      .stream()
                      .flatMap(
                          animationValue ->
                              animationValue.mapTo(
                                  createKeyValue(creatorConfig.getInterpolator(), animationValue)))
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

    timeline.setAutoReverse(creatorConfig.isAutoReverse());
    timeline.setCycleCount(creatorConfig.getCycleCount());
    timeline.setDelay(creatorConfig.getDelay());
    timeline.setRate(creatorConfig.getRate());
    timeline.setOnFinished(creatorConfig::handleOnFinish);

    return timeline;
  }

  public static <N> JFXAnimationTimer buildAnimationTimer(JFXAnimationTemplate<N> creator) {

    JFXAnimationTimer animationTimer = new JFXAnimationTimer();
    JFXAnimationTemplateConfig creatorConfig = creator.buildAndGetTemplateConfig();

    creator
        .buildAndGetAnimationValues()
        .forEach(
            (percent, animationValues) -> {

              // calc the percentage duration of total duration.
              Duration percentageDuration = creatorConfig.getDuration().multiply((percent / 100));

              // Create the key values.
              JFXKeyValue<?>[] keyValues =
                  animationValues
                      .stream()
                      .flatMap(
                          animationValue ->
                              animationValue.mapTo(
                                  createJFXKeyValue(
                                      creatorConfig.getInterpolator(), animationValue)))
                      .toArray(JFXKeyValue<?>[]::new);

              JFXKeyFrame keyFrame = new JFXKeyFrame(percentageDuration, keyValues);
              try {
                animationTimer.addKeyFrame(keyFrame);
              } catch (Exception e) {
                // Nothing happens cause timer can't run at this point.
              }
            });

    animationTimer.setOnFinished(() -> creatorConfig.handleOnFinish(new ActionEvent()));

    return animationTimer;
  }

  private static Function<WritableValue<Object>, KeyValue> createKeyValue(
      Interpolator globalInterpolator, JFXAnimationTemplateAction<?, ?> animationValue) {
    return (writableValue) -> {
      Interpolator interpolator = animationValue.getInterpolator();
      return new KeyValue(
          writableValue,
          animationValue.getEndValue(),
          interpolator == null ? globalInterpolator : interpolator);
    };
  }

  private static Function<WritableValue<Object>, JFXKeyValue<?>> createJFXKeyValue(
      Interpolator globalInterpolator, JFXAnimationTemplateAction<?, ?> animationValue) {
    return (writableValue) -> {
      Interpolator interpolator = animationValue.getInterpolator();
      return JFXKeyValue.builder()
          .setTarget(writableValue)
          .setEndValue(animationValue.getEndValue())
          .setInterpolator(interpolator == null ? globalInterpolator : interpolator)
          .setAnimateCondition(() -> true)
          .build();
    };
  }
}
