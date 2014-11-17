package customui.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
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
import javafx.util.StringConverter;

import com.sun.javafx.scene.control.behavior.SliderBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class C3DSliderSkin extends BehaviorSkinBase<Slider, SliderBehavior> {

	private double thumbTop;
	private double thumbLeft;
	private double trackStart;
	private double trackLength;
	private boolean trackClicked, compInitialized, isHorizontal;

	private static final String DEFAULT_STYLE_CLASS = "c3d-slider";

	private double preDragThumbPos;
	private Point2D dragStart; // in skin coordinates

	private Label animatedThumb;
	private Circle thumbCircle;
	private double thumbRadius = 5;
	private Text valueText;
	private Line valueLine;
	private Line trackLine;

	private Color selectedColor;
	private Color unSelectedColor;

	private Timeline timeline;

	public C3DSliderSkin(Slider slider) {
		super(slider, new SliderBehavior(slider));
		slider.getStyleClass().setAll(DEFAULT_STYLE_CLASS);

		initialize();

		registerChangeListener(slider.minProperty(), "MIN");
		registerChangeListener(slider.maxProperty(), "MAX");
		registerChangeListener(slider.valueProperty(), "VALUE");
		registerChangeListener(slider.orientationProperty(), "ORIENTATION");
	}

	private void initialize() {
		isHorizontal = getSkinnable().getOrientation() == Orientation.HORIZONTAL;

		trackLine = new Line();
		trackLine.getStyleClass().setAll("trackLine");

		thumbCircle = new Circle();
		thumbCircle.getStyleClass().setAll("thumbCircle");
		thumbCircle.setRadius(thumbRadius);

		valueText = new Text();
		valueText.getStyleClass().setAll("valueText");

		StackPane animationPane = new StackPane();
		animationPane.setScaleX(0);
		animationPane.setScaleY(0);

		animatedThumb = new Label();
		animatedThumb.setRotate(isHorizontal ? -45 : 45);
		animatedThumb.setPadding(new Insets(5, 13, 4, 13));
		animatedThumb.setBackground(new Background(new BackgroundFill(selectedColor, new CornerRadii(50, 50, 50, 0, true), null)));

		valueLine = new Line();
		valueLine.getStyleClass().setAll("valueLine");

		animationPane.getChildren().addAll(animatedThumb, valueText);

		if (isHorizontal) {
			timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(animationPane.scaleXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(animationPane.scaleYProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(animationPane.layoutYProperty(), thumbCircle.getCenterY(), Interpolator.EASE_BOTH)), new KeyFrame(Duration.seconds(0.2), new KeyValue(animationPane.scaleXProperty(), 1,
					Interpolator.EASE_BOTH), new KeyValue(animationPane.scaleYProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(animationPane.layoutYProperty(), thumbCircle.getCenterY() - 25,
					Interpolator.EASE_BOTH)));
		} else {
			timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(animationPane.scaleXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(animationPane.scaleYProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(animationPane.layoutXProperty(), thumbCircle.getCenterX(), Interpolator.EASE_BOTH)), new KeyFrame(Duration.seconds(0.2), new KeyValue(animationPane.scaleXProperty(), 1,
					Interpolator.EASE_BOTH), new KeyValue(animationPane.scaleYProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(animationPane.layoutXProperty(), thumbCircle.getCenterX() + 35,
					Interpolator.EASE_BOTH)));
		}

		thumbCircle.layoutXProperty().addListener((o, oldVal, newVal) -> {
			animationPane.setLayoutX(newVal.doubleValue());
			valueLine.setEndX(newVal.doubleValue());
			valueText.setText("" + Math.round(getSkinnable().getValue()));
		});

		thumbCircle.layoutYProperty().addListener((o, oldVal, newVal) -> {
			animationPane.setLayoutY(newVal.doubleValue());
			valueLine.setEndY(newVal.doubleValue());
			valueText.setText("" + Math.round(getSkinnable().getValue()));
		});

		getChildren().clear();
		getChildren().addAll(trackLine, thumbCircle, animationPane, valueLine);

		getSkinnable().setOnMousePressed(me -> {
			if (!thumbCircle.isPressed()) {
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
			if (!thumbCircle.isPressed()) {
				if (isHorizontal) {
					getBehavior().trackPress(me, (me.getX() / trackLength));
				} else {
					getBehavior().trackPress(me, (me.getY() / trackLength));
				}
			}
		});

		thumbCircle.setOnMousePressed(me -> {
			getBehavior().thumbPressed(me, 0.0f);
			dragStart = thumbCircle.localToParent(me.getX(), me.getY());
			preDragThumbPos = (getSkinnable().getValue() - getSkinnable().getMin()) / (getSkinnable().getMax() - getSkinnable().getMin());
		});

		thumbCircle.setOnMouseReleased(me -> {
			getBehavior().thumbReleased(me);
		});

		thumbCircle.setOnMouseDragged(me -> {
			Point2D cur = thumbCircle.localToParent(me.getX(), me.getY());
			double dragPos = isHorizontal ? cur.getX() - dragStart.getX() : -(cur.getY() - dragStart.getY());
			getBehavior().thumbDragged(me, preDragThumbPos + dragPos / trackLength);
		});
	}

	StringConverter<Number> stringConverterWrapper = new StringConverter<Number>() {
		Slider slider = getSkinnable();

		@Override
		public String toString(Number object) {
			return (object != null) ? slider.getLabelFormatter().toString(object.doubleValue()) : "";
		}

		@Override
		public Number fromString(String string) {
			return slider.getLabelFormatter().fromString(string);
		}
	};

	@Override
	protected void handleControlPropertyChanged(String p) {
		super.handleControlPropertyChanged(p);
		if ("ORIENTATION".equals(p)) {
			getSkinnable().requestLayout();
		} else if ("VALUE".equals(p)) {
			// only animate thumb if the track was clicked - not if the thumb is dragged
			positionAll(trackClicked);
		} else if ("MIN".equals(p)) {
			getSkinnable().requestLayout();
		} else if ("MAX".equals(p)) {
			getSkinnable().requestLayout();
		}
	}

	/**
	 * Called when ever either min, max or value changes, so thumb's layoutX, Y is recomputed.
	 */
	void positionAll(final boolean animate) {
		Slider s = getSkinnable();
		if (s.getValue() > s.getMax()) {
			return; // this can happen if we are bound to something 
		}
		final double endX = (isHorizontal) ? (((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))))) : thumbLeft;
		final double endY = (isHorizontal) ? thumbTop : trackLength - (trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))); //  - thumbHeight/2

		if (animate) {
			// lets animate the thumb transition
			final double startX = thumbCircle.getLayoutX();
			final double startY = thumbCircle.getLayoutY();
			Transition transition = new Transition() {
				{
					setCycleDuration(Duration.millis(20));
				}

				@Override
				protected void interpolate(double frac) {
					if (!Double.isNaN(startX)) {
						thumbCircle.setLayoutX(startX + frac * (endX - startX));
					}
					if (!Double.isNaN(startY)) {
						thumbCircle.setLayoutY(startY + frac * (endY - startY));
					}
				}
			};
			transition.play();
		} else {
			thumbCircle.setLayoutX(endX);
			thumbCircle.setLayoutY(endY);
		}
		if (isHorizontal) {
			if (endX == 0) {
				thumbCircle.setStroke(unSelectedColor);
				thumbCircle.setFill(Color.TRANSPARENT);
				valueLine.setVisible(false);
				trackLine.setStartX(thumbRadius);
				animatedThumb.setBackground(new Background(new BackgroundFill(unSelectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			} else {
				thumbCircle.setStroke(selectedColor);
				thumbCircle.setFill(selectedColor);
				valueLine.setVisible(true);
				trackLine.setStartX(trackStart);
				animatedThumb.setBackground(new Background(new BackgroundFill(selectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			}
		} else {
			if (endY == trackLength) {
				thumbCircle.setStroke(unSelectedColor);
				thumbCircle.setFill(Color.TRANSPARENT);
				valueLine.setVisible(false);
				trackLine.setStartY(trackLength - thumbRadius);
				animatedThumb.setBackground(new Background(new BackgroundFill(unSelectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			} else {
				thumbCircle.setStroke(selectedColor);
				thumbCircle.setFill(selectedColor);
				valueLine.setVisible(true);
				trackLine.setStartY(trackStart + trackLength);
				animatedThumb.setBackground(new Background(new BackgroundFill(selectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			}
		}
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if (!compInitialized) {
			initializeComponents(x, y, w, h);
			compInitialized = true;
		}
		positionAll(true);
	}

	private void initializeComponents(final double x, final double y, final double w, final double h) {
		selectedColor = (Color) thumbCircle.getFill();
		unSelectedColor = (Color) trackLine.getStroke();
		if (isHorizontal) {
			thumbRadius = thumbCircle.getRadius() + thumbCircle.getStrokeWidth();
			double startY = y + (h / 2);
			trackLength = snapSize(w - thumbRadius);
			trackStart = snapPosition(x + (thumbRadius / 2));

			trackLine.setStartX(trackStart);
			trackLine.setEndX(trackLength);
			valueLine.setStartX(trackStart);
			valueLine.setStartY(startY);
			valueLine.setEndY(startY);
			trackLine.setStartY(startY);
			trackLine.setEndY(startY);

			thumbTop = (int) startY;
		} else {
			thumbRadius = thumbCircle.getRadius() + thumbCircle.getStrokeWidth();
			double startX = x + (w / 2);
			getSkinnable().getWidth();
			getSkinnable().getHeight();
			trackLength = snapSize(h - thumbRadius);
			trackStart = snapPosition(y + (thumbRadius / 2));

			trackLine.setStartY(trackStart + trackLength);
			trackLine.setEndY(trackStart);
			valueLine.setStartY(trackStart + trackLength);
			valueLine.setStartX(startX);
			valueLine.setEndX(startX);
			trackLine.setStartX(startX);
			trackLine.setEndX(startX);

			thumbLeft = (int) startX;
		}
	}

	double minTrackLength() {
		return 2 * thumbCircle.prefWidth(-1);
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return (leftInset + minTrackLength() + thumbCircle.minWidth(-1) + rightInset);
		} else {
			return (leftInset + thumbCircle.prefWidth(-1) + rightInset);
		}
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return topInset + thumbCircle.prefHeight(-1) + bottomInset;
		} else {
			return topInset + minTrackLength() + thumbCircle.prefHeight(-1) + bottomInset;
		}
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return 140;
		} else {
			return leftInset + Math.max(thumbCircle.prefWidth(-1), trackLine.prefWidth(-1)) + rightInset;
		}
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		if (isHorizontal) {
			return topInset + Math.max(thumbCircle.prefHeight(-1), trackLine.prefHeight(-1)) + bottomInset;
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
