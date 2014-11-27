package com.cctintl.c3dfx.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.C3DRippler.RipplerMask;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;

public class C3DRadioButtonSkin extends RadioButtonSkin {

	private boolean compInitialized;
	private double padding = 15;
	private double contWidth, contHeight;
	private double maxHeight, radioRadius = 8, minRadius = -1;
	private final C3DRippler rippler;

	private Circle radio, dot;

	private Color unSelectedColor = Color.valueOf("#5A5A5A");
	private Color selectedColor;

	private Timeline timeline;

	private final AnchorPane container = new AnchorPane();
	private double labelOffset = -5;

	public C3DRadioButtonSkin(RadioButton control) {
		super(control);

		radio = new Circle();
		radio.getStyleClass().setAll("radio");

		dot = new Circle();
		dot.getStyleClass().setAll("dot");

		StackPane boxContainer = new StackPane();
		boxContainer.getChildren().addAll(radio, dot);
		boxContainer.setPadding(new Insets(padding));

		rippler = new C3DRippler(boxContainer, RipplerMask.CIRCLE);

		container.getChildren().add(rippler);

		AnchorPane.setRightAnchor(rippler, labelOffset);
		updateChildren();

		getSkinnable().selectedProperty().addListener((o, oldVal, newVal) -> {
			rippler.setRipplerFill(newVal ? unSelectedColor : selectedColor);
			timeline.setRate(newVal ? 1 : -1);
			timeline.play();
		});

	}

	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (radio != null) {
			removeRadio();
			getChildren().add(container);
		}
	}

	private void removeRadio() {
		for (int i = 0; i < getChildren().size(); i++) {
			if ("radio".equals(getChildren().get(i).getStyleClass().get(0))) {
				getChildren().remove(i);
			}
		}
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(radio.minWidth(-1)) + labelOffset + 2 * padding;
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(radio.prefWidth(-1)) + labelOffset + 2 * padding;
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if (!compInitialized) {
			initializeComponents(x, y, w, h);
			compInitialized = true;
		}
	}

	private void initializeComponents(final double x, final double y, final double w, final double h) {

		final RadioButton radioButton = getSkinnable();

		radio.setRadius(radioRadius);
		radio.setStroke(unSelectedColor);
		dot.setRadius(minRadius);

		selectedColor = (Color) dot.getFill();

		rippler.setRipplerFill(getSkinnable().isSelected() ? unSelectedColor : selectedColor);
		double radioWidth = radioRadius + radio.getStrokeWidth() / 2;

		timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(dot.radiusProperty(), minRadius, Interpolator.EASE_BOTH)), new KeyFrame(Duration.millis(200), new KeyValue(dot.radiusProperty(),
				radioWidth, Interpolator.EASE_BOTH)));

		contWidth = snapSize(container.prefWidth(-1));
		contHeight = snapSize(container.prefHeight(-1));
		final double computeWidth = Math.min(radioButton.prefWidth(-1), radioButton.minWidth(-1)) + labelOffset + 2 * padding;
		final double labelWidth = Math.min(computeWidth - contWidth, w - snapSize(contWidth)) + labelOffset + 2 * padding;
		final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
		maxHeight = Math.max(contHeight, labelHeight);

		layoutLabelInArea(contWidth, 0, labelWidth, maxHeight, radioButton.getAlignment());
		container.resize(contWidth, contHeight);
		positionInArea(container, 0, 0, contWidth, maxHeight, 0, radioButton.getAlignment().getHpos(), radioButton.getAlignment().getVpos());
	}

}