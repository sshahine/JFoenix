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

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSlider.IndicatorPosition;
import com.sun.javafx.scene.control.behavior.SliderBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

/**
 * @author Bashi Elias & Shadi Shaheen
 *
 */
public class JFXSliderSkin extends BehaviorSkinBase<Slider, SliderBehavior> {

	/** Track if slider is vertical/horizontal and cause re layout */
	private boolean isHorizontal, isIndicatorLeft;

	private Paint thumbColor = Color.valueOf("#0F9D58"), trackColor = Color.valueOf("#CCCCCC");
	private double thumbRadius, trackStart, trackLength, thumbTop, thumbLeft, preDragThumbPos, indicatorRotation, horizontalRotation, rotationAngle = 45, shifting;
	private Point2D dragStart; // in skin coordinates

	private Circle thumb;
	private StackPane animatedThumb;
	private Line track, coloredTrack;
	private Text sliderValue;
	private boolean trackClicked, initialization;

	private Timeline timeline;

	public JFXSliderSkin(JFXSlider slider) {
		super(slider, new SliderBehavior(slider));

		isIndicatorLeft = slider.getIndicatorPosition() == IndicatorPosition.LEFT ? true : false;
		initialize();

		slider.requestLayout();
		registerChangeListener(slider.minProperty(), "MIN");
		registerChangeListener(slider.maxProperty(), "MAX");
		registerChangeListener(slider.valueProperty(), "VALUE");
		registerChangeListener(slider.orientationProperty(), "ORIENTATION");
	}

	private void initialize() {
		isHorizontal = getSkinnable().getOrientation() == Orientation.HORIZONTAL;

		thumb = new Circle();
		thumb.setStrokeWidth(2);
		thumb.setRadius(7);
		thumb.setFill(thumbColor);
		thumb.setStroke(thumbColor);
		thumb.getStyleClass().setAll("thumb");

		track = new Line();
		track.setStroke(trackColor);
		track.setStrokeWidth(3);
		track.getStyleClass().setAll("track");

		coloredTrack = new Line();
		coloredTrack.strokeProperty().bind(thumb.strokeProperty());
		coloredTrack.strokeWidthProperty().bind(track.strokeWidthProperty());

		sliderValue = new Text();
		sliderValue.setStroke(Color.WHITE);
		sliderValue.setFont(new Font(10));
		sliderValue.getStyleClass().setAll("sliderValue");

		animatedThumb = new StackPane();
		animatedThumb.getChildren().add(sliderValue);

		getChildren().clear();
		getChildren().addAll(track, coloredTrack, animatedThumb, thumb);
	}

	// JFXSliderSkin
	private double trackSize;
	
	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		
		if (trackSize != (isHorizontal ? w : h)) {
			
			trackSize = (isHorizontal ? w : h);
			
			if (!initialization) initializeVariables();
			
			if (isHorizontal) {
				double trackHeight = snapSize(track.getStrokeWidth());
				double trackAreaHeight = Math.max(trackHeight, 2 * thumbRadius);
				double startY = y + Math.max(h, trackAreaHeight) / 2; // center slider in available height vertically
				trackLength = snapSize(w - 2 * thumbRadius);
				trackStart = snapPosition(x + thumbRadius);
				double trackTop = (int) (startY - (trackHeight / 2));
				thumbTop = (int) (startY);

				track.setStartX(trackStart);
				track.setEndX(trackStart + trackLength);
				track.setStartY(trackTop + trackHeight / 2);
				track.setEndY(trackTop + trackHeight / 2);

				coloredTrack.setStartX(trackStart);
				coloredTrack.setStartY(trackTop + trackHeight / 2);
				coloredTrack.setEndY(trackTop + trackHeight / 2);
			} else {
				double trackWidth = snapSize(track.getStrokeWidth());
				double trackAreaWidth = Math.max(trackWidth, 2 * thumbRadius);
				double startX = x + Math.max(w, trackAreaWidth) / 2; // center slider in available height vertically
				trackLength = snapSize(h - 2 * thumbRadius);
				trackStart = snapPosition(y + thumbRadius);
				double trackLeft = (int) (startX - (trackWidth / 2));
				thumbLeft = (int) (startX);

				track.setStartX(trackLeft + trackWidth / 2);
				track.setEndX(trackLeft + trackWidth / 2);
				track.setStartY(trackStart);
				track.setEndY(trackStart + trackLength);

				coloredTrack.setStartY(trackStart + trackLength);
				coloredTrack.setStartX(trackLeft + trackWidth / 2);
				coloredTrack.setEndX(trackLeft + trackWidth / 2);
			}
		
			if (!initialization) {
				initializeMouseEvents();
				initializeTimeline();
				initialization = true;
			}
		
			positionThumb(true);
		}
	}

	private boolean internalChange = false;
	
	private void initializeVariables() {

		double stroke = thumb.getStrokeWidth();
		double radius = thumb.getRadius();
		thumbRadius = stroke > radius ? stroke : radius;

		trackColor = (Color) track.getStroke();
		thumbColor = (Color) thumb.getStroke();
		
		track.strokeProperty().addListener((o,oldVal,newVal)-> {
			// prevent internal color change
			if(!internalChange)
				trackColor = newVal;		
		});
		
		thumb.strokeProperty().addListener((o,oldVal,newVal)-> {
			// prevent internal color change
			if(!internalChange){				
				thumbColor = newVal;
				if(getSkinnable().getValue() == 0){
					internalChange = true;
					thumb.setFill(trackColor);
					thumb.setStroke(trackColor);
					internalChange = false;
				}
			}
		});

		
		shifting = 30 + thumbRadius;

		if (!isHorizontal) {
			horizontalRotation = -90;
		}

		if (!isIndicatorLeft) {
			indicatorRotation = 180;
			shifting = -shifting;
		}

		sliderValue.setRotate(rotationAngle + indicatorRotation + 3 * horizontalRotation);

		animatedThumb.resize(30, 30);
		animatedThumb.setRotate(-rotationAngle + indicatorRotation + horizontalRotation);
		animatedThumb.backgroundProperty().bind(Bindings.createObjectBinding(() -> new Background(new BackgroundFill(thumb.getStroke(), new CornerRadii(50, 50, 50, 0, true), null)), thumb.strokeProperty()));
		animatedThumb.setScaleX(0);
		animatedThumb.setScaleY(0);
	}

	private void initializeMouseEvents() {
		getSkinnable().setOnMousePressed(me -> {
			if (!thumb.isPressed()) {
				trackClicked = true;
				if (isHorizontal) {
					getBehavior().trackPress(me, (me.getX() / trackLength));
				} else {
					getBehavior().trackPress(me, (me.getY() / trackLength));
				}
				trackClicked = false;
			}
			timeline.setRate(1);
			timeline.play();
		});

		getSkinnable().setOnMouseReleased(me -> {
			timeline.setRate(-1);
			timeline.play();
		});

		getSkinnable().setOnMouseDragged(me -> {
			if (!thumb.isPressed()) {
				if (isHorizontal) {
					getBehavior().trackPress(me, (me.getX() / trackLength));
				} else {
					getBehavior().trackPress(me, (me.getY() / trackLength));
				}
			}
		});

		thumb.setOnMousePressed(me -> {
			getBehavior().thumbPressed(me, 0.0f);
			dragStart = thumb.localToParent(me.getX(), me.getY());
			preDragThumbPos = (getSkinnable().getValue() - getSkinnable().getMin()) / (getSkinnable().getMax() - getSkinnable().getMin());
		});

		thumb.setOnMouseReleased(me -> {
			getBehavior().thumbReleased(me);
		});

		thumb.setOnMouseDragged(me -> {
			Point2D cur = thumb.localToParent(me.getX(), me.getY());
			double dragPos = (isHorizontal) ? cur.getX() - dragStart.getX() : -(cur.getY() - dragStart.getY());
			getBehavior().thumbDragged(me, preDragThumbPos + dragPos / trackLength);
		});

		thumb.layoutXProperty().addListener((o, oldVal, newVal) -> {
			if (isHorizontal) {
				animatedThumb.setLayoutX(newVal.doubleValue() - 2 * thumbRadius - 1);
				long value = Math.round(getSkinnable().getValue());
				sliderValue.setText("" + value);
				if (coloredTrack.getStartX() < newVal.doubleValue()) {
					coloredTrack.setEndX(newVal.doubleValue());
				} else {
					coloredTrack.setEndX(coloredTrack.getStartX());
				}

				internalChange = true;
				if (value == 0) {
					thumb.setFill(trackColor);
					thumb.setStroke(trackColor);
					coloredTrack.setVisible(false);
				} else {
					thumb.setFill(thumbColor);
					thumb.setStroke(thumbColor);
					coloredTrack.setVisible(true);
				}
				internalChange = false;
			}
		});

		thumb.layoutYProperty().addListener((o, oldVal, newVal) -> {
			if (!isHorizontal) {
				animatedThumb.setLayoutY(newVal.doubleValue() - 2 * thumbRadius - 1);
				long value = Math.round(getSkinnable().getValue());
				sliderValue.setText("" + value);
				if (coloredTrack.getStartY() > newVal.doubleValue()) {
					coloredTrack.setEndY(newVal.doubleValue() + thumbRadius);
				} else {
					coloredTrack.setEndY(coloredTrack.getStartY());
				}
				internalChange = true;
				if (value == 0) {
					thumb.setFill(trackColor);
					thumb.setStroke(trackColor);
					coloredTrack.setVisible(false);
				} else {
					thumb.setFill(thumbColor);
					thumb.setStroke(thumbColor);
					coloredTrack.setVisible(true);
				}
				internalChange = false;
			}
		});
	}

	private void initializeTimeline() {
		if (isHorizontal) {
			double thumbPosY = thumb.getLayoutY() - thumbRadius;
			timeline = new Timeline(
					new KeyFrame(
							Duration.ZERO,
							new KeyValue(animatedThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.scaleYProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.layoutYProperty(), thumbPosY, Interpolator.EASE_BOTH)),
					new KeyFrame(
							Duration.seconds(0.2),
							new KeyValue(animatedThumb.scaleXProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue( animatedThumb.layoutYProperty(), thumbPosY - shifting, Interpolator.EASE_BOTH)));
		} else {
			double thumbPosX = thumb.getLayoutX() - thumbRadius;
			timeline = new Timeline(
					new KeyFrame(
							Duration.ZERO,
							new KeyValue(animatedThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.scaleYProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.layoutXProperty(), thumbPosX, Interpolator.EASE_BOTH)),
					new KeyFrame(
							Duration.seconds(0.2),
							new KeyValue(animatedThumb.scaleXProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH),
							new KeyValue(animatedThumb.layoutXProperty(), thumbPosX - shifting, Interpolator.EASE_BOTH)));
		}
	}

	@Override
	protected void handleControlPropertyChanged(String p) {
		super.handleControlPropertyChanged(p);
		if ("ORIENTATION".equals(p)) {
			getSkinnable().requestLayout();
		} else if ("VALUE".equals(p)) {
			// only animate thumb if the track was clicked - not if the thumb is dragged
			positionThumb(trackClicked);
		} else if ("MIN".equals(p)) {
			getSkinnable().requestLayout();
		} else if ("MAX".equals(p)) {
			getSkinnable().requestLayout();
		}
	}

	private void positionThumb(final boolean animate) {
		Slider s = getSkinnable();
		if (s.getValue() > s.getMax()) {
			return;// this can happen if we are bound to something 
		}
		final double endX = (isHorizontal) ? trackStart + (((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))))) - snappedLeftInset() : thumbLeft;
		final double endY = (isHorizontal) ? thumbTop : snappedTopInset() + thumbRadius + trackLength - (trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin())));

		if (animate) {
			// lets animate the thumb transition
			final double startX = thumb.getLayoutX();
			final double startY = thumb.getLayoutY();
			Transition transition = new Transition() {
				{
					setCycleDuration(Duration.millis(20));
				}

				@Override
				protected void interpolate(double frac) {
					if (!Double.isNaN(startX)) {
						thumb.setLayoutX(startX + frac * (endX - startX));
					}
					if (!Double.isNaN(startY)) {
						thumb.setLayoutY(startY + frac * (endY - startY));
					}
				}
			};
			transition.play();
		} else {
			thumb.setLayoutX(endX);
			thumb.setLayoutY(endY);
		}
	}

	double minTrackLength() {
		return 2 * thumb.prefWidth(-1);
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return (leftInset + minTrackLength() + thumb.minWidth(-1) + rightInset);
		} else {
			return (leftInset + thumb.prefWidth(-1) + rightInset);
		}
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return topInset + thumb.prefHeight(-1) + bottomInset;
		} else {
			return topInset + minTrackLength() + thumb.prefHeight(-1) + bottomInset;
		}
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return 140;
		} else {
			return leftInset + Math.max(thumb.prefWidth(-1), track.prefWidth(-1)) + rightInset;
		}
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return topInset + Math.max(thumb.prefHeight(-1), track.prefHeight(-1)) + bottomInset;
		} else {
			return 140;
		}
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return Double.MAX_VALUE;
		} else {
			return getSkinnable().prefWidth(-1);
		}
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return getSkinnable().prefHeight(width);
		} else {
			return Double.MAX_VALUE;
		}
	}
}
