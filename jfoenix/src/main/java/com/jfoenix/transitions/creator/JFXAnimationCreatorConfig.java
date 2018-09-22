package com.jfoenix.transitions.creator;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public class JFXAnimationCreatorConfig {

      private final Supplier<Duration> durationSupplier;
      private final Supplier<Integer> cycleCountSupplier;
      private final Supplier<Boolean> autoReverseSupplier;
      private final Supplier<Interpolator> interpolatorSupplier;
      private final Supplier<Duration> delaySupplier;
      private final Supplier<Double> rateSupplier;
      private final EventHandler<ActionEvent> onFinish;

      private JFXAnimationCreatorConfig(Builder builder) {
        durationSupplier = builder.durationSupplier;
        cycleCountSupplier = builder.cycleCountSupplier;
        autoReverseSupplier = builder.autoReverseSupplier;
        interpolatorSupplier = builder.interpolatorSupplier;
        delaySupplier = builder.delaySupplier;
        rateSupplier = builder.rateSupplier;
        onFinish = builder.onFinish;
      }

      public static JFXAnimationCreatorConfig.Builder builder() {
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

        public Builder duration(Duration duration) {
          return duration(() -> duration);
        }

        public Builder duration(Supplier<Duration> durationSupplier) {
          this.durationSupplier = durationSupplier;
          return this;
        }

        public Builder cycleCount(int cycleCount) {
          return cycleCount(() -> cycleCount);
        }

        public Builder cycleCount(Supplier<Integer> cycleCountSupplier) {
          this.cycleCountSupplier = cycleCountSupplier;
          return this;
        }

        public Builder infiniteCycle() {
          return cycleCount(Animation.INDEFINITE);
        }

        public Builder autoReverse(boolean reverse) {
          return autoReverse(() -> reverse);
        }

        public Builder autoReverse(Supplier<Boolean> reverseSupplier) {
          this.autoReverseSupplier = reverseSupplier;
          return this;
        }

        public Builder interpolator(Interpolator interpolator) {
          return interpolator(() -> interpolator);
        }

        public Builder interpolator(Supplier<Interpolator> interpolatorSupplier) {
          this.interpolatorSupplier = interpolatorSupplier;
          return this;
        }

        public Builder delay(Duration delay) {
          return delay(() -> delay);
        }

        public Builder delay(Supplier<Duration> delaySupplier) {
          this.delaySupplier = delaySupplier;
          return this;
        }

        public Builder rate(double rate) {
          return rate(() -> rate);
        }

        public Builder rate(Supplier<Double> rateSupplier) {
          this.rateSupplier = rateSupplier;
          return this;
        }

        public Builder onFinish(EventHandler<ActionEvent> onFinish) {
          this.onFinish = onFinish;
          return this;
        }

        public JFXAnimationCreatorConfig build() {
          return new JFXAnimationCreatorConfig(this);
        }
    }
}
