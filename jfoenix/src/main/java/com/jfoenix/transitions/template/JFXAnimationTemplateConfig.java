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

import com.jfoenix.transitions.template.helper.Direction;
import com.jfoenix.transitions.template.helper.InterpretationMode;
import com.jfoenix.transitions.template.interpolator.FluentTransitionInterpolator;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
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
  private final IntSupplier cycleCountSupplier;
  private final BooleanSupplier autoReverseSupplier;
  private final Supplier<Interpolator> interpolatorSupplier;
  private final Supplier<InterpretationMode> interpolatorInterpretationModeSupplier;
  private final Supplier<Duration> delaySupplier;
  private final DoubleSupplier rateSupplier;
  private final EventHandler<ActionEvent> onFinish;
  private final Supplier<Direction> fluentTransitionSupplier;
  private final BooleanSupplier fromToAutoGenSupplier;
  private final BooleanSupplier autoResetSupplier;

  private JFXAnimationTemplateConfig(Builder builder) {
    durationSupplier = builder.durationSupplier;
    cycleCountSupplier = builder.cycleCountSupplier;
    autoReverseSupplier = builder.autoReverseSupplier;
    interpolatorSupplier = builder.interpolatorSupplier;
    interpolatorInterpretationModeSupplier = builder.interpolatorInterpretationModeSupplier;
    delaySupplier = builder.delaySupplier;
    rateSupplier = builder.rateSupplier;
    onFinish = builder.onFinish;
    fluentTransitionSupplier = builder.fluentTransitionSupplier;
    fromToAutoGenSupplier = builder.fromToAutoGenSupplier;
    autoResetSupplier = builder.autoResetSupplier;
  }

  public static JFXAnimationTemplateConfig.Builder builder() {
    return new Builder();
  }

  public Duration getDuration() {
    return durationSupplier.get();
  }

  public int getCycleCount() {
    return cycleCountSupplier.getAsInt();
  }

  public boolean isAutoReverse() {
    return autoReverseSupplier.getAsBoolean();
  }

  public Interpolator getInterpolator() {
    return interpolatorSupplier.get();
  }

  public InterpretationMode getInterpolatorInterpretationMode() {
    return interpolatorInterpretationModeSupplier.get();
  }

  public Duration getDelay() {
    return delaySupplier.get();
  }

  public double getRate() {
    return rateSupplier.getAsDouble();
  }

  public void handleOnFinish(ActionEvent actionEvent) {
    onFinish.handle(actionEvent);
  }

  public boolean hasFluentTransition() {
    return fluentTransitionSupplier != null;
  }

  public Direction getFluentTransition() {
    return fluentTransitionSupplier.get();
  }

  public boolean isFromToAutoGen() {
    return fromToAutoGenSupplier.getAsBoolean();
  }

  public boolean isAutoReset() {
    return autoResetSupplier.getAsBoolean();
  }

  public static final class Builder {

    private Supplier<Duration> durationSupplier = () -> Duration.ZERO;
    private IntSupplier cycleCountSupplier = () -> 1;
    private BooleanSupplier autoReverseSupplier = () -> false;
    private Supplier<Interpolator> interpolatorSupplier = () -> Interpolator.LINEAR;
    private Supplier<InterpretationMode> interpolatorInterpretationModeSupplier =
        () -> InterpretationMode.STATIC;
    private Supplier<Duration> delaySupplier = () -> Duration.ZERO;
    private DoubleSupplier rateSupplier = () -> 1d;
    private EventHandler<ActionEvent> onFinish = event -> {};
    private Supplier<Direction> fluentTransitionSupplier;
    private BooleanSupplier fromToAutoGenSupplier = () -> false;
    private BooleanSupplier autoResetSupplier = () -> false;

    private Builder() {}

    /**
     * The total {@link Duration} of this animation. <br>
     * The given {@link Duration} is the base for all {@link JFXTemplateProcess#percent(double,
     * double...)} definitions. <br>
     * If there exist a {@link JFXTemplateProcess#time(Duration, Duration...)} definition with a
     * greater {@link Duration}, the animation will take longer.
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
     * @param cycleCountSupplier the number of cycles {@link IntSupplier}.
     * @return the {@link Builder} instance.
     */
    public Builder cycleCount(IntSupplier cycleCountSupplier) {
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
     * The default {@code true} version of {@link #autoReverse(boolean)}.
     *
     * @see #autoReverse(boolean)
     * @return the {@link Builder} instance.
     */
    public Builder autoReverse() {
      return autoReverse(true);
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
     * @param reverseSupplier the reverse boolean {@link BooleanSupplier}.
     * @return the {@link Builder} instance.
     */
    public Builder autoReverse(BooleanSupplier reverseSupplier) {
      this.autoReverseSupplier = reverseSupplier;
      return this;
    }

    /**
     * The global {@link Interpolator} which is set to all {@link JFXTemplateAction}s without a
     * defined {@link Interpolator} with {@link
     * JFXAnimationTemplateAction.Builder#interpolator(Interpolator)}. <br>
     * If no {@link Interpolator} is defined, the {@link Interpolator#LINEAR} is default. <br>
     * The {@link InterpretationMode} is {@link InterpretationMode#STATIC}.
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
     * The lazy version of {@link #interpolator(InterpretationMode, Supplier)} where the {@link
     * InterpretationMode} is computed when the {@link JFXAnimationTemplateConfig} is build.
     *
     * @see #interpolator(InterpretationMode, Supplier)
     * @param interpolatorInterpretationModeSupplier the {@link InterpretationMode} {@link
     *     Supplier}.
     * @param interpolatorSupplier the global {@link Interpolator} {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder interpolator(
        Supplier<InterpretationMode> interpolatorInterpretationModeSupplier,
        Supplier<Interpolator> interpolatorSupplier) {
      this.interpolatorInterpretationModeSupplier = interpolatorInterpretationModeSupplier;
      this.interpolatorSupplier = interpolatorSupplier;
      return this;
    }

    /**
     * Same as {@link #interpolator(Supplier)} but with an {@link InterpretationMode}. <br>
     * If {@link InterpretationMode#DYNAMIC} is defined, the {@link Interpolator} is evaluated
     * during animation runtime which means, that the {@link Interpolator} is exchangeable while an
     * animation is running.
     *
     * @param interpolatorInterpretationMode the {@link InterpretationMode}.
     * @param interpolatorSupplier the global {@link Interpolator} {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder interpolator(
        InterpretationMode interpolatorInterpretationMode,
        Supplier<Interpolator> interpolatorSupplier) {
      return interpolator(() -> interpolatorInterpretationMode, interpolatorSupplier);
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
     * @see #rate(double)
     * @param rateSupplier the animation rate {@link DoubleSupplier}.
     * @return the {@link Builder} instance.
     */
    public Builder rate(DoubleSupplier rateSupplier) {
      this.rateSupplier = rateSupplier;
      return this;
    }

    /**
     * Defines a {@link #rate(double)} of -1, which plays the animation backwards with normal speed.
     *
     * @return the {@link Builder} instance.
     */
    public Builder reverse() {
      return rate(-1);
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

    /**
     * The lazy version of {@link #fluentTransition(Direction)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #fluentTransition(Direction)
     * @param fluentTransitionSupplier the {@link FluentTransitionInterpolator} {@link Direction}
     *     {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder fluentTransition(Supplier<Direction> fluentTransitionSupplier) {
      this.fluentTransitionSupplier = fluentTransitionSupplier;
      return this;
    }

    /**
     * Defines a {@link FluentTransitionInterpolator} for all actions without a set {@link
     * JFXAnimationTemplateAction.Builder#fluentTransition(Direction)}. <br>
     * This {@link Interpolator} uses the current value from a target {@link
     * javafx.beans.value.WritableValue} as start- or end value. <br>
     * Useful if an animation action is interpolated after an interruption which gets otherwise
     * clipped.
     *
     * @param fluentTransitionDirection the {@link Direction} of the {@link
     *     FluentTransitionInterpolator}.
     * @return the {@link Builder} instance.
     */
    public Builder fluentTransition(Direction fluentTransitionDirection) {
      return fluentTransition(() -> fluentTransitionDirection);
    }

    /**
     * The default {@link Direction#FORWARDS} version of {@link #fluentTransition(Direction)}.
     *
     * @see #fluentTransition(Direction)
     * @return the {@link Builder} instance.
     */
    public Builder fluentTransition() {
      return fluentTransition(Direction.FORWARDS);
    }

    /**
     * The lazy version of {@link #fromToAutoGen(boolean)} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #fromToAutoGen(boolean)
     * @param fromToAutoGenSupplier the fromToAutoGen {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder fromToAutoGen(BooleanSupplier fromToAutoGenSupplier) {
      this.fromToAutoGenSupplier = fromToAutoGenSupplier;
      return this;
    }

    /**
     * If {@code true} generates for every action target which has no action definition for
     * animation start ({@link JFXTemplateProcess#percent(double, double...)} with 0 percent, {@link
     * JFXTemplateProcess#time(Duration, Duration...)} with {@link Duration#ZERO}) or end ({@link
     * JFXTemplateProcess#percent(double, double...)} with 100 percent, {@link
     * JFXTemplateProcess#time(Duration, Duration...)} with max {@link Duration}), the corresponding
     * actions. <br>
     * This emulates the corresponding behavior of a CSS animation.
     *
     * @param fromToAutoGen the fromToAutoGen boolean.
     * @return the {@link Builder} instance.
     */
    public Builder fromToAutoGen(boolean fromToAutoGen) {
      return fromToAutoGen(() -> fromToAutoGen);
    }

    /**
     * The default {@code true} version of {@link #fromToAutoGen(boolean)}.
     *
     * @see #fromToAutoGen(boolean)
     * @return the {@link Builder} instance.
     */
    public Builder fromToAutoGen() {
      return fromToAutoGen(true);
    }

    /**
     * The lazy version of {@link #autoReset(boolean)} )} which is computed when the {@link
     * JFXAnimationTemplateConfig} is build.
     *
     * @see #autoReset(boolean)
     * @param autoResetSupplier the autoReset {@link Supplier}.
     * @return the {@link Builder} instance.
     */
    public Builder autoReset(BooleanSupplier autoResetSupplier) {
      this.autoResetSupplier = autoResetSupplier;
      return this;
    }

    /**
     * If {@code true} resets all action targets to the values previously set before the animation
     * was build. <br>
     * The behaviour is similar to the CSS {@code animation-fill-mode: backwards;} definition.
     *
     * @param autoReset the autoReset boolean.
     * @return the {@link Builder} instance.
     */
    public Builder autoReset(boolean autoReset) {
      return autoReset(() -> autoReset);
    }

    /**
     * The default {@code true} version of {@link #autoReset(boolean)}.
     *
     * @see #autoReset(boolean)
     * @return the {@link Builder} instance.
     */
    public Builder autoReset() {
      return autoReset(true);
    }

    public JFXAnimationTemplateConfig build() {
      return new JFXAnimationTemplateConfig(this);
    }
  }
}
