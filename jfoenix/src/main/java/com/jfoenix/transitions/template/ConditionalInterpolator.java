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
