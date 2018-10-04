package com.jfoenix.transitions.creator;

import javafx.animation.Interpolator;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public final class JFXAnimationTemplateAction<N, T> {

  private final Stream<Function<N, WritableValue<T>>> targetFunctions;
  private final Function<N, T> endValueSupplier;
  private final Function<N, Interpolator> interpolatorSupplier;
  private final boolean animateWhen;
  private final BiConsumer<N, ActionEvent> onFinish;
  private final N animationObject;

  private JFXAnimationTemplateAction(Builder<N, T> builder) {
    targetFunctions = builder.targetFunctions;
    endValueSupplier = builder.endValueFunction;
    interpolatorSupplier = builder.interpolatorFunction;
    animationObject = builder.animationHelper;
    animateWhen = builder.animateWhenPredicate.test(animationObject);
    onFinish = builder.onFinish;
  }

  public static <N> InitBuilder<N> builder(Class<N> animationObjectType) {
    return new InitBuilder<>(
        animationObjectType, JFXAnimationTemplate.DEFAULT_ANIMATION_OBJECT_KEY);
  }

  public static InitBuilder<Node> builder() {
    return builder(Node.class);
  }

  public Stream<Function<N, WritableValue<T>>> getTargetFunctions() {
    return targetFunctions;
  }

  public Optional<WritableValue<T>> getFirstTarget() {
    return getTargetFunctions().findFirst().map(function -> function.apply(animationObject));
  }

  public T getEndValue() {
    return endValueSupplier.apply(animationObject);
  }

  public Interpolator getInterpolator() {
    return interpolatorSupplier.apply(animationObject);
  }

  public void handleOnFinish(ActionEvent actionEvent) {
    if (animateWhen) {
      onFinish.accept(animationObject, actionEvent);
    }
  }

  @SuppressWarnings("unchecked")
  public <M> Stream<M> mapTo(Function<WritableValue<Object>, M> mappingFunction) {
    return animateWhen
        ? getTargetFunctions()
            .map(
                function ->
                    mappingFunction.apply((WritableValue<Object>) function.apply(animationObject)))
        : Stream.empty();
  }

  public static final class Builder<N, T> {

    private final Stream<Function<N, WritableValue<T>>> targetFunctions;
    private final InitBuilder<N> initBuilder;
    private Function<N, T> endValueFunction = node -> null;
    private Function<N, Interpolator> interpolatorFunction = node -> null;
    private Predicate<N> animateWhenPredicate = node -> true;
    private BiConsumer<N, ActionEvent> onFinish = (node, event) -> {};
    private N animationHelper;

    private Builder(
        InitBuilder<N> initBuilder, Stream<Function<N, WritableValue<T>>> targetFunctions) {
      this.initBuilder = initBuilder;
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

    public JFXAnimationTemplateAction<N, T> build(Function<String, ?> buildFunction) {
      this.animationHelper =
          initBuilder.animationObjectType.cast(buildFunction.apply(initBuilder.animationObjectId));
      return new JFXAnimationTemplateAction<>(this);
    }

    public JFXAnimationTemplateAction<N, T> build() {
      return new JFXAnimationTemplateAction<>(this);
    }
  }

  public static final class InitBuilder<N> {

    private final Class<N> animationObjectType;
    private final String animationObjectId;

    private InitBuilder(Class<N> animationObjectType, String animationObjectId) {
      this.animationObjectType = animationObjectType;
      this.animationObjectId = animationObjectId;
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
          this, Stream.concat(Stream.of(targetFunction), Stream.of(targetFunctions)));
    }

    public final Builder<N, ?> animateWhen(boolean animateWhen) {
      return new Builder<>(this, Stream.empty()).animateWhen(animateWhen);
    }

    public final Builder<N, ?> animateWhen(Predicate<N> animateWhenPredicate) {
      return new Builder<>(this, Stream.empty()).animateWhen(animateWhenPredicate);
    }

    public Builder<N, ?> ignoreAnimation() {
      return new Builder<>(this, Stream.empty()).ignoreAnimation();
    }

    public Builder<N, ?> onFinish(BiConsumer<N, ActionEvent> onFinish) {
      return new Builder<>(this, Stream.empty()).onFinish(onFinish);
    }

    public <T> InitBuilder<T> withAnimationObject(Class<T> clazz, String classKey) {
      return new InitBuilder<>(clazz, classKey);
    }
  }
}
