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
package com.jfoenix.transitions.creator;

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
public class JFXAnimationTemplate<N> implements TemplateConfig<N>, TemplateBuilder<N> {

  static final String DEFAULT_ANIMATION_OBJECT_KEY = "_DefaultKey_";

  private final Set<Double> percents = new HashSet<>();
  private final Map<
          Double,
          List<
              Function<
                  JFXAnimationTemplateAction.InitBuilder<N>,
                  JFXAnimationTemplateAction.Builder<?, ?>>>>
      creatorValueBuilderFunctions = new HashMap<>();
  final Map<String, Object> animationObjects = new HashMap<>();
  private final Class<N> animationObjectType;
  private Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
      creatorConfigBuilderFunction;
  private boolean clearPercents;

  private JFXAnimationTemplate(Class<N> animationObjectType) {
    this.animationObjectType = animationObjectType;
  }

  public static <N> TemplateProcess<N> create(Class<N> animationObjectType) {
    return new JFXAnimationTemplate<>(animationObjectType);
  }

  public static TemplateProcess<Node> create() {
    return create(Node.class);
  }

  public Map<Double, List<JFXAnimationTemplateAction<?, ?>>> buildAndGetAnimationValues() {

    Map<Double, List<JFXAnimationTemplateAction<?, ?>>> animationValueMap = new HashMap<>();
    creatorValueBuilderFunctions.forEach(
        (percent, animationValueBuilderFunctions) -> {
          List<JFXAnimationTemplateAction<?, ?>> animationValues =
              animationValueBuilderFunctions
                  .stream()
                  .map(
                      builderFunction ->
                          builderFunction
                              .apply(JFXAnimationTemplateAction.builder(animationObjectType))
                              .build(animationObjects::get))
                  .collect(Collectors.toList());
          animationValueMap.put(percent, animationValues);
        });
    return animationValueMap;
  }

  public JFXAnimationTemplateConfig buildAndGetTemplateConfig() {
    return creatorConfigBuilderFunction.apply(JFXAnimationTemplateConfig.builder()).build();
  }

  @Override
  public TemplateAction<N> percent(double first, double... rest) {
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
  public TemplateAction<N> from() {
    return percent(0);
  }

  @Override
  public TemplateAction<N> to() {
    return percent(100);
  }

  @Override
  public TemplateConfig<N> action(
      Function<JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
          valueBuilderFunction) {
    for (Double percent : percents) {
      creatorValueBuilderFunctions.get(percent).add(valueBuilderFunction);
    }
    clearPercents = true;
    return this;
  }

  @Override
  public TemplateConfig<N> action(JFXAnimationTemplateAction.Builder<?, ?> animationValueBuilder) {
    return action(builder -> animationValueBuilder);
  }

  @Override
  public TemplateBuilder<N> config(
      Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
          configBuilderFunction) {
    creatorConfigBuilderFunction = configBuilderFunction;
    return this;
  }

  @Override
  public TemplateBuilder<N> config(JFXAnimationTemplateConfig.Builder configBuilder) {
    return config(builder -> configBuilder);
  }

  public final <B> B build(
      Function<JFXAnimationTemplate<N>, B> builderFunction,
      N animationObject,
      Map<String, ?> animationObjects) {
    this.animationObjects.put(DEFAULT_ANIMATION_OBJECT_KEY, animationObject);
    this.animationObjects.putAll(animationObjects);
    return builderFunction.apply(this);
  }

  @Override
  public <B> B build(Function<JFXAnimationTemplate<N>, B> builderFunction, N animationObject) {
    return build(builderFunction, animationObject, Collections.emptyMap());
  }

  public final Timeline build(N animationObject, Map<String, ?> animationObjects) {
    return build(JFXAnimationTemplates::buildTimeline, animationObject, animationObjects);
  }

  @Override
  public Timeline build(N animationObject) {
    return build(animationObject, Collections.emptyMap());
  }
}
