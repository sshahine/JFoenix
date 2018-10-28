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
package com.jfoenix.transitions.template;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * Class which represents a builder and the configuration of an animation. The configuration methods
 * are based on the methods of a {@link javafx.animation.Timeline}.<br>
 * It is possible that not all methods supported because the specific implementation of an animation
 * can diverge from a general {@link javafx.animation.Timeline} implementation.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public class JFXAnimationTemplateConfig {

  private final Supplier<Duration> durationSupplier;
  private final Supplier<Integer> cycleCountSupplier;
  private final Supplier<Boolean> autoReverseSupplier;
  private final Supplier<Interpolator> interpolatorSupplier;
  private final Supplier<Duration> delaySupplier;
  private final Supplier<Double> rateSupplier;
  private final EventHandler<ActionEvent> onFinish;

  private JFXAnimationTemplateConfig(Builder builder) {
    durationSupplier = builder.durationSupplier;
    cycleCountSupplier = builder.cycleCountSupplier;
    autoReverseSupplier = builder.autoReverseSupplier;
    interpolatorSupplier = builder.interpolatorSupplier;
    delaySupplier = builder.delaySupplier;
    rateSupplier = builder.rateSupplier;
    onFinish = builder.onFinish;
  }

  public static JFXAnimationTemplateConfig.Builder builder() {
    return new Builder();
  }

  public Duration getDuration() {
    return durationSupplier.get();
  }

  public int getCycleCount() {
    return cycleCountSupplier.get();
  }

  public boolean isAutoReverse() {
    return autoReverseSupplier.get();
  }

  public Interpolator getInterpolator() {
    return interpolatorSupplier.get();
  }

  public Duration getDelay() {
    return delaySupplier.get();
  }

  public double getRate() {
    return rateSupplier.get();
  }

  public void handleOnFinish(ActionEvent actionEvent) {
    onFinish.handle(actionEvent);
  }

  public static final class Builder {

    private Supplier<Duration> durationSupplier = () -> Duration.seconds(1);
    private Supplier<Integer> cycleCountSupplier = () -> 1;
    private Supplier<Boolean> autoReverseSupplier = () -> false;
    private Supplier<Interpolator> interpolatorSupplier = () -> Interpolator.LINEAR;
    private Supplier<Duration> delaySupplier = () -> Duration.ZERO;
    private Supplier<Double> rateSupplier = () -> 1d;
    private EventHandler<ActionEvent> onFinish = event -> {};

    private Builder() {}

    /**
     * The total {@link Duration} of this animation.
     *
     * @param duration the animation {@link Duration}.
     * @return the {@link Builder} instance.
     */
    public Builder duration(Duration duration) {
      return duration(() -> duration);
    }

    /**
     * The lazy version of {@link #duration(Duration)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @param durationSupplier the animation {@link Duration} {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder duration(Supplier<Duration> durationSupplier) {
      this.durationSupplier = durationSupplier;
      return this;
    }

    /**
     * Defines the number of cycles in this animation.
     *
     * @param cycleCount the number of cycles.
     * @return the {@link Builder} instance.
     */
    public Builder cycleCount(int cycleCount) {
      return cycleCount(() -> cycleCount);
    }

    /**
     * The lazy version of {@link #cycleCount(int)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #cycleCount(int)
     * @param cycleCountSupplier the number of cycles {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder cycleCount(Supplier<Integer> cycleCountSupplier) {
      this.cycleCountSupplier = cycleCountSupplier;
      return this;
    }

    /**
     * A {@link Animation#INDEFINITE} count of cycles in this animation.
     *
     * @return the {@link Builder} instance.
     */
    public Builder infiniteCycle() {
      return cycleCount(Animation.INDEFINITE);
    }

    /**
     * After the first cycle the animation is played backwards and after this again forwards.
     *
     * @param reverse the reverse boolean.
     * @return the {@link Builder} instance.
     */
    public Builder autoReverse(boolean reverse) {
      return autoReverse(() -> reverse);
    }

    /**
     * The lazy version of {@link #autoReverse(boolean)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #autoReverse(boolean)
     * @param reverseSupplier the reverse boolean {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder autoReverse(Supplier<Boolean> reverseSupplier) {
      this.autoReverseSupplier = reverseSupplier;
      return this;
    }

    /**
     * The global {@link Interpolator} which is set to all {@link JFXTemplateAction}s without a
     * defined {@link Interpolator} with {@link
     * JFXAnimationTemplateAction.Builder#interpolator(Interpolator)}.
     *
     * @param interpolator the global {@link Interpolator}.
     * @return the {@link Builder} instance.
     */
    public Builder interpolator(Interpolator interpolator) {
      return interpolator(() -> interpolator);
    }

    /**
     * The lazy version of {@link #interpolator(Interpolator)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #interpolator(Interpolator)
     * @param interpolatorSupplier the global {@link Interpolator} {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder interpolator(Supplier<Interpolator> interpolatorSupplier) {
      this.interpolatorSupplier = interpolatorSupplier;
      return this;
    }

    /**
     * Delays the start of an animation.
     *
     * @param delay the delay {@link Duration}.
     * @return the {@link Builder} instance.
     */
    public Builder delay(Duration delay) {
      return delay(() -> delay);
    }

    /**
     * The lazy version of {@link #delay(Duration)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #delay(Duration)
     * @param delaySupplier the delay {@link Duration} {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder delay(Supplier<Duration> delaySupplier) {
      this.delaySupplier = delaySupplier;
      return this;
    }

    /**
     * Defines the direction/speed at which the animation is expected to be played.
     *
     * @param rate the animation rate.
     * @return the {@link Builder} instance.
     */
    public Builder rate(double rate) {
      return rate(() -> rate);
    }

    /**
     * The lazy version of {@link #rate(double)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @param rateSupplier the animation rate {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder rate(Supplier<Double> rateSupplier) {
      this.rateSupplier = rateSupplier;
      return this;
    }

    /**
     * The action to be executed at the conclusion of this animation.
     *
     * @param onFinish the finish {@link EventHandler}.
     * @return the {@link Builder} instance.
     */
    public Builder onFinish(EventHandler<ActionEvent> onFinish) {
      this.onFinish = onFinish;
      return this;
    }

    public JFXAnimationTemplateConfig build() {
      return new JFXAnimationTemplateConfig(this);
    }
  }
}
