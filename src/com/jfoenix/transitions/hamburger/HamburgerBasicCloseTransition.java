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

package com.jfoenix.transitions.hamburger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.CachedTransition;

public class HamburgerBasicCloseTransition extends CachedTransition implements HamburgerTransition {
	
	public HamburgerBasicCloseTransition(){
		super(null,null);
	}
	
	public HamburgerBasicCloseTransition(JFXHamburger burger) {
		super(burger, createTimeline(burger));
		timeline.bind(Bindings.createObjectBinding(()->createTimeline(burger),
				burger.widthProperty(), burger.heightProperty(),
				((Region) burger.getChildren().get(0)).widthProperty(), ((Region) burger.getChildren().get(0)).heightProperty() ));							
		// reduce the number to increase the shifting , increase number to reduce shifting
		setCycleDuration(Duration.seconds(0.3));
		setDelay(Duration.seconds(0));
	}
	
	private static Timeline createTimeline(JFXHamburger burger){
		double burgerWidth = burger.getChildren().get(0).getLayoutBounds().getWidth();
		double burgerHeight = burger.getChildren().get(2).getBoundsInParent().getMaxY() - burger.getChildren().get(0).getBoundsInParent().getMinY();
		
		double hypotenuse = Math.sqrt(Math.pow(burgerHeight, 2) + Math.pow(burgerWidth, 2));
		double angle = (Math.toDegrees(Math.asin(burgerWidth/hypotenuse)) - 90) * -1;
		return new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(burger.rotateProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(0).rotateProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(0).translateYProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(2).rotateProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(2).translateYProperty(), 0,Interpolator.EASE_BOTH),
						new KeyValue(burger.getChildren().get(1).opacityProperty(), 1,Interpolator.EASE_BOTH)
						),
						new KeyFrame(Duration.millis(1000),
								new KeyValue(burger.rotateProperty(), 0,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(0).rotateProperty(), angle,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(0).translateYProperty(), (burgerHeight/2)-burger.getChildren().get(0).getBoundsInLocal().getHeight()/2 ,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(2).rotateProperty(), -angle,Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(2).translateYProperty(), - ((burgerHeight/2)-burger.getChildren().get(0).getBoundsInLocal().getHeight()/2),Interpolator.EASE_BOTH),
								new KeyValue(burger.getChildren().get(1).opacityProperty(), 0,Interpolator.EASE_BOTH)
								)
				);
	}
	
	public Transition getAnimation(JFXHamburger burger){
		return new HamburgerBasicCloseTransition(burger);
	}
	
}