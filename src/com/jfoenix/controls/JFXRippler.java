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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.converters.RipplerMaskTypeConverter;
import com.jfoenix.transitions.CachedTransition;
import com.sun.javafx.css.converters.PaintConverter;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.Event;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

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
	private double defaultRadius = 200;
	private double minRadius = 100;
	private double rippleRadius = 150;
	private boolean enabled = true;

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

			control.boundsInParentProperty().addListener((o,oldVal,newVal)->{
				rippleRadius = newVal.getWidth();
				if(rippleRadius > defaultRadius)
					rippleRadius = defaultRadius;
				if(rippleRadius < minRadius)
					rippleRadius = minRadius;
			});

			this.getChildren().add(ripplerPane);

			// add listeners
			initListeners();
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
		Shape mask = new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth() - 0.1 - 2 * borderWidth, bounds.getHeight() - 0.1 - 2 * borderWidth); // -0.1 to prevent resizing the anchor pane
		if (maskType.get().equals(RipplerMask.CIRCLE)) {
			double radius = Math.min((bounds.getWidth() / 2) - 0.1 - 2 * borderWidth, (bounds.getHeight() / 2) - 0.1 - 2 * borderWidth);
			mask = new Circle((bounds.getMinX() + bounds.getMaxX()) / 2, (bounds.getMinY() + bounds.getMaxY()) / 2, radius, Color.BLUE);
		}
		return mask;
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

		// if the control got resized the overlay rect must be rest
		if(this.control instanceof Region){
			((Region)this.control).widthProperty().addListener((o,oldVal,newVal)-> resetOverLay());
			((Region)this.control).heightProperty().addListener((o,oldVal,newVal)-> resetOverLay());
		}
	}

	/**
	 * creates Ripple effect
	 */
	protected void createRipple(double x, double y){
		rippler.setGeneratorCenterX(x);
		rippler.setGeneratorCenterY(y);
		rippler.createRipple();
	}
	
	/**
	 * fire event to the rippler pane manually
	 * @param event
	 */
	public void fireEventProgrammatically(Event event){
		if(!event.isConsumed())
			ripplerPane.fireEvent(event);
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
		private boolean generating = false;

		public RippleGenerator() {
			/* 
			 * improve in performance, by preventing  
			 * redrawing the parent when the ripple effect is triggered
			 */
			this.setManaged(false);
		}
		public void createRipple() {
			if(enabled){
				if(!generating){
					generating = true;
					// create overlay once then change its color later 
					createOverlay();

					// create the ripple effect
					final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);				
					ripple.setClip(getMask());			
					getChildren().add(ripple);	

					overlayRect.outAnimation.stop();
					overlayRect.inAnimation.play();
					ripple.inAnimation.play();

					// create fade out transition for the ripple
					ripplerPane.setOnMouseReleased((e)->{
						generating = false;
						if(overlayRect!=null)overlayRect.inAnimation.stop();
						ripple.inAnimation.pause();
						double fadeOutRadious = rippleRadius + 20;
						if(ripple.radiusProperty().get() < rippleRadius*0.5)
							fadeOutRadious = rippleRadius;

						Timeline outAnimation = new Timeline( new KeyFrame(Duration.seconds(0.4),
								new KeyValue(ripple.radiusProperty(), fadeOutRadious ,Interpolator.LINEAR),
								new KeyValue(ripple.opacityProperty(), 0, Interpolator.EASE_BOTH)));

						outAnimation.setOnFinished((event)-> {
							getChildren().remove(ripple);
							// remove overlay rect after 200 ms in case rippler is not generated
							new Thread(()->{
								try { Thread.sleep(200); } catch (Exception e1) { }
								if(getChildren().size() == 1 )
									JFXUtilities.runInFXAndWait(()->resetOverLay());
							}).start();
						});
						outAnimation.play();
						if(overlayRect!=null) overlayRect.outAnimation.play();
					});
				}
			}
		}

		public void createOverlay(){
			if(overlayRect == null){
				overlayRect = new OverLayRipple();
				overlayRect.setClip(getMask());
				getChildren().add(overlayRect);
			}					
			overlayRect.setFill(new Color(((Color)ripplerFill.get()).getRed(), ((Color)ripplerFill.get()).getGreen(), ((Color)ripplerFill.get()).getBlue(),0.2));
		}

		public void setGeneratorCenterX(double generatorCenterX) {
			this.generatorCenterX = generatorCenterX;
		}

		public void setGeneratorCenterY(double generatorCenterY) {
			this.generatorCenterY = generatorCenterY;
		}

		private class OverLayRipple extends Rectangle{
			// Overlay ripple animations 
			CachedTransition inAnimation = new CachedTransition(this, new Timeline(new KeyFrame(Duration.millis(1300),new KeyValue(opacityProperty(), 1,Interpolator.EASE_BOTH)))){{setDelay(Duration.millis(0));setCycleDuration(Duration.millis(300));}};
			CachedTransition outAnimation = new CachedTransition(this, new Timeline(new KeyFrame(Duration.millis(1300),new KeyValue(opacityProperty(), 0,Interpolator.EASE_BOTH)))){{setDelay(Duration.millis(0));setCycleDuration(Duration.millis(300));}};

			public OverLayRipple() {
				super(control.getLayoutBounds().getWidth() - 0.1,control.getLayoutBounds().getHeight() - 0.1);
				this.getStyleClass().add("jfx-rippler-overlay");
				this.widthProperty().bind(Bindings.createDoubleBinding(()-> control.getLayoutBounds().getWidth() - 0.1, control.boundsInParentProperty()));
				this.heightProperty().bind(Bindings.createDoubleBinding(()-> control.getLayoutBounds().getHeight() - 0.1, control.boundsInParentProperty()));
				this.setOpacity(0);
				setCache(true);
				setCacheHint(CacheHint.SPEED);
				setCacheShape(true);
				setSnapToPixel(false);
			}
		}

		private class Ripple extends Circle {

			CachedTransition inAnimation = new CachedTransition(this,  new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(radiusProperty(),  0,Interpolator.LINEAR),
							new KeyValue(opacityProperty(), 1,Interpolator.EASE_BOTH)
							),new KeyFrame(Duration.millis(1300), 
									new KeyValue(radiusProperty(),  rippleRadius ,Interpolator.LINEAR)					
									))){{
										setCycleDuration(Duration.millis(300));
										setDelay(Duration.millis(0));
									}};

									private Ripple(double centerX, double centerY) {
										super(centerX, centerY, 0, null);	
										setCache(true);
										setCacheHint(CacheHint.SPEED);
										setCacheShape(true);
										setSnapToPixel(false);
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
		private static final CssMetaData< JFXRippler, Paint> RIPPLER_FILL =
				new CssMetaData< JFXRippler, Paint>("-fx-rippler-fill",
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
		private static final CssMetaData< JFXRippler, RipplerMask> MASK_TYPE =
				new CssMetaData< JFXRippler, RipplerMask>("-fx-mask-type", RipplerMaskTypeConverter.getInstance(), RipplerMask.RECT) {
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

}
