package com.jfoenix.transitions;

import javafx.animation.*;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-18
 */
public class JFXAnimation<N> {

  private static final String DEFAULT_CLASS_KEY = "_DefaultKey_";
  private final Builder<N> builder;

  private JFXAnimation(Builder<N> builder) {
    this.builder = builder;
  }

  public static <N> AnimationProcess<N> builder(Class<N> clazz) {
    return new Builder<>(clazz);
  }

  public static AnimationProcess<Node> builder() {
    return builder(Node.class);
  }

  private Timeline buildTimeline() {

    Timeline timeline = new Timeline();
    JFXAnimationConfig animationConfig =
        builder.animationConfigBuilderFunction.apply(JFXAnimationConfig.builder()).build();

    builder.animationValueBuilderFunctions.forEach(
        (percent, animationValueBuilderFunctions) -> {

          // calc the percentage duration of total duration.
          Duration percentageDuration = animationConfig.getDuration().multiply((percent / 100));

          // Build the animation values once and store.
          List<JFXAnimationValue<?, ?>> animationValues =
              animationValueBuilderFunctions
                  .stream()
                  .map(
                      builderFunction ->
                          builderFunction
                              .apply(JFXAnimationValue.builder(builder.clazz))
                              .build(this.builder.animationsTargets::get))
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

  private JFXAnimationTimer buildAnimationTimer() {

    JFXAnimationTimer animationTimer = new JFXAnimationTimer();
    JFXAnimationConfig animationConfig =
        builder.animationConfigBuilderFunction.apply(JFXAnimationConfig.builder()).build();

    builder.animationValueBuilderFunctions.forEach(
        (percent, animationValueBuilderFunctions) -> {

          // calc the percentage duration of total duration.
          Duration percentageDuration = animationConfig.getDuration().multiply((percent / 100));

          // Build the animation values once and store.
          List<JFXAnimationValue<?, ?>> animationValues =
              animationValueBuilderFunctions
                  .stream()
                  .map(
                      builderFunction ->
                          builderFunction
                              .apply(JFXAnimationValue.builder(builder.clazz))
                              .build(this.builder.animationsTargets::get))
                  .collect(Collectors.toList());

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

  public static final class Builder<N> implements AnimationConfig<N> {

    private final Set<Double> percents = new HashSet<>();
    private final Map<
            Double,
            List<
                Function<
                    JFXAnimationValue.GenericBuilderWrapper<N>, JFXAnimationValue.Builder<?, ?>>>>
        animationValueBuilderFunctions = new HashMap<>();
    private final Map<String, Object> animationsTargets = new HashMap<>();
    private final Class<N> clazz;
    private Function<JFXAnimationConfig.Builder, JFXAnimationConfig.Builder>
        animationConfigBuilderFunction;
    private boolean clearPercents;

    private Builder(Class<N> clazz) {
      this.clazz = clazz;
    }

    @Override
    public AnimationAction<N> percent(double first, double... rest) {
      if (clearPercents) {
        percents.clear();
        clearPercents = false;
      }
      // Clamp value between 0 and 100.
      percents.add(Math.max(0, Math.min(100, first)));
      animationValueBuilderFunctions.put(first, new ArrayList<>());

      for (double percent : rest) {
        percents.add(Math.max(0, Math.min(100, percent)));
        animationValueBuilderFunctions.put(percent, new ArrayList<>());
      }
      return this;
    }

    @Override
    public AnimationConfig<N> action(
        Function<JFXAnimationValue.GenericBuilderWrapper<N>, JFXAnimationValue.Builder<?, ?>>
            valueBuilderFunction) {
      for (Double percent : percents) {
        animationValueBuilderFunctions.get(percent).add(valueBuilderFunction);
      }
      clearPercents = true;
      return this;
    }

    @Override
    public Builder<N> config(
        Function<JFXAnimationConfig.Builder, JFXAnimationConfig.Builder> configBuilderFunction) {
      animationConfigBuilderFunction = configBuilderFunction;
      return this;
    }

    @SafeVarargs
    public final Timeline build(N animationNode, Pair<String, ?>... animationNodes) {
      animationsTargets.put(DEFAULT_CLASS_KEY, animationNode);
      for (Pair<String, ?> pair : animationNodes) {
        animationsTargets.put(pair.getKey(), pair.getValue());
      }
      return new JFXAnimation<>(this).buildTimeline();
    }

    @SafeVarargs
    public final JFXAnimationTimer buildJFXAnimationTimer(
        N animationNode, Pair<String, ?>... animationNodes) {
      animationsTargets.put(DEFAULT_CLASS_KEY, animationNode);
      for (Pair<String, ?> pair : animationNodes) {
        animationsTargets.put(pair.getKey(), pair.getValue());
      }
      return new JFXAnimation<>(this).buildAnimationTimer();
    }
  }

  public static final class JFXAnimationValue<N, T> {

    private final Stream<Function<N, WritableValue<T>>> targetFunctions;
    private final Function<N, T> endValueSupplier;
    private final Function<N, Interpolator> interpolatorSupplier;
    private final boolean animateWhen;
    private final BiConsumer<N, ActionEvent> onFinish;
    private final N animationNode;
    private final String classKey;
    private final Class<N> clazz;

    private JFXAnimationValue(Builder<N, T> builder) {
      targetFunctions = builder.targetFunctions;
      endValueSupplier = builder.endValueFunction;
      interpolatorSupplier = builder.interpolatorFunction;
      animationNode = builder.animationNode;
      animateWhen = builder.animateWhenPredicate.test(animationNode);
      onFinish = builder.onFinish;
      classKey = builder.classKey;
      clazz = builder.clazz;
    }

    public static <N> GenericBuilderWrapper<N> builder(Class<N> clazz) {
      return new GenericBuilderWrapper<>(clazz, DEFAULT_CLASS_KEY);
    }

    public static GenericBuilderWrapper<Node> builder() {
      return builder(Node.class);
    }

    public Stream<Function<N, WritableValue<T>>> getTargetFunctions() {
      return targetFunctions;
    }

    public Optional<WritableValue<T>> getFirstTarget() {
      return getTargetFunctions().findFirst().map(function -> function.apply(animationNode));
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

    public String getClassKey() {
      return classKey;
    }

    public Optional<Class<N>> getClazz() {
      return Optional.ofNullable(clazz);
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

    public Stream<JFXKeyValue> toJFXKeyValues(Interpolator globalInterpolator) {
      Interpolator interpolator = getInterpolator();
      return animateWhen
          ? getTargetFunctions()
              .map(
                  function ->
                      JFXKeyValue.builder()
                          .setTarget(function.apply(animationNode))
                          .setEndValue(getEndValue())
                          .setInterpolator(interpolator == null ? globalInterpolator : interpolator)
                          .setAnimateCondition(() -> true)
                          .build())
          : Stream.empty();
    }

    public static final class Builder<N, T> {

      private final String classKey;
      private final Class<N> clazz;
      private final Stream<Function<N, WritableValue<T>>> targetFunctions;
      private Function<N, T> endValueFunction = node -> null;
      private Function<N, Interpolator> interpolatorFunction = node -> null;
      private Predicate<N> animateWhenPredicate = node -> true;
      private BiConsumer<N, ActionEvent> onFinish = (node, event) -> {};
      private N animationNode;

      private Builder(
          Class<N> clazz, String classKey, Stream<Function<N, WritableValue<T>>> targetFunctions) {
        this.clazz = clazz;
        this.classKey = classKey;
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

      public JFXAnimationValue<N, T> build(Function<String, ?> buildFunction) {
        this.animationNode = clazz == null ? null : clazz.cast(buildFunction.apply(classKey));
        return new JFXAnimationValue<>(this);
      }

      public JFXAnimationValue<N, T> build() {
        return new JFXAnimationValue<>(this);
      }
    }

    public static final class GenericBuilderWrapper<N> {

      private final String classKey;
      private final Class<N> clazz;

      private GenericBuilderWrapper(Class<N> clazz, String classKey) {
        this.clazz = clazz;
        this.classKey = classKey;
      }

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
        return new Builder<>(
            clazz, classKey, Stream.concat(Stream.of(targetFunction), Stream.of(targetFunctions)));
      }

      public final JFXAnimationValue.Builder<N, ?> noTargets() {
        return new Builder<>(clazz, classKey, Stream.empty());
      }

      public <T> JFXAnimationValue.GenericBuilderWrapper<T> withClass(
          Class<T> clazz, String classKey) {
        return new GenericBuilderWrapper<>(clazz, classKey);
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

  public interface AnimationProcess<N> {

    AnimationAction<N> percent(double percent, double... percents);

    default AnimationAction<N> from() {
      return percent(0);
    }

    default AnimationAction<N> to() {
      return percent(100);
    }
  }

  public interface AnimationAction<N> extends AnimationProcess<N> {

    AnimationConfig<N> action(
        Function<JFXAnimationValue.GenericBuilderWrapper<N>, JFXAnimationValue.Builder<?, ?>>
            valueBuilderFunction);

    default AnimationConfig<N> action(JFXAnimationValue.Builder<?, ?> animationValueBuilder) {
      return action(builder -> animationValueBuilder);
    }
  }

  public interface AnimationConfig<N> extends AnimationAction<N> {

    Builder<N> config(
        Function<JFXAnimationConfig.Builder, JFXAnimationConfig.Builder> configBuilderFunction);

    default Builder<N> config(JFXAnimationConfig.Builder configBuilder) {
      return config(builder -> configBuilder);
    }
  }
}
