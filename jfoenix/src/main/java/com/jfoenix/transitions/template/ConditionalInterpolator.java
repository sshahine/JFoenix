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

import java.util.function.BooleanSupplier;

/**
 * Intercepts a {@link Interpolator} with a defined condition.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-24
 */
public class ConditionalInterpolator extends Interpolator {

  private final Interpolator interpolator;
  private final WritableValue<Object> target;
  private final BooleanSupplier condition;

  public ConditionalInterpolator(
      Interpolator interpolator, WritableValue<Object> target, BooleanSupplier condition) {
    this.interpolator = interpolator;
    this.target = target;
    this.condition = condition;
  }

  @Override
  public Object interpolate(Object startValue, Object endValue, double fraction) {
    return condition.getAsBoolean()
        ? interpolator.interpolate(startValue, endValue, fraction)
        : target.getValue();
  }

  @Override
  public boolean interpolate(boolean startValue, boolean endValue, double fraction) {
    return condition.getAsBoolean()
        ? interpolator.interpolate(startValue, endValue, fraction)
        : (boolean) target.getValue();
  }

  @Override
  public double interpolate(double startValue, double endValue, double fraction) {
    return condition.getAsBoolean()
        ? interpolator.interpolate(startValue, endValue, fraction)
        : (double) target.getValue();
  }

  @Override
  public int interpolate(int startValue, int endValue, double fraction) {
    return condition.getAsBoolean()
        ? interpolator.interpolate(startValue, endValue, fraction)
        : (int) target.getValue();
  }

  @Override
  public long interpolate(long startValue, long endValue, double fraction) {
    return condition.getAsBoolean()
        ? interpolator.interpolate(startValue, endValue, fraction)
        : (long) target.getValue();
  }

  @Override
  protected double curve(double t) {
    return 0;
  }
}
