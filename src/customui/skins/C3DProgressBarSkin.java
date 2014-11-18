package customui.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import com.sun.javafx.scene.control.skin.ProgressBarSkin;

import customui.components.C3DProgressBar;

public class C3DProgressBarSkin extends ProgressBarSkin {

	private StackPane effectsPane;

	private Line line;
	private Line progressLine;
	private double barWidth;

	public C3DProgressBarSkin(C3DProgressBar bar) {
		super(bar);
		InvalidationListener listener = new InvalidationListener() {
			@Override public void   invalidated(Observable valueModel) {
				updateProgress();
			}
		};
		bar.widthProperty().addListener(listener);		
		bar.progressProperty().addListener(listener);
	}

	@Override
	protected void updateProgress() {
		ProgressBar control = (ProgressBar) getSkinnable();
		barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
		getSkinnable().requestLayout();
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		effectsPane  = new StackPane();
		line = new Line();
		double startX = getSkinnable().getBoundsInLocal().getMinX();
		double endX = getSkinnable().getBoundsInLocal().getMaxX();
		Number strokeWidth = ((C3DProgressBar)getSkinnable()).getStrokeWidth();
		
		line.setStartX( startX + strokeWidth.doubleValue());
		line.setEndX(endX);
		line.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
		line.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
		line.setStroke(((C3DProgressBar)getSkinnable()).getTrackColor());
		line.setStrokeWidth(strokeWidth.doubleValue());
		line.setStrokeType(StrokeType.CENTERED);		
		effectsPane.getChildren().add(line);
		this.getChildren().add(effectsPane);

		if(!getSkinnable().isIndeterminate()){
			progressLine = new Line();
			startX = getSkinnable().getBoundsInLocal().getMinX();
			endX = barWidth;
			progressLine.setStartX( startX + strokeWidth.doubleValue());
			progressLine.setEndX(endX);
			progressLine.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
			progressLine.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
			progressLine.setStroke(((C3DProgressBar)getSkinnable()).getProgressColor());
			progressLine.setStrokeWidth(strokeWidth.doubleValue());
			progressLine.setStrokeType(StrokeType.CENTERED);		
			effectsPane.getChildren().add(progressLine);
			StackPane.setAlignment(progressLine, Pos.CENTER_LEFT);
		}
		else{
			progressLine = new Line();
			startX = getSkinnable().getBoundsInLocal().getMinX();
			endX = getSkinnable().getBoundsInLocal().getMaxX()/4;			
			progressLine.setStartX( startX + strokeWidth.doubleValue());
			progressLine.setEndX(endX);
			progressLine.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
			progressLine.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
			progressLine.setStroke(Color.valueOf("#0F9D58"));
			progressLine.setStrokeWidth(strokeWidth.doubleValue());
			progressLine.setStrokeType(StrokeType.CENTERED);		
			effectsPane.getChildren().add(progressLine);
			progressLine.setTranslateX(barWidth);			
			StackPane.setAlignment(progressLine, Pos.CENTER_LEFT);
			double width =  (endX - startX) * progressLine.scaleXProperty().get();
			Timeline indeterminateAnimation = new Timeline(
					new KeyFrame(Duration.millis(0),
							new KeyValue(progressLine.scaleXProperty(), 0.0 ,Interpolator.EASE_BOTH),
							new KeyValue(progressLine.translateXProperty(),  -width/2 ,Interpolator.EASE_BOTH)
					),
					new KeyFrame(Duration.millis(500),
							new KeyValue(progressLine.scaleXProperty(), 1.0 ,Interpolator.EASE_BOTH)
					),
					new KeyFrame(Duration.millis(1000),
							new KeyValue(progressLine.scaleXProperty(), 0.0 ,Interpolator.EASE_BOTH),
							new KeyValue(progressLine.translateXProperty(), getSkinnable().getBoundsInLocal().getMaxX() - width/2,Interpolator.EASE_BOTH)
					)
			);
			indeterminateAnimation.setCycleCount(Timeline.INDEFINITE);
			indeterminateAnimation.play();
		}
		layoutInArea(effectsPane, x, y, w, h, -1, HPos.CENTER, VPos.BOTTOM);
	}

}
