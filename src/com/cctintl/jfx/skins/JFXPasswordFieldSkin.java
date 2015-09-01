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

package com.cctintl.jfx.skins;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import com.cctintl.jfx.controls.JFXPasswordField;
import com.cctintl.jfx.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

public class JFXPasswordFieldSkin extends TextFieldSkin{

	private AnchorPane cursorPane = new AnchorPane();
	
	private Line line = new Line();
	private Line focusedLine = new Line();
	private Label errorLabel = new Label();
	private StackPane errorIcon = new StackPane();
	
	private double endX;
	private double startX;
	private double mid ;

	private boolean invalid = true;
	private HBox errorContainer;

	private double oldErrorLabelHeight = -1;
	private Pane textPane;
	private double initYlayout = -1;
	private double initHeight = -1;
	private boolean errorShowen = false;
	private double currentFieldHeight = -1;
	private double errorLabelInitHeight = 0;

	private Timeline hideErrorAnimation;

	
	public JFXPasswordFieldSkin(JFXPasswordField field) {
		super(field);
		//initial styles
		//		field.setStyle("-fx-background-color: transparent ;-fx-font-weight: BOLD;-fx-prompt-text-fill: #808080;-fx-alignment: top-left ;");
		field.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		field.setAlignment(Pos.TOP_LEFT);


		errorLabel.getStyleClass().add("errorLabel");
		errorLabel.setWrapText(true);		
		errorLabel.maxWidthProperty().bind(Bindings.createDoubleBinding(()->field.getWidth()/1.14, field.widthProperty()));
		errorLabel.minWidthProperty().bind(Bindings.createDoubleBinding(()->field.getWidth()/1.14, field.widthProperty()));
		//		errorLabel.setStyle("-fx-border-color:BLUE;");
		AnchorPane errorLabelContainer = new AnchorPane();
		errorLabelContainer.getChildren().add(errorLabel);		

		errorContainer = new HBox();
		errorContainer.getChildren().add(errorLabelContainer);
		errorContainer.getChildren().add(errorIcon);
		errorIcon.setTranslateY(3);		
		errorContainer.setSpacing(10);
		errorContainer.setTranslateY(25);
		errorContainer.setVisible(false);		
		errorContainer.setOpacity(0);
		//		errorContainer.setStyle("-fx-border-color:GREEN;");

		this.getChildren().add(errorContainer);


		// add listeners to show error label
		errorLabel.heightProperty().addListener((o,oldVal,newVal)->{
			if(errorShowen){
				if(oldErrorLabelHeight == -1)
					oldErrorLabelHeight = errorLabelInitHeight = oldVal.doubleValue();

				double newHeight = this.getSkinnable().getHeight() - oldErrorLabelHeight +  newVal.doubleValue();
				// show the error
				Timeline errorAnimation = new Timeline(
						new KeyFrame(Duration.ZERO, new KeyValue(getSkinnable().minHeightProperty(), currentFieldHeight,  Interpolator.EASE_BOTH)),
						new KeyFrame(Duration.millis(160),
								// text pane animation
								new KeyValue(textPane.translateYProperty(), (initYlayout + textPane.getMaxHeight()/2) - newHeight/2, Interpolator.EASE_BOTH),
								// animate the height change effect
								new KeyValue(getSkinnable().minHeightProperty(), newHeight, Interpolator.EASE_BOTH)));
				errorAnimation.play();
				// show the error label when finished
				errorAnimation.setOnFinished(finish->new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_BOTH))).play());
				currentFieldHeight = newHeight;
				oldErrorLabelHeight = newVal.doubleValue();
			}	
		});
		errorContainer.visibleProperty().addListener((o,oldVal,newVal)->{
			// show the error label if it's not shown
			Platform.runLater(()->new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_BOTH))).play());
		});


		field.activeValidatorProperty().addListener((o,oldVal,newVal)->{
			if(hideErrorAnimation!=null && hideErrorAnimation.getStatus().equals(Status.RUNNING))
				hideErrorAnimation.stop();
			if(newVal!=null){
				hideErrorAnimation = new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 0, Interpolator.EASE_BOTH)));
				hideErrorAnimation.setOnFinished(finish->{
					showError(newVal);
				});
				hideErrorAnimation.play();
			}else{				
				hideError();
			}
		});

		field.focusedProperty().addListener((o,oldVal,newVal) -> {
			if (newVal) focus();
			else focusedLine.setOpacity(0);	
		});

		field.prefWidthProperty().addListener((o,oldVal,newVal)-> {
			field.setMaxWidth(newVal.doubleValue());
			field.setMinWidth(newVal.doubleValue());
		});

	}

	@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefHeight(width, topInset, rightInset, bottomInset + 5, leftInset);
	}

	@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMaxHeight(width, topInset, rightInset, bottomInset + 5, leftInset);
	}
	@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMinHeight(width, topInset, rightInset, bottomInset + 1, leftInset);
	}


	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		super.layoutChildren(x, y, w, h);
		
		if(invalid){
			textPane = ((Pane)this.getChildren().get(0));
			textPane.prefWidthProperty().bind(getSkinnable().prefWidthProperty());
			
			line.setStartX(0);
			line.endXProperty().bind(textPane.widthProperty());
			line.startYProperty().bind(textPane.heightProperty());
			line.endYProperty().bind(line.startYProperty());
			line.strokeProperty().bind(((JFXPasswordField)getSkinnable()).unFocusColorProperty());
			line.setStrokeWidth(1);
			line.setTranslateY(-2);
			line.setStrokeType(StrokeType.CENTERED);
			if(getSkinnable().isDisabled()) line.getStrokeDashArray().addAll(2d);
			getSkinnable().disabledProperty().addListener((o,oldVal,newVal) -> {
				line.getStrokeDashArray().clear();
				if(newVal)
					line.getStrokeDashArray().addAll(2d);
			});

			textPane.widthProperty().addListener((o,oldVal,newVal)->{
				startX = 0;
				endX = newVal.doubleValue();
				mid = (endX - startX )/2;
				focusedLine.setStartX(mid);
				focusedLine.setEndX(mid);
			});

			startX = 0;
			endX = textPane.getWidth();
			mid = (endX - startX )/2;
			focusedLine.setStartX(mid);
			focusedLine.setEndX(mid);
			focusedLine.startYProperty().bind(line.startYProperty());
			focusedLine.endYProperty().bind(line.startYProperty());
			focusedLine.strokeProperty().bind(((JFXPasswordField)getSkinnable()).focusColorProperty());
			focusedLine.setStrokeWidth(2);
			focusedLine.setTranslateY(-1);
			focusedLine.setStrokeType(StrokeType.CENTERED);
			focusedLine.setOpacity(0);

			line.translateXProperty().bind(Bindings.createDoubleBinding(()-> -focusedLine.getStrokeWidth(), focusedLine.strokeWidthProperty()));
			focusedLine.translateXProperty().bind(Bindings.createDoubleBinding(()-> -focusedLine.getStrokeWidth(), focusedLine.strokeWidthProperty()));


			textPane.getChildren().remove(line);
			textPane.getChildren().add(line);

			textPane.getChildren().remove(focusedLine);
			textPane.getChildren().add(focusedLine);

			cursorPane.setMaxSize(40, textPane.getHeight() - 5);
			cursorPane.setMinSize(40, textPane.getHeight() - 5);
			cursorPane.backgroundProperty().bind(Bindings.createObjectBinding(()-> new Background(new BackgroundFill(((JFXPasswordField)getSkinnable()).getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)), ((JFXPasswordField)getSkinnable()).focusColorProperty()));
			cursorPane.setTranslateX(40);
			cursorPane.setVisible(false);

			textPane.getChildren().remove(cursorPane);
			textPane.getChildren().add(cursorPane);

			invalid = false;
		}				
	}

	private void focus(){
		Timeline linesAnimation = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(focusedLine.startXProperty(), mid ,Interpolator.EASE_BOTH),
						new KeyValue(focusedLine.opacityProperty(), 0 ,Interpolator.EASE_BOTH),									
						new KeyValue(focusedLine.endXProperty(), mid ,Interpolator.EASE_BOTH)
						),
						new KeyFrame(
								Duration.millis(5),
								new KeyValue(focusedLine.opacityProperty(), 1 ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(
										Duration.millis(160),
										new KeyValue(focusedLine.startXProperty(), startX ,Interpolator.EASE_BOTH),
										new KeyValue(focusedLine.endXProperty(), endX ,Interpolator.EASE_BOTH)
										)

				);

		Timeline cursorAnimation = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(cursorPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
						new KeyValue(cursorPane.scaleXProperty(), 1 ,Interpolator.EASE_BOTH),
						new KeyValue(cursorPane.translateXProperty(), 40 ,Interpolator.EASE_BOTH),
						new KeyValue(cursorPane.opacityProperty(), 0.75 ,Interpolator.EASE_BOTH)
						),
						new KeyFrame(
								Duration.millis(5),
								new KeyValue(cursorPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(
										Duration.millis(160),
										new KeyValue(cursorPane.scaleXProperty(), 1/cursorPane.getWidth() ,Interpolator.EASE_BOTH),
										new KeyValue(cursorPane.translateXProperty(), -40 ,Interpolator.EASE_BOTH),
										new KeyValue(cursorPane.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
										)

				);
		ParallelTransition transition = new ParallelTransition();
		transition.getChildren().add(linesAnimation);
		if(getSkinnable().getText().length() == 0)
			transition.getChildren().add(cursorAnimation);
		transition.play();
	}


	
	private void showError(ValidatorBase validator){
		// set text in error label
		errorLabel.setText(validator.getMessage());
		// show error icon
		Node awsomeIcon = validator.getAwsomeIcon();
		errorIcon.getChildren().clear();
		if(awsomeIcon!=null){
			errorIcon.getChildren().add(awsomeIcon);
			StackPane.setAlignment(awsomeIcon, Pos.TOP_RIGHT);	
		}
		// init only once, to fix the text pane from resizing
		if(initYlayout == -1){
			textPane.setMaxHeight(textPane.getHeight());
			initYlayout = textPane.getBoundsInParent().getMinY(); 
			initHeight = getSkinnable().getHeight();
			currentFieldHeight = initHeight;
		}
		errorContainer.setVisible(true);
		errorShowen = true;
	}

	private void hideError(){		
		new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(textPane.translateYProperty(), 0, Interpolator.EASE_BOTH))).play();
		// rest the height of text field
		new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(getSkinnable().minHeightProperty(), initHeight, Interpolator.EASE_BOTH))).play();
		// clear error label text
		errorLabel.setText(null);
		oldErrorLabelHeight = errorLabelInitHeight;		
		// clear error icon
		errorIcon.getChildren().clear();
		// reset the height of the text field
		currentFieldHeight = initHeight;
		// hide error container
		errorContainer.setVisible(false);
		errorShowen = false;	
	}

	
}
