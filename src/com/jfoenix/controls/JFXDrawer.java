/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
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
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

import com.jfoenix.controls.events.JFXDrawerEvent;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.CachedTransition;

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
	StackPane sidePane = new StackPane();
	private StackPane content = new StackPane();
	private Transition inTransition;
	private Transition outTransition;
	private Transition partialTransition;
	private Duration holdTime = Duration.seconds(0.2);
	private PauseTransition holdTimer = new PauseTransition(holdTime);

	private double initOffset = 30;
	private DoubleProperty initTranslate = new SimpleDoubleProperty();
	private BooleanProperty overLayVisible = new SimpleBooleanProperty(true);
	private double activeOffset = 20;
	private double startMouse = -1;
	private double startTranslate = -1;	
	private double startSize = -1;	
	private DoubleProperty translateProperty = sidePane.translateXProperty();
	private boolean resizable = false;

	private DoubleProperty defaultSizeProperty = new SimpleDoubleProperty();
	private ObjectProperty<DoubleProperty> maxSizeProperty = new SimpleObjectProperty<>(sidePane.maxWidthProperty());
	private ObjectProperty<DoubleProperty> minSizeProperty = new SimpleObjectProperty<>(sidePane.minWidthProperty());
	private ObjectProperty<DoubleProperty> prefSizeProperty = new SimpleObjectProperty<>(sidePane.prefWidthProperty());
	private ObjectProperty<ReadOnlyDoubleProperty> sizeProperty = new SimpleObjectProperty<>(sidePane.widthProperty());
	private ObjectProperty<ReadOnlyDoubleProperty> parentSizeProperty = new SimpleObjectProperty<>();
	private ObjectProperty<Node> boundedNode = new SimpleObjectProperty<>();

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
		initTranslate.bind(Bindings.createDoubleBinding(()-> -1 * directionProperty.get().doubleValue() * defaultSizeProperty.getValue() - initOffset * directionProperty.get().doubleValue(), defaultSizeProperty, directionProperty ));

		// add listeners to update drawer properties
		overLayVisibleProperty().addListener((o,oldVal,newVal)->{
			overlayPane.setStyle(!newVal?"-fx-background-color : transparent;":"");
			overlayPane.setMouseTransparent(!newVal);
			overlayPane.setPickOnBounds(newVal);	
		});

		directionProperty.addListener((o,oldVal,newVal)-> updateDirection(newVal));

		initTranslate.addListener((o,oldVal,newVal) -> updateDrawerAnimation(newVal.doubleValue()));

		// content listener for mouse hold on a side
		this.content.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> { 
			if(!e.isConsumed()){
				double size = 0 ;
				long valid = 0;
				for (int i =0 ; i < callBacks.size(); i++)
					if(!callBacks.get(i).call(null)) valid++;			
				//				long valid = callBacks.stream().filter(callback->!callback.call(null)).count();
				if(directionProperty.get().equals(DrawerDirection.RIGHT)) size = content.getWidth();
				else if(directionProperty.get().equals(DrawerDirection.BOTTOM)) size = content.getHeight();

				double eventPoint = 0;
				if(directionProperty.get().equals(DrawerDirection.RIGHT) || directionProperty.get().equals(DrawerDirection.LEFT)) eventPoint = e.getX();
				else eventPoint = e.getY();

				if(size + directionProperty.get().doubleValue() * eventPoint < activeOffset && (content.getCursor() == Cursor.DEFAULT || content.getCursor() == null) && valid == 0){
					holdTimer.play();
					e.consume();
				}
			}
		});

		// mouse drag handler
		translateProperty.addListener((o,oldVal,newVal)->{
//			if(newVal.doubleValue() == 0 || newVal.doubleValue() == initTranslate.doubleValue())
			double opValue = 1-newVal.doubleValue()/initTranslate.doubleValue();
			overlayPane.setOpacity(opValue);
			if(opValue <= 0) overlayPane.setVisible(false);
			else overlayPane.setVisible(true);
		});

		this.sidePane.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_RELEASED,mouseReleasedHandler);
		this.sidePane.addEventHandler(MouseEvent.MOUSE_PRESSED,mousePressedHandler);

		this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
			holdTimer.stop();
			this.content.removeEventFilter(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
		});		

		holdTimer.setOnFinished((e)->{
			partialTransition = new DrawerPartialTransition(initTranslate.doubleValue(), initTranslate.doubleValue()  + initOffset * directionProperty.get().doubleValue() + activeOffset * directionProperty.get().doubleValue());
			partialTransition.play();
			partialTransition.setOnFinished((event)-> {
				this.content.addEventFilter(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
				this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
				this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<Event>() {
					@Override
					public void handle(Event event) {
						JFXDrawer.this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
						JFXDrawer.this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
					}
				});
			});				
		});	
	}


	ChangeListener<? super Node> widthListener = (o,oldVal,newVal) -> {if(newVal!=null && newVal instanceof Region) parentSizeProperty.set(((Region)newVal).widthProperty());};
	ChangeListener<? super Node> heightListener = (o,oldVal,newVal) -> {if(newVal!=null && newVal instanceof Region) parentSizeProperty.set(((Region)newVal).heightProperty());};
	
	/**
	 * this method will change the drawer behavior according to its direction
	 * @param dir
	 */	
	private final void updateDirection(DrawerDirection dir){
		maxSizeProperty.get().set(-1);
		prefSizeProperty.get().set(-1);
		
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
			prefSizeProperty.set(sidePane.prefWidthProperty());
			sizeProperty.set(sidePane.widthProperty());
			this.boundedNodeProperty().removeListener(heightListener);
			this.boundedNodeProperty().addListener(widthListener);
			if(getBoundedNode()==null) this.boundedNodeProperty().bind(this.parentProperty());
		}else if(dir.equals(DrawerDirection.RIGHT)){
			StackPane.setAlignment(sidePane, Pos.CENTER_RIGHT);
			translateProperty.set(0);
			translateProperty = sidePane.translateXProperty();
			maxSizeProperty.set(sidePane.maxWidthProperty());
			minSizeProperty.set(sidePane.minWidthProperty());
			prefSizeProperty.set(sidePane.prefWidthProperty());
			sizeProperty.set(sidePane.widthProperty());
			this.boundedNodeProperty().removeListener(heightListener);
			this.boundedNodeProperty().addListener(widthListener);
			if(getBoundedNode()==null) this.boundedNodeProperty().bind(this.parentProperty());
		}else if(dir.equals(DrawerDirection.TOP)){
			StackPane.setAlignment(sidePane, Pos.TOP_CENTER);
			translateProperty.set(0);
			translateProperty = sidePane.translateYProperty();
			maxSizeProperty.set(sidePane.maxHeightProperty());
			minSizeProperty.set(sidePane.minHeightProperty());
			prefSizeProperty.set(sidePane.prefHeightProperty());
			sizeProperty.set(sidePane.heightProperty());
			this.boundedNodeProperty().removeListener(widthListener);
			this.boundedNodeProperty().addListener(heightListener);
			if(getBoundedNode()==null) this.boundedNodeProperty().bind(this.parentProperty());
		}else if(dir.equals(DrawerDirection.BOTTOM)){
			StackPane.setAlignment(sidePane, Pos.BOTTOM_CENTER);
			translateProperty.set(0);
			translateProperty = sidePane.translateYProperty();
			maxSizeProperty.set(sidePane.maxHeightProperty());
			minSizeProperty.set(sidePane.minHeightProperty());
			prefSizeProperty.set(sidePane.prefHeightProperty());
			sizeProperty.set(sidePane.heightProperty());
			this.boundedNodeProperty().removeListener(widthListener);
			this.boundedNodeProperty().addListener(heightListener);
			if(getBoundedNode()==null) this.boundedNodeProperty().bind(this.parentProperty());
		}
		setDefaultDrawerSize(defaultSizeProperty.get());
		updateDrawerAnimation(initTranslate.doubleValue());
	}

	private final void updateDrawerAnimation(double translation){
		inTransition = new InDrawerTransition(translation, 0);
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


//	public boolean isDrawn(){
//		switch (directionProperty.get()) {
//		case LEFT:
//		case TOP:
//			return translateProperty.get() >= 0;
//		case RIGHT:
//		case BOTTOM:
//			return translateProperty.get() <= 0;
//			return true;
//		}
//	}


	/**
	 * this method is only used in drawers stack component
	 * @param callback
	 */
	void bringToFront(Callback<Void, Void> callback){

		EventHandler<? super MouseEvent> eventFilter = (event)->event.consume();
		final boolean bindSize = prefSizeProperty.get().isBound();
		prefSizeProperty.get().unbind();
		maxSizeProperty.get().unbind();
		// disable mouse events
		this.addEventFilter(MouseEvent.ANY, eventFilter);

		EventHandler<ActionEvent> drawerDrawer = (finish)->{
			outTransition.setOnFinished(null);
			callback.call(null);

			if(this.inTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() != 0){
				if(tempDrawerSize > getDefaultDrawerSize()) {
					ParallelTransition parallelTransition = new ParallelTransition(new InDrawerSizeTransition(), new InDrawerTransition(initTranslate.doubleValue(),0));
					parallelTransition.setOnFinished((finish1)->{
						if(bindSize){ 
							prefSizeProperty.get().bind(parentSizeProperty.get());
							maxSizeProperty.get().bind(parentSizeProperty.get());
						}
					});
					parallelTransition.play();
				}else {
					EventHandler<ActionEvent> oldFinishHandler = this.inTransition.getOnFinished();
					this.inTransition.setOnFinished((finish1)->{
						if(bindSize){
							prefSizeProperty.get().bind(parentSizeProperty.get());
							maxSizeProperty.get().bind(parentSizeProperty.get());
						}
						this.inTransition.setOnFinished(oldFinishHandler);});
					this.inTransition.play();	
				}
			}
			// enable mouse events
			this.removeEventFilter(MouseEvent.ANY, eventFilter);
		};

		if(sizeProperty.get().get() > getDefaultDrawerSize()){
			tempDrawerSize = sizeProperty.get().get();
			ParallelTransition parallelTransition = new ParallelTransition(new OutDrawerSizeTransition(), new OutDrawerTransition(initTranslate.doubleValue(),0));
			parallelTransition.setOnFinished(drawerDrawer);
			parallelTransition.play();
		}else{
			if(outTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() == 0) {
				outTransition.setOnFinished(drawerDrawer);
				outTransition.play();	
			}
			tempDrawerSize = getDefaultDrawerSize();
		}
	}

	/**
	 * this method indicates whether the drawer is shown or not 
	 * @return
	 */
	public boolean isShown() {
		if(this.inTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() == 0) return true;
		return false;
	}

	public void draw() {
		if(this.inTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() != 0){
			this.inTransition.setOnFinished((finish)-> onDrawerOpenedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.OPENED)));
			this.inTransition.play();
		}else{
			onDrawerOpenedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.OPENED));
		}
	}

	public void hide(){
		// (sidePane.getTranslateX() == 0), prevents the drawer from playing the hidden animation if it's already closed

		// unbind properties as the drawer size might be bound to stage size
		maxSizeProperty.get().unbind();
		prefSizeProperty.get().unbind();
		
		if(sizeProperty.get().get() > getDefaultDrawerSize()){
			tempDrawerSize = prefSizeProperty.get().get();
			ParallelTransition parallelTransition = new ParallelTransition(new OutDrawerSizeTransition(), new OutDrawerTransition(initTranslate.doubleValue(),0));
			parallelTransition.setOnFinished((finish)->onDrawerClosedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.CLOSED)));
			parallelTransition.play();
		}else{
			if(outTransition.getStatus().equals(Status.STOPPED) && translateProperty.get() == 0){
				outTransition.setOnFinished((finish)->onDrawerClosedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.CLOSED)));
				outTransition.play();	
			}
			tempDrawerSize = getDefaultDrawerSize();
		}
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
		this.sidePane.getChildren().setAll(sidePane);
	}

	public ObservableList<Node> getContent() {
		return content.getChildren();
	}

	public void setContent(Node... content) {
		this.content.getChildren().setAll(content);
	}

	public double getDefaultDrawerSize() {
		return defaultSizeProperty.get();
	}

	public void setDefaultDrawerSize(double drawerWidth) {
		defaultSizeProperty.set(drawerWidth);
		maxSizeProperty.get().set(drawerWidth);
		prefSizeProperty.get().set(drawerWidth);
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

	public boolean isResizableOnDrag(){
		return resizable;
	}

	public void setResizableOnDrag(boolean resizable){
		this.resizable = resizable;
	}

	public final ObjectProperty<Node> boundedNodeProperty() {
		return this.boundedNode;
	}

	public final Node getBoundedNode() {
		return this.boundedNodeProperty().get();
	}

	public final void setBoundedNode(final Node boundedNode) {
		this.boundedNodeProperty().unbind();
		this.boundedNodeProperty().set(boundedNode);
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
		if(!mouseEvent.isConsumed()){
			mouseEvent.consume();
			double size = 0 ;
			Bounds sceneBounds = content.localToScene(content.getLayoutBounds());
			if(directionProperty.get().equals(DrawerDirection.RIGHT)) size = sceneBounds.getMinX() + sceneBounds.getWidth();
			else if(directionProperty.get().equals(DrawerDirection.BOTTOM)) size = sceneBounds.getMinY() + sceneBounds.getHeight();

			if(startSize == -1) startSize = sizeProperty.get().get();
			
			double eventPoint = 0;
			if(directionProperty.get().equals(DrawerDirection.RIGHT) || directionProperty.get().equals(DrawerDirection.LEFT)) eventPoint = mouseEvent.getSceneX();
			else eventPoint = mouseEvent.getSceneY();

			if(size + directionProperty.get().doubleValue() * eventPoint >= activeOffset && partialTransition !=null){
				partialTransition = null;
			}else if(partialTransition == null){
				double currentTranslate ;
				if(startMouse < 0)  currentTranslate = initTranslate.doubleValue() + directionProperty.get().doubleValue() * initOffset + directionProperty.get().doubleValue() * (size + directionProperty.get().doubleValue() * eventPoint);
				else currentTranslate = directionProperty.get().doubleValue() * (startTranslate + directionProperty.get().doubleValue() * ( eventPoint - startMouse ));			

				if(directionProperty.get().doubleValue() * currentTranslate <= 0){
					// the drawer is hidden
					if(resizable){
						maxSizeProperty.get().unbind();
						prefSizeProperty.get().unbind();
						if((startSize - getDefaultDrawerSize()) + directionProperty.get().doubleValue() * currentTranslate > 0){
							// change the side drawer size if dragging from hidden
							maxSizeProperty.get().set(startSize + directionProperty.get().doubleValue() * currentTranslate);
							prefSizeProperty.get().set(startSize + directionProperty.get().doubleValue() * currentTranslate);
						}else{
							// if the side drawer is not fully shown perform translation to show it , and set its default size
							maxSizeProperty.get().set(defaultSizeProperty.get());
							maxSizeProperty.get().set(defaultSizeProperty.get());
							translateProperty.set(directionProperty.get().doubleValue() * ((startSize - getDefaultDrawerSize()) + directionProperty.get().doubleValue() * currentTranslate));
						}	
					}else
						translateProperty.set(currentTranslate);				
				}else{
					// the drawer is already shown
					if(resizable){
						if(startSize + directionProperty.get().doubleValue() * currentTranslate <= parentSizeProperty.get().get()){
							// change the side drawer size after being shown 
							maxSizeProperty.get().unbind();
							prefSizeProperty.get().unbind();
							maxSizeProperty.get().set(startSize + directionProperty.get().doubleValue() * currentTranslate);
							prefSizeProperty.get().set(startSize + directionProperty.get().doubleValue() * currentTranslate);
						}else{
							// bind the drawer size to its parent  
							maxSizeProperty.get().bind(parentSizeProperty.get());
							prefSizeProperty.get().bind(parentSizeProperty.get());
						}	
					}
					translateProperty.set(0);
				}
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
			partialTransition.setOnFinished((event)-> {
				translateProperty.set(initTranslate.doubleValue());	
				onDrawerClosedProperty.get().handle(new JFXDrawerEvent(JFXDrawerEvent.CLOSED));
			});
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

	private double tempDrawerSize = getDefaultDrawerSize();

	private class OutDrawerSizeTransition extends CachedTransition{
		public OutDrawerSizeTransition() {
			super(sidePane, new Timeline(new KeyFrame(Duration.millis(1000),
					new KeyValue(prefSizeProperty.get(), getDefaultDrawerSize(),Interpolator.EASE_BOTH),
					new KeyValue(maxSizeProperty.get(), getDefaultDrawerSize(),Interpolator.EASE_BOTH)))
			);
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}

	private class InDrawerSizeTransition extends CachedTransition{
		public InDrawerSizeTransition() {
			super(sidePane, new Timeline(
					new KeyFrame(Duration.millis(0),
							new KeyValue(maxSizeProperty.get(), getDefaultDrawerSize(),Interpolator.EASE_BOTH),
							new KeyValue(prefSizeProperty.get(), getDefaultDrawerSize(),Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000),
							new KeyValue(maxSizeProperty.get(), tempDrawerSize,Interpolator.EASE_BOTH),
							new KeyValue(prefSizeProperty.get(), tempDrawerSize,Interpolator.EASE_BOTH)
					)));
			setCycleDuration(Duration.seconds(0.5));
			setDelay(Duration.seconds(0));
		}
	}


	private class InDrawerTransition extends CachedTransition{
		public InDrawerTransition(double start, double end) {
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

	private class OutDrawerTransition extends CachedTransition{
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


	private class DrawerPartialTransition extends CachedTransition{
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

