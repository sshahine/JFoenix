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

import javafx.animation.Transition;
import javafx.beans.DefaultProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.jfoenix.transitions.hamburger.HamburgerTransition;

/**
 * @author Shadi Shaheen
 *
 */
@DefaultProperty(value="animation")
public class JFXHamburger extends VBox {

	private static final String DEFAULT_STYLE_CLASS = "jfx-hamburger";
	
	private Transition animation;
	
	public JFXHamburger() {
				
		StackPane line1 = new StackPane();
		StackPane line2 = new StackPane();
		StackPane line3 = new StackPane();
		
		initStyle(line1);
		initStyle(line2);
		initStyle(line3);
		
		this.getChildren().add(line1);
		this.getChildren().add(line2);
		this.getChildren().add(line3);
		
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.setAlignment(Pos.CENTER);
		this.setFillWidth(false);
		this.setSpacing(4);
	}

	public Transition getAnimation() {
		return animation;
	}

	public void setAnimation(Transition animation) {
		this.animation = ((HamburgerTransition)animation).getAnimation(this);
		this.animation.setRate(-1);
	}

	private void initStyle(StackPane pane){
		pane.setOpacity(1);
		pane.setPrefSize(30, 4);
		pane.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)));
	}

}
