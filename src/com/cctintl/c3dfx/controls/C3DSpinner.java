package com.cctintl.c3dfx.controls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class C3DSpinner extends Region {

	private Color greenColor, redColor, yellowColor, blueColor;
	private Timeline timeline;
	private Arc arc;

	public C3DSpinner() {
		super();
		initialize();
	}

	private void initialize() {
		this.resize(30, 30);

		blueColor = Color.valueOf("#4285f4");
		redColor = Color.valueOf("#db4437");
		yellowColor = Color.valueOf("#f4b400");
		greenColor = Color.valueOf("#0F9D58");

		arc = new Arc(150, 155, 25, 25, 90, 50);
		arc.setStroke(blueColor);
		arc.setStrokeWidth(7);
		arc.setFill(Color.TRANSPARENT);

		KeyFrame[] blueFrame = getKeyFrames(0, 0, blueColor);
		KeyFrame[] redFrame = getKeyFrames(450, 1.4, redColor);
		KeyFrame[] yellowFrame = getKeyFrames(900, 2.8, yellowColor);
		KeyFrame[] greenFrame = getKeyFrames(1350, 4.2, greenColor);

		KeyFrame endingFrame = new KeyFrame(Duration.seconds(5.6), new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(arc.startAngleProperty(), 1845, Interpolator.LINEAR));

		timeline = new Timeline(blueFrame[0], blueFrame[1], blueFrame[2], blueFrame[3], redFrame[0], redFrame[1], redFrame[2], redFrame[3], yellowFrame[0], yellowFrame[1], yellowFrame[2], yellowFrame[3],
				greenFrame[0], greenFrame[1], greenFrame[2], greenFrame[3], endingFrame);

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setRate(1);
		timeline.play();

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

}
