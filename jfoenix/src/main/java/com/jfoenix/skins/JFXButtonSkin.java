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
package com.jfoenix.skins;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.CachedTransition;
import com.sun.javafx.scene.control.skin.ButtonSkin;
import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.animation.*;
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

/**
 * <h1>Material Design Button Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXButtonSkin extends ButtonSkin {

	private final StackPane buttonContainer = new StackPane();
	private JFXRippler buttonRippler;
	private Transition clickedAnimation ;
	private final CornerRadii defaultRadii = new CornerRadii(3);
	
	private boolean invalid = true;
	private Runnable releaseManualRippler = null;
	public JFXButtonSkin(JFXButton button) {
		super(button);

		buttonRippler = new JFXRippler(new StackPane()){
			@Override protected Node getMask(){
				StackPane mask = new StackPane(); 
				mask.shapeProperty().bind(buttonContainer.shapeProperty());				
				mask.backgroundProperty().bind(Bindings.createObjectBinding(()->{					
					return new Background(new BackgroundFill(Color.WHITE, 
							buttonContainer.backgroundProperty().get()!=null && buttonContainer.getBackground().getFills().size() > 0 ?buttonContainer.getBackground().getFills().get(0).getRadii() : defaultRadii,
							buttonContainer.backgroundProperty().get()!=null && buttonContainer.getBackground().getFills().size() > 0 ?buttonContainer.getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
				}, buttonContainer.backgroundProperty()));				
				mask.resize(buttonContainer.getWidth()-buttonContainer.snappedRightInset()-buttonContainer.snappedLeftInset(), buttonContainer.getHeight()-buttonContainer.snappedBottomInset()-buttonContainer.snappedTopInset());
				return mask;
			}
			@Override protected void initListeners(){
				ripplerPane.setOnMousePressed((event) -> {
					if(releaseManualRippler!=null) releaseManualRippler.run();
					releaseManualRippler = null;
					createRipple(event.getX(),event.getY());
				});
			}
		};
		
		getSkinnable().armedProperty().addListener((o,oldVal,newVal)->{
			if(newVal){
				releaseManualRippler = buttonRippler.createManualRipple();
				if(clickedAnimation!=null){
					clickedAnimation.setRate(1);
					clickedAnimation.play();	
				}
			}else{
				if(releaseManualRippler!=null) releaseManualRippler.run();
				if(clickedAnimation!=null){
					clickedAnimation.setRate(-1);
					clickedAnimation.play();	
				}
			}
		});
		
		buttonContainer.getChildren().add(buttonRippler);


		// add listeners to the button and bind properties
		button.buttonTypeProperty().addListener((o,oldVal,newVal)->updateButtonType(newVal));
		button.setOnMousePressed(e ->{
			if(clickedAnimation!=null){
				clickedAnimation.setRate(1);
				clickedAnimation.play();	
			}
		});
		button.setOnMouseReleased(e ->{
			if(clickedAnimation!=null){
				clickedAnimation.setRate(-1);
				clickedAnimation.play();
			}
		});
		
		// show focused state
		button.focusedProperty().addListener((o,oldVal,newVal)->{
			if(newVal){
				if(!getSkinnable().isPressed()) buttonRippler.showOverlay();
			}else buttonRippler.hideOverlay();
		});
		button.pressedProperty().addListener((o,oldVal,newVal)-> buttonRippler.hideOverlay());
		
		/*
		 * disable action when clicking on the button shadow 
		 */
		button.setPickOnBounds(false);
		buttonContainer.setPickOnBounds(false);
		buttonContainer.shapeProperty().bind(getSkinnable().shapeProperty());
		buttonContainer.borderProperty().bind(getSkinnable().borderProperty());		
		buttonContainer.backgroundProperty().bind(Bindings.createObjectBinding(()->{
			// reset button background to transparent if its set to java default values
			if(button.getBackground() == null || isJavaDefaultBackground(button.getBackground()) || isJavaDefaultClickedBackground(button.getBackground()) )
				button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, defaultRadii, null)));
			try{
				//Insets always Empty
				if(getSkinnable().getBackground()!=null && getSkinnable().getBackground().getFills().get(0).getInsets().equals(new Insets(-0.2,-0.2, -0.2,-0.2))){
					return new Background(new BackgroundFill(getSkinnable().getBackground()!=null?getSkinnable().getBackground().getFills().get(0).getFill() : Color.TRANSPARENT, 
							getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getRadii() : defaultRadii,
							Insets.EMPTY/*new Insets(0,0,-1.0,0)*/));
				}else{
					return new Background(new BackgroundFill(getSkinnable().getBackground()!=null?getSkinnable().getBackground().getFills().get(0).getFill() : Color.TRANSPARENT, 
							getSkinnable().getBackground()!=null?getSkinnable().getBackground().getFills().get(0).getRadii() : defaultRadii,
							Insets.EMPTY/*getSkinnable().backgroundProperty().get()!=null?getSkinnable().getBackground().getFills().get(0).getInsets() : Insets.EMPTY*/));	
				}				
			}catch(Exception e){
				return getSkinnable().getBackground();
			}
		}, getSkinnable().backgroundProperty()));
		
		button.ripplerFillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
		
		// set default background to transparent
		if(button.getBackground() == null || isJavaDefaultBackground(button.getBackground()))
			button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, defaultRadii, null)));
		
		updateButtonType(button.getButtonType());
		updateChildren();
	}

	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (buttonContainer != null) getChildren().add(0,buttonContainer);			
		for(int i = 1 ; i < getChildren().size(); i++)
			getChildren().get(i).setMouseTransparent(true);
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if(invalid){
			if(((JFXButton)getSkinnable()).getRipplerFill() == null){
				// change rippler fill according to the last LabeledText/Label child
				for(int i = getChildren().size()-1; i >= 1; i--){
					if(getChildren().get(i) instanceof LabeledText){
						buttonRippler.setRipplerFill(((LabeledText)getChildren().get(i)).getFill());			
						((LabeledText)getChildren().get(i)).fillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
						break;
					}else if(getChildren().get(i) instanceof Label){
						buttonRippler.setRipplerFill(((Label)getChildren().get(i)).getTextFill());			
						((Label)getChildren().get(i)).textFillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
						break;
					}	
				}
			}else{
				buttonRippler.setRipplerFill(((JFXButton)getSkinnable()).getRipplerFill());
			}
			invalid = false;
		}
		double shift = 1;
		buttonContainer.resizeRelocate(getSkinnable().getLayoutBounds().getMinX()-shift, getSkinnable().getLayoutBounds().getMinY()-shift, getSkinnable().getWidth()+(2*shift), getSkinnable().getHeight()+(2*shift));
		layoutLabelInArea(x, y, w, h);
	}

	private boolean isJavaDefaultBackground(Background background){
		try{
			return background.getFills().get(0).getFill().toString().equals("0xffffffba") 
				|| background.getFills().get(0).getFill().toString().equals("0xffffffbf") 
				|| background.getFills().get(0).getFill().toString().equals("0xffffffbd");	
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean isJavaDefaultClickedBackground(Background background){
		try{
			return background.getFills().get(0).getFill().toString().equals("0x039ed3ff");	
		}catch(Exception e){
			return false;
		}
	}
	
	private void updateButtonType(ButtonType type){
		switch (type) {
		case RAISED:
			JFXDepthManager.setDepth(buttonContainer, 2);
			clickedAnimation = new ButtonClickTransition(); 
			break;
		default:
			buttonContainer.setEffect(null);
			break;
		}
	}


	private class ButtonClickTransition extends CachedTransition {

		public ButtonClickTransition() {
			super(buttonContainer, new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(((DropShadow)buttonContainer.getEffect()).radiusProperty(), JFXDepthManager.getShadowAt(2).radiusProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonContainer.getEffect()).spreadProperty(), JFXDepthManager.getShadowAt(2).spreadProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetXProperty(), JFXDepthManager.getShadowAt(2).offsetXProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetYProperty(), JFXDepthManager.getShadowAt(2).offsetYProperty().get(), Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(1000),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).radiusProperty(), JFXDepthManager.getShadowAt(5).radiusProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).spreadProperty(), JFXDepthManager.getShadowAt(5).spreadProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetXProperty(), JFXDepthManager.getShadowAt(5).offsetXProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonContainer.getEffect()).offsetYProperty(), JFXDepthManager.getShadowAt(5).offsetYProperty().get(), Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.2));
			setDelay(Duration.seconds(0));
		}

	}


}
