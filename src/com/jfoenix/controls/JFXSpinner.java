/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class JFXSpinner extends Region {

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
		this.setMinSize(40, 40);

		blueColor = Color.valueOf("#4285f4");
		redColor = Color.valueOf("#db4437");
		yellowColor = Color.valueOf("#f4b400");
		greenColor = Color.valueOf("#0F9D58");

		arc = new Arc(20, 20, 12, 12, 0, 5);
		arc.setFill(Color.TRANSPARENT);
		arc.setStrokeWidth(3);
		arc.getStyleClass().addAll("arc");

		getChildren().add(arc);
	}

	private KeyFrame[] getKeyFrames(double angle, double duration, Color color) {
		KeyFrame[] frames = new KeyFrame[4];
		frames[0] = new KeyFrame(Duration.seconds(duration), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 45, Interpolator.LINEAR));
		frames[1] = new KeyFrame(Duration.seconds(duration + 0.4), new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 90, Interpolator.LINEAR));
		frames[2] = new KeyFrame(Duration.seconds(duration + 0.7), new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 135, Interpolator.LINEAR));
		frames[3] = new KeyFrame(Duration.seconds(duration + 1.1), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), angle + 435, Interpolator.LINEAR),
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

			KeyFrame[] blueFrame = getKeyFrames(0, 0, initialColor == null ? blueColor : initialColor);
			KeyFrame[] redFrame = getKeyFrames(450, 1.4, initialColor == null ? redColor : initialColor);
			KeyFrame[] yellowFrame = getKeyFrames(900, 2.8, initialColor == null ? yellowColor : initialColor);
			KeyFrame[] greenFrame = getKeyFrames(1350, 4.2, initialColor == null ? greenColor : initialColor);

			KeyFrame endingFrame = new KeyFrame(Duration.seconds(5.6), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), 1845, Interpolator.LINEAR));

			timeline = new Timeline(blueFrame[0], blueFrame[1], blueFrame[2], blueFrame[3], redFrame[0], redFrame[1], redFrame[2], redFrame[3], yellowFrame[0], yellowFrame[1], yellowFrame[2], yellowFrame[3],
					greenFrame[0], greenFrame[1], greenFrame[2], greenFrame[3], endingFrame);

			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.setRate(1);
			timeline.play();

			initialized = true;
		}
	}

}
