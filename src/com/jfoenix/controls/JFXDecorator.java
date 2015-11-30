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

package com.jfoenix.controls;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import com.jfoenix.svg.SVGGlyph;


/**
 * @author sshahine
 * this class is been re implemented in the main controller and its fxml, 
 * if not used later , it can be deleted or moved to C3DFX project
 * 
 * this class is not been tested or shown in the demo
 * FUTURE WORK
 */


public class JFXDecorator extends VBox {

	private Stage primaryStage;
	
	private double xOffset = 0;
	private double yOffset = 0;
	private double newX;
	private double newY;
	private double initX;
	private double initY;

	private boolean allowMove = false;
	private boolean isDragging = false;
	private Timeline windowDecoratorAnimation;
	private StackPane contentPlaceHolder = new StackPane();
	private HBox buttonsContainer;
	private ObjectProperty<Color> buttonsColor = new SimpleObjectProperty<Color>(Color.WHITE);
	private ObjectProperty<Color> decoratorColor = new SimpleObjectProperty<Color>(Color.BLACK);
	private ObjectProperty<Runnable> onCloseButtonAction = new SimpleObjectProperty<>(()->{Platform.exit();});
	
	public JFXDecorator(Stage stage, Node node){
		this(stage,node,true,true,true);
	}
	
	public JFXDecorator(Stage stage, Node node, boolean fullScreen, boolean max, boolean min) {
		super();
		primaryStage = stage;
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		setPickOnBounds(false);
		this.getStyleClass().add("jfx-decorator");
		
		SVGGlyph full = new SVGGlyph(0, "FULLSCREEN", "M598 214h212v212h-84v-128h-128v-84zM726 726v-128h84v212h-212v-84h128zM214 426v-212h212v84h-128v128h-84zM298 598v128h128v84h-212v-212h84z", Color.WHITE);
		full.fillProperty().bind(buttonsColor);
		full.setSize(16, 16);
		SVGGlyph minus = new SVGGlyph(0, "MINUS", "M804.571 420.571v109.714q0 22.857-16 38.857t-38.857 16h-694.857q-22.857 0-38.857-16t-16-38.857v-109.714q0-22.857 16-38.857t38.857-16h694.857q22.857 0 38.857 16t16 38.857z", Color.WHITE);
		minus.fillProperty().bind(buttonsColor);
		minus.setSize(12, 2);
		minus.setTranslateY(4);
		SVGGlyph resizeMax = new SVGGlyph(0, "RESIZE_MAX", "M726 810v-596h-428v596h428zM726 44q34 0 59 25t25 59v768q0 34-25 60t-59 26h-428q-34 0-59-26t-25-60v-768q0-34 25-60t59-26z", Color.WHITE);
		resizeMax.fillProperty().bind(buttonsColor);
		resizeMax.setSize(12, 12);
		SVGGlyph resizeMin = new SVGGlyph(0, "RESIZE_MIN", "M80.842 943.158v-377.264h565.894v377.264h-565.894zM0 404.21v619.79h727.578v-619.79h-727.578zM377.264 161.684h565.894v377.264h-134.736v80.842h215.578v-619.79h-727.578v323.37h80.842v-161.686z", Color.WHITE);
		resizeMin.fillProperty().bind(buttonsColor);
		resizeMin.setSize(12, 12);
		SVGGlyph close = new SVGGlyph(0, "CLOSE", "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z", Color.WHITE);
		close.fillProperty().bind(buttonsColor);
		close.setSize(12, 12);
		
		JFXButton btnFull = new JFXButton();
		btnFull.getStyleClass().add("jfx-decorator-button");
		btnFull.setCursor(Cursor.HAND);
		btnFull.setOnAction((action)->primaryStage.setFullScreen(!primaryStage.isFullScreen()));
		btnFull.setGraphic(full);
		btnFull.setTranslateX(-30);
		btnFull.ripplerFillProperty().bind(buttonsColor);

		JFXButton btnClose = new JFXButton();
		btnClose.getStyleClass().add("jfx-decorator-button");
		btnClose.setCursor(Cursor.HAND);
		btnClose.setOnAction((action)->onCloseButtonAction.get().run());
		btnClose.setGraphic(close);
		btnClose.ripplerFillProperty().bind(buttonsColor);		

		JFXButton btnMin = new JFXButton();
		btnMin.getStyleClass().add("jfx-decorator-button");
		btnMin.setCursor(Cursor.HAND);
		btnMin.setOnAction((action)->primaryStage.setIconified(true));
		btnMin.setGraphic(minus);
		btnMin.ripplerFillProperty().bind(buttonsColor);
		
		JFXButton btnMax = new JFXButton();
		btnMax.getStyleClass().add("jfx-decorator-button");
		btnMax.setCursor(Cursor.HAND);
		btnMax.ripplerFillProperty().bind(buttonsColor);
		btnMax.setOnAction((action)->{
			primaryStage.setMaximized(!primaryStage.isMaximized());
			if(primaryStage.isMaximized()){
				btnMax.setGraphic(resizeMin);
				btnMax.setTooltip(new Tooltip("Restore Down"));
			}else{
				btnMax.setGraphic(resizeMax);
				btnMax.setTooltip(new Tooltip("Maximize"));
			}
		});
		btnMax.setGraphic(resizeMax);
		
		
		buttonsContainer = new HBox();
		buttonsContainer.getStyleClass().add("jfx-decorator-buttons-container");
		buttonsContainer.backgroundProperty().bind(Bindings.createObjectBinding(()->{
			return new Background(new BackgroundFill(decoratorColor.get(), CornerRadii.EMPTY, Insets.EMPTY));
		}, decoratorColor));
		buttonsContainer.setPadding(new Insets(4));		
		buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
		// customize decorator buttons
		List<JFXButton> btns = new ArrayList<>();
		if(fullScreen) {
			btns.add(btnFull);
			// maximize/restore the window on header double click
			buttonsContainer.setOnMouseClicked((mouseEvent)->{ if(mouseEvent.getClickCount() == 2) btnMax.fire(); });
		}
		if(min) btns.add(btnMin);
		if(max) btns.add(btnMax);
		btns.add(btnClose);
		
		buttonsContainer.getChildren().addAll(btns);
		buttonsContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, (enter)->allowMove = true);
		buttonsContainer.addEventHandler(MouseEvent.MOUSE_EXITED, (enter)->{ if(!isDragging) allowMove = false;});
		buttonsContainer.setMinWidth(180);		
		contentPlaceHolder.setMinSize(0, 0);
		((Region)node).setMinSize(0, 0);
		((Region)node).minWidthProperty().addListener((o,oldVal,newVal)->{
			
		});
		
		contentPlaceHolder.getChildren().add(node);
		VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);
		contentPlaceHolder.borderProperty().bind(Bindings.createObjectBinding(()->{
			return new Border(new BorderStroke(decoratorColor.get(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 4, 4, 4)));
		}, decoratorColor));
		contentPlaceHolder.getStyleClass().add("resize-border");
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(((Region)node).widthProperty());
		clip.heightProperty().bind(((Region)node).heightProperty());
		node.setClip(clip);
		this.getChildren().addAll(buttonsContainer,contentPlaceHolder);

		
		
		primaryStage.fullScreenProperty().addListener((o,oldVal,newVal)->{
			if(newVal){
				// remove border
				contentPlaceHolder.getStyleClass().remove("resize-border");
				contentPlaceHolder.borderProperty().unbind();
				contentPlaceHolder.setBorder(Border.EMPTY);
				if(windowDecoratorAnimation!=null)windowDecoratorAnimation.stop();
				windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320), new KeyValue(this.translateYProperty(), -buttonsContainer.getHeight(), Interpolator.EASE_BOTH )));
				windowDecoratorAnimation.setOnFinished((finish)->{
					this.getChildren().remove(buttonsContainer);
					this.setTranslateY(0);
				});
				windowDecoratorAnimation.play();
			}else{
				// add border
				if(windowDecoratorAnimation!=null){
					if(windowDecoratorAnimation.getStatus().equals(Animation.Status.RUNNING)) windowDecoratorAnimation.stop();	
					else this.getChildren().add(0,buttonsContainer);							
				}
				this.setTranslateY(-buttonsContainer.getHeight());
				windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),new KeyValue(this.translateYProperty(), 0, Interpolator.EASE_BOTH)));
				windowDecoratorAnimation.setOnFinished((finish)->{
					contentPlaceHolder.borderProperty().bind(Bindings.createObjectBinding(()->{
						return new Border(new BorderStroke(decoratorColor.get(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 4, 4, 4)));
					}, decoratorColor));
					contentPlaceHolder.getStyleClass().add("resize-border");	
				});
				windowDecoratorAnimation.play();
			}
		});	
		
		
		
//		primaryStage.maximizedProperty().addListener((o,oldVal,newVal)->{
//			if(newVal){
//				resizeBorder = this.getBorder();
//				this.setBorder(Border.EMPTY);				
//			}else{
//				this.setBorder(resizeBorder);
//			}
//		});		
//		this.setPadding(new Insets(0,0,0,0));
		
		// clip the node in case it has hidden child nodes outside it's bounds (e.g drawer pane)
//		Rectangle clip = new Rectangle();
//		clip.widthProperty().bind(Bindings.createDoubleBinding(()->((Region)node).getWidth(), ((Region)node).widthProperty()));
//		clip.heightProperty().bind(Bindings.createDoubleBinding(()->((Region)node).getHeight(), ((Region)node).heightProperty()));
//		node.setClip(clip);
//		node.getStyleClass().add("jfx-decorator-content");
//		node.setStyle("-fx-border-color:RED;");
		
		// save the mouse pressed position when clicking on the decorator pane
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
			initX = mouseEvent.getScreenX();
			initY = mouseEvent.getScreenY();
			xOffset = mouseEvent.getSceneX();
			yOffset = mouseEvent.getSceneY();
		});


		// show the drag cursor on the borders
		this.setOnMouseMoved((mouseEvent)->{
			if (primaryStage.isMaximized() || primaryStage.isFullScreen()) {
				this.setCursor(Cursor.DEFAULT);
				return; // maximized mode does not support resize
			}
			if (!primaryStage.isResizable()) {
				return;
			}
			double x = mouseEvent.getX();
			double y = mouseEvent.getY();
			Bounds boundsInParent = this.getBoundsInParent();
			if(contentPlaceHolder.getBorder()!=null && contentPlaceHolder.getBorder().getStrokes().size() > 0){
				double borderWidth = contentPlaceHolder.getBorder().getStrokes().get(0).getWidths().getLeft();
				if (isRightEdge(x, y, boundsInParent)) {
					if (y < borderWidth) {
						this.setCursor(Cursor.NE_RESIZE);
					} else if (y > this.getHeight() - (double) (borderWidth)) {
						this.setCursor(Cursor.SE_RESIZE);
					} else {
						this.setCursor( Cursor.E_RESIZE);
					}
				} else if (isLeftEdge(x, y, boundsInParent)) {
					if (y < borderWidth) {
						this.setCursor(Cursor.NW_RESIZE);
					} else if (y > this.getHeight() - (double) (borderWidth)) {
						this.setCursor( Cursor.SW_RESIZE);
					} else {
						this.setCursor(Cursor.W_RESIZE);
					}
				} else if (isTopEdge(x, y, boundsInParent)) {
					this.setCursor( Cursor.N_RESIZE);
				} else if (isBottomEdge(x, y, boundsInParent)) {
					this.setCursor(Cursor.S_RESIZE);
				} else {
					this.setCursor(Cursor.DEFAULT);
				}	
			}
		});


		// handle drag events on the decorator pane
		this.setOnMouseReleased((mouseEvent)-> isDragging = false);

		this.setOnMouseDragged((mouseEvent)->{
			isDragging = true;
			if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
				return;
			}
			/*
			 * Long press generates drag event!
			 */
			if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || primaryStage.isMaximized()) {
				return;
			}

			newX = mouseEvent.getScreenX();
			newY = mouseEvent.getScreenY();

			double deltax = newX - initX;
			double deltay = newY - initY;
			Cursor cursor = this.getCursor();

			if (Cursor.E_RESIZE.equals(cursor)) {
				setStageWidth(primaryStage.getWidth() + deltax);
				mouseEvent.consume();
			} else if (Cursor.NE_RESIZE.equals(cursor)) {
				if (setStageHeight(primaryStage.getHeight() - deltay)) {
					primaryStage.setY(primaryStage.getY() + deltay);
				}
				setStageWidth(primaryStage.getWidth() + deltax);
				mouseEvent.consume();
			} else if (Cursor.SE_RESIZE.equals(cursor)) {
				setStageWidth(primaryStage.getWidth() + deltax);
				setStageHeight( primaryStage.getHeight() + deltay);
				mouseEvent.consume();
			} else if (Cursor.S_RESIZE.equals(cursor)) {
				setStageHeight( primaryStage.getHeight() + deltay);
				mouseEvent.consume();
			} else if (Cursor.W_RESIZE.equals(cursor)) {
				if (setStageWidth( primaryStage.getWidth() - deltax)) {
					primaryStage.setX(primaryStage.getX() + deltax);
				}
				mouseEvent.consume();
			} else if (Cursor.SW_RESIZE.equals(cursor)) {
				if (setStageWidth(primaryStage.getWidth() - deltax)) {
					primaryStage.setX(primaryStage.getX() + deltax);
				}
				setStageHeight( primaryStage.getHeight() + deltay);
				mouseEvent.consume();
			} else if (Cursor.NW_RESIZE.equals(cursor)) {
				if (setStageWidth( primaryStage.getWidth() - deltax)) {
					primaryStage.setX(primaryStage.getX() + deltax);
				}
				if (setStageHeight( primaryStage.getHeight() - deltay)) {
					primaryStage.setY(primaryStage.getY() + deltay);
				}
				mouseEvent.consume();
			} else if (Cursor.N_RESIZE.equals(cursor)) {
				if (setStageHeight( primaryStage.getHeight() - deltay)) {
					primaryStage.setY(primaryStage.getY() + deltay);
				}
				mouseEvent.consume();
			}else if(allowMove){
				primaryStage.setX(mouseEvent.getScreenX() - xOffset);
				primaryStage.setY(mouseEvent.getScreenY() - yOffset);
				mouseEvent.consume();
			}
		});
	}


	private boolean isRightEdge(double x, double y, Bounds boundsInParent) {
		if (x < this.getWidth() && x > this.getWidth() - contentPlaceHolder.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	private boolean isTopEdge(double x, double y, Bounds boundsInParent) {
		if (y >= 0 && y < contentPlaceHolder.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	private boolean isBottomEdge(double x, double y, Bounds boundsInParent) {
		if (y < this.getHeight() && y > this.getHeight() - contentPlaceHolder.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	private boolean isLeftEdge(double x, double y, Bounds boundsInParent) {
		if (x >= 0 && x < contentPlaceHolder.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	boolean setStageWidth( double width) {
		if (width >= primaryStage.getMinWidth() && width >= buttonsContainer.getMinWidth()) {
			primaryStage.setWidth(width);
			initX = newX;
			return true;
		}else if( width >= primaryStage.getMinWidth() && width <= buttonsContainer.getMinWidth() ){
			width = buttonsContainer.getMinWidth();
			primaryStage.setWidth(width);
		}
		return false;
	}
	boolean setStageHeight(double height) {
		if (height >= primaryStage.getMinHeight() && height >= buttonsContainer.getHeight()) {			
			primaryStage.setHeight(height);
			initY = newY;
			return true;
		}else if(height >= primaryStage.getMinHeight() && height <= buttonsContainer.getHeight()){
			height = buttonsContainer.getHeight();
			primaryStage.setHeight(height);
		}
		return false;
	}

	public final ObjectProperty<Color> buttonsColorProperty() {
		return this.buttonsColor;
	}

	public final Color getButtonsColor() {
		return this.buttonsColorProperty().get();
	}

	public final void setButtonsColor(final Color buttonsColor) {
		this.buttonsColorProperty().set(buttonsColor);
	}

	public final ObjectProperty<Color> decoratorColorProperty() {
		return this.decoratorColor;
	}

	public final Color getDecoratorColor() {
		return this.decoratorColorProperty().get();
	}

	public final void setDecoratorColor(final Color decoratorColor) {
		this.decoratorColorProperty().set(decoratorColor);
	}
	
	public void setOnCloseButtonAction(Runnable onCloseButtonAction) {
		this.onCloseButtonAction.set(onCloseButtonAction);
	}


} 