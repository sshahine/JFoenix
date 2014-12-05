package com.cctintl.c3dfx.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DProgressBar;
import com.sun.javafx.scene.control.skin.ProgressBarSkin;

public class C3DProgressBarSkin extends ProgressBarSkin {

	private StackPane effectsPane;

	private Line line;
	private Line progressLine;
	private double barWidth;
	private boolean invalid = true;

	public C3DProgressBarSkin(C3DProgressBar bar) {
		super(bar);
		InvalidationListener listener = new InvalidationListener() {
			@Override public void   invalidated(Observable valueModel) {
				updateProgress();
			}
		};
		bar.widthProperty().addListener(listener);		
		bar.progressProperty().addListener(listener);

		effectsPane  = new StackPane();
		line = new Line();
		progressLine = new Line();
		effectsPane.getChildren().add(line);
		effectsPane.getChildren().add(progressLine);
		StackPane.setAlignment(progressLine, Pos.CENTER_LEFT);

		this.getChildren().add(effectsPane);
	}

	@Override
	protected void updateProgress() {
		ProgressBar control = (ProgressBar) getSkinnable();
		barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
		getSkinnable().requestLayout();
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		double startX = getSkinnable().getBoundsInLocal().getMinX();
		double endX = getSkinnable().getBoundsInLocal().getMaxX();
		Number strokeWidth = ((C3DProgressBar)getSkinnable()).getStrokeWidth();

		if(invalid){
			line.setStartX( startX + strokeWidth.doubleValue());
			line.setEndX(endX);
			line.setStartY(getSkinnable().getBoundsInLocal().getMaxY());
			line.setEndY(getSkinnable().getBoundsInLocal().getMaxY());
			line.strokeProperty().bind(((C3DProgressBar)getSkinnable()).trackColorProperty());
			line.strokeWidthProperty().bind(((C3DProgressBar)getSkinnable()).strokeWidthProperty());
			line.setStrokeType(StrokeType.CENTERED);	

			progressLine.setStartX( startX + strokeWidth.doubleValue());
			progressLine.setStartY(getSkinnable().getBoundsInLocal().getMaxY());
			progressLine.setEndY(getSkinnable().getBoundsInLocal().getMaxY());
			progressLine.strokeProperty().bind(((C3DProgressBar)getSkinnable()).progressColorProperty());
			progressLine.strokeWidthProperty().bind(((C3DProgressBar)getSkinnable()).strokeWidthProperty());
			progressLine.setStrokeType(StrokeType.CENTERED);
			
			if(!getSkinnable().isIndeterminate()){
				progressLine.setEndX(barWidth);
			}
			else{
				endX = getSkinnable().getBoundsInLocal().getMaxX()/4;		
				double width =  (endX - startX) * progressLine.scaleXProperty().get();
				progressLine.setEndX(endX);
				
				Timeline indeterminateAnimation = new Timeline(
						new KeyFrame(Duration.millis(0),
								new KeyValue(progressLine.scaleXProperty(), 0.0 ,Interpolator.EASE_BOTH),
								new KeyValue(progressLine.translateXProperty(),  0 ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(Duration.millis(500),
										new KeyValue(progressLine.scaleXProperty(), 1.0 ,Interpolator.EASE_BOTH)
										),
										new KeyFrame(Duration.millis(1000),
												new KeyValue(progressLine.scaleXProperty(), 0.0 ,Interpolator.EASE_BOTH),
												new KeyValue(progressLine.translateXProperty(), getSkinnable().getBoundsInLocal().getMaxX(),Interpolator.EASE_BOTH)
												)
						);
				indeterminateAnimation.setCycleCount(Timeline.INDEFINITE);				
				indeterminateAnimation.play();
			}
		}

		if(!getSkinnable().isIndeterminate()){
			progressLine.setEndX(barWidth);
		}

		layoutInArea(effectsPane, x, y, w, h, -1, HPos.CENTER, VPos.BOTTOM);
	}

}
