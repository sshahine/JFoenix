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
import javafx.beans.property.SimpleObjectProperty;
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

import com.cctintl.c3dfx.jidefx.CachedTimelineTransition;

public class C3DDrawer extends StackPane {


	public static enum DrawerDirection{
		LEFT(1), RIGHT(-1); 
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
	private DoubleProperty initTranslateX = new SimpleDoubleProperty();
	private double drawerWidth = 0;
	private double activeOffset = 20;
	private double startMouseX = -1;
	private double startTranslateX = -1;

	private SimpleObjectProperty<DrawerDirection> directionProperty = new SimpleObjectProperty<DrawerDirection>(DrawerDirection.LEFT);

	public C3DDrawer(){
		super();
		initialize();
		
		overlayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));
		overlayPane.setVisible(false);
		overlayPane.setOpacity(0);
		overlayPane.getStyleClass().add("c3d-drawer-overlay-pane");

		sidePane.getStyleClass().add("c3d-drawer-side-pane");
		sidePane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		DepthManager.setDepth(sidePane, 2);

		this.getChildren().add(content);		
		this.getChildren().add(overlayPane);
		this.getChildren().add(sidePane);

		// add listeners
		overlayPane.setOnMouseClicked((e) -> hide());
		initListeners();

	}


	private void initListeners(){
		
		if(directionProperty.get().equals(DrawerDirection.LEFT)){
			StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);
		}else if(directionProperty.get().equals(DrawerDirection.RIGHT)){
			StackPane.setAlignment(sidePane, Pos.CENTER_RIGHT);
		}
		
		directionProperty.addListener((o,oldVal,newVal)->{
			if(newVal.equals(DrawerDirection.LEFT)){
				StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);
			}else if(newVal.equals(DrawerDirection.RIGHT)){
				StackPane.setAlignment(sidePane, Pos.CENTER_RIGHT);
			}
		});
		
		initTranslateX.bind(Bindings.createDoubleBinding(()-> -1 * directionProperty.get().doubleValue() * sidePane.maxWidthProperty().getValue() - initOffset * directionProperty.get().doubleValue(), sidePane.maxWidthProperty(), directionProperty));
		initTranslateX.addListener((o,oldVal,newVal) ->{ 
			inTransition = new DrawerTransition(initTranslateX.doubleValue(), 0);
			outTransition = new OutDrawerTransition(initTranslateX.doubleValue(),0);
			sidePane.setTranslateX(newVal.doubleValue());
		});
		
		// content listener for mouse hold on a side
		this.content.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> { 
			double width = 0 ;
			if(directionProperty.get().equals(DrawerDirection.RIGHT)) width = content.getWidth();
			if(width + directionProperty.get().doubleValue() * e.getX() < activeOffset) holdTimer.play(); 
		});

		// mouse drag handler
		this.sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(newVal.doubleValue() == 0 || newVal.doubleValue() == initTranslateX.doubleValue())
				this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
			double opValue = 1-newVal.doubleValue()/initTranslateX.doubleValue();
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
			partialTransition = new DrawerPartialTransition(initTranslateX.doubleValue(), initTranslateX.doubleValue()  + initOffset * directionProperty.get().doubleValue() + activeOffset * directionProperty.get().doubleValue());
			partialTransition.play();
			partialTransition.setOnFinished((event)-> {
				this.content.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragHandler);
				this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
			});				
		});	

	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	public void draw() {
		if(this.inTransition.getStatus().equals(Status.STOPPED)){
			this.inTransition.play();
		}
	}

	public void hide(){
		if(outTransition.getStatus().equals(Status.STOPPED)){
			outTransition.play();
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

	public DrawerDirection getDirection() {
		return directionProperty.get();
	}

	public SimpleObjectProperty<DrawerDirection> directionProperty(){
		return directionProperty;
	}
	
	public void setDirection(DrawerDirection direction) {
		this.directionProperty.set(direction);
	}


	/***************************************************************************
	 *                                                                         *
	 * Action Handlers                                                         *
	 *                                                                         *
	 **************************************************************************/

	private EventHandler<MouseEvent> mouseDragHandler = (mouseEvent)->{
		double width = 0 ;
		if(directionProperty.get().equals(DrawerDirection.RIGHT)) width = content.getWidth();
		
		if(width + directionProperty.get().doubleValue() * mouseEvent.getSceneX() >= activeOffset && partialTransition !=null){
			partialTransition = null;
		}else if(partialTransition == null){
			double translateX ;
			if(startMouseX < 0) translateX = initTranslateX.doubleValue() + directionProperty.get().doubleValue() * initOffset + directionProperty.get().doubleValue() * (width + directionProperty.get().doubleValue() * mouseEvent.getSceneX());
			else translateX = directionProperty.get().doubleValue() * (startTranslateX + directionProperty.get().doubleValue() * ( mouseEvent.getSceneX() - startMouseX ));			
			
			if(directionProperty.get().doubleValue() * translateX <= 0) sidePane.setTranslateX(translateX);
			else sidePane.setTranslateX(0);
		}
	};

	private EventHandler<MouseEvent> mousePressedHandler = (mouseEvent)->{
		startMouseX = mouseEvent.getSceneX();
		startTranslateX = sidePane.getTranslateX();
	};


	private EventHandler<MouseEvent> mouseReleasedHandler = (mouseEvent)->{
		if(directionProperty.get().doubleValue() * sidePane.getTranslateX() > directionProperty.get().doubleValue() * initTranslateX.doubleValue() /2){
			partialTransition = new DrawerPartialTransition(sidePane.getTranslateX(), 0);
			partialTransition.play();
			partialTransition.setOnFinished((event)-> sidePane.setTranslateX(0));
		}else{
			// hide the sidePane
			partialTransition = new DrawerPartialTransition(sidePane.getTranslateX(), initTranslateX.doubleValue() );
			partialTransition.play();
			partialTransition.setOnFinished((event)-> sidePane.setTranslateX(initTranslateX.doubleValue()));
		}	
		startMouseX = -1;
		startTranslateX = -1;
		partialTransition = null;
	};

	private boolean drawCalled = false;
	private boolean hideCalled = true;

	public void setOnDrawingAction(EventHandler<Event> handler){
		sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(!drawCalled && hideCalled && directionProperty.get().doubleValue() * newVal.doubleValue() > directionProperty.get().doubleValue() * initTranslateX.doubleValue() /2){
				drawCalled = true;
				hideCalled = false;
				handler.handle(null);
			}
		});
	}
	public void setOnHidingAction(EventHandler<Event> handler){
		sidePane.translateXProperty().addListener((o,oldVal,newVal)->{
			if(drawCalled && !hideCalled && directionProperty.get().doubleValue() * newVal.doubleValue() < directionProperty.get().doubleValue() * initTranslateX.doubleValue() /2){
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

	private class DrawerTransition extends CachedTimelineTransition{
		public DrawerTransition(double start, double end) {
			super(sidePane, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(overlayPane.visibleProperty(), false ,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(100),
									new KeyValue(sidePane.translateXProperty(), start  ,Interpolator.EASE_BOTH),
									new KeyValue(overlayPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(1000),
											new KeyValue(overlayPane.opacityProperty(), 1,Interpolator.EASE_BOTH),
											new KeyValue(sidePane.translateXProperty(), end , Interpolator.EASE_BOTH)									
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
							new KeyValue(sidePane.translateXProperty(), end , Interpolator.EASE_BOTH)				
							),
							new KeyFrame(Duration.millis(900),
									new KeyValue(sidePane.translateXProperty(), start  ,Interpolator.EASE_BOTH),
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

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-drawer";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}

}

