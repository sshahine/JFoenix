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

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class which represents a general {@link JFXAnimationTemplate}. <br>
 * This class is responsible for providing methods where it's possible to build a CSS like
 * structured animation.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-18
 */
public class JFXAnimationTemplate<N> implements JFXTemplateConfig<N>, JFXTemplateBuilder<N> {

  private static final String NO_KEY_FOUND_MESSAGE =
      "No animation objects with key \"%s\" found.\n"
          + "Please check your build method where the "
          + "namedAnimationObjects are defined or your "
          + "withAnimationObject methods where the keys are accessed.";

  private final Set<ActionKey> actionKeys = new HashSet<>();
  private final Map<
          ActionKey,
          List<
              Function<
                  JFXAnimationTemplateAction.InitBuilder<N>,
                  JFXAnimationTemplateAction.Builder<?, ?>>>>
      actionBuilderFunctionMap = new HashMap<>();
  private final Class<N> animationObjectType;
  private Map<String, Collection<Object>> animationObjects;
  private Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
      configBuilderFunction;
  private boolean clearActionKeys;

  private JFXAnimationTemplate(Class<N> animationObjectType) {
    this.animationObjectType = animationObjectType;
  }

  /**
   * Create a {@link JFXTemplateProcess} with a specific default animation type.<br>
   * The default animation objects with this type are set later in the {@link
   * JFXTemplateBuilder#build(Object)} method.<br>
   * These objects are generally used in {@link JFXTemplateAction#action(Function)} methods.
   *
   * @param animationObjectType a specific animation object type.
   * @param <N> the specific type.
   * @return a {@link JFXTemplateProcess} instance.
   */
  public static <N> JFXTemplateProcess<N> create(Class<N> animationObjectType) {
    return new JFXAnimationTemplate<>(animationObjectType);
  }

  /**
   * Same method as {@link #create(Class)} but with the default type {@link Node}. This type is a
   * general default type in a {@link JFXAnimationTemplate}.
   *
   * @see #create(Class)
   * @return a {@link JFXTemplateProcess} instance.
   */
  public static JFXTemplateProcess<Node> create() {
    return create(Node.class);
  }

  public Map<ActionKey, List<JFXAnimationTemplateAction<?, ?>>> buildAndGetActions() {
    return buildAndGetActions(Function.identity());
  }

  public <KM> Map<KM, List<JFXAnimationTemplateAction<?, ?>>> buildAndGetActions(
      Function<ActionKey, KM> keyMappingFunction) {

    Map<KM, List<JFXAnimationTemplateAction<?, ?>>> actionMap = new HashMap<>();
    actionBuilderFunctionMap.forEach(
        (key, actionBuilderFunctions) -> {
          List<JFXAnimationTemplateAction<?, ?>> actions =
              actionBuilderFunctions
                  .stream()
                  .flatMap(
                      builderFunction ->
                          builderFunction
                              .apply(JFXAnimationTemplateAction.builder(animationObjectType))
                              .buildActions(this::getAnimationObjectsByKeys))
                  .collect(Collectors.toList());
          actionMap.put(keyMappingFunction.apply(key), actions);
        });
    return actionMap;
  }

  private List<Object> getAnimationObjectsByKeys(Collection<String> keys) {
    List<Object> animationObjectList = new ArrayList<>();
    for (String key : keys) {
      Collection<?> animationObjectsPerKey = animationObjects.get(key);
      if (animationObjectsPerKey == null) {
        throw new NoSuchElementException(String.format(NO_KEY_FOUND_MESSAGE, key));
      }
      animationObjectList.addAll(animationObjectsPerKey);
    }
    return animationObjectList;
  }

  public JFXAnimationTemplateConfig buildAndGetConfig() {
    return configBuilderFunction.apply(JFXAnimationTemplateConfig.builder()).build();
  }

  @Override
  public JFXTemplateAction<N> percent(double first, double... rest) {
    tryClearActionKeys();

    // Ignore if smaller 0% or bigger 100%
    if (first >= 0 && first <= 100) {
      saveActionKey(new ActionKey(first));
    }

    for (double percent : rest) {
      if (first >= 0 && first <= 100) {
        saveActionKey(new ActionKey(percent));
      }
    }
    return this;
  }

  @Override
  public JFXTemplateAction<N> from() {
    return percent(0);
  }

  @Override
  public JFXTemplateAction<N> to() {
    return percent(100);
  }

  @Override
  public JFXTemplateAction<N> time(Duration time, Duration... times) {
    tryClearActionKeys();

    if (time != null) {
      saveActionKey(new ActionKey(time));
    }

    for (Duration elem : times) {
      if (elem != null) {
        saveActionKey(new ActionKey(elem));
      }
    }
    return this;
  }

  private void saveActionKey(ActionKey actionKey) {
    actionKeys.add(actionKey);
    actionBuilderFunctionMap.put(actionKey, new ArrayList<>());
  }

  private void tryClearActionKeys() {
    if (clearActionKeys) {
      actionKeys.clear();
      clearActionKeys = false;
    }
  }

  @Override
  public JFXTemplateConfig<N> action(
      Function<JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
          valueBuilderFunction) {
    for (ActionKey actionKey : actionKeys) {
      actionBuilderFunctionMap.get(actionKey).add(valueBuilderFunction);
    }
    clearActionKeys = true;
    return this;
  }

  @Override
  public JFXTemplateConfig<N> action(
      JFXAnimationTemplateAction.Builder<?, ?> animationValueBuilder) {
    return action(builder -> animationValueBuilder);
  }

  @Override
  public JFXTemplateBuilder<N> config(
      Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
          configBuilderFunction) {
    this.configBuilderFunction = configBuilderFunction;
    return this;
  }

  @Override
  public JFXTemplateBuilder<N> config(JFXAnimationTemplateConfig.Builder configBuilder) {
    return config(builder -> configBuilder);
  }

  @Override
  public <B> B build(
      Function<JFXAnimationTemplate<N>, B> builderFunction,
      Function<JFXAnimationObjectMapBuilder<N>, JFXAnimationObjectMapBuilder<N>>
          mapBuilderFunction) {
    animationObjects =
        mapBuilderFunction.apply(JFXAnimationObjectMapBuilder.builder()).getAnimationObjects();
    // Provide a null value as default animation object if it's absent.
    animationObjects.putIfAbsent(
        JFXAnimationObjectMapBuilder.DEFAULT_ANIMATION_OBJECT_NAME,
        Collections.singletonList(null));
    return builderFunction.apply(this);
  }

  @Override
  public <B> B build(
      Function<JFXAnimationTemplate<N>, B> builderFunction, N defaultAnimationObject) {
    return build(builderFunction, b -> b.defaultObject(defaultAnimationObject));
  }

  @Override
  public Timeline build(
      Function<JFXAnimationObjectMapBuilder<N>, JFXAnimationObjectMapBuilder<N>>
          mapBuilderFunction) {
    return build(JFXAnimationTemplates::buildTimeline, mapBuilderFunction);
  }

  @Override
  public Timeline build(N defaultAnimationObject) {
    return build(b -> b.defaultObject(defaultAnimationObject));
  }

  @Override
  public Timeline build() {
    return build(Function.identity());
  }

  public static class ActionKey {

    private double percent = Double.NaN;
    private Duration time;

    private ActionKey(double percent) {
      this.percent = percent;
    }

    private ActionKey(Duration time) {
      this.time = time;
    }

    public double getPercent() {
      return percent;
    }

    public Optional<Double> getPercentOptional() {
      return Double.isNaN(getPercent()) ? Optional.empty() : Optional.of(getPercent());
    }

    public Duration getTime() {
      return time;
    }

    public Optional<Duration> getTimeOptional() {
      return Optional.ofNullable(getTime());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ActionKey actionKey = (ActionKey) o;
      return Double.compare(actionKey.percent, percent) == 0
          && Objects.equals(time, actionKey.time);
    }

    @Override
    public int hashCode() {
      return Objects.hash(percent, time);
    }
  }
}
