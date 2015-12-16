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

package com.jfoenix.skins;

import java.util.ArrayList;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.CachedTransition;


/**
 * @author sshahine
 *
 */

class JFXCustomColorPicker extends Pane {

	ObjectProperty<RecentColorPath> selectedPath = new SimpleObjectProperty<>();
	private MoveTo startPoint;
	private CubicCurveTo curve0To;
	private CubicCurveTo outerCircleCurveTo;
	private CubicCurveTo curve1To;
	private CubicCurveTo innerCircleCurveTo;
	private ArrayList<CubicCurve> curves = new ArrayList<>();

	private double distance=200;
	private double centerX = distance;
	private double centerY = distance;
	private double radius = 110;

	private int shapesNumber = 13;
	private ArrayList<RecentColorPath> shapes = new ArrayList<>();
	private CachedTransition showAnimation;
	private JFXColorPickerUI hslColorPicker;

	public JFXCustomColorPicker(){
		this.setPickOnBounds(false);
		this.setMinSize(distance*2, distance*2);

		DoubleProperty rotationAngle = new SimpleDoubleProperty(2.1);

		// draw recent colors shape using cubic curves
		init(rotationAngle, centerX + 53 ,centerY + 162);

		hslColorPicker = new JFXColorPickerUI((int) distance);
		hslColorPicker.setLayoutX(centerX - distance/2);
		hslColorPicker.setLayoutY(centerY - distance/2);
		this.getChildren().add(hslColorPicker);

		// add recent colors shapes
		int shapesStartIndex = this.getChildren().size();
		int shapesEndIndex = shapesStartIndex + shapesNumber;
		for (int i = 0 ; i < shapesNumber; i++) {
			double angle = 2 * i * Math.PI / shapesNumber ;
			RecentColorPath path = new RecentColorPath(startPoint, curve0To, outerCircleCurveTo, curve1To, innerCircleCurveTo);
			shapes.add(path);
			path.setPickOnBounds(false);
			Rotate rotate = new Rotate(Math.toDegrees(angle), centerX, centerY);
			path.getTransforms().add(rotate);
			this.getChildren().add(shapesStartIndex, path);
			path.setFill(Color.valueOf(getDefaultColor(i)));
			path.addEventHandler(MouseEvent.MOUSE_CLICKED, (event)-> selectedPath.set(path));
		}

		// add selection listeners
		selectedPath.addListener((o,oldVal,newVal)->{
			if(oldVal!=null){
				hslColorPicker.removeColorSelectionNode(oldVal);
				oldVal.playTransition(-1);
			}
			// re-arrange children
			while(this.getChildren().indexOf(newVal) != shapesEndIndex - 1){
				Node temp = this.getChildren().get(shapesEndIndex-1);
				this.getChildren().remove(shapesEndIndex-1);
				this.getChildren().add(shapesStartIndex, temp);
			}
			// update path fill according to the color picker
			newVal.setStroke(Color.rgb(255, 255, 255, 0.87));
			newVal.playTransition(1);
			hslColorPicker.moveToColor((Color) newVal.getFill());
			hslColorPicker.addColorSelectionNode(newVal);
		});
		// init selection
		selectedPath.set((RecentColorPath) this.getChildren().get(shapesStartIndex));


		//		JFXSlider slider = new JFXSlider(-Math.PI, Math.PI, 2.10);
		//		slider.setIndicatorPosition(IndicatorPosition.RIGHT);
		//		rotationAngle.bind(slider.valueProperty());
		//
		//		VBox info = new VBox();
		//		Label startX = new Label();
		//		startX.textProperty().bind(Bindings.createStringBinding(()->"StartX : " + curves.get(0).getControlX1(), curves.get(0).controlX1Property()));
		//		Label startY = new Label();
		//		startY.textProperty().bind(Bindings.createStringBinding(()->"startY : " + curves.get(0).getControlY1(), curves.get(0).controlY1Property()));
		//		Label endX = new Label();
		//		endX.textProperty().bind(Bindings.createStringBinding(()->"endX : " + curves.get(0).getControlX2(), curves.get(0).controlX2Property()));
		//		Label endY = new Label();
		//		endY.textProperty().bind(Bindings.createStringBinding(()->"endY : " + curves.get(0).getControlY2(), curves.get(0).controlY2Property()));
		//		Label rotation = new Label();
		//		rotation.textProperty().bind(Bindings.createStringBinding(()->"rotation : " + rotationAngle.get(), rotationAngle));
		//		info.getChildren().add(startX);
		//		info.getChildren().add(startY);
		//		info.getChildren().add(endX);
		//		info.getChildren().add(endY);
		//		info.getChildren().add(rotation);    
		//
		//		VBox container = new VBox();
		//		container.getChildren().add(info);
		//		container.getChildren().add(slider);
		//		container.getChildren().add(pane);


		//		Line controlLine1 = new BoundLine(curves.get(0).controlX1Property(), curves.get(0).controlY1Property(), curves.get(0).startXProperty(), curves.get(0).startYProperty());
		//	    Line controlLine2 = new BoundLine(curves.get(0).controlX2Property(), curves.get(0).controlY2Property(), curves.get(0).endXProperty(),   curves.get(0).endYProperty());
		//
		//	    Anchor control1 = new Anchor(Color.GOLD,      curves.get(0).controlX1Property(), curves.get(0).controlY1Property());
		//	    Anchor control2 = new Anchor(Color.GOLDENROD, curves.get(0).controlX2Property(), curves.get(0).controlY2Property());
		//	    Anchor start    = new Anchor(Color.PALEGREEN, curves.get(0).startXProperty(),    curves.get(0).startYProperty());
		//	    Anchor end      = new Anchor(Color.TOMATO,    curves.get(0).endXProperty(),      curves.get(0).endYProperty());
		//	    pane.getChildren().addAll(control1);

		//		curves.get(0).setControlX1(curves.get(0).getControlX2());
		//		curves.get(0).setControlY1(curves.get(0).getControlY2());
		//		rotationAngle.set(0);
		//		
		//		new Timeline(new KeyFrame(Duration.millis(2000),
		//				new KeyValue(curves.get(0).controlX1Property(), x, Interpolator.EASE_BOTH),
		//				new KeyValue(curves.get(0).controlY1Property(), y, Interpolator.EASE_BOTH),
		//				new KeyValue(rotationAngle, 2.1, Interpolator.EASE_BOTH)
		//				)).play();

		//    for (int i = 0 ; i < numMoons; i++) {
		//    	double angle = 2 * i * Math.PI / numMoons ;
		//    	
		//    	StackPane shapeContainer = new StackPane();
		//    	shapeContainer.setLayoutX(300);
		//    	shapeContainer.setLayoutY(300);
		////    	double xOffset = distance * Math.cos(angle);
		////        double yOffset = distance * Math.sin(angle);
		////        final double startx = centerX + xOffset ;
		////        final double starty = centerY + yOffset ;
		//        
		//    	shapeContainer.setShape(path);
		////    	shapeContainer.maxWidthProperty().bind(Bindings.createDoubleBinding(()-> path.getLayoutBounds().getWidth(), path.layoutBoundsProperty()));
		////    	shapeContainer.maxHeightProperty().bind(Bindings.createDoubleBinding(()-> path.getLayoutBounds().getHeight(), path.layoutBoundsProperty()));
		//    	shapeContainer.minWidthProperty().bind(Bindings.createDoubleBinding(()-> path.getLayoutBounds().getWidth(), path.layoutBoundsProperty()));
		//    	shapeContainer.minHeightProperty().bind(Bindings.createDoubleBinding(()-> path.getLayoutBounds().getHeight(), path.layoutBoundsProperty()));
		//    	JFXDepthManager.setDepth(shapeContainer, 1);
		//
		//    	Rotate rotate = new Rotate(Math.toDegrees(angle), 45, 0);
		//    	shapeContainer.getTransforms().add(rotate);
		//    	shapeContainer.widthProperty().addListener((o,oldVal,newVal)->{
		//    		((Rotate)shapeContainer.getTransforms().get(0)).setPivotX(newVal.doubleValue()/5.4653);
		//    	});
		//    	
		//    	colorPicker.getChildren().add(shapeContainer);
		//    	
		//    	switch (i) {
		//		case 0:
		//			shapeContainer.setStyle("-fx-background-color:#8F3F7E");
		//			break;
		//		case 1:
		//			shapeContainer.setStyle("-fx-background-color:#B5305F");
		//			break;
		//		case 2:
		//			shapeContainer.setStyle("-fx-background-color:#CE584A");
		//			break;
		//		case 3:
		//			shapeContainer.setStyle("-fx-background-color:#DB8D5C");
		//			break;
		//		case 4:
		//			shapeContainer.setStyle("-fx-background-color:#DA854E;");
		//			break;
		//		case 5:
		//			shapeContainer.setStyle("-fx-background-color:#E9AB44;");
		//			break;
		//		case 6:
		//			shapeContainer.setStyle("-fx-background-color:#FEE435");
		//			break;
		//		case 7:
		//			shapeContainer.setStyle("-fx-background-color:#99C286");
		//			break;
		//		case 8:
		//			shapeContainer.setStyle("-fx-background-color:#01A05E");
		//			break;
		//		case 9:
		//			shapeContainer.setStyle("-fx-background-color:#4A8895");
		//			break;
		//		case 10:
		//			shapeContainer.setStyle("-fx-background-color:#16669B");
		//			break;
		//		case 11:
		//			shapeContainer.setStyle("-fx-background-color:#2F65A5");
		//			break;
		//		case 12:
		//			shapeContainer.setStyle("-fx-background-color:#4E6A9C");
		//			break;
		//		default:
		//			break;
		//		}
		////    	
		////    	if(i  > 0 ){
		////    		rotate.pivotXProperty().bind(((Rotate)colorPicker.getChildren().get(1).getTransforms().get(0)).pivotXProperty());
		////    		rotate.pivotYProperty().bind(((Rotate)colorPicker.getChildren().get(1).getTransforms().get(0)).pivotYProperty());
		////    		shapeContainer.setMouseTransparent(true);
		////    	}else if( i == 0){
		////    		shapeContainer.setStyle("-fx-background-color:blue; -fx-border-color:RED;");
		////    		shapeContainer.setOnMouseMoved((move)->{
		////    			System.out.println(move.getX() + " , " + move.getY());
		////    			rotate.setPivotX(move.getX());
		////    			rotate.setPivotY(move.getY());
		////    		});
		////    	}
		////    	
		//    	
		//    	
		//    	
		//    	
		//    	
		//        
		//    	
		//    }

		//	container.getChildren().add(colorPicker);
		//	VBox.setMargin(colorPicker, new Insets(250,0,0,250));

		//		stage.setTitle("Cubic Curve Manipulation Sample");
		//		stage.setScene(new Scene(pane, 700, 700, Color.ALICEBLUE));
		//		stage.show();

		//    ScenicView.show(stage.getScene());

	}


	public int getShapesNumber(){
		return shapesNumber;
	}

	public int getSelectedIndex(){
		if(selectedPath.get()!=null)
			return shapes.indexOf(selectedPath.get());
		return -1;
	}

	public void setColor(Color color){
		shapes.get(getSelectedIndex()).setFill(color);
		hslColorPicker.moveToColor(color);	
	}
	
	public Color getColor(int index){
		if(index < shapes.size() && index >= 0) return (Color) shapes.get(index).getFill();
		return Color.WHITE;
	}

	
	public void preAnimate(){
		double x  = curves.get(0).getStartX();
		double y  = curves.get(0).getStartY();
		curves.get(0).setStartX(centerX);
		curves.get(0).setStartY(centerY);

		double x1  = curves.get(1).getStartX();
		double y1  = curves.get(1).getStartY();
		curves.get(1).setStartX(centerX);
		curves.get(1).setStartY(centerY);

		double cx1 = curves.get(0).getControlX1();
		double cy1 = curves.get(0).getControlY1();
		curves.get(0).setControlX1(centerX + radius);
		curves.get(0).setControlY1(centerY + radius/2);
		
		showAnimation = new CachedTransition(this, new Timeline(new KeyFrame(Duration.millis(1000),
				new KeyValue(curves.get(0).startXProperty(),x, Interpolator.EASE_BOTH),
				new KeyValue(curves.get(0).startYProperty(),y, Interpolator.EASE_BOTH),
				new KeyValue(curves.get(1).startXProperty(),x1, Interpolator.EASE_BOTH),
				new KeyValue(curves.get(1).startYProperty(),y1, Interpolator.EASE_BOTH),
				new KeyValue(curves.get(0).controlX1Property(),cx1, Interpolator.EASE_BOTH),
				new KeyValue(curves.get(0).controlY1Property(),cy1, Interpolator.EASE_BOTH)
				))){{
					setCycleDuration(Duration.millis(240));
					setDelay(Duration.millis(0));
				}};
	}
	
	public void animate() {
		showAnimation.play();
	}

	private void init(DoubleProperty rotationAngle, double initControlX1, double initControlY1){

		Circle innerCircle = new Circle(centerX, centerY, radius, Color.TRANSPARENT);
		Circle outerCircle = new Circle(centerX, centerY, radius*2, Color.web("blue", 0.5));

		// Create a composite shape of 4 cubic curves
		// create 2 cubic curves of the shape
		createQuadraticCurve(rotationAngle, initControlX1 ,initControlY1);

		// inner circle curve
		CubicCurve innerCircleCurve = new CubicCurve();
		innerCircleCurve.startXProperty().bind(curves.get(0).startXProperty());
		innerCircleCurve.startYProperty().bind(curves.get(0).startYProperty());
		innerCircleCurve.endXProperty().bind(curves.get(1).startXProperty());
		innerCircleCurve.endYProperty().bind(curves.get(1).startYProperty());
		curves.get(0).startXProperty().addListener((o,oldVal,newVal)->{
			Point2D controlPoint = makeControlPoint(newVal.doubleValue(), curves.get(0).getStartY(), innerCircle, shapesNumber, -1);
			innerCircleCurve.setControlX1(controlPoint.getX());
			innerCircleCurve.setControlY1(controlPoint.getY());	
		});
		curves.get(0).startYProperty().addListener((o,oldVal,newVal)->{
			Point2D controlPoint = makeControlPoint(curves.get(0).getStartX(), newVal.doubleValue(), innerCircle, shapesNumber, -1);
			innerCircleCurve.setControlX1(controlPoint.getX());
			innerCircleCurve.setControlY1(controlPoint.getY());	
		});
		curves.get(1).startXProperty().addListener((o,oldVal,newVal)->{
			Point2D controlPoint = makeControlPoint(newVal.doubleValue(), curves.get(1).getStartY(), innerCircle, shapesNumber, 1);
			innerCircleCurve.setControlX2(controlPoint.getX());
			innerCircleCurve.setControlY2(controlPoint.getY());
		});
		curves.get(1).startYProperty().addListener((o,oldVal,newVal)->{
			Point2D controlPoint = makeControlPoint(curves.get(1).getStartX(), newVal.doubleValue(), innerCircle, shapesNumber, 1);
			innerCircleCurve.setControlX2(controlPoint.getX());
			innerCircleCurve.setControlY2(controlPoint.getY());
		});
		Point2D controlPoint = makeControlPoint(curves.get(0).getStartX(), curves.get(0).getStartY(), innerCircle, shapesNumber, -1);
		innerCircleCurve.setControlX1(controlPoint.getX());
		innerCircleCurve.setControlY1(controlPoint.getY());
		controlPoint = makeControlPoint(curves.get(1).getStartX(), curves.get(1).getStartY(), innerCircle, shapesNumber, 1);
		innerCircleCurve.setControlX2(controlPoint.getX());
		innerCircleCurve.setControlY2(controlPoint.getY());
		//		innerCircleCurve.setStroke(Color.FORESTGREEN);
		//		innerCircleCurve.setStrokeWidth(1);
		//		innerCircleCurve.setStrokeLineCap(StrokeLineCap.ROUND);
		//		innerCircleCurve.setFill(Color.TRANSPARENT);
		//		innerCircleCurve.setMouseTransparent(true);
		//		pane.getChildren().add(new Group(  innerCircleCurve));

		// outter circle curve
		CubicCurve outerCircleCurve = new CubicCurve();
		outerCircleCurve.startXProperty().bind(curves.get(0).endXProperty());
		outerCircleCurve.startYProperty().bind(curves.get(0).endYProperty());
		outerCircleCurve.endXProperty().bind(curves.get(1).endXProperty());
		outerCircleCurve.endYProperty().bind(curves.get(1).endYProperty());
		controlPoint = makeControlPoint(curves.get(0).getEndX(), curves.get(0).getEndY(), outerCircle, shapesNumber, -1);
		outerCircleCurve.setControlX1(controlPoint.getX());
		outerCircleCurve.setControlY1(controlPoint.getY());
		controlPoint = makeControlPoint(curves.get(1).getEndX(), curves.get(1).getEndY(), outerCircle, shapesNumber, 1);
		outerCircleCurve.setControlX2(controlPoint.getX());
		outerCircleCurve.setControlY2(controlPoint.getY());
		//		outerCircleCurve.setStroke(Color.FORESTGREEN);
		//		outerCircleCurve.setStrokeWidth(1);
		//		outerCircleCurve.setStrokeLineCap(StrokeLineCap.ROUND);
		//		outerCircleCurve.setFill(Color.TRANSPARENT);
		//		outerCircleCurve.setMouseTransparent(true);
		//		pane.getChildren().add(new Group(outerCircleCurve));



		startPoint = new MoveTo();
		startPoint.xProperty().bind(curves.get(0).startXProperty());
		startPoint.yProperty().bind(curves.get(0).startYProperty());

		curve0To = new CubicCurveTo();
		curve0To.controlX1Property().bind(curves.get(0).controlX1Property());
		curve0To.controlY1Property().bind(curves.get(0).controlY1Property());
		curve0To.controlX2Property().bind(curves.get(0).controlX2Property());
		curve0To.controlY2Property().bind(curves.get(0).controlY2Property());
		curve0To.xProperty().bind(curves.get(0).endXProperty());
		curve0To.yProperty().bind(curves.get(0).endYProperty());

		outerCircleCurveTo = new CubicCurveTo();
		outerCircleCurveTo.controlX1Property().bind(outerCircleCurve.controlX1Property());
		outerCircleCurveTo.controlY1Property().bind(outerCircleCurve.controlY1Property());
		outerCircleCurveTo.controlX2Property().bind(outerCircleCurve.controlX2Property());
		outerCircleCurveTo.controlY2Property().bind(outerCircleCurve.controlY2Property());
		outerCircleCurveTo.xProperty().bind(outerCircleCurve.endXProperty());
		outerCircleCurveTo.yProperty().bind(outerCircleCurve.endYProperty());

		curve1To = new CubicCurveTo();
		curve1To.controlX1Property().bind(curves.get(1).controlX2Property());
		curve1To.controlY1Property().bind(curves.get(1).controlY2Property());
		curve1To.controlX2Property().bind(curves.get(1).controlX1Property());
		curve1To.controlY2Property().bind(curves.get(1).controlY1Property());
		curve1To.xProperty().bind(curves.get(1).startXProperty());
		curve1To.yProperty().bind(curves.get(1).startYProperty());

		innerCircleCurveTo = new CubicCurveTo();
		innerCircleCurveTo.controlX1Property().bind(innerCircleCurve.controlX2Property());
		innerCircleCurveTo.controlY1Property().bind(innerCircleCurve.controlY2Property());
		innerCircleCurveTo.controlX2Property().bind(innerCircleCurve.controlX1Property());
		innerCircleCurveTo.controlY2Property().bind(innerCircleCurve.controlY1Property());
		innerCircleCurveTo.xProperty().bind(innerCircleCurve.startXProperty());
		innerCircleCurveTo.yProperty().bind(innerCircleCurve.startYProperty());
	}


	private void createQuadraticCurve(DoubleProperty rotationAngle, double initControlX1, double initControlY1) {

		for (int i = 0 ; i < 2; i++) {

			double angle = 2 * i * Math.PI / shapesNumber ;
			double xOffset = radius * Math.cos(angle);
			double yOffset = radius * Math.sin(angle);
			final double startx = centerX + xOffset ;
			final double starty = centerY + yOffset ;

			double startXR = Math.cos(rotationAngle.get()) * (startx - centerX) - Math.sin(rotationAngle.get())*(starty-centerY) + centerX;
			double startYR = Math.sin(rotationAngle.get()) * (startx - centerX) + Math.cos(rotationAngle.get())*(starty-centerY) + centerY;

			angle = 2 * i * Math.PI / shapesNumber ;
			xOffset = distance * Math.cos(angle);
			yOffset = distance * Math.sin(angle);

			double endx = centerX + xOffset ;
			double endy = centerY + yOffset ;

			CubicCurve curvedLine = new CubicCurve();
			curvedLine.setStartX(startXR);
			curvedLine.setStartY(startYR);
			curvedLine.setControlX1(startXR);
			curvedLine.setControlY1(startYR);
			curvedLine.setControlX2(endx);
			curvedLine.setControlY2(endy);
			curvedLine.setEndX(endx);
			curvedLine.setEndY(endy);
			curvedLine.setStroke(Color.FORESTGREEN);
			curvedLine.setStrokeWidth(1);
			curvedLine.setStrokeLineCap(StrokeLineCap.ROUND);
			curvedLine.setFill(Color.TRANSPARENT);
			curvedLine.setMouseTransparent(true);
			rotationAngle.addListener((o,oldVal,newVal)->{
				double newstartXR = Math.cos(rotationAngle.get()) * (startx - centerX) - Math.sin(rotationAngle.get())*(starty-centerY) + centerX;
				double newstartYR = Math.sin(rotationAngle.get()) * (startx - centerX) + Math.cos(rotationAngle.get())*(starty-centerY) + centerY;
				curvedLine.setStartX(newstartXR);
				curvedLine.setStartY(newstartYR);
			});

			curves.add(curvedLine);

			if(i == 0){
				curvedLine.setControlX1(initControlX1);
				curvedLine.setControlY1(initControlY1);
			}else{
				curvedLine.controlX1Property().bind(Bindings.createDoubleBinding(()->{
					double curveTeta =  2 * curves.indexOf(curvedLine) * Math.PI / shapesNumber;
					return Math.cos(curveTeta) * (curves.get(0).getControlX1() - centerX) - Math.sin(curveTeta)*(curves.get(0).getControlY1()-centerY) + centerX;
				}, curves.get(0).controlX1Property(), curves.get(0).controlY1Property()));

				curvedLine.controlY1Property().bind(Bindings.createDoubleBinding(()->{
					double curveTeta =  2 * curves.indexOf(curvedLine) * Math.PI / shapesNumber;
					return Math.sin(curveTeta) * (curves.get(0).getControlX1() - centerX) + Math.cos(curveTeta)*(curves.get(0).getControlY1()-centerY) + centerY; 
				}, curves.get(0).controlX1Property(), curves.get(0).controlY1Property()));


				curvedLine.controlX2Property().bind(Bindings.createDoubleBinding(()->{
					double curveTeta =  2 * curves.indexOf(curvedLine) * Math.PI / shapesNumber;
					return Math.cos(curveTeta) * (curves.get(0).getControlX2() - centerX) - Math.sin(curveTeta)*(curves.get(0).getControlY2()-centerY) + centerX;
				}, curves.get(0).controlX2Property(), curves.get(0).controlY2Property()));

				curvedLine.controlY2Property().bind(Bindings.createDoubleBinding(()->{
					double curveTeta =  2 * curves.indexOf(curvedLine) * Math.PI / shapesNumber;
					return Math.sin(curveTeta) * (curves.get(0).getControlX2() - centerX) + Math.cos(curveTeta)*(curves.get(0).getControlY2()-centerY) + centerY;
				}, curves.get(0).controlX2Property(), curves.get(0).controlY2Property()));
			}
		}
	}

	private String getDefaultColor(int i) {
		String color = "#FFFFFF";
		switch (i) {
		case 0:
			color = "#8F3F7E";
			break;
		case 1:
			color = "#B5305F";
			break;
		case 2:
			color = "#CE584A";
			break;
		case 3:
			color = "#DB8D5C";
			break;
		case 4:
			color = "#DA854E";
			break;
		case 5:
			color = "#E9AB44";
			break;
		case 6:
			color = "#FEE435";
			break;
		case 7:
			color = "#99C286";
			break;
		case 8:
			color = "#01A05E";
			break;
		case 9:
			color = "#4A8895";
			break;
		case 10:
			color = "#16669B";
			break;
		case 11:
			color = "#2F65A5";
			break;
		case 12:
			color = "#4E6A9C";
			break;
		default:
			break;
		}
		return color;
	}


	//	class BoundLine extends Line {
	//		BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
	//			startXProperty().bind(startX);
	//			startYProperty().bind(startY);
	//			endXProperty().bind(endX);
	//			endYProperty().bind(endY);
	//			setStrokeWidth(2);
	//			setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
	//			setStrokeLineCap(StrokeLineCap.BUTT);
	//			getStrokeDashArray().setAll(10.0, 5.0);
	//		}
	//	}

	// a draggable anchor displayed around a point.
	//	class Anchor extends Circle { 
	//		Anchor(Color color, DoubleProperty x, DoubleProperty y) {
	//			super(x.get(), y.get(), 10);
	//			setFill(color.deriveColor(1, 1, 1, 0.5));
	//			setStroke(color);
	//			setStrokeWidth(2);
	//			setStrokeType(StrokeType.OUTSIDE);
	//
	//			x.bind(centerXProperty());
	//			y.bind(centerYProperty());
	//			enableDrag();
	//		}
	//
	//		// make a node movable by dragging it around with the mouse.
	//		private void enableDrag() {
	//			final Delta dragDelta = new Delta();
	//			setOnMousePressed(new EventHandler<MouseEvent>() {
	//				@Override public void handle(MouseEvent mouseEvent) {
	//					// record a delta distance for the drag and drop operation.
	//					dragDelta.x = getCenterX() - mouseEvent.getX();
	//					dragDelta.y = getCenterY() - mouseEvent.getY();
	//					getScene().setCursor(Cursor.MOVE);
	//				}
	//			});
	//			setOnMouseReleased(new EventHandler<MouseEvent>() {
	//				@Override public void handle(MouseEvent mouseEvent) {
	//					getScene().setCursor(Cursor.HAND);
	//				}
	//			});
	//			setOnMouseDragged(new EventHandler<MouseEvent>() {
	//				@Override public void handle(MouseEvent mouseEvent) {
	//					double newX = mouseEvent.getX() + dragDelta.x;
	//					if (newX > 0 && newX < getScene().getWidth()) {
	//						setCenterX(newX);
	//					}  
	//					double newY = mouseEvent.getY() + dragDelta.y;
	//					if (newY > 0 && newY < getScene().getHeight()) {
	//						setCenterY(newY);
	//					}  
	//				}
	//			});
	//			setOnMouseEntered(new EventHandler<MouseEvent>() {
	//				@Override public void handle(MouseEvent mouseEvent) {
	//					if (!mouseEvent.isPrimaryButtonDown()) {
	//						getScene().setCursor(Cursor.HAND);
	//					}
	//				}
	//			});
	//			setOnMouseExited(new EventHandler<MouseEvent>() {
	//				@Override public void handle(MouseEvent mouseEvent) {
	//					if (!mouseEvent.isPrimaryButtonDown()) {
	//						getScene().setCursor(Cursor.DEFAULT);
	//					}
	//				}
	//			});
	//		}
	//
	//		// records relative x and y co-ordinates.
	//		private class Delta { double x, y; }
	//	}  

	class RecentColorPath extends Path {
		PathClickTransition transition;
		public RecentColorPath(PathElement... elements) {
			super(elements);
			this.setStrokeLineCap(StrokeLineCap.ROUND);
			this.setStrokeWidth(0);
			this.setStrokeType(StrokeType.CENTERED);
			this.setCache(true);
			JFXDepthManager.setDepth(this, 2);
			this.transition = new PathClickTransition(this);
		}
		public void playTransition(double rate){
			transition.setRate(rate);
			transition.play();
		}
	}

	private class PathClickTransition extends CachedTransition {
		public PathClickTransition(Path path) {
			super(JFXCustomColorPicker.this, new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(((DropShadow)path.getEffect()).radiusProperty(), JFXDepthManager.getShadowAt(2).radiusProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)path.getEffect()).spreadProperty(), JFXDepthManager.getShadowAt(2).spreadProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)path.getEffect()).offsetXProperty(), JFXDepthManager.getShadowAt(2).offsetXProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)path.getEffect()).offsetYProperty(), JFXDepthManager.getShadowAt(2).offsetYProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(path.strokeWidthProperty(), 0, Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(1000),
									new KeyValue(((DropShadow)path.getEffect()).radiusProperty(), JFXDepthManager.getShadowAt(5).radiusProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)path.getEffect()).spreadProperty(), JFXDepthManager.getShadowAt(5).spreadProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)path.getEffect()).offsetXProperty(), JFXDepthManager.getShadowAt(5).offsetXProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)path.getEffect()).offsetYProperty(), JFXDepthManager.getShadowAt(5).offsetYProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(path.strokeWidthProperty(), 2, Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.millis(120));
			setDelay(Duration.seconds(0));
			setAutoReverse(false);
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Util methods	                                                           *
	 *                                                                         *
	 **************************************************************************/

	private Point2D rotate(Point2D a, Point2D center, double angle){
		double resultX = center.getX() + (a.getX() - center.getX())*Math.cos(angle) -  (a.getY() - center.getY())*Math.sin(angle);
		double resultY = center.getY() + (a.getX() - center.getX())*Math.sin(angle) +  (a.getY() - center.getY())*Math.cos(angle);
		return new Point2D(resultX,resultY);
	}

	private Point2D makeControlPoint(double endX,double endY,Circle circle, int numSegments,int direction){
		double controlPointDistance = (4.0/3.0) * Math.tan(Math.PI / (2*numSegments)) * circle.getRadius();
		Point2D center = new Point2D(circle.getCenterX(),circle.getCenterY());
		Point2D end = new Point2D(endX,endY);
		Point2D perp = rotate(center, end, direction*Math.PI/2.);
		Point2D diff = perp.subtract(end);
		diff = diff.normalize();
		diff = scale(diff, controlPointDistance);
		perp = end.add(diff);
		return perp;
	}

	private Point2D scale(Point2D a, double scale){
		return new Point2D(a.getX()*scale,a.getY()*scale);
	}

}