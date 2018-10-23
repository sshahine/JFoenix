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
 * Class which provides methods for the final animation configuration. <br>
 * This action is comparable to a {@link javafx.animation.Timeline} and represents a CSS like final
 * configuration of the general keyframe animation.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface JFXTemplateConfig<N> extends JFXTemplateAction<N> {

  /**
   * The method provides via a {@link Function} an config builder {@link
   * JFXAnimationTemplateConfig#builder()} which holds all the specific methods. <br>
   * These {@link JFXAnimationTemplateConfig#builder()} methods represents the methods e.g. from a
   * {@link javafx.animation.Timeline}. <br>
   * Example:
   *
   * <pre>{@code
   * .config(b -> b.duration(Duration.seconds(2)).interpolator(Interpolator.EASE_BOTH))
   * }</pre>
   *
   * @param configBuilderFunction a {@link Function} which provides and accepts an {@link
   *     JFXAnimationTemplateConfig} builder.
   * @return a {@link JFXTemplateBuilder} instance.
   */
  JFXTemplateBuilder<N> config(
      Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
          configBuilderFunction);

  /**
   * Same as the {@link #config(Function)} method but without the lazy behaviour and doesn't provide
   * a {@link JFXAnimationTemplateConfig#builder()} instance.
   *
   * @see #config(Function)
   * @param configBuilder the {@link JFXAnimationTemplateConfig#builder()} instance.
   * @return a {@link JFXTemplateBuilder} instance.
   */
  JFXTemplateBuilder<N> config(JFXAnimationTemplateConfig.Builder configBuilder);
}
