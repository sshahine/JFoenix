package com.cctintl.c3dfx.controls;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
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
	private StackPane content = new StackPane();
	private Transition transition;
	private Transition partialTransition;
	private Duration holdTime = Duration.seconds(0.2);
	private PauseTransition holdTimer = new PauseTransition(holdTime);

	private double initOffset = 30;
	private DoubleProperty initTranslateX = new SimpleDoubleProperty();
	private double drawerWidth = 0;
	private double activeOffset = 20;
	private double startMouseX = -1;
	private double startTranslateX = -1;
	
	public C3DDrawer(){
		super();

		shadowedPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));
		shadowedPane.setVisible(false);
		shadowedPane.setOpacity(0);
		shadowedPane.getStyleClass().add("c3d-shadow-pane");

		sidePane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		initTranslateX.bind(Bindings.createDoubleBinding(()-> -1 * sidePane.maxWidthProperty().getValue() - initOffset, sidePane.maxWidthProperty()));
		initTranslateX.addListener((o,oldVal,newVal) ->{ 
			transition = new DrawerTransition();
			sidePane.setTranslateX(newVal.doubleValue());
		});
		DepthManager.setDepth(sidePane, 2);

		this.getChildren().add(content);		
		this.getChildren().add(shadowedPane);
		this.getChildren().add(sidePane);
		StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);

		// add listeners
		shadowedPane.setOnMouseClicked((e) -> hide());
		
		
		// mouse drag handler
		EventHandler<MouseEvent> mouseDragHandler = (mouseEvent)->{
			if(mouseEvent.getSceneX() >= activeOffset && partialTransition !=null){
				partialTransition = null;
			}else if(partialTransition == null){
				shadowedPane.setVisible(true);
				shadowedPane.setOpacity(1);
				double translateX ;
				if(startMouseX < 0)					
					translateX = initTranslateX.doubleValue() + initOffset + mouseEvent.getSceneX();
				else
					translateX = startTranslateX + (mouseEvent.getSceneX() - startMouseX);
				
				if(translateX <= 0) sidePane.setTranslateX(translateX);
				else sidePane.setTranslateX(0);
			}
		};

		EventHandler<MouseEvent> mousePressedHandler = (mouseEvent)->{
			startMouseX = mouseEvent.getSceneX();
			startTranslateX = sidePane.getTranslateX();
		};

		
		EventHandler<MouseEvent> mouseReleasedHandler = (mouseEvent)->{
			if(sidePane.getTranslateX() > initTranslateX.doubleValue() /2){
				partialTransition = new DrawerPartialTransition(sidePane.getTranslateX(), 0);
				partialTransition.play();
				partialTransition.setOnFinished((event)-> sidePane.setTranslateX(0));
			}else{
				// hide the sidePane
				partialTransition = new DrawerPartialTransition(sidePane.getTranslateX(), initTranslateX.doubleValue() );
				partialTransition.play();
				partialTransition.setOnFinished((event)-> sidePane.setTranslateX(initTranslateX.doubleValue() ));
				shadowedPane.setVisible(false);
				shadowedPane.setOpacity(0);
			}	
			startMouseX = -1;
			startTranslateX = -1;
			partialTransition = null;
		};


		this.sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(newVal.doubleValue() == 0 || newVal.doubleValue() == initTranslateX.doubleValue())
				this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		});

		this.sidePane.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_RELEASED,mouseReleasedHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_PRESSED,mousePressedHandler);
		
		this.content.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> { if(e.getX() < activeOffset) holdTimer.play(); });
		this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
			holdTimer.stop();
			this.content.removeEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
		});		

		holdTimer.setOnFinished((e)->{
			partialTransition = new DrawerPartialTransition(initTranslateX.doubleValue(), initTranslateX.doubleValue()  + initOffset + activeOffset);
			partialTransition.play();
			partialTransition.setOnFinished((event)-> {
				this.content.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
				this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
			});				
		});


	}

	public void draw() {
		if(this.transition.getStatus().equals(Status.STOPPED)){
			this.transition.setRate(1);
			transition.setOnFinished((e)->sidePane.setTranslateX(0));
			this.transition.play();
		}
	}

	public void hide(){
		if(transition.getStatus().equals(Status.STOPPED)){
			transition.setRate(-1);
			transition.setOnFinished((e)->sidePane.setTranslateX(initTranslateX.doubleValue()));
			transition.playFrom(transition.getTotalDuration());
		}
	}
	
	public  ObservableList<Node> getSidePane() {
		return sidePane.getChildren();
	}

	public void setSidePane(Node... sidePane) {
		this.sidePane.getChildren().addAll(sidePane);
	}

	public ObservableList<Node> getContent() {
		return content.getChildren();
	}

	public void setContent(Node... content) {
		this.content.getChildren().addAll(content);
	}

	public double getDrawerWidth() {
		return drawerWidth;
	}

	public void setDrawerWidth(double drawerWidth) {
		sidePane.setMaxWidth(drawerWidth);
		this.drawerWidth = drawerWidth;
	}

	private boolean drawCalled = false;
	private boolean hideCalled = true;
	
	public void setOnDrawingAction(EventHandler<Event> handler){
		sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(!drawCalled && hideCalled && newVal.doubleValue() > initTranslateX.doubleValue() /2){
				drawCalled = true;
				hideCalled = false;
				handler.handle(null);
			}
		});
	}
	public void setOnHidingAction(EventHandler<Event> handler){
		sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(drawCalled && !hideCalled && newVal.doubleValue() < initTranslateX.doubleValue() /2){
				hideCalled = true;
				drawCalled = false;
				handler.handle(null);
			}
		});
	}

	private class DrawerTransition extends CachedTimelineTransition{
		public DrawerTransition() {
			super(sidePane, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(shadowedPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
							new KeyValue(shadowedPane.opacityProperty(), 1,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(100),
									new KeyValue(sidePane.translateXProperty(), initTranslateX.doubleValue()  ,Interpolator.EASE_BOTH),
									new KeyValue(shadowedPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(1000),
											new KeyValue(shadowedPane.opacityProperty(), 1,Interpolator.EASE_BOTH),
											new KeyValue(sidePane.translateXProperty(), 0 , Interpolator.EASE_BOTH)									
											)
					));
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
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}

}

