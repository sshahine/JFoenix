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
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
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

	private double preDragThumbPos;
	private Point2D dragStart; // in skin coordinates

	private Label animatedThumb;
	private Circle thumb;
	private double thumbRadius = 5;
	private double strokeWidth = 2;
	private Text valueText;
	private Line line;
	private Line track;

	private Color selectedColor = Color.valueOf("#0F9D58");
	private Color unSelectedColor = Color.valueOf("#CCCCCC");

	private Timeline timeline;

	public C3DSliderSkin(Slider slider) {
		super(slider, new SliderBehavior(slider));

		initialize();
		registerChangeListener(slider.minProperty(), "MIN");
		registerChangeListener(slider.maxProperty(), "MAX");
		registerChangeListener(slider.valueProperty(), "VALUE");
		registerChangeListener(slider.orientationProperty(), "ORIENTATION");
	}

	private void initialize() {
		isHorizontal = getSkinnable().getOrientation() == Orientation.HORIZONTAL;

		track = new Line();
		track.setStroke(unSelectedColor);
		track.setStrokeWidth(strokeWidth);

		thumb = new Circle();
		thumb.setRadius(thumbRadius);
		thumb.setVisible(true);
		thumb.setStroke(selectedColor);
		thumb.setFill(selectedColor);
		thumb.setStrokeWidth(strokeWidth);

		valueText = new Text();
		valueText.setStroke(Color.WHITE);
		valueText.setStyle("-fx-font-size:10;");

		StackPane bashThumb = new StackPane();
		bashThumb.setScaleX(0);
		bashThumb.setScaleY(0);

		animatedThumb = new Label();
		animatedThumb.setPadding(new Insets(5, 13, 4, 13));
		animatedThumb.setBackground(new Background(new BackgroundFill(selectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
		animatedThumb.setRotate(isHorizontal ? -45 : 45);

		bashThumb.getChildren().addAll(animatedThumb, valueText);

		timeline = isHorizontal ? new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(bashThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(bashThumb.scaleYProperty(), 0,
				Interpolator.EASE_BOTH), new KeyValue(bashThumb.layoutYProperty(), thumb.getCenterY(), Interpolator.EASE_BOTH)), new KeyFrame(Duration.seconds(0.2), new KeyValue(bashThumb.scaleXProperty(), 1,
				Interpolator.EASE_BOTH), new KeyValue(bashThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(bashThumb.layoutYProperty(), thumb.getCenterY() - 20, Interpolator.EASE_BOTH)))
				: new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(bashThumb.scaleXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(bashThumb.scaleYProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(bashThumb.layoutXProperty(), thumb.getCenterX(), Interpolator.EASE_BOTH)), new KeyFrame(Duration.seconds(0.2), new KeyValue(bashThumb.scaleXProperty(), 1,
						Interpolator.EASE_BOTH), new KeyValue(bashThumb.scaleYProperty(), 1, Interpolator.EASE_BOTH), new KeyValue(bashThumb.layoutXProperty(), thumb.getCenterX() + 35, Interpolator.EASE_BOTH)));

		line = new Line();
		line.setStroke(selectedColor);
		line.setStrokeWidth(strokeWidth);

		thumb.layoutXProperty().addListener((o, oldVal, newVal) -> {
			bashThumb.setLayoutX(newVal.doubleValue());
			line.setEndX(newVal.doubleValue());
			valueText.setText("" + Math.round(getSkinnable().getValue()));
		});

		thumb.layoutYProperty().addListener((o, oldVal, newVal) -> {
			bashThumb.setLayoutY(newVal.doubleValue());
			line.setEndY(newVal.doubleValue());
			valueText.setText("" + Math.round(getSkinnable().getValue()));
		});

		getChildren().clear();
		getChildren().addAll(track, thumb, bashThumb, line);

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
		final double endX = (isHorizontal) ? trackStart + (((trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))))) : thumbLeft;
		final double endY = (isHorizontal) ? thumbTop : trackStart + trackLength - (trackLength * ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()))); //  - thumbHeight/2

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
		if (isHorizontal) {
			if (endX == trackStart) {
				thumb.setStroke(unSelectedColor);
				thumb.setFill(Color.TRANSPARENT);
				line.setVisible(false);
				track.setStartX(trackStart + thumbRadius);
				animatedThumb.setBackground(new Background(new BackgroundFill(unSelectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			} else {
				thumb.setStroke(selectedColor);
				thumb.setFill(selectedColor);
				line.setVisible(true);
				track.setStartX(trackStart);
				animatedThumb.setBackground(new Background(new BackgroundFill(selectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			}
		} else {
			if (endY == trackStart + trackLength) {
				thumb.setStroke(unSelectedColor);
				thumb.setFill(Color.TRANSPARENT);
				line.setVisible(false);
				track.setStartY(trackStart + trackLength - thumbRadius);
				animatedThumb.setBackground(new Background(new BackgroundFill(unSelectedColor, new CornerRadii(50, 50, 50, 0, true), null)));
			} else {
				thumb.setStroke(selectedColor);
				thumb.setFill(selectedColor);
				line.setVisible(true);
				track.setStartY(trackStart + trackLength);
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
		if (isHorizontal) {
			double trackHeight = snapSize(track.prefHeight(-1));
			double trackAreaHeight = Math.max(trackHeight, thumbRadius);
			double startY = y + (h / 2);
			trackLength = snapSize(w - thumbRadius);
			trackStart = snapPosition(x + (thumbRadius / 2));

			track.setStartX(trackStart);
			track.setEndX(trackLength);
			line.setStartX(trackStart);
			line.setStartY(startY);
			line.setEndY(startY);
			track.setStartY(startY);
			track.setEndY(startY);
			animatedThumb.setLayoutX(thumb.getLayoutX());

			thumbTop = (int) (startY + ((trackAreaHeight - thumbRadius) / 2));
		} else {
			double trackWidth = snapSize(track.prefWidth(-1));
			double trackAreaWidth = Math.max(trackWidth, thumbRadius);
			double startX = x + (w / 2);
			getSkinnable().getWidth();
			getSkinnable().getHeight();
			trackLength = snapSize(h - thumbRadius);
			trackStart = snapPosition(y + (thumbRadius / 2));

			track.setStartY(trackStart + trackLength);
			track.setEndY(trackStart);
			line.setStartY(trackStart + trackLength);
			line.setStartX(startX);
			line.setEndX(startX);
			track.setStartX(startX);
			track.setEndX(startX);
			animatedThumb.setLayoutY(thumb.getLayoutY());

			thumbLeft = (int) (startX + ((trackAreaWidth - thumbRadius) / 2));
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
