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

import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;


/**
 * @author sshahine
 * this class is been re implemented in the main controller and its fxml, 
 * if not used later , it can be deleted or moved to C3DFX project
 * 
 * this class is not been tested or shown in the demo
 * FUTURE WORK, TODO LIST 
 */


public class C3DDecorator extends VBox {

	private double xOffset = 0;
	private double yOffset = 0;
	private Stage primaryStage;
	private double newX;
	private double newY;
	private double initX;
	private double initY;
	private Border resizeBorder = Border.EMPTY;

	public C3DDecorator(Stage stage, Node node) {
		super();
		this.getStyleClass().add("c3d-decorator-window");

		primaryStage = stage;
		primaryStage.maximizedProperty().addListener((o,oldVal,newVal)->{
			if(newVal){
				resizeBorder = this.getBorder();
				this.setBorder(Border.EMPTY);				
			}else{
				this.setBorder(resizeBorder);
			}
		});
		
		
		this.setPadding(new Insets(0,0,0,0));

		C3DButton btnMax = new C3DButton();
		btnMax.setGraphic(new Icon(AwesomeIcon.MINUS,"15px",";","icon"));
		btnMax.setOnAction((action)->primaryStage.setMaximized(!primaryStage.isMaximized()));
				
		Button btnClose = new C3DButton();
		btnClose.setGraphic(new Icon(AwesomeIcon.CLOSE,"15px",";","icon"));
		btnClose.setOnAction((action)->primaryStage.close());
		
		HBox buttonContainer = new HBox();
		buttonContainer.prefWidthProperty().bind(this.widthProperty());
		buttonContainer.getChildren().addAll(btnMax,btnClose);
		buttonContainer.setAlignment(Pos.CENTER_RIGHT);
		buttonContainer.getStyleClass().add("c3d-decorator-buttons-holder");
		
		
		// clip the node in case it has hidden child nodes outside it's bounds (e.g drawer pane)
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(Bindings.createDoubleBinding(()->((Region)node).getWidth(), ((Region)node).widthProperty()));
		clip.heightProperty().bind(Bindings.createDoubleBinding(()->((Region)node).getHeight(), ((Region)node).heightProperty()));
		node.setClip(clip);

		node.getStyleClass().add("c3d-decorator-content");
//		node.setStyle("-fx-border-color:RED;");
		
		this.getChildren().addAll(buttonContainer,node);

		// save the mouse pressed position when clicking on the decorator pane
		this.setOnMousePressed((mouseEvent) -> {
			initX = mouseEvent.getScreenX();
			initY = mouseEvent.getScreenY();
			xOffset = mouseEvent.getSceneX();
			yOffset = mouseEvent.getSceneY();
		});


		// show the drag cursor on the borders
		this.setOnMouseMoved((mouseEvent)->{
			if (stage.isMaximized()) {
				this.setCursor(Cursor.DEFAULT);
				return; // maximized mode does not support resize
			}
			if (stage.isFullScreen()) {
				return;
			}
			if (!stage.isResizable()) {
				return;
			}
			double x = mouseEvent.getX();
			double y = mouseEvent.getY();
			Bounds boundsInParent = node.getBoundsInParent();
//			if(this.getBorder()!=null && this.getBorder().getStrokes().size() > 0){
				double borderWidth = 4;
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
//			}
		});


		// handle drag events on the decorator pane
		this.setOnMouseDragged((mouseEvent)->{
			if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
				return;
			}
			if (stage.isFullScreen()) {
				return;
			}
			/*
			 * Long press generates drag event!
			 */
			if (mouseEvent.isStillSincePress()) {
				return;
			}
			if (primaryStage.isMaximized()) {				
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
			}else {
				primaryStage.setX(mouseEvent.getScreenX() - xOffset);
				primaryStage.setY(mouseEvent.getScreenY() - yOffset);
			}
//			((Region)node).requestLayout();
		});
	}


	private boolean isRightEdge(double x, double y, Bounds boundsInParent) {
		if (x < this.getWidth() && x > this.getWidth() - this.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	private boolean isTopEdge(double x, double y, Bounds boundsInParent) {
		if (y >= 0 && y < this.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	private boolean isBottomEdge(double x, double y, Bounds boundsInParent) {
		if (y < this.getHeight() && y > this.getHeight() - this.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}
	private boolean isLeftEdge(double x, double y, Bounds boundsInParent) {
		if (x >= 0 && x < this.getBorder().getStrokes().get(0).getWidths().getLeft()) {
			return true;
		}
		return false;
	}

	boolean setStageWidth( double width) {
		if (width >= primaryStage.getMinWidth()) {
			primaryStage.setWidth(width);
			initX = newX;
			return true;
		}
		return false;
	}
	boolean setStageHeight(double height) {
		if (height >= primaryStage.getMinHeight()) {
			primaryStage.setHeight(height);
			initY = newY;
			return true;
		}
		return false;
	}


} 