package com.jfoenix.transitions.creator;

import com.jfoenix.transitions.JFXKeyValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public final class JFXAnimationCreatorValue<N, T> {

  private final Stream<Function<N, WritableValue<T>>> targetFunctions;
  private final Function<N, T> endValueSupplier;
  private final Function<N, Interpolator> interpolatorSupplier;
  private final boolean animateWhen;
  private final BiConsumer<N, ActionEvent> onFinish;
  private final N animationHelper;
  private final String helperObjectKey;
  private final Class<N> mainHelperType;

  private JFXAnimationCreatorValue(Builder<N, T> builder) {
    targetFunctions = builder.targetFunctions;
    endValueSupplier = builder.endValueFunction;
    interpolatorSupplier = builder.interpolatorFunction;
    animationHelper = builder.animationHelper;
    animateWhen = builder.animateWhenPredicate.test(animationHelper);
    onFinish = builder.onFinish;
    helperObjectKey = builder.helperObjectKey;
    mainHelperType = builder.mainHelperType;
  }

  public static <N> GenericBuilderWrapper<N> builder(Class<N> clazz) {
    return new GenericBuilderWrapper<>(clazz, JFXAnimationCreator.DEFAULT_CLASS_KEY);
  }

  public static GenericBuilderWrapper<Node> builder() {
    return builder(Node.class);
  }

  public Stream<Function<N, WritableValue<T>>> getTargetFunctions() {
    return targetFunctions;
  }

  public Optional<WritableValue<T>> getFirstTarget() {
    return getTargetFunctions().findFirst().map(function -> function.apply(animationHelper));
  }

  public T getEndValue() {
    return endValueSupplier.apply(animationHelper);
  }

  public Interpolator getInterpolator() {
    return interpolatorSupplier.apply(animationHelper);
  }

  public void handleOnFinish(ActionEvent actionEvent) {
    if (animateWhen) {
      onFinish.accept(animationHelper, actionEvent);
    }
  }

  public String getHelperObjectKey() {
    return helperObjectKey;
  }

  public Optional<Class<N>> getMainHelperType() {
    return Optional.ofNullable(mainHelperType);
  }

  public Stream<KeyValue> toKeyValues(Interpolator globalInterpolator) {
    Interpolator interpolator = getInterpolator();
    return animateWhen
        ? getTargetFunctions()
            .map(
                function ->
                    new KeyValue(
                        function.apply(animationHelper),
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
                        .setTarget(function.apply(animationHelper))
                        .setEndValue(getEndValue())
                        .setInterpolator(interpolator == null ? globalInterpolator : interpolator)
                        .setAnimateCondition(() -> true)
                        .build())
        : Stream.empty();
  }

  public static final class Builder<N, T> {

    private final String helperObjectKey;
    private final Class<N> mainHelperType;
    private final Stream<Function<N, WritableValue<T>>> targetFunctions;
    private Function<N, T> endValueFunction = node -> null;
    private Function<N, Interpolator> interpolatorFunction = node -> null;
    private Predicate<N> animateWhenPredicate = node -> true;
    private BiConsumer<N, ActionEvent> onFinish = (node, event) -> {};
    private N animationHelper;

    private Builder(
        Class<N> mainHelperType, String helperObjectKey, Stream<Function<N, WritableValue<T>>> targetFunctions) {
      this.mainHelperType = mainHelperType;
      this.helperObjectKey = helperObjectKey;
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

    public JFXAnimationCreatorValue<N, T> build(Function<String, ?> buildFunction) {
      this.animationHelper = mainHelperType == null ? null : mainHelperType.cast(buildFunction.apply(helperObjectKey));
      return new JFXAnimationCreatorValue<>(this);
    }

    public JFXAnimationCreatorValue<N, T> build() {
      return new JFXAnimationCreatorValue<>(this);
    }
  }

  public static final class GenericBuilderWrapper<N> {

    private final String classKey;
    private final Class<N> clazz;

    private GenericBuilderWrapper(Class<N> clazz, String classKey) {
      this.clazz = clazz;
      this.classKey = classKey;
    }

    // Instance method wraps the value create target method for convenience.
    @SafeVarargs
    public final <T> Builder<N, T> target(WritableValue<T> target, WritableValue<T>... targets) {
      Function<N, WritableValue<T>>[] targetFunctions =
          Stream.of(targets)
              .map(value -> (Function<N, WritableValue<T>>) n -> value)
              .toArray((IntFunction<Function<N, WritableValue<T>>[]>) Function[]::new);
      return target(node -> target, targetFunctions);
    }

    // Instance method wraps the value create target method for convenience.
    @SafeVarargs
    public final <T> Builder<N, T> target(
        Function<N, WritableValue<T>> targetFunction,
        Function<N, WritableValue<T>>... targetFunctions) {
      return new Builder<>(
          clazz, classKey, Stream.concat(Stream.of(targetFunction), Stream.of(targetFunctions)));
    }

    public final Builder<N, ?> noTargets() {
      return new Builder<>(clazz, classKey, Stream.empty());
    }

    public <T> GenericBuilderWrapper<T> withClass(Class<T> clazz, String classKey) {
      return new GenericBuilderWrapper<>(clazz, classKey);
    }
  }
}
