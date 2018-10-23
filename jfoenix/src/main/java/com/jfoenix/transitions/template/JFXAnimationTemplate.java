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

import javafx.animation.*;
import javafx.scene.Node;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
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

  private final Set<Double> percents = new HashSet<>();
  private final Map<
          Double,
          List<
              Function<
                  JFXAnimationTemplateAction.InitBuilder<N>,
                  JFXAnimationTemplateAction.Builder<?, ?>>>>
      creatorValueBuilderFunctions = new HashMap<>();
  private final Class<N> animationObjectType;
  private Map<String, Collection<Object>> animationObjects;
  private Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
      creatorConfigBuilderFunction;
  private boolean clearPercents;

  private JFXAnimationTemplate(Class<N> animationObjectType) {
    this.animationObjectType = animationObjectType;
  }

  public static <N> JFXTemplateProcess<N> create(Class<N> animationObjectType) {
    return new JFXAnimationTemplate<>(animationObjectType);
  }

  public static JFXTemplateProcess<Node> create() {
    return create(Node.class);
  }

  public Map<Double, List<JFXAnimationTemplateAction<?, ?>>> buildAndGetAnimationValues() {

    Map<Double, List<JFXAnimationTemplateAction<?, ?>>> animationValueMap = new HashMap<>();
    creatorValueBuilderFunctions.forEach(
        (percent, animationValueBuilderFunctions) -> {
          List<JFXAnimationTemplateAction<?, ?>> animationValues =
              animationValueBuilderFunctions
                  .stream()
                  .flatMap(
                      builderFunction ->
                          builderFunction
                              .apply(JFXAnimationTemplateAction.builder(animationObjectType))
                              .buildActions(this::getAnimationObjectsWithKeys))
                  .collect(Collectors.toList());
          animationValueMap.put(percent, animationValues);
        });
    return animationValueMap;
  }

  private List<Object> getAnimationObjectsWithKeys(Collection<String> keys) {
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

  public JFXAnimationTemplateConfig buildAndGetTemplateConfig() {
    return creatorConfigBuilderFunction.apply(JFXAnimationTemplateConfig.builder()).build();
  }

  @Override
  public JFXTemplateAction<N> percent(double first, double... rest) {
    if (clearPercents) {
      percents.clear();
      clearPercents = false;
    }
    // Clamp value between 0 and 100.
    percents.add(Math.max(0, Math.min(100, first)));
    creatorValueBuilderFunctions.put(first, new ArrayList<>());

    for (double percent : rest) {
      percents.add(Math.max(0, Math.min(100, percent)));
      creatorValueBuilderFunctions.put(percent, new ArrayList<>());
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
  public JFXTemplateConfig<N> action(
      Function<JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
          valueBuilderFunction) {
    for (Double percent : percents) {
      creatorValueBuilderFunctions.get(percent).add(valueBuilderFunction);
    }
    clearPercents = true;
    return this;
  }

  @Override
  public JFXTemplateConfig<N> action(JFXAnimationTemplateAction.Builder<?, ?> animationValueBuilder) {
    return action(builder -> animationValueBuilder);
  }

  @Override
  public JFXTemplateBuilder<N> config(
      Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
          configBuilderFunction) {
    creatorConfigBuilderFunction = configBuilderFunction;
    return this;
  }

  @Override
  public JFXTemplateBuilder<N> config(JFXAnimationTemplateConfig.Builder configBuilder) {
    return config(builder -> configBuilder);
  }

  @Override
  public <B> B build(
      Function<JFXAnimationTemplate<N>, B> builderFunction,
      Function<JFXAnimationObjectMapBuilder<N>, JFXAnimationObjectMapBuilder<N>> mapBuilderFunction) {
    animationObjects =
        mapBuilderFunction.apply(JFXAnimationObjectMapBuilder.builder()).getAnimationObjects();
    return builderFunction.apply(this);
  }

  @Override
  public <B> B build(
      Function<JFXAnimationTemplate<N>, B> builderFunction, N defaultAnimationObject) {
    return build(builderFunction, b -> b.defaultObject(defaultAnimationObject));
  }

  @Override
  public Timeline build(
      Function<JFXAnimationObjectMapBuilder<N>, JFXAnimationObjectMapBuilder<N>> mapBuilderFunction) {
    return build(JFXAnimationTemplates::buildTimeline, mapBuilderFunction);
  }

  @Override
  public Timeline build(N defaultAnimationObject) {
    return build(b -> b.defaultObject(defaultAnimationObject));
  }

  @Override
  public Timeline build() {
    // Provide a null value as default animation object.
    return build(b -> b.defaultObject(null));
  }
}
