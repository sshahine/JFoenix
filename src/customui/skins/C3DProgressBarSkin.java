package customui.skins;

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

import com.sun.javafx.scene.control.skin.ProgressBarSkin;

public class C3DProgressBarSkin extends ProgressBarSkin {

	private StackPane effectsPane;

	private Line line;
	private Line progressLine;
	private double offset = 3;
	private double endX;
	private double startX;
	private double barWidth;

	public C3DProgressBarSkin(ProgressBar bar) {
		super(bar);
		InvalidationListener listener = new InvalidationListener() {
			@Override public void   invalidated(Observable valueModel) {
				updateProgress();
			}
		};
		bar.widthProperty().addListener(listener);
		bar.progressProperty().addListener(listener);
	}

	private void  updateProgress() {
		ProgressBar control = getSkinnable();
		barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
		getSkinnable().requestLayout();
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		effectsPane  = new StackPane();
		line = new Line();
		startX = getSkinnable().getBoundsInLocal().getMinX();
		endX = getSkinnable().getBoundsInLocal().getMaxX();
		line.setStartX( startX + offset);
		line.setEndX(endX);
		line.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
		line.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
		line.setStroke(Color.valueOf("#C8C8C8"));
		line.setStrokeWidth(3);
		line.setStrokeType(StrokeType.CENTERED);		
		effectsPane.getChildren().add(line);
		this.getChildren().add(effectsPane);

		progressLine = new Line();
		startX = getSkinnable().getBoundsInLocal().getMinX();
		endX = barWidth;
		progressLine.setStartX( startX + offset);
		progressLine.setEndX(endX);
		progressLine.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
		progressLine.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
		progressLine.setStroke(Color.valueOf("#0F9D58"));
		progressLine.setStrokeWidth(3);
		progressLine.setStrokeType(StrokeType.CENTERED);		
		effectsPane.getChildren().add(progressLine);
		StackPane.setAlignment(progressLine, Pos.CENTER_LEFT);

		layoutInArea(effectsPane, x, y, w, h, -1, HPos.CENTER, VPos.BOTTOM);
	}

}
