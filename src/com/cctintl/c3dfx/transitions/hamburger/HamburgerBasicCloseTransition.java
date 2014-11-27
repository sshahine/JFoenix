package com.cctintl.c3dfx.transitions.hamburger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DHamburger;
import com.fxexperience.javafx.animation.CachedTimelineTransition;

public class HamburgerBasicCloseTransition extends CachedTimelineTransition implements HamburgerTransition {
	
	public HamburgerBasicCloseTransition(){
		super(null,null);
	}
	
	public HamburgerBasicCloseTransition(C3DHamburger burger) {
		super(
				burger,
				new Timeline(
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
										new KeyValue(burger.getChildren().get(0).rotateProperty(), 45,Interpolator.EASE_BOTH),
										new KeyValue(burger.getChildren().get(0).translateYProperty(), ((SVGPath)burger.getChildren().get(0)).getBoundsInLocal().getWidth()/3.5,Interpolator.EASE_BOTH),
										new KeyValue(burger.getChildren().get(2).rotateProperty(), -45,Interpolator.EASE_BOTH),
										new KeyValue(burger.getChildren().get(2).translateYProperty(), -((SVGPath)burger.getChildren().get(2)).getBoundsInLocal().getWidth()/3.5,Interpolator.EASE_BOTH),
										new KeyValue(burger.getChildren().get(1).opacityProperty(), 0,Interpolator.EASE_BOTH)
										)
						)
				);
		setCycleDuration(Duration.seconds(0.3));
		setDelay(Duration.seconds(0));
	}
	
	public HamburgerTransition getAnimation(C3DHamburger burger){
		return new HamburgerBasicCloseTransition(burger);
	}
	
}