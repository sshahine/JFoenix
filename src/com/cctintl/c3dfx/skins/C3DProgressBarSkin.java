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

package com.cctintl.c3dfx.skins;

import java.util.Collections;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DProgressBar;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class C3DProgressBarSkin extends BehaviorSkinBase<ProgressIndicator, BehaviorBase<ProgressIndicator>> {

	private Color indicatorColor = Color.valueOf("#0F9D58"), trackColor = Color.valueOf("#CCCCCC");
	private double trackStart, trackLength;

	private Line track, bar;
	private boolean initialization, isIndeterminate;

	private Timeline timeline;

	public C3DProgressBarSkin(C3DProgressBar bar) {
		super(bar, new BehaviorBase<ProgressIndicator>(bar, Collections.emptyList()));

		initialize();
		bar.requestLayout();
	}

	private void initialize() {
		track = new Line();
		track.setStroke(trackColor);
		track.setStrokeWidth(3);
		track.getStyleClass().setAll("track");

		bar = new Line();
		bar.setStroke(indicatorColor);
		bar.strokeWidthProperty().bind(track.strokeWidthProperty());
		bar.getStyleClass().setAll("bar");

		getChildren().clear();
		getChildren().addAll(track, bar);
		
		getSkinnable().indeterminateProperty().addListener((o, oldVal, newVal) -> {
			timeline.stop();
			initialization = false;
		});
		
		getSkinnable().prefWidthProperty().addListener((o,oldVal,newVal)-> initialization = false);
		getSkinnable().maxHeightProperty().bind(track.strokeWidthProperty());
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		if (!initialization) {
			isIndeterminate = getSkinnable().isIndeterminate();
			double trackHeight = snapSize(track.getStrokeWidth());
			double borderWidth = 0;
			if(getSkinnable().getBorder()!=null)
				borderWidth = getSkinnable().getBorder().getStrokes().get(0).getWidths().getLeft() + getSkinnable().getBorder().getStrokes().get(0).getWidths().getRight();
			
			trackLength = snapSize(getSkinnable().getPrefWidth() - track.getStrokeWidth() - borderWidth);			
			trackStart = snapPosition(x);

			track.setStartX(trackStart);
			track.setEndX(trackStart + trackLength);
			track.setStartY(y + trackHeight / 2);
			track.setEndY(y + trackHeight / 2);
			
			bar.setStartX(trackStart);
			bar.setStartY(y + trackHeight / 2);
			bar.setEndY(y + trackHeight / 2);
			
			initializeListeners();

			if (isIndeterminate) {
				initializeTimeline();
				timeline.setCycleCount(Timeline.INDEFINITE);
				timeline.setRate(1);
				timeline.play();
			}

			initialization = true;
		}
	}

	private void initializeListeners() {
		getSkinnable().progressProperty().addListener((o, oldVal, newVal) -> {
			if (!isIndeterminate) {
				double barWidth = ((int) (trackLength - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, newVal.doubleValue()))) / 2.0F;
				bar.setEndX(barWidth);
			}
		});
	}

	private void initializeTimeline() {
		double first = trackLength / 3;
		double second = 2 * trackLength / 3;
		timeline = new Timeline(
						new KeyFrame(
								Duration.ZERO,
								new KeyValue(bar.startXProperty(), 0, Interpolator.LINEAR),
								new KeyValue(bar.endXProperty(), 0, Interpolator.LINEAR)),
						new KeyFrame(
								Duration.seconds(0.5),
								new KeyValue(bar.startXProperty(), first, Interpolator.LINEAR),
								new KeyValue(bar.endXProperty(), second, Interpolator.LINEAR)),
						new KeyFrame(
								Duration.seconds(1),
								new KeyValue(bar.startXProperty(), trackLength, Interpolator.LINEAR),
								new KeyValue(bar.endXProperty(), trackLength, Interpolator.LINEAR)));
	}
}
