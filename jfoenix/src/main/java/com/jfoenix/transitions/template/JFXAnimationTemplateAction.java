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

import javafx.animation.Interpolator;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-22
 */
public final class JFXAnimationTemplateAction<N, T> {

  private final Collection<Function<N, WritableValue<T>>> targetFunctions;
  private final Function<N, T> endValueSupplier;
  private final Function<N, Interpolator> interpolatorSupplier;
  private final boolean animateWhen;
  private final BiConsumer<N, ActionEvent> onFinish;
  private final N animationObject;

  private JFXAnimationTemplateAction(Builder<N, T> builder) {
    targetFunctions = builder.targetFunctions;
    endValueSupplier = builder.endValueFunction;
    interpolatorSupplier = builder.interpolatorFunction;
    animationObject = builder.animationObject;
    animateWhen = builder.animateWhenPredicate.test(animationObject);
    onFinish = builder.onFinish;
  }

  public static <N> InitBuilder<N> builder(Class<N> animationObjectType) {
    return new InitBuilder<>(
        animationObjectType,
        JFXTemplateBuilder.JFXAnimationObjectMapBuilder.DEFAULT_ANIMATION_OBJECT_NAME);
  }

  public static InitBuilder<Node> builder() {
    return builder(Node.class);
  }

  public Collection<Function<N, WritableValue<T>>> getTargetFunctions() {
    return targetFunctions;
  }

  public Optional<WritableValue<T>> getFirstTarget() {
    return getTargetFunctions()
        .stream()
        .findFirst()
        .map(function -> function.apply(animationObject));
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
            .stream()
            .map(
                function ->
                    mappingFunction.apply((WritableValue<Object>) function.apply(animationObject)))
        : Stream.empty();
  }

  public static final class Builder<N, T> {

    private final Collection<Function<N, WritableValue<T>>> targetFunctions;
    private final InitBuilder<N> initBuilder;
    private Function<N, T> endValueFunction = node -> null;
    private Function<N, Interpolator> interpolatorFunction = node -> null;
    private Predicate<N> animateWhenPredicate = node -> true;
    private BiConsumer<N, ActionEvent> onFinish = (node, event) -> {};
    private N animationObject;

    private Builder(
        InitBuilder<N> initBuilder, Collection<Function<N, WritableValue<T>>> targetFunctions) {
      this.initBuilder = initBuilder;
      this.targetFunctions = targetFunctions;
    }

    private Builder(InitBuilder<N> initBuilder) {
      this(initBuilder, Collections.emptyList());
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

    public JFXAnimationTemplateAction<N, T> build(
        Function<Collection<String>, Object> buildFunction) {
      this.animationObject =
          initBuilder.animationObjectType.cast(buildFunction.apply(initBuilder.animationObjectNames));
      return new JFXAnimationTemplateAction<>(this);
    }

    public Stream<JFXAnimationTemplateAction<N, T>> buildActions(
        Function<Collection<String>, Collection<Object>> buildFunction) {
      return buildFunction
          .apply(initBuilder.animationObjectNames)
          .stream()
          .map(
              animationObject -> {
                this.animationObject = initBuilder.animationObjectType.cast(animationObject);
                return new JFXAnimationTemplateAction<>(this);
              });
    }

    public JFXAnimationTemplateAction<N, T> build() {
      return new JFXAnimationTemplateAction<>(this);
    }
  }

  public static final class InitBuilder<N> {

    private final Class<N> animationObjectType;
    private final Collection<String> animationObjectNames = new ArrayList<>();

    private InitBuilder(
        Class<N> animationObjectType, String animationObjectId, String... animationObjectNames) {
      this.animationObjectType = animationObjectType;
      this.animationObjectNames.add(animationObjectId);
      this.animationObjectNames.addAll(Arrays.asList(animationObjectNames));
    }

    @SafeVarargs
    public final <T> Builder<N, T> target(WritableValue<T> target, WritableValue<T>... targets) {
      Function<N, WritableValue<T>>[] targetFunctions =
          Stream.of(targets)
              .map(value -> (Function<N, WritableValue<T>>) n -> value)
              .toArray((IntFunction<Function<N, WritableValue<T>>[]>) Function[]::new);
      return target(node -> target, targetFunctions);
    }

    @SafeVarargs
    public final <T> Builder<N, T> target(
        Function<N, WritableValue<T>> targetFunction,
        Function<N, WritableValue<T>>... targetFunctions) {
      Collection<Function<N, WritableValue<T>>> functions = new ArrayList<>();
      functions.add(targetFunction);
      functions.addAll(Arrays.asList(targetFunctions));
      return new Builder<>(this, functions);
    }

    public final Builder<N, ?> animateWhen(boolean animateWhen) {
      return new Builder<>(this).animateWhen(animateWhen);
    }

    public final Builder<N, ?> animateWhen(Predicate<N> animateWhenPredicate) {
      return new Builder<>(this).animateWhen(animateWhenPredicate);
    }

    public Builder<N, ?> ignoreAnimation() {
      return new Builder<>(this).ignoreAnimation();
    }

    public Builder<N, ?> onFinish(BiConsumer<N, ActionEvent> onFinish) {
      return new Builder<>(this).onFinish(onFinish);
    }

    public <T> InitBuilder<T> withAnimationObject(
        Class<T> clazz, String animationObjectName, String... animationObjectNames) {
      return new InitBuilder<>(clazz, animationObjectName, animationObjectNames);
    }
  }
}
