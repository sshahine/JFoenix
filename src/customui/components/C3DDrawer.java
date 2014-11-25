package customui.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.fxexperience.javafx.animation.CachedTimelineTransition;

public class C3DDrawer extends StackPane {

	private StackPane shadowedPane = new StackPane();
	private StackPane sidePane = new StackPane();
	private Node content;
	private Transition transition;
	private Transition partialTransition;
	private Duration holdTime = Duration.seconds(0.2);
	private PauseTransition holdTimer = new PauseTransition(holdTime);

	private double initOffset = 30;
	private double initTranslateX = 0;
	private double activeOffset = 20;

	public C3DDrawer(Node content, double drawerWidth){
		super();

		this.content = content;

		shadowedPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));
		shadowedPane.setVisible(false);
		shadowedPane.setOpacity(0);

		sidePane.setMaxWidth(drawerWidth);
		sidePane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		initTranslateX = -1 * sidePane.maxWidthProperty().getValue() - initOffset;
		sidePane.setTranslateX(initTranslateX);
		DepthManager.setDepth(sidePane, 2);

		this.getChildren().add(this.content);		
		this.getChildren().add(shadowedPane);
		this.getChildren().add(sidePane);
		StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);

		// add listeners
		transition = new DrawerTransition();	
		shadowedPane.setOnMouseClicked((e) -> {
			transition.setRate(-1);		
			transition.play();
		});

		// mouse drag handler
		EventHandler<MouseEvent> dragHandler = (mouseEvent)->{
			if(mouseEvent.getSceneX() >= activeOffset && partialTransition !=null){
				partialTransition = null;
				shadowedPane.setVisible(true);
				shadowedPane.setOpacity(1);
			}else if(partialTransition == null){
				double translateX = initTranslateX + initOffset + mouseEvent.getSceneX();
				if(translateX <= 0)
					sidePane.setTranslateX(translateX);
			}
		};

		EventHandler<MouseEvent> mouseReleasedHandler = (mouseEvent)->{
			if(sidePane.getTranslateX() > initTranslateX/2){
				partialTransition = new DrawerPartialTransition(sidePane.getTranslateX(), 0);
				partialTransition.play();
				partialTransition.setOnFinished((event)-> {
					sidePane.setTranslateX(0);
				});
			}else{
				// hide the sidePane
				partialTransition = new DrawerPartialTransition(sidePane.getTranslateX(), initTranslateX);
				partialTransition.play();
				partialTransition.setOnFinished((event)-> sidePane.setTranslateX(initTranslateX));
				shadowedPane.setVisible(false);
				shadowedPane.setOpacity(0);
			}	
			partialTransition = null;
		};


		this.sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(newVal.doubleValue() == 0 || newVal.doubleValue() == initTranslateX)
				this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		});

		this.sidePane.addEventHandler(MouseEvent.MOUSE_DRAGGED,dragHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_RELEASED,mouseReleasedHandler);

		this.content.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {if(e.getX() < activeOffset) holdTimer.play();});

		this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
			holdTimer.stop();
			this.content.removeEventHandler(MouseEvent.MOUSE_DRAGGED,dragHandler);
		});		

		holdTimer.setOnFinished((e)->{
			partialTransition = new DrawerPartialTransition(initTranslateX,initTranslateX + initOffset + activeOffset);
			partialTransition.play();
			partialTransition.setOnFinished((event)-> {
				this.content.addEventHandler(MouseEvent.MOUSE_DRAGGED,dragHandler);
				this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
			});				
		});


	}

	public void draw() {
		this.transition.setRate(1);
		this.transition.play();
	}

	private class DrawerTransition extends CachedTimelineTransition{
		public DrawerTransition() {
			super(shadowedPane, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(shadowedPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
							new KeyValue(shadowedPane.opacityProperty(), 1,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(100),
									new KeyValue(sidePane.translateXProperty(), initTranslateX ,Interpolator.EASE_BOTH),
									new KeyValue(shadowedPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(1000),
											new KeyValue(shadowedPane.opacityProperty(), 1,Interpolator.EASE_BOTH),
											new KeyValue(sidePane.translateXProperty(), 0 , Interpolator.EASE_BOTH)									
											)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}

	private class DrawerPartialTransition extends CachedTimelineTransition{
		public DrawerPartialTransition(double start, double end) {
			super(sidePane, new Timeline(
					new KeyFrame(
							Duration.ZERO,
							new KeyValue(sidePane.translateXProperty(), start ,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(600),											
									new KeyValue(sidePane.translateXProperty(), end , Interpolator.EASE_BOTH)									
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}

}

