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

import java.util.function.Function;

/**
 * Class which provides methods for an animation action. <br>
 * This action is comparable to a {@link javafx.animation.KeyValue} and represents a CSS like modify
 * block (for a specific part) in keyframe animations.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface JFXTemplateAction<N> extends JFXTemplateProcess<N> {

  /**
   * The action method is similar like a {@link javafx.animation.KeyValue}. <br>
   * The method provides via a {@link Function} an action builder {@link
   * JFXAnimationTemplateAction#builder()} which holds all the specific methods. <br>
   * Example:
   *
   * <pre>{@code
   * .action(b -> b.target(Node::opacityProperty).endValue(0))
   * }</pre>
   *
   * @param valueBuilderFunction a {@link Function} which provides and accepts an {@link
   *     JFXAnimationTemplateAction} builder.
   * @return a {@link JFXTemplateConfig} instance.
   */
  JFXTemplateConfig<N> action(
      Function<JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
          valueBuilderFunction);

  /**
   * Same as the {@link #action(Function)} method but without the lazy behaviour and doesn't provide
   * a {@link JFXAnimationTemplateAction#builder()} instance.
   *
   * @see #action(Function)
   * @param animationValueBuilder the {@link JFXAnimationTemplateAction#builder()} instance.
   * @return a {@link JFXTemplateConfig} instance.
   */
  JFXTemplateConfig<N> action(JFXAnimationTemplateAction.Builder<?, ?> animationValueBuilder);
}
