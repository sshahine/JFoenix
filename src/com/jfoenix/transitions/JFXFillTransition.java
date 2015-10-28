/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.transitions;

import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This {@code Transition} creates an animation, that changes the filling of a
 * pane over a {@code duration}. This is done by updating the {@code background}
 * property of the {@code pane} at regular intervals.
 * <p>
 * It starts from the {@code fromValue}. 
 * <p>
 * It stops at the {@code toValue} value.
 * <p>
 * It's similar to JavaFX FillTransition, however it can be applied on Region 
 * instead of shape
 * 
 * @author sshahine
 */

public final class JFXFillTransition extends Transition {

	private Color start;
	private Color end;
	private CacheHint oldCacheHint = CacheHint.DEFAULT;
	private boolean oldCache = false;


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * The target region of this {@code FillTransition}.
	 * <p>
	 * It is not possible to change the target {@code region} of a running
	 * {@code FillTransition}. If the value of {@code region} is changed for a
	 * running {@code FillTransition}, the animation has to be stopped and
	 * started again to pick up the new value.
	 */
	private ObjectProperty<Region> region;

	public final void setRegion(Region value) {    	
		if ((region != null) || (value != null /* DEFAULT_SHAPE */)) {
			regionProperty().set(value);
		}
	}

	public final Region getRegion() {
		return (region == null)? null : region.get();
	}

	public final ObjectProperty<Region> regionProperty() {
		if (region == null) {
			region = new SimpleObjectProperty<Region>(this, "region", null);
		}
		return region;
	}

	/**
	 * The duration of this {@code FillTransition}.
	 * <p>
	 * It is not possible to change the {@code duration} of a running
	 * {@code FillTransition}. If the value of {@code duration} is changed for a
	 * running {@code FillTransition}, the animation has to be stopped and
	 * started again to pick up the new value.
	 * <p>
	 * Note: While the unit of {@code duration} is a millisecond, the
	 * granularity depends on the underlying operating system and will in
	 * general be larger. For example animations on desktop systems usually run
	 * with a maximum of 60fps which gives a granularity of ~17 ms.
	 *
	 * Setting duration to value lower than {@link Duration#ZERO} will result
	 * in {@link IllegalArgumentException}.
	 * 
	 * @defaultValue 400ms
	 */
	private ObjectProperty<Duration> duration;
	private static final Duration DEFAULT_DURATION = Duration.millis(400);

	public final void setDuration(Duration value) {
		if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
			durationProperty().set(value);
		}
	}

	public final Duration getDuration() {
		return (duration == null)? DEFAULT_DURATION : duration.get();
	}

	public final ObjectProperty<Duration> durationProperty() {
		if (duration == null) {
			duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {

				@Override
				public void invalidated() {
					try {
						setCycleDuration(getDuration());
					} catch (IllegalArgumentException e) {
						if (isBound()) {
							unbind();
						}
						set(getCycleDuration());
						throw e;
					}
				}

				@Override
				public Object getBean() {
					return JFXFillTransition.this;
				}

				@Override
				public String getName() {
					return "duration";
				}
			};
		}
		return duration;
	}

	/**
	 * Specifies the start color value for this {@code FillTransition}.
	 * <p>
	 * It is not possible to change {@code fromValue} of a running
	 * {@code FillTransition}. If the value of {@code fromValue} is changed for
	 * a running {@code FillTransition}, the animation has to be stopped and
	 * started again to pick up the new value.
	 * 
	 * @defaultValue {@code null}
	 */
	private ObjectProperty<Color> fromValue;
	private static final Color DEFAULT_FROM_VALUE = null;

	public final void setFromValue(Color value) {
		if ((fromValue != null) || (value != null /* DEFAULT_FROM_VALUE */)) {
			fromValueProperty().set(value);
		}
	}

	public final Color getFromValue() {
		return (fromValue == null)? DEFAULT_FROM_VALUE : fromValue.get();
	}

	public final ObjectProperty<Color> fromValueProperty() {
		if (fromValue == null) {
			fromValue = new SimpleObjectProperty<Color>(this, "fromValue", DEFAULT_FROM_VALUE);
		}
		return fromValue;
	}

	/**
	 * Specifies the stop color value for this {@code FillTransition}.
	 * <p>
	 * It is not possible to change {@code toValue} of a running
	 * {@code FillTransition}. If the value of {@code toValue} is changed for a
	 * running {@code FillTransition}, the animation has to be stopped and
	 * started again to pick up the new value.
	 * 
	 * @defaultValue {@code null}
	 */
	private ObjectProperty<Color> toValue;
	private static final Color DEFAULT_TO_VALUE = null;

	public final void setToValue(Color value) {
		if ((toValue != null) || (value != null /* DEFAULT_TO_VALUE */)) {
			toValueProperty().set(value);
		}
	}

	public final Color getToValue() {
		return (toValue == null)? DEFAULT_TO_VALUE : toValue.get();
	}

	public final ObjectProperty<Color> toValueProperty() {
		if (toValue == null) {
			toValue = new SimpleObjectProperty<Color>(this, "toValue", DEFAULT_TO_VALUE);
		}
		return toValue;
	}

	/**
	 * The constructor of {@code FillTransition}
	 * @param duration The duration of the {@code FillTransition}
	 * @param region The {@code region} which filling will be animated
	 * @param fromValue The start value of the color-animation
	 * @param toValue The end value of the color-animation
	 */
	public JFXFillTransition(Duration duration, Region shape, Color fromValue,
			Color toValue) {
		setDuration(duration);
		setRegion(shape);
		setFromValue(fromValue);
		setToValue(toValue);
		setCycleDuration(duration);
		statusProperty().addListener(new ChangeListener<Status>() {
			@Override public void changed(ObservableValue<? extends Status> ov, Status t, Status newStatus) {
				switch(newStatus) {
				case RUNNING:
					starting();
					break;
				default:
					stopping();
					break;
				}
			}
		});
	}

	/**
	 * The constructor of {@code FillTransition}
	 * @param duration The duration of the {@code FillTransition}
	 * @param fromValue The start value of the color-animation
	 * @param toValue The end value of the color-animation
	 */
	public JFXFillTransition(Duration duration, Color fromValue, Color toValue) {
		this(duration, null, fromValue, toValue);
	}

	/**
	 * The constructor of {@code FillTransition}
	 * 
	 * @param duration
	 *            The duration of the {@code FillTransition}
	 * @param region
	 *            The {@code region} which filling will be animated
	 */
	public JFXFillTransition(Duration duration, Region shape) {
		this(duration, shape, null, null);
	}

	/**
	 * The constructor of {@code FillTransition}
	 * 
	 * @param duration
	 *            The duration of the {@code FadeTransition}
	 */
	public JFXFillTransition(Duration duration) {
		this(duration, null, null, null);
	}

	/**
	 * The constructor of {@code FillTransition}
	 */
	public JFXFillTransition() {
		this(DEFAULT_DURATION, null);
	}

	/**
	 * Called when the animation is starting
	 */
	private CornerRadii radii;
	private Insets insets;
	protected void starting() {
		// init animation values
		oldCache = region.get().isCache();
		oldCacheHint = region.get().getCacheHint();
		radii = region.get().getBackground()==null ? null : region.get().getBackground().getFills().get(0).getRadii();
		insets = region.get().getBackground()==null ? null : region.get().getBackground().getFills().get(0).getInsets();
		start = fromValue.get();
		end = toValue.get();
		region.get().setCache(true);		
		region.get().setCacheHint(CacheHint.SPEED);
	}
	/**
	 * Called when the animation is stopping
	 */
	protected void stopping() {
		region.get().setCache(oldCache);
		region.get().setCacheHint(oldCacheHint);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void interpolate(double frac) {
		final Color newColor = start.interpolate(end, frac);
		region.get().setBackground(new Background(new BackgroundFill(newColor, radii, insets)));
	}

}
