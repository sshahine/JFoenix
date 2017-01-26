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
package com.jfoenix.skins;

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.CachedTransition;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.animation.Animation.Status;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.lang.reflect.Field;

/**
 * <h1>Material Design TextField Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXTextFieldOldSkin extends TextFieldSkin{

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

	private boolean heightChanged = false;

	private Timeline hideErrorAnimation;
	private ParallelTransition transition;
	private StackPane promptContainer;

	private Text promptText;
	private CachedTransition promptTextUpTransition;
	private CachedTransition promptTextDownTransition;
	private CachedTransition promptTextColorTransition;
	private Paint oldPromptTextFill;

	private BooleanBinding usePromptText = Bindings.createBooleanBinding(()-> usePromptText(), getSkinnable().textProperty(), getSkinnable().promptTextProperty());

	public JFXTextFieldOldSkin(JFXTextField field) {
		super(field);
		// initial styles
		//		field.setStyle("-fx-background-color: transparent ;-fx-font-weight: BOLD;-fx-prompt-text-fill: #808080;-fx-alignment: top-left ;");
		field.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		field.setAlignment(Pos.TOP_LEFT);

		errorLabel.getStyleClass().add("errorLabel");
		errorLabel.setWrapText(true);		
		errorIcon.setTranslateY(3);

		AnchorPane errorLabelContainer = new AnchorPane();
		errorLabelContainer.getChildren().add(errorLabel);		

		promptContainer = new StackPane();
		this.getChildren().add(promptContainer);

		errorContainer = new HBox();
		errorContainer.getChildren().setAll(errorLabelContainer, errorIcon);
		HBox.setHgrow(errorLabelContainer, Priority.ALWAYS);
		errorContainer.setSpacing(10);
		errorContainer.setVisible(false);		
		errorContainer.setOpacity(0);

		this.getChildren().add(errorContainer);

		// add listeners to show error label
		errorLabel.heightProperty().addListener((o,oldVal,newVal)->{
			if(errorShowen){
				if(oldErrorLabelHeight == -1)
					oldErrorLabelHeight = errorLabelInitHeight = oldVal.doubleValue();
				heightChanged = true;
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
			if(newVal) new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_BOTH))).play();
		});


		field.labelFloatProperty().addListener((o,oldVal,newVal)->{
			if(newVal){
				JFXUtilities.runInFX(()->createFloatingLabel());
			}else{
				promptText.visibleProperty().bind(usePromptText);
			}
		});

		field.activeValidatorProperty().addListener((o,oldVal,newVal)->{
			if(textPane != null){
				if(!((JFXTextField)getSkinnable()).isDisableAnimation()){
					if(hideErrorAnimation!=null && hideErrorAnimation.getStatus().equals(Status.RUNNING))
						hideErrorAnimation.stop();
					if(newVal!=null){
						hideErrorAnimation = new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 0, Interpolator.EASE_BOTH)));
						hideErrorAnimation.setOnFinished(finish->{
							errorContainer.setVisible(false);
							JFXUtilities.runInFX(()->showError(newVal));
						});
						hideErrorAnimation.play();
					}else{				
						JFXUtilities.runInFX(()->hideError());
					}
				}else{
					if(newVal!=null) JFXUtilities.runInFXAndWait(()->showError(newVal));
					else JFXUtilities.runInFXAndWait(()->hideError());
				}
			}
		});

		// handle animation on focus gained/lost event
		field.focusedProperty().addListener((o,oldVal,newVal) -> {
			if (newVal) focus();
			else unfocus();	
		});

		// handle text changing at runtime
		field.textProperty().addListener((o,oldVal,newVal)->{
			if(!getSkinnable().isFocused() && ((JFXTextField)getSkinnable()).isLabelFloat()){
				if(newVal == null || newVal.isEmpty()) animateFLoatingLabel(false);
				else animateFLoatingLabel(true);
			}	
		});

		field.prefWidthProperty().addListener((o,oldVal,newVal)-> {
			if(!field.maxWidthProperty().isBound()) field.setMaxWidth(newVal.doubleValue());
			if(!field.minWidthProperty().isBound()) field.setMinWidth(newVal.doubleValue());
		});

		// prevent setting prompt text fill to transparent when text field is focused (override java transparent color if the control was focused)
		promptTextFill.addListener((o,oldVal,newVal)->{
			if(Color.TRANSPARENT.equals(newVal) && ((JFXTextField)getSkinnable()).isLabelFloat()){
				promptTextFill.set(oldVal);
			}
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

		// change control properties if and only if animations are stopped 
		if((transition == null || transition.getStatus().equals(Status.STOPPED))){
			if(getSkinnable().isFocused() && ((JFXTextField)getSkinnable()).isLabelFloat()){
				promptTextFill.set(((JFXTextField)getSkinnable()).getFocusColor());
			}
		}

		if(invalid){	
			invalid = false;
			textPane = ((Pane)this.getChildren().get(0));
			textPane.prefWidthProperty().bind(getSkinnable().prefWidthProperty());
			// bind error container
			errorLabel.maxWidthProperty().bind(Bindings.createDoubleBinding(()->textPane.getWidth()/1.14, textPane.widthProperty()));
			errorContainer.translateYProperty().bind(Bindings.createDoubleBinding(()->{
				return textPane.getHeight() + focusedLine.getStrokeWidth();
			}, textPane.heightProperty(), focusedLine.strokeWidthProperty()));

			// draw lines
			line.setStartX(0);
			line.endXProperty().bind(textPane.widthProperty());
			line.startYProperty().bind(textPane.heightProperty());
			line.endYProperty().bind(line.startYProperty());
			line.strokeProperty().bind(((JFXTextField)getSkinnable()).unFocusColorProperty());
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
			});

			startX = 0;
			endX = textPane.getWidth();
			mid = (endX - startX )/2;
			focusedLine.setStartX(mid);
			focusedLine.setEndX(mid);

			focusedLine.startYProperty().bind(line.startYProperty());
			focusedLine.endYProperty().bind(line.startYProperty());
			focusedLine.strokeProperty().bind(((JFXTextField)getSkinnable()).focusColorProperty());
			focusedLine.setStrokeWidth(2);
			focusedLine.setTranslateY(-1);
			focusedLine.setStrokeType(StrokeType.CENTERED);
			focusedLine.setOpacity(0);
			line.translateXProperty().bind(Bindings.createDoubleBinding(()-> -focusedLine.getStrokeWidth(), focusedLine.strokeWidthProperty()));
			focusedLine.translateXProperty().bind(Bindings.createDoubleBinding(()-> -focusedLine.getStrokeWidth(), focusedLine.strokeWidthProperty()));
			focusedLine.strokeProperty().addListener((o,oldVal,newVal)->{
				if(((JFXTextField)getSkinnable()).isLabelFloat())
					promptTextColorTransition = new CachedTransition(textPane,  new Timeline(
							new KeyFrame(Duration.millis(1300),new KeyValue(promptTextFill, newVal, Interpolator.EASE_BOTH))))
				{{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }
				protected void starting() {super.starting(); oldPromptTextFill = promptTextFill.get();};};	
			});

			createFloatingLabel();
			super.layoutChildren(x, y, w, h);

			textPane.getChildren().remove(line);
			textPane.getChildren().add(line);

			textPane.getChildren().remove(focusedLine);
			textPane.getChildren().add(focusedLine);

			cursorPane.setMaxSize(40, textPane.getHeight() - 5);
			cursorPane.setMinSize(40, textPane.getHeight() - 5);
			cursorPane.backgroundProperty().bind(Bindings.createObjectBinding(()-> new Background(new BackgroundFill(((JFXTextField)getSkinnable()).getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)), ((JFXTextField)getSkinnable()).focusColorProperty()));
			cursorPane.setTranslateX(40);
			cursorPane.setVisible(false);
			textPane.getChildren().remove(cursorPane);
			textPane.getChildren().add(cursorPane);

			if(((JFXTextField)getSkinnable()).getActiveValidator()!=null){
				if(hideErrorAnimation!=null && hideErrorAnimation.getStatus().equals(Status.RUNNING))
					hideErrorAnimation.stop();
				hideErrorAnimation = new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 0, Interpolator.EASE_BOTH)));
				hideErrorAnimation.setOnFinished(finish->{
					errorContainer.setVisible(false);
					showError(((JFXTextField)getSkinnable()).getActiveValidator());
				});
				hideErrorAnimation.play();
			}

			if(getSkinnable().isFocused()) focus();			
		}	

	}


	private void createFloatingLabel() {
		if(((JFXTextField)getSkinnable()).isLabelFloat()){
			if(promptText == null){
				// get the prompt text node or create it
				boolean triggerFloatLabel = false;
				if(textPane.getChildren().get(0) instanceof Text) promptText = (Text) textPane.getChildren().get(0);
				else{
					Field field;
					try {
						field = TextFieldSkin.class.getDeclaredField("promptNode");
						field.setAccessible(true);
						createPromptNode();
						field.set(this, promptText);
						// position the prompt node in its position
						triggerFloatLabel = true;
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				promptContainer.getChildren().add(promptText);	

				if(triggerFloatLabel){
					promptText.setTranslateY(-textPane.getHeight());
					promptText.setTranslateX(-(promptText.getLayoutBounds().getWidth()*0.15)/2);
					promptText.setLayoutY(0);
					promptText.setScaleX(0.85);
					promptText.setScaleY(0.85);								
				}
			}

			promptTextUpTransition = new CachedTransition(textPane, new Timeline(
					new KeyFrame(Duration.millis(1300),
							new KeyValue(promptText.translateYProperty(), -textPane.getHeight(), Interpolator.EASE_BOTH),
							new KeyValue(promptText.translateXProperty(), - (promptText.getLayoutBounds().getWidth()*0.15 )/ 2, Interpolator.EASE_BOTH),
							new KeyValue(promptText.scaleXProperty(),0.85 , Interpolator.EASE_BOTH),
							new KeyValue(promptText.scaleYProperty(),0.85 , Interpolator.EASE_BOTH)))){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }};

							promptTextColorTransition = new CachedTransition(textPane,  new Timeline(
									new KeyFrame(Duration.millis(1300),new KeyValue(promptTextFill, focusedLine.getStroke(), Interpolator.EASE_BOTH))))
							{{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }
							protected void starting() {super.starting(); oldPromptTextFill = promptTextFill.get();};};			

							promptTextDownTransition = new CachedTransition(textPane, new Timeline(
									new KeyFrame(Duration.millis(1300), 
											new KeyValue(promptText.translateYProperty(), 0, Interpolator.EASE_BOTH),
											new KeyValue(promptText.translateXProperty(), 0, Interpolator.EASE_BOTH),
											new KeyValue(promptText.scaleXProperty(),1 , Interpolator.EASE_BOTH),
											new KeyValue(promptText.scaleYProperty(),1 , Interpolator.EASE_BOTH))					 
									)){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }};
									promptTextDownTransition.setOnFinished((finish)->{
										promptText.setTranslateX(0);
										promptText.setTranslateY(0);
										promptText.setScaleX(1);
										promptText.setScaleY(1);
									});

									promptText.visibleProperty().unbind();
									promptText.visibleProperty().set(true);
		}
	}

	private void createPromptNode(){
		promptText = new Text();
		promptText.setManaged(false);
		promptText.getStyleClass().add("text");
		promptText.visibleProperty().bind(usePromptText);
		promptText.fontProperty().bind(getSkinnable().fontProperty());
		promptText.textProperty().bind(getSkinnable().promptTextProperty());
		promptText.fillProperty().bind(promptTextFill);
		promptText.setLayoutX(1);
	}

	private void focus(){
		/*
		 * in case the method request layout is not called before focused
		 * this is bug is reported while editing treetableview cells
		 */
		if(textPane == null){
			Platform.runLater(()->focus());
		}else{
			// create the focus animations
			focusedLine.endXProperty().unbind();
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

			transition = new ParallelTransition();
			if(((JFXTextField)getSkinnable()).isLabelFloat()){
				transition.getChildren().add(promptTextUpTransition);
				transition.getChildren().add(promptTextColorTransition);
			}
			transition.getChildren().add(linesAnimation);
			transition.setOnFinished((finish)->{
				if(transition.getStatus().equals(Status.STOPPED))
					focusedLine.endXProperty().bind(textPane.widthProperty());	
			});
			if(getSkinnable().getText()==null || getSkinnable().getText().length() == 0){
				if(!((JFXTextField)getSkinnable()).isLabelFloat()){
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
					transition.getChildren().add(cursorAnimation);
				}
			}
			transition.play();
		}
	}

	private void unfocus() {
		if(transition!=null) transition.stop();
		focusedLine.setOpacity(0);
		if(((JFXTextField)getSkinnable()).isLabelFloat() && oldPromptTextFill != null){
			promptTextFill.set(oldPromptTextFill);
			if(usePromptText()) promptTextDownTransition.play();
		}
	}

	/**
	 * this method is called when the text property is changed when the
	 * field is not focused (changed in code)
	 * @param up
	 */
	private void animateFLoatingLabel(boolean up){
		if(promptText == null){
			Platform.runLater(()-> animateFLoatingLabel(up));
		}else{
			if(transition!=null){
				transition.stop();
				transition.getChildren().remove(promptTextUpTransition);
			}
			if(up && promptText.getTranslateY() == 0){
				promptTextDownTransition.stop();
				promptTextUpTransition.play();
			}else if(!up){
				promptTextUpTransition.stop();
				promptTextDownTransition.play();
			}	
		}
	}

	private boolean usePromptText() {
		String txt = getSkinnable().getText();
		String promptTxt = getSkinnable().getPromptText();
		boolean hasPromptText = (txt == null || txt.isEmpty()) && promptTxt != null && !promptTxt.isEmpty() && !promptTextFill.get().equals(Color.TRANSPARENT);
		return hasPromptText;
	}

	private void showError(ValidatorBase validator){
		// set text in error label
		errorLabel.setText(validator.getMessage());
		// show error icon
		Node awsomeIcon = validator.getIcon();
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
		if(heightChanged){
			new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(textPane.translateYProperty(), 0, Interpolator.EASE_BOTH))).play();
			// reset the height of text field
			new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(getSkinnable().minHeightProperty(), initHeight, Interpolator.EASE_BOTH))).play();	
			heightChanged = false;
		}
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
