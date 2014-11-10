package customui.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import com.fxexperience.javafx.animation.CachedTimelineTransition;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;

import customui.components.Rippler;
import customui.components.Rippler.RipplerMask;

public class C3DRadioButtonSkin extends RadioButtonSkin {

	private double lineThick = 2;
	private double padding = 15;
	private double contWidth, contHeight;
	private double maxHeight, maxRadius = 8, minRadius = -1;
	private final Rippler rippler;

	private Circle circle, winCircle;

	private Color unSelectedColor = Color.valueOf("#5A5A5A");
	private Color selectedColor = Color.valueOf("#0F9D58");
	private Color strokeColor = Color.valueOf("#5a5a5a");

	private Transition transition;

	private final AnchorPane container = new AnchorPane();
	private double labelOffset = -5;

	private boolean invalid = true;

	public C3DRadioButtonSkin(RadioButton control) {
		super(control);

		circle = new Circle();
		circle.setRadius(maxRadius);
		circle.setVisible(true);
		circle.setStroke(strokeColor);
		circle.setFill(Color.TRANSPARENT);
		circle.setStrokeWidth(2);

		winCircle = new Circle();
		winCircle.setRadius(minRadius);
		winCircle.setStroke(selectedColor);
		winCircle.setFill(selectedColor);
		winCircle.setStrokeWidth(2);

		StackPane boxContainer = new StackPane();
		boxContainer.getChildren().addAll(circle, winCircle);
		boxContainer.setPadding(new Insets(padding));

		rippler = new Rippler(boxContainer, RipplerMask.CIRCLE);
		rippler.setColor(selectedColor);
		rippler.setColor(getSkinnable().isSelected() ? unSelectedColor : selectedColor);

		container.getChildren().add(rippler);

		AnchorPane.setRightAnchor(rippler, labelOffset);
		updateChildren();

		getSkinnable().selectedProperty().addListener((o, oldVal, newVal) -> {
			rippler.setColor(newVal ? unSelectedColor : selectedColor);
			transition.setRate(newVal ? 1 : -1);
			transition.play();
		});

	}

	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (circle != null) {
			getChildren().remove(1);
			getChildren().add(container);
		}
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(circle.minWidth(-1)) + labelOffset + 2 * padding;
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(circle.prefWidth(-1)) + labelOffset + 2 * padding;
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		final RadioButton radioButton = getSkinnable();
		contWidth = snapSize(container.prefWidth(-1));
		contHeight = snapSize(container.prefHeight(-1));
		final double computeWidth = Math.min(radioButton.prefWidth(-1), radioButton.minWidth(-1)) + labelOffset + 2 * padding;
		final double labelWidth = Math.min(computeWidth - contWidth, w - snapSize(contWidth)) + labelOffset + 2 * padding;
		final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
		maxHeight = Math.max(contHeight, labelHeight);

		if (invalid) {
			circle.setCenterX((contWidth + padding - labelOffset) / 2);
			circle.setCenterY(maxHeight - padding - lineThick);
			transition = new RadioButtonTransition();
			invalid = false;
		}

		layoutLabelInArea(contWidth, 0, labelWidth, maxHeight, radioButton.getAlignment());
		container.resize(contWidth, contHeight);
		positionInArea(container, 0, 0, contWidth, maxHeight, 0, radioButton.getAlignment().getHpos(), radioButton.getAlignment().getVpos());

	}

	private class RadioButtonTransition extends CachedTimelineTransition {

		public RadioButtonTransition() {
			super(winCircle, new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(winCircle.radiusProperty(), minRadius, Interpolator.EASE_BOTH)), new KeyFrame(Duration.millis(1000), new KeyValue(
					winCircle.radiusProperty(), maxRadius, Interpolator.EASE_BOTH))));
			setCycleDuration(Duration.seconds(0.28));
			setDelay(Duration.seconds(0));
		}
	}

}