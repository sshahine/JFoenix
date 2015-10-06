/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import com.sun.javafx.css.converters.SizeConverter;

public class JFXSpinner extends StackPane {

	private static final String DEFAULT_STYLE_CLASS = "jfx-spinner";

	
	private Color greenColor, redColor, yellowColor, blueColor, initialColor;
	private Timeline timeline;
	private Arc arc;
	private boolean initialized;

	public JFXSpinner() {
		super();
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		initialize();
	}

	private void initialize() {

		blueColor = Color.valueOf("#4285f4");
		redColor = Color.valueOf("#db4437");
		yellowColor = Color.valueOf("#f4b400");
		greenColor = Color.valueOf("#0F9D58");

		arc = new Arc(0, 0, 12, 12, 0, 360);
		arc.setFill(Color.TRANSPARENT);
		arc.setStrokeWidth(3);
		arc.getStyleClass().addAll("arc");
		arc.radiusXProperty().bindBidirectional(radius);
		arc.radiusYProperty().bindBidirectional(radius);
		getChildren().add(arc);
		
		this.minWidthProperty().bind(Bindings.createDoubleBinding(()->{
			return getRadius()*2 + arc.getStrokeWidth() + 5;
		}, radius,arc.strokeWidthProperty()));
		
		this.maxWidthProperty().bind(Bindings.createDoubleBinding(()->{
			return getRadius()*2 + arc.getStrokeWidth() + 5;
		}, radius,arc.strokeWidthProperty()));

		this.minHeightProperty().bind(Bindings.createDoubleBinding(()->{
			return getRadius()*2 + arc.getStrokeWidth() + 5;
		}, radius,arc.strokeWidthProperty()));
		
		this.maxHeightProperty().bind(Bindings.createDoubleBinding(()->{
			return getRadius()*2 + arc.getStrokeWidth() + 5;
		}, radius,arc.strokeWidthProperty()));
		
	}

	private KeyFrame[] getKeyFrames(double angle, double duration, Color color) {
		KeyFrame[] frames = new KeyFrame[4];
		frames[0] = new KeyFrame(Duration.seconds(duration), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 45 + getStartingAngle(), Interpolator.LINEAR));
		frames[1] = new KeyFrame(Duration.seconds(duration + 0.4), new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 90 + getStartingAngle(), Interpolator.LINEAR));
		frames[2] = new KeyFrame(Duration.seconds(duration + 0.7), new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 135 + getStartingAngle(), Interpolator.LINEAR));
		frames[3] = new KeyFrame(Duration.seconds(duration + 1.1), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 435 + getStartingAngle(), Interpolator.LINEAR),
				new KeyValue(arc.strokeProperty(), color, Interpolator.EASE_BOTH));
		return frames;
	}

	protected void layoutChildren() {
		if (!initialized) {
			super.layoutChildren();
			initialColor = (Color) arc.getStroke();
			if (initialColor == null) {
				arc.setStroke(blueColor);
			}

			KeyFrame[] blueFrame = getKeyFrames( 0, 0, initialColor == null ? blueColor : initialColor);
			KeyFrame[] redFrame = getKeyFrames( 450, 1.4, initialColor == null ? redColor : initialColor);
			KeyFrame[] yellowFrame = getKeyFrames( 900, 2.8, initialColor == null ? yellowColor : initialColor);
			KeyFrame[] greenFrame = getKeyFrames( 1350, 4.2, initialColor == null ? greenColor : initialColor);

			KeyFrame endingFrame = new KeyFrame(Duration.seconds(5.6), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), 1845 + getStartingAngle(), Interpolator.LINEAR));

			if(timeline!=null) timeline.stop();
			timeline = new Timeline(blueFrame[0], blueFrame[1], blueFrame[2], blueFrame[3], redFrame[0], redFrame[1], redFrame[2], redFrame[3], yellowFrame[0], yellowFrame[1], yellowFrame[2], yellowFrame[3],
					greenFrame[0], greenFrame[1], greenFrame[2], greenFrame[3], endingFrame);
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.setRate(1);
			timeline.play();

			initialized = true;
		}
	}

	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	
	private StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(StyleableProperties.RADIUS, JFXSpinner.this, "radius", 12.0);

	public final StyleableDoubleProperty radiusProperty() {
		return this.radius;
	}

	public final double getRadius() {
		return this.radiusProperty().get();
	}

	public final void setRadius(final double radius) {
		this.radiusProperty().set(radius);
	}
	
	private StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(StyleableProperties.STARTING_ANGLE, JFXSpinner.this, "starting_angle", 360 - Math.random()*720);

	public final StyleableDoubleProperty startingAngleProperty() {
		return this.startingAngle;
	}

	public final double getStartingAngle() {
		return this.startingAngleProperty().get();
	}

	public final void setStartingAngle(final double startingAngle) {
		this.startingAngleProperty().set(startingAngle);
	}
	
	private static class StyleableProperties {
		private static final CssMetaData<JFXSpinner, Number> RADIUS =
				new CssMetaData< JFXSpinner, Number>("-fx-radius",
						SizeConverter.getInstance(), 12) {
			@Override
			public boolean isSettable(JFXSpinner control) {
				return control.radius == null || !control.radius.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXSpinner control) {
				return control.radius;
			}
		};
		
		private static final CssMetaData<JFXSpinner, Number> STARTING_ANGLE =
				new CssMetaData< JFXSpinner, Number>("-fx-starting-angle",
						SizeConverter.getInstance(), 360 - Math.random()*720) {
			@Override
			public boolean isSettable(JFXSpinner control) {
				return control.startingAngle == null || !control.startingAngle.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXSpinner control) {
				return control.startingAngle;
			}
		};
		
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Parent.getClassCssMetaData());
			Collections.addAll(styleables,
					RADIUS,
					STARTING_ANGLE
					);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}
	
	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		if(STYLEABLES == null){
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Parent.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.CHILD_STYLEABLES;
	}

	
}
