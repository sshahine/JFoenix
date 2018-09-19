package com.jfoenix.transitions;

import javafx.animation.*;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-18
 */
public class JFXAnimation<N extends Node> {

    private final Builder<N> builder;

    private JFXAnimation(Builder<N> builder) {
        this.builder = builder;
    }

    public static <N extends Node> AnimationProcess<N> builder(Class<N> clazz) {
        return new Builder<>();
    }

    public static AnimationProcess<Node> builder() {
        return builder(Node.class);
    }

    private Timeline buildAnimation() {

        Timeline timeline = new Timeline();
        JFXAnimationConfig animationConfig = builder.animationConfigBuilder.build();

        builder.animationValueBuilders.forEach(
            (percent, animationValueBuilders) -> {

                // calc the percentage duration of total duration.
                Duration percentageDuration = animationConfig.getDuration().multiply((percent / 100));

                // Build the animation values once and store.
                List<JFXAnimationValue<N, ?>> animationValues =
                    animationValueBuilders
                        .stream()
                        .map(builder -> builder.build(this.builder.animationNode))
                        .collect(Collectors.toList());

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

    public static final class Builder<N extends Node> implements AnimationConfig<N> {

        private final Set<Double> percents = new HashSet<>();
        private final Map<Double, List<JFXAnimationValue.Builder<N, ?>>> animationValueBuilders =
            new HashMap<>();
        private N animationNode;
        private JFXAnimationConfig.Builder animationConfigBuilder;
        private boolean clearPercents;

        private Builder() {}

        @Override
        public AnimationAction<N> percent(double first, double... rest) {
            if (clearPercents) {
                percents.clear();
                clearPercents = false;
            }
            // Clamp value between 0 and 100.
            percents.add(Math.max(0, Math.min(100, first)));
            animationValueBuilders.put(first, new ArrayList<>());

            for (double percent : rest) {
                percents.add(Math.max(0, Math.min(100, percent)));
                animationValueBuilders.put(percent, new ArrayList<>());
            }
            return this;
        }

        @Override
        public AnimationConfig<N> action(
            Function<JFXAnimationValue.GenericBuilderWrapper<N>, JFXAnimationValue.Builder<N, ?>>
                valueBuilderFunction) {
            for (Double percent : percents) {
                animationValueBuilders
                    .get(percent)
                    //TODO create builder in buildAnimation method
                    .add(valueBuilderFunction.apply(JFXAnimationValue.builder()));
            }
            clearPercents = true;
            return this;
        }

        @Override
        public Builder<N> config(
            Function<JFXAnimationConfig.Builder, JFXAnimationConfig.Builder> configBuilderFunction) {
            //TODO create builder in buildAnimation method
            animationConfigBuilder = configBuilderFunction.apply(JFXAnimationConfig.builder());
            return this;
        }

        public Timeline build(N animationNode) {
            this.animationNode = animationNode;
            return new JFXAnimation<>(this).buildAnimation();
        }
    }

    public static final class JFXAnimationValue<N extends Node, T> {

        private final Stream<Function<N, WritableValue<T>>> targetFunctions;
        private final Function<N, T> endValueSupplier;
        private final Function<N, Interpolator> interpolatorSupplier;
        private final boolean animateWhen;
        private final BiConsumer<N, ActionEvent> onFinish;
        private final N animationNode;

        private JFXAnimationValue(Builder<N, T> builder) {
            targetFunctions = builder.targetFunctions;
            endValueSupplier = builder.endValueFunction;
            interpolatorSupplier = builder.interpolatorFunction;
            animationNode = builder.animationNode;
            animateWhen = builder.animateWhenPredicate.test(animationNode);
            onFinish = builder.onFinish;
        }

        public static <N extends Node> GenericBuilderWrapper<N> builder() {
            return new GenericBuilderWrapper<>();
        }

        public Stream<Function<N, WritableValue<T>>> getTargetFunctions() {
            return targetFunctions;
        }

        public T getEndValue() {
            return endValueSupplier.apply(animationNode);
        }

        public Interpolator getInterpolator() {
            return interpolatorSupplier.apply(animationNode);
        }

        public void handleOnFinish(ActionEvent actionEvent) {
            if (animateWhen) {
                onFinish.accept(animationNode, actionEvent);
            }
        }

        public Stream<KeyValue> toKeyValues(Interpolator globalInterpolator) {
            Interpolator interpolator = getInterpolator();
            return animateWhen
                ? getTargetFunctions()
                .map(
                    function ->
                        new KeyValue(
                            function.apply(animationNode),
                            getEndValue(),
                            interpolator == null ? globalInterpolator : interpolator))
                : Stream.empty();
        }

        public static final class Builder<N extends Node, T> {

            private final Stream<Function<N, WritableValue<T>>> targetFunctions;
            private Function<N, T> endValueFunction = node -> null;
            private Function<N, Interpolator> interpolatorFunction = node -> null;
            private Predicate<N> animateWhenPredicate = node -> true;
            private BiConsumer<N, ActionEvent> onFinish = (node, event) -> {};
            private N animationNode;

            private Builder(Stream<Function<N, WritableValue<T>>> targetFunctions) {
                this.targetFunctions = targetFunctions;
            }

            public Builder<N, T> endValue(T endValue) {
                return endValue(node -> endValue);
            }

            public Builder<N, T> endValue(Function<N, T> endValueFunction) {
                this.endValueFunction = endValueFunction;
                return this;
            }

            public Builder<N, T> interpolator(Interpolator interpolator) {
                return interpolator(node -> interpolator);
            }

            public Builder<N, T> interpolator(Function<N, Interpolator> interpolatorFunction) {
                this.interpolatorFunction = interpolatorFunction;
                return this;
            }

            public Builder<N, T> animateWhen(boolean animateWhen) {
                return animateWhen(n -> animateWhen);
            }

            public Builder<N, T> animateWhen(Predicate<N> animateWhenPredicate) {
                this.animateWhenPredicate = animateWhenPredicate;
                return this;
            }

            public Builder<N, T> ignoreAnimation() {
                return animateWhen(false);
            }

            public Builder<N, T> onFinish(BiConsumer<N, ActionEvent> onFinish) {
                this.onFinish = onFinish;
                return this;
            }

            public JFXAnimationValue<N, T> build(N animationNode) {
                this.animationNode = animationNode;
                return new JFXAnimationValue<>(this);
            }
        }

        public static final class GenericBuilderWrapper<N extends Node> {
            // Instance method wraps the value builder target method for convenience.
            @SafeVarargs
            public final <T> JFXAnimationValue.Builder<N, T> target(
                WritableValue<T> target, WritableValue<T>... targets) {
                Function<N, WritableValue<T>>[] targetFunctions =
                    Stream.of(targets)
                          .map(value -> (Function<N, WritableValue<T>>) n -> value)
                          .toArray((IntFunction<Function<N, WritableValue<T>>[]>) Function[]::new);
                return target(node -> target, targetFunctions);
            }

            // Instance method wraps the value builder target method for convenience.
            @SafeVarargs
            public final <T> JFXAnimationValue.Builder<N, T> target(
                Function<N, WritableValue<T>> targetFunction,
                Function<N, WritableValue<T>>... targetFunctions) {
                return new Builder<>(Stream.concat(Stream.of(targetFunction), Stream.of(targetFunctions)));
            }

            public final JFXAnimationValue.Builder<N, ?> noTargets() {
                return new Builder<>(Stream.empty());
            }
        }
    }

    public static final class JFXAnimationConfig {

        private final Supplier<Duration> durationSupplier;
        private final Supplier<Integer> cycleCountSupplier;
        private final Supplier<Boolean> autoReverseSupplier;
        private final Supplier<Interpolator> interpolatorSupplier;
        private final Supplier<Duration> delaySupplier;
        private final Supplier<Double> rateSupplier;
        private final EventHandler<ActionEvent> onFinish;

        private JFXAnimationConfig(Builder builder) {
            durationSupplier = builder.durationSupplier;
            cycleCountSupplier = builder.cycleCountSupplier;
            autoReverseSupplier = builder.autoReverseSupplier;
            interpolatorSupplier = builder.interpolatorSupplier;
            delaySupplier = builder.delaySupplier;
            rateSupplier = builder.rateSupplier;
            onFinish = builder.onFinish;
        }

        public static JFXAnimationConfig.Builder builder() {
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

            public JFXAnimationConfig build() {
                return new JFXAnimationConfig(this);
            }
        }
    }

    public interface AnimationProcess<N extends Node> {

        AnimationAction<N> percent(double percent, double... percents);

        default AnimationAction<N> from() {
            return percent(0);
        }

        default AnimationAction<N> to() {
            return percent(100);
        }
    }

    public interface AnimationAction<N extends Node> extends AnimationProcess<N> {

        AnimationConfig<N> action(
            Function<JFXAnimationValue.GenericBuilderWrapper<N>, JFXAnimationValue.Builder<N, ?>>
                valueBuilderFunction);

        default AnimationConfig<N> action(JFXAnimationValue.Builder<N, ?> animationValueBuilder) {
            return action(builder -> animationValueBuilder);
        }
    }

    public interface AnimationConfig<N extends Node> extends AnimationAction<N> {

        Builder<N> config(
            Function<JFXAnimationConfig.Builder, JFXAnimationConfig.Builder> configBuilderFunction);

        default Builder<N> config(JFXAnimationConfig.Builder configBuilder) {
            return config(builder -> configBuilder);
        }
    }
}
