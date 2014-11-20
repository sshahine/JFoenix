package customui.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import com.sun.javafx.scene.control.behavior.SliderBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

/**
 * Region/css based skin for Slider
*/
public class C3DSliderSkin extends BehaviorSkinBase<Slider, SliderBehavior> {

	/** Track if slider is vertical/horizontal and cause re layout */
	private boolean isHorizontal;

	private Color thumbColor, trackColor;
	private double thumbWidth;
	private double thumbHeight;

	private double trackStart;
	private double trackLength;
	private double thumbTop;
	private double thumbLeft;
	private double preDragThumbPos;
	private Point2D dragStart; // in skin coordinates

	private StackPane thumb, track, animatedThumb;
	private Line coloredTrack;
	private Text sliderValue;
	private boolean trackClicked, initialization;

	private Timeline timeline;

	public C3DSliderSkin(Slider slider) {
		super(slider, new SliderBehavior(slider));

		initialize();

		slider.requestLayout();
		registerChangeListener(slider.minProperty(), "MIN");
		registerChangeListener(slider.maxProperty(), "MAX");
		registerChangeListener(slider.valueProperty(), "VALUE");
		registerChangeListener(slider.orientationProperty(), "ORIENTATION");
	}

	private void initialize() {
		isHorizontal = getSkinnable().getOrientation() == Orientation.HORIZONTAL;

		thumb = new StackPane();
		thumb.getStyleClass().setAll("thumb");

		coloredTrack = new Line();

		track = new StackPane();
		track.getStyleClass().setAll("track");

		sliderValue = new Text();
		sliderValue.getStyleClass().setAll("sliderValue");

		animatedThumb = new StackPane();

		getChildren().clear();
		getChildren().addAll(track, thumb, coloredTrack, animatedThumb);
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
		final double endX = (isHorizontal) ? trackStart + (((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))) - thumbWidth)) : thumbLeft;
		final double endY = (isHorizontal) ? thumbTop : snappedTopInset() + trackLength - (trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))); //  - thumbHeight/2

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

		if (!initialization) {

			thumbWidth = snapSize(thumb.prefWidth(-1));
			thumbHeight = snapSize(thumb.prefHeight(-1));
			thumb.resize(thumbWidth, thumbHeight);
			thumbColor = (Color) thumb.getBackground().getFills().get(0).getFill();

			double trackRadius = track.getBackground() == null ? 0 : track.getBackground().getFills().size() > 0 ? track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0;

			if (isHorizontal) {
				double trackHeight = snapSize(track.prefHeight(-1));
				double trackAreaHeight = Math.max(trackHeight, thumbHeight);
				double totalHeightNeeded = trackAreaHeight;
				double startY = y + ((h - totalHeightNeeded) / 2); // center slider in available height vertically
				trackLength = snapSize(w - thumbWidth);
				trackStart = snapPosition(x + (thumbWidth / 2));
				double trackTop = (int) (startY + ((trackAreaHeight - trackHeight) / 2));
				thumbTop = (int) (startY + ((trackAreaHeight - thumbHeight) / 2));
				track.resizeRelocate((int) (trackStart - trackRadius), trackTop, (int) (trackLength + trackRadius + trackRadius), trackHeight);
				trackColor = (Color) track.getBackground().getFills().get(0).getFill();
				double snap = track.prefHeight(-1);
				coloredTrack.setStrokeWidth(snap);
				coloredTrack.setStartX(trackStart - trackRadius + snap / 2);
				coloredTrack.setStartY(trackTop + snap / 2);
				coloredTrack.setEndY(trackTop + snap / 2);
			} else {
				double trackWidth = snapSize(track.prefWidth(-1));
				double trackAreaWidth = Math.max(trackWidth, thumbWidth);
				double totalWidthNeeded = trackAreaWidth;
				double startX = x + ((w - totalWidthNeeded) / 2); // center slider in available width horizontally
				trackLength = snapSize(h - thumbHeight);
				trackStart = snapPosition(y + (thumbHeight / 2));
				double trackLeft = (int) (startX + ((trackAreaWidth - trackWidth) / 2));
				thumbLeft = (int) (startX + ((trackAreaWidth - thumbWidth) / 2));
				track.resizeRelocate(trackLeft, (int) (trackStart - trackRadius), trackWidth, (int) (trackLength + trackRadius + trackRadius));
				trackColor = (Color) track.getBackground().getFills().get(0).getFill();
				double snap = track.prefWidth(-1);
				coloredTrack.setStrokeWidth(snap);
				coloredTrack.setStartY(trackStart + trackLength - trackRadius + snap / 2);
				coloredTrack.setStartX(trackLeft + snap / 2);
				coloredTrack.setEndX(trackLeft + snap / 2);
			}

			initializeComponents();
			initializeMouseEvents();
			initializeTimeline();

			initialization = true;
			positionThumb(true);
		}
	}

	private String convertColorToHex(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
	}

	private void initializeComponents() {
		sliderValue.setRotate(isHorizontal ? 45 : -45);

		animatedThumb.resize(30, 30);
		animatedThumb.setRotate(isHorizontal ? -45 : 45);
		animatedThumb.setBackground(new Background(new BackgroundFill(thumbColor, new CornerRadii(50, 50, 50, 0, true), null)));
		animatedThumb.getChildren().add(sliderValue);
		animatedThumb.setScaleX(0);
		animatedThumb.setScaleY(0);

		coloredTrack.setStroke(thumbColor);
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
				animatedThumb.setLayoutX(newVal.doubleValue() - 1 - thumb.getLayoutBounds().getWidth() / 2);
				long value = Math.round(getSkinnable().getValue());
				sliderValue.setText("" + value);
				if (coloredTrack.getStartX() < newVal.doubleValue()) {
					coloredTrack.setEndX(newVal.doubleValue());
				} else {
					coloredTrack.setEndX(coloredTrack.getStartX());
				}

				if (value == 0) {
					thumb.setStyle("-fx-background-color: " + convertColorToHex(trackColor));
					coloredTrack.setVisible(false);
					animatedThumb.setBackground(new Background(new BackgroundFill(trackColor, new CornerRadii(50, 50, 50, 0, true), null)));
				} else {
					thumb.setStyle("-fx-background-color: " + convertColorToHex(thumbColor));
					coloredTrack.setVisible(true);
					animatedThumb.setBackground(new Background(new BackgroundFill(thumbColor, new CornerRadii(50, 50, 50, 0, true), null)));
				}
			}
		});

		thumb.layoutYProperty().addListener((o, oldVal, newVal) -> {
			if (!isHorizontal) {
				animatedThumb.setLayoutY(newVal.doubleValue() - 1 - thumb.getLayoutBounds().getHeight() / 2);
				long value = Math.round(getSkinnable().getValue());
				sliderValue.setText("" + value);
				if (coloredTrack.getStartY() > newVal.doubleValue()) {
					coloredTrack.setEndY(newVal.doubleValue() + thumbWidth);
				} else {
					coloredTrack.setEndY(coloredTrack.getStartY());
				}

				if (value == 0) {
					thumb.setStyle("-fx-background-color: " + convertColorToHex(trackColor));
					coloredTrack.setVisible(false);
					animatedThumb.setBackground(new Background(new BackgroundFill(trackColor, new CornerRadii(50, 50, 50, 0, true), null)));
				} else {
					thumb.setStyle("-fx-background-color: " + convertColorToHex(thumbColor));
					coloredTrack.setVisible(true);
					animatedThumb.setBackground(new Background(new BackgroundFill(thumbColor, new CornerRadii(50, 50, 50, 0, true), null)));
				}
			}
		});
	}

	private void initializeTimeline() {
		if (isHorizontal) {
			double thumbPosX = thumb.getLayoutY() - thumb.getLayoutBounds().getWidth() / 2;
			timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(animatedThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(animatedThumb.scaleYProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(animatedThumb.layoutYProperty(), thumbPosX, Interpolator.EASE_BOTH)), new KeyFrame(Duration.seconds(0.2),
					new KeyValue(animatedThumb.scaleXProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(animatedThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(
							animatedThumb.layoutYProperty(), thumbPosX - 35, Interpolator.EASE_BOTH)));
		} else {
			timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(animatedThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(animatedThumb.scaleYProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(animatedThumb.layoutXProperty(), thumb.getLayoutX(), Interpolator.EASE_BOTH)), new KeyFrame(Duration.seconds(0.2), new KeyValue(animatedThumb.scaleXProperty(), 1,
					Interpolator.EASE_BOTH), new KeyValue(animatedThumb.scaleXProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(animatedThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(
					animatedThumb.layoutXProperty(), thumb.getLayoutX() + 35, Interpolator.EASE_BOTH)));
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
