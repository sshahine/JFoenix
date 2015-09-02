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

package com.cctintl.jfx.transitions.hamburger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import com.cctintl.jfx.controls.JFXHamburger;
import com.cctintl.jfx.jidefx.CachedTimelineTransition;

public class HamburgerBackArrowBasicTransition extends CachedTimelineTransition implements HamburgerTransition {
	
	public HamburgerBackArrowBasicTransition() {
		super(null,null);
	}
	
	public HamburgerBackArrowBasicTransition(JFXHamburger burger) {
		super(burger, createTimeline(burger));
		timeline.bind(Bindings.createObjectBinding(()->createTimeline(burger),
				burger.widthProperty(), burger.heightProperty(),
				((Region) burger.getChildren().get(0)).widthProperty(), ((Region) burger.getChildren().get(0)).heightProperty() ));							
		// reduce the number to increase the shifting , increase number to reduce shifting
		setCycleDuration(Duration.seconds(0.3));
		setDelay(Duration.seconds(0));
	}
	
	private static Timeline createTimeline(JFXHamburger burger){
		
		double hypotenuse = Math.sqrt(Math.pow(burger.getHeight()/2 - burger.getChildren().get(0).getLayoutBounds().getHeight()/2 , 2) + Math.pow(burger.getWidth()/2, 2));
		double angle = Math.toDegrees((Math.asin((burger.getHeight()/2 - burger.getChildren().get(0).getLayoutBounds().getHeight()/2)/hypotenuse)));
				
		double burgerDiagonal = Math.sqrt(Math.pow(burger.getChildren().get(0).getLayoutBounds().getHeight(), 2) + Math.pow(burger.getChildren().get(0).getBoundsInParent().getWidth()/2, 2));
		double theta = (90-angle) + Math.toDegrees(Math.atan((burger.getChildren().get(0).getLayoutBounds().getHeight())/(burger.getChildren().get(0).getBoundsInParent().getWidth()/2)));
		double hOffset = Math.cos(Math.toRadians(theta)) * burgerDiagonal/2;
		
		double transY = burger.getChildren().get(0).getLayoutBounds().getHeight()/2 + burger.getSpacing() - hOffset;
		double transX = burger.getWidth()/2 -  Math.sin(Math.toRadians(theta)) * (burgerDiagonal/2);
		
		return new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(burger.rotateProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(0).rotateProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(0).translateYProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(0).translateXProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(0).scaleXProperty(), 1,Interpolator.EASE_BOTH),
						
						new KeyValue(burger.getChildren().get(2).rotateProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(2).translateYProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(2).translateXProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(2).scaleXProperty(), 1,Interpolator.EASE_BOTH)
						
						),
						new KeyFrame(Duration.millis(1000),
								new KeyValue(burger.rotateProperty(), 0,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(0).rotateProperty(), -angle,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(0).translateYProperty(), transY,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(0).translateXProperty(), - transX,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(0).scaleXProperty(), 0.5,Interpolator.EASE_BOTH),
								
								new KeyValue(burger.getChildren().get(2).rotateProperty(), angle,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(2).translateYProperty(), -transY,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(2).translateXProperty(), -transX,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(2).scaleXProperty(), 0.5,Interpolator.EASE_BOTH)
								)
					);
	}
	
	public Transition getAnimation(JFXHamburger burger){
		return new HamburgerBackArrowBasicTransition(burger);
	}

}
