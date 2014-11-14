package customui.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.fxexperience.javafx.animation.CachedTimelineTransition;


public class C3DDialog extends StackPane {

	public static enum C3DDialogLayout{PLAIN, HEADING, ACTIONS, BACKDROP};
	public static enum C3DDialogAnimation{CENTER, TOP, RIGHT, BOTTOM, LEFT};

	private C3DDialogLayout layout;
	private C3DDialogAnimation animationType = C3DDialogAnimation.CENTER;
	private Transition transition;
	private StackPane contentHolder;
	private StackPane overlayPane;

	private double offsetX = 0;
	private double offsetY = 0;

	public C3DDialog(Pane parent, Region content, C3DDialogAnimation animationType) {		
		
		this.animationType = animationType;
		
		contentHolder = new StackPane();
		contentHolder.getChildren().add(content);
		contentHolder.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		DepthManager.setDepth(contentHolder, 4);
		// ensure stackpane is never resized beyond it's preferred size
		contentHolder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		
		overlayPane = new StackPane();
		overlayPane.getChildren().add(contentHolder);
		StackPane.setAlignment(contentHolder, Pos.CENTER);
		overlayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), null, null)));
		// close the dialog if clicked on the overlay pane
		overlayPane.setOnMouseClicked((e)->parent.getChildren().remove(overlayPane));
		parent.getChildren().add(overlayPane);
		
		offsetX = (overlayPane.getParent().getBoundsInLocal().getWidth()/2 + content.getPrefWidth());
		offsetY = (overlayPane.getParent().getBoundsInLocal().getHeight()/2 + content.getPrefHeight());
		transition = getShowAnimation();	
	}

	public void setAnimationType(C3DDialogAnimation animationType){
		this.animationType = animationType;
	}
	
	public void show(){
		transition.play();
	}
	
	private Transition getShowAnimation(){
		Transition transition = null;
		switch (animationType) {		
		case LEFT:	
			contentHolder.setTranslateX(-offsetX);
			transition = new LeftTransition();
			break;
		case RIGHT:			
			contentHolder.setTranslateX(offsetX);
			transition = new RightTransition();
			break;
		case TOP:	
			contentHolder.setTranslateY(-offsetY);
			transition = new TopTransition();
			break;
		case BOTTOM:			
			contentHolder.setTranslateY(offsetY);
			transition = new BottomTransition();
			break;
		default:
			contentHolder.setScaleX(0);
			contentHolder.setScaleY(0);
			transition = new CenterTransition();
			break;
		}
		return transition;
	}


	private class LeftTransition extends CachedTimelineTransition {
		public LeftTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateXProperty(), -offsetX ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateXProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class RightTransition extends CachedTimelineTransition {
		public RightTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateXProperty(), offsetX ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateXProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class TopTransition extends CachedTimelineTransition {
		public TopTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateYProperty(), -offsetY ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateYProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class BottomTransition extends CachedTimelineTransition {
		public BottomTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateYProperty(), offsetY ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateYProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class CenterTransition extends CachedTimelineTransition {
		public CenterTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, 
							new KeyValue(contentHolder.scaleXProperty(), 0 ,Interpolator.EASE_BOTH),
							new KeyValue(contentHolder.scaleYProperty(), 0 ,Interpolator.EASE_BOTH)							
							),
							new KeyFrame(Duration.millis(1000), 							
									new KeyValue(contentHolder.scaleXProperty(), 1 ,Interpolator.EASE_BOTH),
									new KeyValue(contentHolder.scaleYProperty(), 1 ,Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}
}

