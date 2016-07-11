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
package com.jfoenix.skins;

import java.util.Collections;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import com.jfoenix.controls.JFXProgressBar;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

/**
 * <h1>Material Design ProgressBar Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXProgressBarSkin extends BehaviorSkinBase<ProgressIndicator, BehaviorBase<ProgressIndicator>> {

	private Color indicatorColor = Color.valueOf("#0F9D58"), trackColor = Color.valueOf("#CCCCCC");
	private double trackStart, trackLength;

	private Line track, bar;
	private boolean initialization, isIndeterminate;

	private Timeline timeline;

	public JFXProgressBarSkin(JFXProgressBar bar) {
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
			if(timeline!=null) timeline.stop();
			initialization = false;
			getSkinnable().requestLayout();
		});
		
		getSkinnable().progressProperty().addListener((o, oldVal, newVal) -> {
			if (!isIndeterminate) {
				double barWidth = ((int) (trackLength - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, newVal.doubleValue()))) / 2.0F;
				bar.setEndX(barWidth);
			}
		});
		
		getSkinnable().prefWidthProperty().addListener((o,oldVal,newVal)-> initialization = false);
		getSkinnable().maxHeightProperty().bind(track.strokeWidthProperty());
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		if (!initialization) {
			isIndeterminate = getSkinnable().isIndeterminate();
			double trackHeight = snapSize(track.getStrokeWidth());
//			double borderWidth = 0;
//			if(getSkinnable().getBorder()!=null)
//				borderWidth = getSkinnable().getBorder().getStrokes().get(0).getWidths().getLeft() + getSkinnable().getBorder().getStrokes().get(0).getWidths().getRight();
			
			trackLength = snapSize(getSkinnable().getPrefWidth() - track.getStrokeWidth() - (getSkinnable().snappedLeftInset() + getSkinnable().snappedRightInset()));			
			trackStart = snapPosition(x);
			getSkinnable().setMaxWidth(trackLength);
			
			track.setStartX(trackStart);
			track.setEndX(trackStart + trackLength);
			track.setStartY(y + trackHeight / 2);
			track.setEndY(y + trackHeight / 2);
			
			bar.setStartX(trackStart);
			bar.setStartY(y + trackHeight / 2);
			bar.setEndY(y + trackHeight / 2);
			
			
			if (!isIndeterminate) {
				double barWidth = ((int) (trackLength - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, getSkinnable().getProgress()))) / 2.0F;
				bar.setEndX(barWidth);
			}

			if (isIndeterminate) {
				initializeTimeline();
				timeline.setCycleCount(Timeline.INDEFINITE);
				timeline.setRate(1);
				timeline.play();
			}

			initialization = true;
		}
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
