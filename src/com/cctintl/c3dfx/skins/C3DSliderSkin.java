package com.cctintl.c3dfx.skins;

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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DSlider;
import com.cctintl.c3dfx.controls.C3DSlider.IndicatorPosition;
import com.sun.javafx.scene.control.behavior.SliderBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

/**
 * Region/css based skin for C3DSlider
*/
public class C3DSliderSkin extends BehaviorSkinBase<Slider, SliderBehavior> {

	/** Track if slider is vertical/horizontal and cause re layout */
	private boolean isHorizontal, isIndicatorLeft;

	private Color thumbColor, initialThumbColor = Color.valueOf("#0F9D58"), trackColor = Color.valueOf("#CCCCCC");
	private double thumbRadius, trackStart, trackLength, thumbTop, thumbLeft, preDragThumbPos, indicatorRotation, horizontalRotation, rotationAngle = 45, shifting;
	private Point2D dragStart; // in skin coordinates

	private Circle thumb;
	private StackPane animatedThumb;
	private Line track, coloredTrack;
	private Text sliderValue;
	private boolean trackClicked, initialization;

	private Timeline timeline;

	public C3DSliderSkin(C3DSlider slider) {
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
		thumb.getStyleClass().setAll("thumb");

		coloredTrack = new Line();

		track = new Line();
		track.getStyleClass().setAll("track");

		sliderValue = new Text();
		sliderValue.getStyleClass().setAll("sliderValue");

		animatedThumb = new StackPane();
		animatedThumb.getChildren().add(sliderValue);

		getChildren().clear();
		getChildren().addAll(track, coloredTrack, animatedThumb, thumb);
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

	/**
	 * Called when ever either min, max or value changes, so thumb's layoutX, Y is recomputed.
	 */
	void positionThumb(final boolean animate) {
		Slider s = getSkinnable();
		if (s.getValue() > s.getMax()) {
			return;// this can happen if we are bound to something 
		}
		final double endX = (isHorizontal) ? trackStart + (((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))))) : thumbLeft;
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

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		if (thumbColor != null) {
			thumb.setStroke(thumbColor);
			thumb.setFill(thumbColor);
		}
		if (!initialization) {

			initializeStyles();

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

				coloredTrack.setStrokeWidth(trackHeight);
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

				coloredTrack.setStrokeWidth(trackWidth);
				coloredTrack.setStartY(trackStart + trackLength);
				coloredTrack.setStartX(trackLeft + trackWidth / 2);
				coloredTrack.setEndX(trackLeft + trackWidth / 2);
			}

			initializeComponents();
			initializeMouseEvents();
			initializeTimeline();

			initialization = true;
			positionThumb(true);
		}
	}

	private void initializeStyles() {
		if (track.getStroke() != null) {
			Color temp = (Color) track.getStroke();
			if (temp != Color.BLACK) {
				trackColor = temp;
			}
		}
		track.setStroke(trackColor);
		if (track.getStrokeWidth() == 1) {
			track.setStrokeWidth(3);
		}

		if (thumb.getRadius() == 0) {
			thumb.setRadius(7);
		}
		Color temp = (Color) thumb.getStroke();
		if (temp != null) {
			initialThumbColor = thumbColor = temp;
		} else {
			thumbColor = initialThumbColor;
		}
		thumb.setStroke(thumbColor);
		thumb.setFill(thumbColor);

		double stroke = thumb.getStrokeWidth();
		double radius = thumb.getRadius();
		thumbRadius = stroke > radius ? stroke : radius;
		shifting = 30 + thumbRadius;

		if (!isHorizontal) {
			horizontalRotation = -90;
		}
		
		System.out.println(isIndicatorLeft);

		if (!isIndicatorLeft) {
			indicatorRotation = 180;
			shifting = -shifting;
		}
	}

	private void initializeComponents() {
		sliderValue.setRotate(rotationAngle + indicatorRotation + 3 * horizontalRotation);

		animatedThumb.resize(30, 30);
		animatedThumb.setRotate(-rotationAngle + indicatorRotation + horizontalRotation);
		animatedThumb.backgroundProperty().bind(Bindings.createObjectBinding(() -> new Background(new BackgroundFill(thumb.getStroke(), new CornerRadii(50, 50, 50, 0, true), null)), thumb.strokeProperty()));
		animatedThumb.setScaleX(0);
		animatedThumb.setScaleY(0);

		coloredTrack.strokeProperty().bind(thumb.strokeProperty());
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

				if (value == 0) {
					thumbColor = trackColor;
					coloredTrack.setVisible(false);
				} else {
					thumbColor = initialThumbColor;
					coloredTrack.setVisible(true);
				}
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

				if (value == 0) {
					thumbColor = trackColor;
					coloredTrack.setVisible(false);
				} else {
					thumbColor = initialThumbColor;
					coloredTrack.setVisible(true);
				}
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
