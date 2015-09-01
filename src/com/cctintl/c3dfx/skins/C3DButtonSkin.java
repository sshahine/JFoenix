/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

package com.cctintl.c3dfx.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DButton.ButtonType;
import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.DepthManager;
import com.cctintl.c3dfx.jidefx.CachedTimelineTransition;
import com.sun.javafx.scene.control.skin.ButtonSkin;
import com.sun.javafx.scene.control.skin.LabeledText;

/**
 * @author sshahine
 * TODO: C3D Button doesn't support borders yet
 */

public class C3DButtonSkin extends ButtonSkin {

	private final StackPane buttonContainer = new StackPane();
	private C3DRippler buttonRippler;
	private Transition clickedAnimation ;

	private final Color disabledColor = Color.valueOf("#EAEAEA");
	private final CornerRadii defaultRadii = new CornerRadii(3);
	
	private boolean invalid = true;

	public C3DButtonSkin(C3DButton button) {
		super(button);

		buttonRippler = new C3DRippler(new StackPane()){
			@Override protected Node getMask(){
				StackPane mask = new StackPane(); 
				mask.shapeProperty().bind(buttonContainer.shapeProperty());				
				mask.backgroundProperty().bind(Bindings.createObjectBinding(()->{					
					return new Background(new BackgroundFill(Color.WHITE, 
							buttonContainer.backgroundProperty().get()!=null?buttonContainer.getBackground().getFills().get(0).getRadii() : defaultRadii,
							buttonContainer.backgroundProperty().get()!=null?buttonContainer.getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
				}, buttonContainer.backgroundProperty()));				
				mask.resize(buttonContainer.getWidth(), buttonContainer.getHeight());
				return mask;
			}
			@Override protected void initListeners(){
				ripplerPane.setOnMousePressed((event) -> {
					createRipple(event.getX(),event.getY());
				});
			}
		};
		buttonContainer.getChildren().add(buttonRippler);


		// add listeners to the button
		button.buttonTypeProperty().addListener((o,oldVal,newVal)->updateButtonType(newVal));
		button.setOnMousePressed((e)->{
			if(clickedAnimation!=null){
				clickedAnimation.setRate(1);
				clickedAnimation.play();	
			}
		});
		button.setOnMouseReleased((e)->{
			if(clickedAnimation!=null){
				clickedAnimation.setRate(-1);
				clickedAnimation.play();
			}
		});
		
		button.ripplerFillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
		button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, defaultRadii, null)));		
		updateButtonType(button.getButtonType());
		updateChildren();
	}

	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (buttonContainer != null) {		
			getChildren().get(0).setMouseTransparent(true);
			getChildren().add(0,buttonContainer);			
		}
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if(invalid){
			buttonContainer.borderProperty().bind(getSkinnable().borderProperty());
			buttonContainer.backgroundProperty().bind(Bindings.createObjectBinding(()->{
				//Insets always Empty
				if(getSkinnable().backgroundProperty().get()!=null && getSkinnable().backgroundProperty().get().getFills().get(0).getInsets().equals(new Insets(-0.2,-0.2, -0.2,-0.2))){
					return new Background(new BackgroundFill(getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getFill() : Color.TRANSPARENT, 
							getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getRadii() : defaultRadii,
							Insets.EMPTY/*new Insets(0,0,-1.0,0)*/));
				}else{
					return new Background(new BackgroundFill(getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getFill() : Color.TRANSPARENT, 
							getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getRadii() : defaultRadii,
							Insets.EMPTY/*getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getInsets() : Insets.EMPTY*/));	
				}
			}, getSkinnable().backgroundProperty()));
			if(((C3DButton)getSkinnable()).getRipplerFill() == null){
				if(getChildren().get(1) instanceof LabeledText){
					buttonRippler.setRipplerFill(((LabeledText)getChildren().get(1)).getFill());			
					((LabeledText)getChildren().get(1)).fillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
				}else if(getChildren().get(1) instanceof Label){
					buttonRippler.setRipplerFill(((Label)getChildren().get(1)).getTextFill());			
					((Label)getChildren().get(1)).textFillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
				}
			}else{
				buttonRippler.setRipplerFill(((C3DButton)getSkinnable()).getRipplerFill());
			}
			invalid = false;
		}
		double shift = 1;
		buttonContainer.resizeRelocate(getSkinnable().getLayoutBounds().getMinX()-shift, getSkinnable().getLayoutBounds().getMinY()-shift, getSkinnable().getWidth()+(2*shift), getSkinnable().getHeight()+(2*shift));
		layoutLabelInArea(x, y, w, h);
	}

	private void updateButtonType(ButtonType type){
		switch (type) {
		case RAISED:
			DepthManager.setDepth(buttonContainer, 2);
			clickedAnimation = new ButtonClickTransition(); 
			break;
		default:
			buttonContainer.setEffect(null);
			break;
		}
	}


	private class ButtonClickTransition extends CachedTimelineTransition {

		public ButtonClickTransition() {
			super(buttonContainer, new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(((DropShadow)buttonContainer.getEffect()).radiusProperty(), DepthManager.getShadowAt(2).radiusProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonContainer.getEffect()).spreadProperty(), DepthManager.getShadowAt(2).spreadProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetXProperty(), DepthManager.getShadowAt(2).offsetXProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetYProperty(), DepthManager.getShadowAt(2).offsetYProperty().get(), Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(1000),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).radiusProperty(), DepthManager.getShadowAt(5).radiusProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).spreadProperty(), DepthManager.getShadowAt(5).spreadProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetXProperty(), DepthManager.getShadowAt(5).offsetXProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetYProperty(), DepthManager.getShadowAt(5).offsetYProperty().get(), Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.2));
			setDelay(Duration.seconds(0));
		}

	}


}
