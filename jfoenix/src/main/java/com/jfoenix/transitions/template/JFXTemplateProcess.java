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

/**
 * Class which provides methods for the animation process. <br>
 * This action is comparable to a {@link javafx.animation.KeyFrame} and represents a CSS like
 * percentage definition (for a specific part) in keyframe animations.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface JFXTemplateProcess<N> {

  /**
   * Takes a percentage value or percentage values between 0 and 100 which defines the specific
   * animation interval.
   *
   * @param percent the percentage value.
   * @param percents the percentage values.
   * @return A {@link JFXTemplateAction} instance.
   */
  JFXTemplateAction<N> percent(double percent, double... percents);

  /**
   * Defines a percentage value of 0 in the specific animation interval.
   *
   * @return A {@link JFXTemplateAction} instance.
   */
  JFXTemplateAction<N> from();

  /**
   * Defines a percentage value of 100 in the specific animation interval.
   *
   * @return A {@link JFXTemplateAction} instance.
   */
  JFXTemplateAction<N> to();
}
