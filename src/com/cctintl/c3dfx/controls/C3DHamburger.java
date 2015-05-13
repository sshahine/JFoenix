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

package com.cctintl.c3dfx.controls;


import javafx.animation.Transition;
import javafx.beans.DefaultProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import com.cctintl.c3dfx.transitions.hamburger.HamburgerTransition;

@DefaultProperty(value="animation")
public class C3DHamburger extends VBox {

	private Transition animation;

	public C3DHamburger() {
		SVGPath line1 = new SVGPath();
		line1.setContent("m 482.73642,457.05289 24,0 c 1.104,0 2,-0.896 2,-2 0,-1.104 -0.896,-2 -2,-2 l -24,0 c -1.104,0 -2,0.896 -2,2 0,1.104 0.896,2 2,2 z");
		initSVGStyle(line1);
		SVGPath line2 = new SVGPath();
		line2.setContent("m 482.73642,457.05289 24,0 c 1.104,0 2,-0.896 2,-2 0,-1.104 -0.896,-2 -2,-2 l -24,0 c -1.104,0 -2,0.896 -2,2 0,1.104 0.896,2 2,2 z");
		initSVGStyle(line2);
		SVGPath line3 = new SVGPath();
		line3.setContent("m 482.73642,457.05289 24,0 c 1.104,0 2,-0.896 2,-2 0,-1.104 -0.896,-2 -2,-2 l -24,0 c -1.104,0 -2,0.896 -2,2 0,1.104 0.896,2 2,2 z");
		initSVGStyle(line3);
		this.getChildren().add(line1);
		this.getChildren().add(line2);
		this.getChildren().add(line3);
		this.setSpacing(3);
		this.setScaleX(0.8);
		this.setScaleY(0.8);
		this.getStyleClass().add("c3d-hamburger-icon");
		// must be specified 
		this.setAlignment(Pos.CENTER);
	}

	public Transition getAnimation() {
		return animation;
	}

	public void setAnimation(Transition animation) {
		this.animation = ((HamburgerTransition)animation).getAnimation(this);
		this.animation.setRate(-1);
	}

	private void initSVGStyle(SVGPath path){
		path.setStrokeWidth(0);
		path.setStroke(Color.TRANSPARENT);
		path.setOpacity(1);
		path.setStrokeMiterLimit(4);
		path.setStrokeLineJoin(StrokeLineJoin.MITER);
		path.setStrokeLineCap(StrokeLineCap.BUTT);
		path.setStrokeDashOffset(0);
		path.setFill(Color.BLACK);
	}

}
