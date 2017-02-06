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

import javafx.beans.DefaultProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

/**
 * <h1>Material Design ScrollPane with header </h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2017-02-06
 */

@DefaultProperty(value="content")
public class JFXScrollPane extends StackPane{

	private static final String DEFAULT_STYLE_CLASS = "jfx-scroll-pane";
	
	private ScrollPane scrollPane = new ScrollPane();
	private VBox contentContainer = new VBox();
	private StackPane headerSpace = new StackPane();
	private StackPane condensedHeaderBG = new StackPane();
	private StackPane headerBG = new StackPane();
	
	private double initY = -1;
	private double maxHeight = -1;
	private double minHeight = -1;

	private StackPane bottomBar;
	Scale scale = new Scale(1, 1, 0, 0);

	//	private Timeline animation;
	private StackPane midBar;
	private StackPane topBar;

	public JFXScrollPane() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		
		// clip content
		Rectangle clip = new Rectangle();
		this.setClip(clip);
		clip.widthProperty().bind(this.widthProperty());
		clip.heightProperty().bind(this.heightProperty());

		
		StackPane header = new  StackPane();
		condensedHeaderBG.setOpacity(0);
		condensedHeaderBG.getStyleClass().add("condensed-header");
		condensedHeaderBG.setBackground(new Background(new BackgroundFill(Color.valueOf("#1E88E5"), CornerRadii.EMPTY, Insets.EMPTY)));
		headerBG.setBackground(new Background(new BackgroundFill(Color.valueOf("#3949AB"), CornerRadii.EMPTY, Insets.EMPTY)));
		headerBG.getStyleClass().add("main-header");
		StackPane bgContainer = new StackPane();
		bgContainer.getChildren().setAll(condensedHeaderBG, headerBG);
		bgContainer.setMouseTransparent(true);

		topBar = new StackPane();
		topBar.setPickOnBounds(false);
		topBar.setMaxHeight(64);

		midBar = new StackPane();
		midBar.setMaxHeight(64);
		midBar.setPickOnBounds(false);

		bottomBar = new StackPane();
		bottomBar.setMaxHeight(64);
		bottomBar.getTransforms().add(scale);
		scale.pivotYProperty().bind(bottomBar.heightProperty().divide(2));
		bottomBar.setPickOnBounds(false);

		StackPane barsContainer = new StackPane(topBar, midBar, bottomBar);
		StackPane.setAlignment(topBar, Pos.TOP_CENTER);
		StackPane.setAlignment(bottomBar, Pos.BOTTOM_CENTER);

		header.setPrefHeight(64*3);
		header.maxHeightProperty().bind(header.prefHeightProperty());
		header.getChildren().setAll(bgContainer, barsContainer);
		StackPane.setAlignment(header, Pos.TOP_CENTER);
		headerSpace.minHeightProperty().bind(header.prefHeightProperty());
		headerSpace.maxHeightProperty().bind(header.prefHeightProperty());
		headerSpace.setFocusTraversable(true);

		contentContainer.getChildren().setAll(headerSpace);

		scrollPane.setContent(contentContainer);
		scrollPane.setFitToWidth(true);
		scrollPane.vvalueProperty().addListener((o,oldVal,newVal)->{

			double ty = newVal.doubleValue() * scrollPane.getContent().getLayoutBounds().getHeight();
			double opacity = ty / minHeight;
			opacity = opacity > 1 ? 1 : (opacity < 0)? 0 : opacity;
			Bounds localToScene = headerSpace.localToScene(headerSpace.getBoundsInLocal());

			if(minHeight == -1) {
				minHeight = bottomBar.getBoundsInParent().getMinY();
				maxHeight = header.getHeight();
				initY = localToScene.getMinY();
			}
			
			// update properties according to the scroll value
			// opacity
			headerBG.setOpacity(1 - opacity);
			condensedHeaderBG.setOpacity(opacity);

			// translation
			double currentY = localToScene.getMinY() - initY;
			topBar.setTranslateY(-currentY <= minHeight ? -currentY : minHeight);
			if(newVal.doubleValue()!= 1 && newVal.doubleValue() < oldVal.doubleValue() && -currentY > minHeight){
				double oldTy = oldVal.doubleValue() * scrollPane.getContent().getLayoutBounds().getHeight();
				double diff =  oldTy - ty;
				if(-(header.getTranslateY() + diff) > minHeight)
					header.setTranslateY(header.getTranslateY() + diff);
				else header.setTranslateY(-minHeight);
			}else{
				if(-currentY > maxHeight){
					double oldTy = oldVal.doubleValue() * scrollPane.getContent().getLayoutBounds().getHeight();
					double diff =  oldTy - ty;
					if(-(header.getTranslateY() + diff) < maxHeight)
						header.setTranslateY(header.getTranslateY() + diff);
					else header.setTranslateY(-maxHeight);
				}else{
					if(newVal.doubleValue() != 0){
						double oldTy = oldVal.doubleValue() * scrollPane.getContent().getLayoutBounds().getHeight();
						double diff =  oldTy - ty;
						if(diff > maxHeight) 
							header.setTranslateY(-maxHeight);
						else
							header.setTranslateY(-currentY < maxHeight ? currentY : -maxHeight);
					}else
						header.setTranslateY(0);
				}
			}
			
			// scale 
			scale.setX(map(opacity, 0, 1, 1, 0.75));
			scale.setY(map(opacity, 0, 1, 1, 0.75));
		});
		scrollPane.setPannable(true);
		getChildren().setAll(scrollPane, header);
	}

	private double map(double val, double min1, double max1, double min2, double max2){
		return min2+(max2-min2)*((val-min1)/(max1-min1));
	}

	public void setContent(Node content){
		if(contentContainer.getChildren().size() == 2)
			contentContainer.getChildren().set(1, content);
		else if(contentContainer.getChildren().size() == 1)
			contentContainer.getChildren().add(content);
		else contentContainer.getChildren().setAll(headerSpace, content);
		VBox.setVgrow(content, Priority.ALWAYS);
	}

	public Node getContent(){
		return contentContainer.getChildren().size() == 2? contentContainer.getChildren().get(1) : null;
	}
	
//	public void setTopBarContent(Node node){
//		topBar.getChildren().setAll(node);
//	}
	public StackPane getTopBar(){
		return topBar;
	}
//	public void setMidBar(Node node){
//		midBar.getChildren().setAll(node);
//	}
	public StackPane getMidBar(){
		return midBar;
//		return midBar.getChildren().isEmpty()? null : midBar.getChildren().get(0);
	}
//	public void setBottomBar(Node node){
//		bottomBar.getChildren().setAll(node);
//	}
	public StackPane getBottomBar(){
		return bottomBar;
	}

	public StackPane getMainHeader(){
		return headerBG;
	}
	public StackPane getCondensedHeader(){
		return condensedHeaderBG;
	}
}
