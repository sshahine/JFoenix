/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.controls;

import java.util.ArrayList;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

import com.jfoenix.controls.events.JFXDrawerEvent;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.jidefx.CachedTimelineTransition;

/**
 * @author sshahine
 *
 */

public class JFXDrawer extends StackPane {


	public static enum DrawerDirection{
		LEFT(1), RIGHT(-1), TOP(1), BOTTOM(-1); 
		private double numVal;
		DrawerDirection(double numVal) {
			this.numVal = numVal;
		}
		public double doubleValue() {
			return numVal;
		}
	};

	private StackPane overlayPane = new StackPane();
	private StackPane sidePane = new StackPane();
	private StackPane content = new StackPane();
	private Transition inTransition;
	private Transition outTransition;
	private Transition partialTransition;
	private Duration holdTime = Duration.seconds(0.2);
	private PauseTransition holdTimer = new PauseTransition(holdTime);
	
	private double initOffset = 30;
	private DoubleProperty initTranslate = new SimpleDoubleProperty();
	private BooleanProperty overLayVisible = new SimpleBooleanProperty(true);
	private double drawerSize = 0;
	private double activeOffset = 20;
	private double startMouse = -1;
	private double startTranslate = -1;	
	private double startSize = -1;	
	private DoubleProperty translateProperty = sidePane.translateXProperty();
	private boolean resizable = false;
	
	private ObjectProperty<DoubleProperty> maxSizeProperty = new SimpleObjectProperty<>(sidePane.maxWidthProperty());
	private ObjectProperty<DoubleProperty> minSizeProperty = new SimpleObjectProperty<>(sidePane.minWidthProperty());
	private ObjectProperty<ReadOnlyDoubleProperty> sizeProperty = new SimpleObjectProperty<>(sidePane.widthProperty());
	private ObjectProperty<ReadOnlyDoubleProperty> sceneSizeProperty = new SimpleObjectProperty<>();
	
	private SimpleObjectProperty<DrawerDirection> directionProperty = new SimpleObjectProperty<DrawerDirection>(DrawerDirection.LEFT);

	public JFXDrawer(){
		super();
		initialize();
		
		overlayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));
		overlayPane.getStyleClass().add("jfx-drawer-overlay-pane");
		overlayPane.setVisible(false);
		overlayPane.setOpacity(0);

		sidePane.getStyleClass().add("jfx-drawer-side-pane");
		sidePane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		JFXDepthManager.setDepth(sidePane, 2);
		sidePane.setPickOnBounds(false);

		this.getChildren().add(content);		
		this.getChildren().add(overlayPane);
		this.getChildren().add(sidePane);

		// add listeners
		overlayPane.setOnMouseClicked((e) -> hide());
		initListeners();

	}


	private void initListeners(){
		
		updateDirection(directionProperty.get());		
		initTranslate.bind(Bindings.createDoubleBinding(()-> -1 * directionProperty.get().doubleValue() * maxSizeProperty.get().getValue() - initOffset * directionProperty.get().doubleValue(), maxSizeProperty.get(), directionProperty ));
		
	
		// add listeners to update drawer properties
		
		overLayVisibleProperty().addListener((o,oldVal,newVal)->{
			overlayPane.setStyle(!newVal?"-fx-background-color : transparent;":"");
			overlayPane.setMouseTransparent(!newVal);
			overlayPane.setPickOnBounds(newVal);	
		});
		
		directionProperty.addListener((o,oldVal,newVal)-> updateDirection(newVal));
						
		maxSizeProperty.addListener((o,oldVal,newVal)->{
			initTranslate.unbind();
			newVal.set(oldVal.get());
			oldVal.set(-1);
			// reset initial translation
			initTranslate.bind(Bindings.createDoubleBinding(()->{
				return -1 * directionProperty.get().doubleValue() * newVal.getValue() - initOffset * directionProperty.get().doubleValue(); 
			}, newVal, directionProperty ));
			updateDrawerAnimation(initTranslate.get());
		});
		
		initTranslate.addListener((o,oldVal,newVal) -> updateDrawerAnimation(newVal.doubleValue()));
		
		// content listener for mouse hold on a side
		this.content.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> { 
			double size = 0 ;
			long valid = 0;
			for (int i =0 ; i < callBacks.size(); i++)
				if(!callBacks.get(i).call(null)) valid++;			
//			long valid = callBacks.stream().filter(callback->!callback.call(null)).count();
			if(directionProperty.get().equals(DrawerDirection.RIGHT)) size = content.getWidth();
			else if(directionProperty.get().equals(DrawerDirection.BOTTOM)) size = content.getHeight();
			
			double eventPoint = 0;
			if(directionProperty.get().equals(DrawerDirection.RIGHT) || directionProperty.get().equals(DrawerDirection.LEFT)) eventPoint = e.getX();
			else eventPoint = e.getY();
			
			if(size + directionProperty.get().doubleValue() * eventPoint < activeOffset && (content.getCursor() == Cursor.DEFAULT || content.getCursor() == null) && valid == 0)
				holdTimer.play(); 
		});

		// mouse drag handler
		translateProperty.addListener((o,oldVal,newVal)->{
			if(newVal.doubleValue() == 0 || newVal.doubleValue() == initTranslate.doubleValue())
				this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
			double opValue = 1-newVal.doubleValue()/initTranslate.doubleValue();
			overlayPane.setOpacity(opValue);
			if(opValue == 0) overlayPane.setVisible(false);
			else overlayPane.setVisible(true);
		});

		this.sidePane.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_RELEASED,mouseReleasedHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_PRESSED,mousePressedHandler);

		this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
			holdTimer.stop();
			this.content.removeEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
		});		

		holdTimer.setOnFinished((e)->{
			partialTransition = new DrawerPartialTransition(initTranslate.doubleValue(), initTranslate.doubleValue()  + initOffset * directionProperty.get().doubleValue() + activeOffset * directionProperty.get().doubleValue());
			partialTransition.play();
			partialTransition.setOnFinished((event)-> {
				this.content.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
				this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
			});				
		});	
	}

	
	/**
	 * this method will change the drawer behavior according to its direction
	 * @param dir
	 */	
	private final void updateDirection(DrawerDirection dir){
		if(dir.equals(DrawerDirection.LEFT)){
			// change the pane position
			StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);
			// reset old translation
			translateProperty.set(0);
			// set the new translation property
			translateProperty = sidePane.translateXProperty();
			// change the size property
			maxSizeProperty.set(sidePane.maxWidthProperty());
			minSizeProperty.set(sidePane.minWidthProperty());
			sizeProperty.set(sidePane.widthProperty());
			this.sceneProperty().addListener((o,oldVal,newVal) -> sceneSizeProperty.set(newVal.widthProperty()));
		}else if(dir.equals(DrawerDirection.RIGHT)){
			StackPane.setAlignment(sidePane, Pos.CENTER_RIGHT);
			translateProperty.set(0);
			translateProperty = sidePane.translateXProperty();
			maxSizeProperty.set(sidePane.maxWidthProperty());
			minSizeProperty.set(sidePane.minWidthProperty());
			sizeProperty.set(sidePane.widthProperty());
			this.sceneProperty().addListener((o,oldVal,newVal) -> sceneSizeProperty.set(newVal.widthProperty()));
		}else if(dir.equals(DrawerDirection.TOP)){
			StackPane.setAlignment(sidePane, Pos.TOP_CENTER);
			translateProperty.set(0);
			translateProperty = sidePane.translateYProperty();
			maxSizeProperty.set(sidePane.maxHeightProperty());
			minSizeProperty.set(sidePane.minHeightProperty());
			sizeProperty.set(sidePane.heightProperty());
			this.sceneProperty().addListener((o,oldVal,newVal) -> sceneSizeProperty.set(newVal.heightProperty()));
		}else if(dir.equals(DrawerDirection.BOTTOM)){
			StackPane.setAlignment(sidePane, Pos.BOTTOM_CENTER);
			translateProperty.set(0);
			translateProperty = sidePane.translateYProperty();
			maxSizeProperty.set(sidePane.maxHeightProperty());
			minSizeProperty.set(sidePane.minHeightProperty());
			sizeProperty.set(sidePane.heightProperty());
			this.sceneProperty().addListener((o,oldVal,newVal) -> sceneSizeProperty.set(newVal.heightProperty()));
		}
		updateDrawerAnimation(initTranslate.doubleValue());
	}
	
	private final void updateDrawerAnimation(double translation){
		inTransition = new DrawerTransition(translation, 0);
		outTransition = new OutDrawerTransition(translation,0);
		translateProperty.set(translation);
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	private ArrayList<Callback<Void, Boolean>> callBacks = new ArrayList<>();
	
	/*
	 *  the callbacks are used to add conditions to allow 
	 *  starting the drawer when holding on the side part of the content
	 */
	public void addInitDrawerCallback(Callback<Void, Boolean> callBack){
		callBacks.add(callBack);
	}
	

	public boolean isDrawn(){
		switch (directionProperty.get()) {
		case LEFT:
		case TOP:
			return translateProperty.get() >= 0;
		case RIGHT:
		case BOTTOM:
			return translateProperty.get() <= 0;
		default:
			return true;
		}
	}
	
	public void draw() {
		if(this.inTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() != 0)
			this.inTransition.play();
		onDrawerOpenedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.OPENED));
	}

	public void hide(){
		// (sidePane.getTranslateX() == 0), prevents the drawer from playing the hidden animation if it's already closed 
		if(sizeProperty.get().get() > drawerSize){
			new ParallelTransition(new DrawerSizeTransition(), new OutDrawerTransition(initTranslate.doubleValue(),0)).play();
		}else{
			if(outTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() == 0)
				outTransition.play();			
		}
		onDrawerClosedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.CLOSED));
	}

	/***************************************************************************
	 *                                                                         *
	 * Setters / Getters                                                       *
	 *                                                                         *
	 **************************************************************************/

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

	public double getDrawerSize() {
		return drawerSize;
	}

	public void setDefaultDrawerSize(double drawerWidth) {
		maxSizeProperty.get().set(drawerWidth);
		this.drawerSize = drawerWidth;
	}

	public DrawerDirection getDirection() {
		return directionProperty.get();
	}

	public SimpleObjectProperty<DrawerDirection> directionProperty(){
		return directionProperty;
	}
	
	public void setDirection(DrawerDirection direction) {
		this.directionProperty.set(direction);
	}

	public final BooleanProperty overLayVisibleProperty() {
		return this.overLayVisible;
	}

	public final boolean isOverLayVisible() {
		return this.overLayVisibleProperty().get();
	}

	public final void setOverLayVisible(final boolean overLayVisible) {
		this.overLayVisibleProperty().set(overLayVisible);
	}
	
	public boolean isResizable(){
		return resizable;
	}
	
	public void setResizable(boolean resizable){
		this.resizable = resizable;
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Custom Events                                                           *
	 *                                                                         *
	 **************************************************************************/
	private ObjectProperty<EventHandler<? super JFXDrawerEvent>> onDrawerClosedProperty = new SimpleObjectProperty<>((closed)->{});

	public void setOnDrawerClosed(EventHandler<? super JFXDrawerEvent> handler){
		onDrawerClosedProperty.set(handler);
	}

	public void getOnDrawerClosed(EventHandler<? super JFXDrawerEvent> handler){
		onDrawerClosedProperty.get();
	}
	

	private ObjectProperty<EventHandler<? super JFXDrawerEvent>> onDrawerOpenedProperty = new SimpleObjectProperty<>((opened)->{});
	
	public void setOnDrawerOpened(EventHandler<? super JFXDrawerEvent> handler){
		onDrawerOpenedProperty.set(handler);
	}

	public void getOnDrawerOpened(EventHandler<? super JFXDrawerEvent> handler){
		onDrawerOpenedProperty.get();
	}
	

	/***************************************************************************
	 *                                                                         *
	 * Action Handlers                                                         *
	 *                                                                         *
	 **************************************************************************/

	private EventHandler<MouseEvent> mouseDragHandler = (mouseEvent)->{
		double size = 0 ;
		if(directionProperty.get().equals(DrawerDirection.RIGHT)) size = content.getWidth();
		else if(directionProperty.get().equals(DrawerDirection.BOTTOM)) size = content.getHeight();
		
		double eventPoint = 0;
		if(directionProperty.get().equals(DrawerDirection.RIGHT) || directionProperty.get().equals(DrawerDirection.LEFT)) eventPoint = mouseEvent.getSceneX();
		else eventPoint = mouseEvent.getSceneY();
		
		if(size + directionProperty.get().doubleValue() * eventPoint >= activeOffset && partialTransition !=null){
			partialTransition = null;
		}else if(partialTransition == null){
			double currentTranslate ;
			if(startMouse < 0) currentTranslate = initTranslate.doubleValue() + directionProperty.get().doubleValue() * initOffset + directionProperty.get().doubleValue() * (size + directionProperty.get().doubleValue() * eventPoint);
			else currentTranslate = directionProperty.get().doubleValue() * (startTranslate + directionProperty.get().doubleValue() * ( eventPoint - startMouse ));			
			
			if(directionProperty.get().doubleValue() * currentTranslate <= 0){
				if(resizable){
					if(minSizeProperty.get().get() >= drawerSize || (startSize - drawerSize) + directionProperty.get().doubleValue() * currentTranslate > 0){
						minSizeProperty.get().unbind();
						minSizeProperty.get().set(startSize + directionProperty.get().doubleValue() * currentTranslate);
					}else{ 
						if(startSize > drawerSize && (startSize - drawerSize) + currentTranslate >= initTranslate.doubleValue()){
							translateProperty.set(directionProperty.get().doubleValue() * ((startSize - drawerSize) + directionProperty.get().doubleValue() * currentTranslate));
						}else{
							translateProperty.set(currentTranslate);
						}
					}	
				}else
					translateProperty.set(currentTranslate);				
			}else{
				if(resizable){
					if(startSize + directionProperty.get().doubleValue() * currentTranslate <= sceneSizeProperty.get().get()){
						minSizeProperty.get().unbind();
						minSizeProperty.get().set(startSize + directionProperty.get().doubleValue() * currentTranslate);
					}else{
						minSizeProperty.get().bind(sceneSizeProperty.get());
					}	
				}
				translateProperty.set(0);
			}
		}
	};

	private EventHandler<MouseEvent> mousePressedHandler = (mouseEvent)->{		
		if(directionProperty.get().equals(DrawerDirection.RIGHT) || directionProperty.get().equals(DrawerDirection.LEFT)) startMouse = mouseEvent.getSceneX();
		else startMouse = mouseEvent.getSceneY();		
		startTranslate = translateProperty.get();
		startSize = sizeProperty.get().get();
	};

	

	private EventHandler<MouseEvent> mouseReleasedHandler = (mouseEvent)->{
		if(directionProperty.get().doubleValue() * translateProperty.get() > directionProperty.get().doubleValue() * initTranslate.doubleValue() /2){
			partialTransition = new DrawerPartialTransition(translateProperty.get(), 0);
			partialTransition.play();
			partialTransition.setOnFinished((event)-> translateProperty.set(0));
			onDrawerOpenedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.OPENED));
		}else{
			// hide the sidePane
			partialTransition = new DrawerPartialTransition(translateProperty.get(), initTranslate.doubleValue() );
			partialTransition.play();
			partialTransition.setOnFinished((event)-> translateProperty.set(initTranslate.doubleValue()));
			onDrawerClosedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.CLOSED));
		}	
		// reset drawer animation properties
		startMouse = -1;
		startTranslate = -1;
		startSize = sizeProperty.get().get();
		partialTransition = null;
	};

	private boolean drawCalled = false;
	private boolean hideCalled = true;

	public void setOnDrawingAction(EventHandler<Event> handler){
		translateProperty.addListener((o,oldVal,newVal)->{
			if(!drawCalled && hideCalled && directionProperty.get().doubleValue() * newVal.doubleValue() > directionProperty.get().doubleValue() * initTranslate.doubleValue() /2){
				drawCalled = true;
				hideCalled = false;
				handler.handle(null);
			}
		});
	}
	public void setOnHidingAction(EventHandler<Event> handler){
		translateProperty.addListener((o,oldVal,newVal)->{
			if(drawCalled && !hideCalled && directionProperty.get().doubleValue() * newVal.doubleValue() < directionProperty.get().doubleValue() * initTranslate.doubleValue() /2){
				hideCalled = true;
				drawCalled = false;
				handler.handle(null);
			}
		});
	}


	/***************************************************************************
	 *                                                                         *
	 * Animations                                                                *
	 *                                                                         *
	 **************************************************************************/

	
	private class DrawerSizeTransition extends CachedTimelineTransition{
		public DrawerSizeTransition() {
			super(sidePane, new Timeline(new KeyFrame(Duration.millis(1000),new KeyValue(minSizeProperty.get(), drawerSize,Interpolator.EASE_BOTH))));
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}
	
	
	private class DrawerTransition extends CachedTimelineTransition{
		public DrawerTransition(double start, double end) {
			super(sidePane, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(overlayPane.visibleProperty(), false ,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(100),
									new KeyValue(translateProperty, start  ,Interpolator.EASE_BOTH),
									new KeyValue(overlayPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(1000),
											new KeyValue(overlayPane.opacityProperty(), 1,Interpolator.EASE_BOTH),
											new KeyValue(translateProperty, end , Interpolator.EASE_BOTH)									
											)
					));
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}
	
	private class OutDrawerTransition extends CachedTimelineTransition{
		public OutDrawerTransition(double start, double end) {
			super(sidePane, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(translateProperty, end , Interpolator.EASE_BOTH)				
							),
							new KeyFrame(Duration.millis(900),
									new KeyValue(translateProperty, start  ,Interpolator.EASE_BOTH),
									new KeyValue(overlayPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(1000),
											new KeyValue(overlayPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
											new KeyValue(overlayPane.opacityProperty(), 0,Interpolator.EASE_BOTH)																
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
							new KeyValue(translateProperty, start ,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(600),											
									new KeyValue(translateProperty, end , Interpolator.EASE_BOTH)									
									)
					)
					);
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "jfx-drawer";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}

	
	
}

