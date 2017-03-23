/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import com.jfoenix.converters.RipplerMaskTypeConverter;
import com.jfoenix.transitions.CachedAnimation;
import com.jfoenix.transitions.CachedTransition;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JFXRippler is the material design implementation of a ripple effect.
 * the ripple effect can be applied to any node in the scene. JFXRippler is
 * a {@link StackPane} container that holds a specified node (control node) and a ripple generator.
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
@DefaultProperty(value="control")
public class JFXRippler extends StackPane {

	public static enum RipplerPos{FRONT, BACK};
	public static enum RipplerMask{CIRCLE, RECT};

	protected RippleGenerator rippler;
	protected Pane ripplerPane;
	protected Node control;
	private boolean enabled = true;
	private double RIPPLE_MAX_RADIUS = 300;
	private Interpolator rippleInterpolator = Interpolator.SPLINE(0.0825, 0.3025, 0.0875, 0.9975); //0.1, 0.54, 0.28, 0.95);

	/**
	 * creates empty rippler node
	 */
	public JFXRippler(){
		this(null,RipplerMask.RECT,RipplerPos.FRONT);
	}

	/**
	 * creates a rippler for the specified control
	 * 
	 * @param control
	 */
	public JFXRippler(Node control){
		this(control, RipplerMask.RECT, RipplerPos.FRONT);
	}

	/**
	 * creates a rippler for the specified control
	 *  
	 * @param control
	 * @param pos can be either FRONT/BACK (position the ripple effect infront of or behind the control)
	 */
	public JFXRippler(Node control, RipplerPos pos){
		this(control, RipplerMask.RECT , pos);
	}

	/**
	 * creates a rippler for the specified control and apply the specified mask to it
	 *  
	 * @param control
	 * @param mask can be either rectangle/cricle
	 */
	public JFXRippler(Node control, RipplerMask mask){
		this(control, mask , RipplerPos.FRONT);
	}

	/**
	 * creates a rippler for the specified control, mask and position. 
	 * 
	 * @param control
	 * @param mask can be either rectangle/cricle
	 * @param pos can be either FRONT/BACK (position the ripple effect infront of or behind the control)
	 */
	public JFXRippler(Node control, RipplerMask mask,  RipplerPos pos){
		super();		
		initialize();
		this.maskType.set(mask);
		this.position.set(pos);
		setControl(control);
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		setCacheShape(true);
		setSnapToPixel(false);
	}	

	/***************************************************************************
	 *                                                                         *
	 * Setters / Getters                                                       *
	 *                                                                         *
	 **************************************************************************/

	public void setControl(Node control){
		if(control!=null){
			this.control = control;

			// create rippler panels
			rippler = new RippleGenerator();
			ripplerPane = new StackPane();
			ripplerPane.getChildren().add(rippler);

			// set the control postion and listen if it's changed
			if(this.position.get() == RipplerPos.BACK) ripplerPane.getChildren().add(this.control);
			else this.getChildren().add(this.control);

			this.position.addListener((o,oldVal,newVal)->{
				if(this.position.get() == RipplerPos.BACK) ripplerPane.getChildren().add(this.control);
				else this.getChildren().add(this.control);	
			});			

			this.getChildren().add(ripplerPane);

			// add listeners
			initListeners();
			// if the control got resized the overlay rect must be rest
			control.layoutBoundsProperty().addListener((o,oldVal,newVal)-> {resetOverLay(); resetClip();});
			control.boundsInParentProperty().addListener((o,oldVal,newVal)->{resetOverLay(); resetClip();});
		}
	}

	public Node getControl(){
		return this.control;
	}

	public void setEnabled(boolean enable){
		this.enabled = enable;
	}

	// methods that can be changed by extending the rippler class
	/**
	 * generate the clipping mask
	 * @return the mask node
	 */
	protected Node getMask(){
		double borderWidth = ripplerPane.getBorder() != null ? ripplerPane.getBorder().getInsets().getTop() : 0;
		Bounds bounds = control.getBoundsInParent();
		double width = control.getLayoutBounds().getWidth();
		double height = control.getLayoutBounds().getHeight();
		double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
		double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
		double diffMaxX = Math.abs(control.getBoundsInLocal().getMaxX() - control.getLayoutBounds().getMaxX());
		double diffMaxY = Math.abs(control.getBoundsInLocal().getMaxY() - control.getLayoutBounds().getMaxY());
		Node mask;		
		switch(getMaskType()){
		case RECT:
			mask = new Rectangle(bounds.getMinX()+diffMinX, bounds.getMinY() + diffMinY, width - 0.1 - 2 * borderWidth, height - 0.1 - 2 * borderWidth); // -0.1 to prevent resizing the anchor pane
			break;
		case CIRCLE:
			double radius = Math.min((width / 2) - 0.1 - 2 * borderWidth, (height / 2) - 0.1 - 2 * borderWidth);
			mask = new Circle((bounds.getMinX() + diffMinX + bounds.getMaxX() - diffMaxX) / 2 , (bounds.getMinY() + diffMinY + bounds.getMaxY() - diffMaxY) / 2, radius, Color.BLUE);
			break;
		default:
			mask = new Rectangle(bounds.getMinX()+diffMinX, bounds.getMinY() + diffMinY, width - 0.1 - 2 * borderWidth, height - 0.1 - 2 * borderWidth); // -0.1 to prevent resizing the anchor pane
			break;			
		}		
		if(control instanceof Shape || (control instanceof Region && ((Region)control).getShape()!=null)){
			mask = new StackPane();
			((Region)mask).setShape((control instanceof Shape)? (Shape) control: ((Region)control).getShape());			
			((Region)mask).setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			mask.resize(width, height);
			mask.relocate(bounds.getMinX()+diffMinX, bounds.getMinY() + diffMinY);
		}		
		return mask;
	}

	/**
	 * compute the ripple raddius
	 * @return the ripple radius size
	 */
	protected double computeRippleRadius() {
		double width2 = control.getLayoutBounds().getWidth() * control.getLayoutBounds().getWidth();
		double height2 = control.getLayoutBounds().getHeight() * control.getLayoutBounds().getHeight();
		double radius = Math.min(Math.sqrt(width2 + height2), RIPPLE_MAX_RADIUS) * 1.1 + 5;
		return radius;
	}

	/**
	 * init mouse listeners on the rippler node
	 */
	protected void initListeners(){
		ripplerPane.setOnMousePressed((event) -> {
			createRipple(event.getX(),event.getY());
			if(this.position.get() == RipplerPos.FRONT)
				this.control.fireEvent(event);
		});
		ripplerPane.setOnMouseReleased((event) -> {
			if(this.position.get() == RipplerPos.FRONT)
				this.control.fireEvent(event);
		});
		ripplerPane.setOnMouseClicked((event) -> {
			if(this.position.get() == RipplerPos.FRONT )
				this.control.fireEvent(event);
		});
	}

	/**
	 * creates Ripple effect
	 */
	protected void createRipple(double x, double y){
		rippler.setGeneratorCenterX(x);
		rippler.setGeneratorCenterY(y);
		rippler.createMouseRipple();
	}

	/**
	 * fire event to the rippler pane manually
	 * @param event
	 */
	public void fireEventProgrammatically(Event event){
		if(!event.isConsumed())
			ripplerPane.fireEvent(event);
	}
	
	public void showOverlay(){
		if(rippler.overlayRect!=null) rippler.overlayRect.outAnimation.stop();
		rippler.createOverlay();
		rippler.overlayRect.inAnimation.play();
	}
	public void hideOverlay(){
		if(rippler.overlayRect!=null) rippler.overlayRect.inAnimation.stop();		
		if(rippler.overlayRect!=null) rippler.overlayRect.outAnimation.play();
	}

	/**
	 * Generates ripples on the screen every 0.3 seconds or whenever
	 * the createRipple method is called. Ripples grow and fade out
	 * over 0.6 seconds
	 */
	class RippleGenerator extends Group {

		private double generatorCenterX = 0;
		private double generatorCenterY = 0;
		private OverLayRipple overlayRect;
		private AtomicBoolean generating = new AtomicBoolean(false);
		private boolean cacheRipplerClip = false;
		private boolean resetClip = false;
		
		public RippleGenerator() {
			/* 
			 * improve in performance, by preventing  
			 * redrawing the parent when the ripple effect is triggered
			 */
			this.setManaged(false);
		}
		public void createMouseRipple() {
			if(enabled){
				if(!generating.getAndSet(true)){					
					// create overlay once then change its color later
					createOverlay();
					if(this.getClip() == null || (getChildren().size() == 1 && !cacheRipplerClip) || resetClip) this.setClip(getMask());
					this.resetClip = false;
					
					// create the ripple effect
					final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
					getChildren().add(ripple);	

					// animate the ripple
					overlayRect.outAnimation.stop();
					overlayRect.inAnimation.play();
					ripple.inAnimation.getAnimation().play();

					// create fade out transition for the ripple
					ripplerPane.setOnMouseReleased((e)->{
						if(generating.getAndSet(false)){
							if(overlayRect!=null) overlayRect.inAnimation.stop();
							ripple.inAnimation.getAnimation().stop();
							ripple.outAnimation = new CachedAnimation(new Timeline(new KeyFrame(Duration.millis(Math.min(800, (0.9 * 500) / ripple.getScaleX())),ripple.outKeyValues)), this);
							ripple.outAnimation.getAnimation().setOnFinished((event)-> getChildren().remove(ripple));
							ripple.outAnimation.getAnimation().play();
							if(overlayRect!=null) overlayRect.outAnimation.play();
						}
					});
				}
			}
		}
		
		public Runnable createManualRipple(){
			if(enabled){
				if(!generating.getAndSet(true)){					
					// create overlay once then change its color later
					createOverlay();
					if(this.getClip() == null || (getChildren().size() == 1 && !cacheRipplerClip) || resetClip) this.setClip(getMask());
					this.resetClip = false;
					
					// create the ripple effect
					final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
					getChildren().add(ripple);	

					// animate the ripple
					overlayRect.outAnimation.stop();
					overlayRect.inAnimation.play();
					ripple.inAnimation.getAnimation().play();
					
					return ()->{
						// create fade out transition for the ripple
						if(generating.getAndSet(false)){
							if(overlayRect!=null) overlayRect.inAnimation.stop();
							ripple.inAnimation.getAnimation().stop();
							ripple.outAnimation = new CachedAnimation(new Timeline(new KeyFrame(Duration.millis(Math.min(800, (0.9 * 500) / ripple.getScaleX())),ripple.outKeyValues)), this);
							ripple.outAnimation.getAnimation().setOnFinished((event)-> getChildren().remove(ripple));
							ripple.outAnimation.getAnimation().play();
							if(overlayRect!=null) overlayRect.outAnimation.play();
						}
					};
				}
			}
			return ()->{};
		}

		void cacheRippleClip(boolean cached){
			cacheRipplerClip = cached;
		}

		public void createOverlay(){
			if(overlayRect == null){
				overlayRect = new OverLayRipple();
				overlayRect.setClip(getMask());
				getChildren().add(0, overlayRect);
				overlayRect.setFill(new Color(((Color)ripplerFill.get()).getRed(), ((Color)ripplerFill.get()).getGreen(), ((Color)ripplerFill.get()).getBlue(),0.2));
			}					
		}

		public void setGeneratorCenterX(double generatorCenterX) {
			this.generatorCenterX = generatorCenterX;
		}

		public void setGeneratorCenterY(double generatorCenterY) {
			this.generatorCenterY = generatorCenterY;
		}

		private class OverLayRipple extends Rectangle{
			// Overlay ripple animations 
			CachedTransition inAnimation = new CachedTransition(this, new Timeline(new KeyFrame(Duration.millis(1300),new KeyValue(opacityProperty(), 1, Interpolator.EASE_IN)))){{setDelay(Duration.millis(0));setCycleDuration(Duration.millis(300));}};
			CachedTransition outAnimation = new CachedTransition(this, new Timeline(new KeyFrame(Duration.millis(1300),new KeyValue(opacityProperty(), 0, Interpolator.EASE_OUT)))){{setDelay(Duration.millis(0));setCycleDuration(Duration.millis(300));}};

			public OverLayRipple() {
				super(control.getLayoutBounds().getWidth() - 0.1,control.getLayoutBounds().getHeight() - 0.1);
				this.getStyleClass().add("jfx-rippler-overlay");
				//				this.widthProperty().bind(Bindings.createDoubleBinding(()-> control.getLayoutBounds().getWidth() - 0.1, control.boundsInParentProperty()));
				//				this.heightProperty().bind(Bindings.createDoubleBinding(()-> control.getLayoutBounds().getHeight() - 0.1, control.boundsInParentProperty()));
				// update initial position
				double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
				double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
				Bounds bounds = control.getBoundsInParent();
				this.setX(bounds.getMinX() + diffMinX);
				this.setY(bounds.getMinY() + diffMinY);
				// set initial attributes
				this.setOpacity(0);
				setCache(true);
				setCacheHint(CacheHint.SPEED);
				setCacheShape(true);
				setSnapToPixel(false);
				outAnimation.setOnFinished((finish)->resetOverLay());
			}
		}

		private class Ripple extends Circle {

			KeyValue[] outKeyValues;
			CachedAnimation outAnimation = null;	
			CachedAnimation inAnimation = null;

			private Ripple(double centerX, double centerY) {
				super(centerX, centerY, ripplerRadius.get().doubleValue() == Region.USE_COMPUTED_SIZE ? computeRippleRadius() : ripplerRadius.get().doubleValue() , null);

				KeyValue[] inKeyValues = new KeyValue[isRipplerRecenter()? 4 : 2];
				outKeyValues = new KeyValue[isRipplerRecenter()? 5 : 3];

				inKeyValues[0] = new KeyValue(scaleXProperty(),  0.9,rippleInterpolator);
				inKeyValues[1] = new KeyValue(scaleYProperty(),  0.9,rippleInterpolator);

				outKeyValues[0] = new KeyValue(this.scaleXProperty(), 1 ,rippleInterpolator);
				outKeyValues[1] = new KeyValue(this.scaleYProperty(), 1 ,rippleInterpolator);
				outKeyValues[2] = new KeyValue(this.opacityProperty(), 0, rippleInterpolator);

				if(isRipplerRecenter()){
					double dx = (control.getLayoutBounds().getWidth()/2 - centerX) / 1.55;
					double dy = (control.getLayoutBounds().getHeight()/2 - centerY) / 1.55;		
					inKeyValues[2] = outKeyValues[3] = new KeyValue(translateXProperty(), Math.signum(dx) * Math.min(Math.abs(dx), this.getRadius()/2), rippleInterpolator);
					inKeyValues[3] = outKeyValues[4] = new KeyValue(translateYProperty(), Math.signum(dy) * Math.min(Math.abs(dy), this.getRadius()/2), rippleInterpolator);
				}				
				inAnimation = new CachedAnimation(new Timeline(new KeyFrame(Duration.ZERO,
						new KeyValue(scaleXProperty(),  0, rippleInterpolator),
						new KeyValue(scaleYProperty(),  0, rippleInterpolator),
						new KeyValue(translateXProperty(),  0, rippleInterpolator),
						new KeyValue(translateYProperty(),  0, rippleInterpolator),
						new KeyValue(opacityProperty(), 1, rippleInterpolator)
						),new KeyFrame(Duration.millis(900), inKeyValues)), this);

				setCache(true);
				setCacheHint(CacheHint.SPEED);
				setCacheShape(true);
				setSnapToPixel(false);
				setScaleX(0);
				setScaleY(0);
				if(ripplerFill.get() instanceof Color){
					Color circleColor = new Color(((Color)ripplerFill.get()).getRed(), ((Color)ripplerFill.get()).getGreen(), ((Color)ripplerFill.get()).getBlue(),0.3);
					setStroke(circleColor);
					setFill(circleColor);
				}else{
					setStroke(ripplerFill.get());
					setFill(ripplerFill.get());
				}
			}
		}

		public void clear() {
			getChildren().clear();
			generating.set(false);
		}
	}

	private void resetOverLay(){
		if(rippler.overlayRect!=null){
			rippler.overlayRect.inAnimation.stop();
			final RippleGenerator.OverLayRipple oldOverlay = rippler.overlayRect;
			rippler.overlayRect.outAnimation.setOnFinished((finish)-> rippler.getChildren().remove(oldOverlay));
			rippler.overlayRect.outAnimation.play();
			rippler.overlayRect = null;			
		}
	}

	private void resetClip(){
		this.rippler.resetClip = true;
	}
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Initialize the style class to 'jfx-rippler'.
	 *
	 * This is the selector class from which CSS can be used to style
	 * this control.
	 */
	private static final String DEFAULT_STYLE_CLASS = "jfx-rippler";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}

	/**
	 * the ripple recenter property, by default it's false.
	 * if true the ripple effect will show gravitational pull to the center of its control
	 */
	private StyleableObjectProperty<Boolean> ripplerRecenter = new SimpleStyleableObjectProperty<Boolean>(StyleableProperties.RIPPLER_RECENTER, JFXRippler.this, "ripplerRecenter", false);

	public Boolean isRipplerRecenter(){
		return ripplerRecenter == null ? false : ripplerRecenter.get();
	}
	public StyleableObjectProperty<Boolean> ripplerRecenterProperty(){		
		return this.ripplerRecenter;
	}
	public void setRipplerRecenter(Boolean radius){
		this.ripplerRecenter.set(radius);
	}

	/**
	 * the ripple radius size, by default it will be automatically computed.
	 */
	private StyleableObjectProperty<Number> ripplerRadius = new SimpleStyleableObjectProperty<Number>(StyleableProperties.RIPPLER_RADIUS, JFXRippler.this, "ripplerRadius", Region.USE_COMPUTED_SIZE);

	public Number getRipplerRadius(){
		return ripplerRadius == null ? Region.USE_COMPUTED_SIZE : ripplerRadius.get();
	}
	public StyleableObjectProperty<Number> ripplerRadiusProperty(){		
		return this.ripplerRadius;
	}
	public void setRipplerRadius(Number radius){
		this.ripplerRadius.set(radius);
	}

	/**
	 * the default color of the ripple effect
	 */
	private StyleableObjectProperty<Paint> ripplerFill = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.RIPPLER_FILL, JFXRippler.this, "ripplerFill", Color.rgb(0, 200, 255));

	public Paint getRipplerFill(){
		return ripplerFill == null ? Color.rgb(0, 200, 255) : ripplerFill.get();
	}
	public StyleableObjectProperty<Paint> ripplerFillProperty(){		
		return this.ripplerFill;
	}
	public void setRipplerFill(Paint color){
		this.ripplerFill.set(color);
	}

	/**
	 * mask property used for clipping the rippler.
	 * can be either CIRCLE/RECT 
	 */
	private StyleableObjectProperty<RipplerMask> maskType = new SimpleStyleableObjectProperty<RipplerMask>(StyleableProperties.MASK_TYPE, JFXRippler.this, "maskType", RipplerMask.RECT );

	public RipplerMask getMaskType(){
		return maskType == null ? RipplerMask.RECT : maskType.get();
	}
	public StyleableObjectProperty<RipplerMask> maskTypeProperty(){		
		return this.maskType;
	}
	public void setMaskType(RipplerMask type){
		this.maskType.set(type);
	}

	/**
	 * indicates whether the ripple effect is infront of or behind the node
	 */
	protected ObjectProperty<RipplerPos> position = new SimpleObjectProperty<RipplerPos>();

	public void setPosition(RipplerPos pos){
		this.position.set(pos);
	}
	public RipplerPos getPosition(){
		return position == null ? RipplerPos.FRONT : position.get();
	}
	public ObjectProperty<RipplerPos> positionProperty(){		
		return this.position;
	}


	private static class StyleableProperties {
		private static final CssMetaData< JFXRippler, Boolean> RIPPLER_RECENTER =
				new CssMetaData< JFXRippler, Boolean>("-jfx-rippler-recenter",
						BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(JFXRippler control) {
				return control.ripplerRecenter == null || !control.ripplerRecenter.isBound();
			}
			@Override
			public StyleableProperty<Boolean> getStyleableProperty(JFXRippler control) {
				return control.ripplerRecenterProperty();
			}
		};
		private static final CssMetaData< JFXRippler, Paint> RIPPLER_FILL =
				new CssMetaData< JFXRippler, Paint>("-jfx-rippler-fill",
						PaintConverter.getInstance(), Color.rgb(0, 200, 255)) {
			@Override
			public boolean isSettable(JFXRippler control) {
				return control.ripplerFill == null || !control.ripplerFill.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXRippler control) {
				return control.ripplerFillProperty();
			}
		};
		private static final CssMetaData< JFXRippler, Number> RIPPLER_RADIUS =
				new CssMetaData< JFXRippler, Number>("-jfx-rippler-radius",
						SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
			@Override
			public boolean isSettable(JFXRippler control) {
				return control.ripplerRadius == null || !control.ripplerRadius.isBound();
			}
			@Override
			public StyleableProperty<Number> getStyleableProperty(JFXRippler control) {
				return control.ripplerRadiusProperty();
			}
		};
		private static final CssMetaData< JFXRippler, RipplerMask> MASK_TYPE =
				new CssMetaData< JFXRippler, RipplerMask>("-jfx-mask-type",
						RipplerMaskTypeConverter.getInstance(), RipplerMask.RECT) {
			@Override
			public boolean isSettable(JFXRippler control) {
				return control.maskType == null || !control.maskType.isBound();
			}
			@Override
			public StyleableProperty<RipplerMask> getStyleableProperty(JFXRippler control) {
				return control.maskTypeProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Parent.getClassCssMetaData());
			Collections.addAll(styleables,
					RIPPLER_RECENTER,
					RIPPLER_RADIUS,
					RIPPLER_FILL,
					MASK_TYPE
					);
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}


	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}
	
	public Runnable createManualRipple() {
		rippler.setGeneratorCenterX(control.getLayoutBounds().getWidth()/2);
		rippler.setGeneratorCenterY(control.getLayoutBounds().getHeight()/2);
		return rippler.createManualRipple();
	}
	
}
