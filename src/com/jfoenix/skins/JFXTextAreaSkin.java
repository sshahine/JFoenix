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

import java.lang.reflect.Field;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.transitions.CachedTransition;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * <h1>Material Design TextArea Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXTextAreaSkin extends TextAreaSkin {
	
	private static Background transparentBackground = new Background(
			new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY),
			new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY),
			new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY),
			new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));

	private AnchorPane cursorPane = new AnchorPane();
	private Line line = new Line();
	private Line focusedLine = new Line();
	private Label errorLabel = new Label();
	private StackPane errorIcon = new StackPane();
	private Pane mainPane = new Pane();
	private ScrollPane scrollPane;

	private double endX;
	private double startX;
	private double mid ;

	private boolean invalid = true;
	private HBox errorContainer;

	private double oldErrorLabelHeight = -1;
	//	private Region textPane;
	private double initYlayout = -1;
	private double initHeight = -1;
	private boolean errorShown = false;
	private double currentFieldHeight = -1;
	private double errorLabelInitHeight = 0;

	private boolean heightChanged = false;

	private Timeline hideErrorAnimation;
	private ParallelTransition transition;
	private StackPane promptContainer;

	private BooleanProperty floatLabel = new SimpleBooleanProperty(false);
	private Text promptText;
	private Group promptTextGroup;
	private CachedTransition promptTextUpTransition;
	private CachedTransition promptTextDownTransition;
	private Timeline promptTextColorTransition;
	private Paint oldPromptTextFill;

	private BooleanBinding usePromptText = Bindings.createBooleanBinding(()->{
		String txt = getSkinnable().getText();
		String promptTxt = getSkinnable().getPromptText();
		return ((txt == null || txt.isEmpty()) && promptTxt != null && !promptTxt.isEmpty() && !promptTextFill.get().equals(Color.TRANSPARENT));
	}, getSkinnable().textProperty(), getSkinnable().promptTextProperty());

	private ChangeListener<? super Boolean> focusPromptTextListener = (o,oldVal,newVal)->{
		String txt = getSkinnable().getText();
		String promptTxt = getSkinnable().getPromptText();
		boolean hasPromptText = (txt == null || txt.isEmpty()) && promptTxt != null && !promptTxt.isEmpty() && !promptTextFill.get().equals(Color.TRANSPARENT);
		if(newVal && hasPromptText) floatLabel.set(true);
		else if(!newVal)  {
			promptTextColorTransition.stop();
			if(oldPromptTextFill!=null) promptTextFill.set(oldPromptTextFill);
			floatLabel.set(!hasPromptText);
		}else if (newVal){
			promptTextColorTransition.playFromStart();
		}
	};

	public JFXTextAreaSkin(JFXTextArea textArea) {
		super(textArea);

		// TODO: FIX the resizing issue of text area, the line goes into textarea bounds
		if(textArea.getPrefHeight() == -1) textArea.setPrefHeight(180);
		if(textArea.getPrefWidth() == -1) textArea.setPrefWidth(511);
		mainPane.getChildren().addAll(this.getChildren());
		scrollPane = (ScrollPane) mainPane.getChildren().get(0);

		promptContainer = new StackPane();
		promptContainer.setFocusTraversable(false);

		errorContainer = new HBox();
		errorContainer.setFocusTraversable(false);
		errorLabel.getStyleClass().add("errorLabel");
		errorLabel.setWrapText(true);
		AnchorPane errorLabelContainer = new AnchorPane();
		errorLabelContainer.getChildren().add(errorLabel);	
		errorContainer.getChildren().add(errorLabelContainer);
		errorContainer.getChildren().add(errorIcon);
		errorContainer.setPadding(new Insets(6,0,0,1));
		HBox.setHgrow(errorLabelContainer, Priority.ALWAYS);
		errorIcon.setTranslateY(3);		
		errorContainer.setSpacing(10);
		errorContainer.setVisible(false);		
		errorContainer.setOpacity(0);

		this.getChildren().setAll(mainPane, new Pane(promptContainer), errorContainer);

		scrollPane.prefWidthProperty().bind(mainPane.widthProperty());
		scrollPane.prefHeightProperty().bind(mainPane.heightProperty());
		// hide text area borders
		scrollPane.setBackground(transparentBackground);
		((Region)scrollPane.getContent()).setBackground(transparentBackground);
		getSkinnable().setBackground(transparentBackground);

		line.layoutXProperty().bind(scrollPane.layoutXProperty());
		line.layoutYProperty().bind(scrollPane.layoutYProperty());
		focusedLine.layoutXProperty().bind(scrollPane.layoutXProperty());
		focusedLine.layoutYProperty().bind(scrollPane.layoutYProperty());
		cursorPane.layoutXProperty().bind(scrollPane.layoutXProperty());
		cursorPane.layoutYProperty().bind(scrollPane.layoutYProperty());

		//		errorContainer.layoutXProperty().bind(scrollPane.layoutXProperty());
		//		errorContainer.layoutYProperty().bind(scrollPane.layoutYProperty());


		// add listeners to show error label
		errorLabel.heightProperty().addListener((o,oldVal,newVal)->{
			if(errorShown){
				if(oldErrorLabelHeight == -1)
					oldErrorLabelHeight = errorLabelInitHeight = oldVal.doubleValue();

				heightChanged = true;
				double newHeight = this.getSkinnable().getHeight() - oldErrorLabelHeight +  newVal.doubleValue();
				//				// show the error
				//				Timeline errorAnimation = new Timeline(
				//						new KeyFrame(Duration.ZERO, new KeyValue(getSkinnable().minHeightProperty(), currentFieldHeight,  Interpolator.EASE_BOTH)),
				//						new KeyFrame(Duration.millis(160),
				//								// text pane animation
				//								new KeyValue(mainPane.translateYProperty(), (initYlayout + mainPane.getMaxHeight()/2) - newHeight/2, Interpolator.EASE_BOTH),
				//								// animate the height change effect
				//								new KeyValue(getSkinnable().minHeightProperty(), newHeight, Interpolator.EASE_BOTH)));
				//				errorAnimation.play();
				//				// show the error label when finished
				//				errorAnimation.setOnFinished(finish->new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_BOTH))).play());
				currentFieldHeight = newHeight;				
				oldErrorLabelHeight = newVal.doubleValue();
			}	
		});
		errorContainer.visibleProperty().addListener((o,oldVal,newVal)->{
			// show the error label if it's not shown
			new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_BOTH))).play();
		});


		textArea.labelFloatProperty().addListener((o,oldVal,newVal)->{
			if(newVal){
				promptText.visibleProperty().unbind();
				promptText.visibleProperty().set(true);
				getSkinnable().focusedProperty().addListener(focusPromptTextListener);		
			}else{
				promptText.visibleProperty().bind(usePromptText);
				getSkinnable().focusedProperty().removeListener(focusPromptTextListener);	
			}
		});

		textArea.activeValidatorProperty().addListener((o,oldVal,newVal)->{
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

		textArea.focusedProperty().addListener((o,oldVal,newVal) -> {
			if (newVal) focus();
			else {
				if(transition!=null) transition.stop();
				focusedLine.setOpacity(0);	
			}
		});

		textArea.prefWidthProperty().addListener((o,oldVal,newVal)-> {
			textArea.setMaxWidth(newVal.doubleValue());
			textArea.setMinWidth(newVal.doubleValue());
		});

		textArea.backgroundProperty().addListener((o,oldVal,newVal)->{
			// Force transparent background
			if(oldVal == transparentBackground && newVal != transparentBackground){
				textArea.setBackground(transparentBackground);
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
		mainPane.resize(w-1, h-1);
		errorContainer.resizeRelocate(x, y, w, 20);		
		promptContainer.relocate(x+2, y);

		if(invalid){
			invalid = false;
			// set the default background of text area viewport to white
			Region viewPort = ((Region)scrollPane.getChildrenUnmodifiable().get(0));
			viewPort.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			// reapply css of scroll pane in case set by the user
			viewPort.applyCss();

			errorLabel.maxWidthProperty().bind(Bindings.createDoubleBinding(()->mainPane.getWidth()/1.14, mainPane.widthProperty()));
			errorContainer.translateYProperty().bind(mainPane.heightProperty());			

			// draw lines
			line.setStartX(3);			
			line.endXProperty().bind(mainPane.widthProperty());
			line.startYProperty().bind(mainPane.heightProperty());
			line.endYProperty().bind(line.startYProperty());
			line.strokeProperty().bind(((JFXTextArea)getSkinnable()).unFocusColorProperty());
			line.setStrokeWidth(1);
			line.setTranslateY(3);
			line.setStrokeType(StrokeType.CENTERED);
			if(getSkinnable().isDisabled()) line.getStrokeDashArray().addAll(2d);
			getSkinnable().disabledProperty().addListener((o,oldVal,newVal) -> {
				line.getStrokeDashArray().clear();
				if(newVal)
					line.getStrokeDashArray().addAll(2d);
			});

			mainPane.widthProperty().addListener((o,oldVal,newVal)->{
				startX = 3;
				endX = newVal.doubleValue();
				mid = (endX - startX )/2;
			});

			startX = 3;
			endX = mainPane.getWidth();
			mid = (endX - startX )/2;
			focusedLine.setStartX(mid);
			focusedLine.setEndX(mid);

			focusedLine.startYProperty().bind(line.startYProperty());
			focusedLine.endYProperty().bind(line.startYProperty());
			focusedLine.strokeProperty().bind(((JFXTextArea)getSkinnable()).focusColorProperty());
			focusedLine.setStrokeWidth(2);
			focusedLine.setTranslateY(4);
			focusedLine.setStrokeType(StrokeType.CENTERED);
			focusedLine.setOpacity(0);

			line.translateXProperty().bind(Bindings.createDoubleBinding(()-> -focusedLine.getStrokeWidth(), focusedLine.strokeWidthProperty()));
			focusedLine.translateXProperty().bind(Bindings.createDoubleBinding(()-> -focusedLine.getStrokeWidth(), focusedLine.strokeWidthProperty()));


			if(((JFXTextArea)getSkinnable()).isLabelFloat()){
				// get the prompt text node or create it
				boolean triggerFloatLabel = false;
				if(((Region)scrollPane.getContent()).getChildrenUnmodifiable().get(0) instanceof Text) promptText = (Text) ((Region)scrollPane.getContent()).getChildrenUnmodifiable().get(0);
				else{
					Field field;
					try {
						field = TextAreaSkin.class.getDeclaredField("promptNode");
						field.setAccessible(true);
						createPromptNode();
						field.set(this, promptText);
						// position the prompt node in its position
						triggerFloatLabel = true;
						oldPromptTextFill = promptTextFill.get();
						floatLabel.set(true);
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

				promptTextGroup = new Group(promptText);			
				promptContainer.getChildren().add(promptTextGroup);
				promptContainer.setAlignment(Pos.CENTER_LEFT);

				// create prompt animations
				promptTextUpTransition = new CachedTransition(promptContainer, new Timeline(
						new KeyFrame(Duration.millis(1300),
								new KeyValue(promptContainer.translateYProperty(), -promptText.getLayoutBounds().getHeight()-5, Interpolator.EASE_BOTH),
								//								new KeyValue(promptText.translateXProperty(), - promptText.getLayoutBounds().getWidth()*0.15/2, Interpolator.EASE_BOTH),
								new KeyValue(promptText.scaleXProperty(),0.85 , Interpolator.EASE_BOTH),
								new KeyValue(promptText.scaleYProperty(),0.85 , Interpolator.EASE_BOTH),
								new KeyValue(promptTextFill, focusedLine.getStroke(), Interpolator.EASE_BOTH)))){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }};

				promptTextColorTransition =  new Timeline(new KeyFrame(Duration.millis(300),new KeyValue(promptTextFill, focusedLine.getStroke(), Interpolator.EASE_BOTH)));				

				promptTextDownTransition = new CachedTransition(promptContainer, new Timeline(
						new KeyFrame(Duration.millis(1300), 
								new KeyValue(promptContainer.translateYProperty(), 0, Interpolator.EASE_BOTH),
								//										new KeyValue(promptText.translateXProperty(), 0, Interpolator.EASE_BOTH),
								new KeyValue(promptText.scaleXProperty(),1 , Interpolator.EASE_BOTH),
								new KeyValue(promptText.scaleYProperty(),1 , Interpolator.EASE_BOTH))					 
						)){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }};

				floatLabel.addListener((o,oldVal,newVal)->{
					if(newVal){
						oldPromptTextFill = promptTextFill.get();
						// if this is removed the prompt text flicker on the 1st focus
						promptTextFill.set(oldPromptTextFill);
						promptTextDownTransition.stop();
						promptTextUpTransition.play();
					} else{						
						promptTextUpTransition.stop();
						promptTextDownTransition.play();
					}
				});

				if(triggerFloatLabel){
					promptContainer.setTranslateY(-promptText.getLayoutBounds().getHeight()-5);
					promptText.setScaleX(0.85);
					promptText.setScaleY(0.85);								
				}

				promptText.visibleProperty().unbind();
				promptText.visibleProperty().set(true);
				getSkinnable().focusedProperty().addListener(focusPromptTextListener);
			}

			mainPane.getChildren().remove(line);
			mainPane.getChildren().add(line);

			mainPane.getChildren().remove(focusedLine);
			mainPane.getChildren().add(focusedLine);

			cursorPane.setMaxSize(40, 20);
			cursorPane.setMinSize(40, 20);
			cursorPane.backgroundProperty().bind(Bindings.createObjectBinding(()-> new Background(new BackgroundFill(((JFXTextArea)getSkinnable()).getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)), ((JFXTextArea)getSkinnable()).focusColorProperty()));
			cursorPane.setTranslateX(40);
			cursorPane.setTranslateY(2);
			cursorPane.setVisible(false);
			mainPane.getChildren().remove(cursorPane);
			mainPane.getChildren().add(cursorPane);

			if(getSkinnable().isFocused()) focus();
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
		if(mainPane == null){
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
			transition.getChildren().add(linesAnimation);
			transition.setOnFinished((finish)->{
				if(transition.getStatus().equals(Status.STOPPED))
					focusedLine.endXProperty().bind(mainPane.widthProperty());	
			});
			if(getSkinnable().getText()==null || getSkinnable().getText().length() == 0){
				if(!((JFXTextArea)getSkinnable()).isLabelFloat()){
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
									new KeyValue(cursorPane.translateXProperty(), -15 ,Interpolator.EASE_BOTH),
									new KeyValue(cursorPane.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
									)

							);				
					transition.getChildren().add(cursorAnimation);
				}
			}
			transition.play();
		}
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
			mainPane.setMaxHeight(mainPane.getHeight());
			initYlayout = mainPane.getBoundsInParent().getMinY(); 
			initHeight = getSkinnable().getHeight();
			currentFieldHeight = initHeight;
		}
		errorContainer.setVisible(true);
		errorShown = true;

		// update prompt color transition
		promptTextUpTransition = new CachedTransition(promptContainer, new Timeline(
				new KeyFrame(Duration.millis(1300),
						new KeyValue(promptContainer.translateYProperty(), -promptText.getLayoutBounds().getHeight()-5, Interpolator.EASE_BOTH),
						//						new KeyValue(promptText.translateXProperty(), - promptText.getLayoutBounds().getWidth()*0.15/2, Interpolator.EASE_BOTH),
						new KeyValue(promptText.scaleXProperty(),0.85 , Interpolator.EASE_BOTH),
						new KeyValue(promptText.scaleYProperty(),0.85 , Interpolator.EASE_BOTH),
						new KeyValue(promptTextFill, focusedLine.getStroke(), Interpolator.EASE_BOTH)))){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }};

	}

	private void hideError(){	
		if(heightChanged){
			new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(mainPane.translateYProperty(), 0, Interpolator.EASE_BOTH))).play();
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
		errorShown = false;

		// update prompt color transition
		promptTextUpTransition = new CachedTransition(promptContainer, new Timeline(
				new KeyFrame(Duration.millis(1300),
						new KeyValue(promptContainer.translateYProperty(), -promptText.getLayoutBounds().getHeight()-5, Interpolator.EASE_BOTH),
						//						new KeyValue(promptText.translateXProperty(), - promptText.getLayoutBounds().getWidth()*0.15/2, Interpolator.EASE_BOTH),
						new KeyValue(promptText.scaleXProperty(),0.85 , Interpolator.EASE_BOTH),
						new KeyValue(promptText.scaleYProperty(),0.85 , Interpolator.EASE_BOTH),
						new KeyValue(promptTextFill, focusedLine.getStroke(), Interpolator.EASE_BOTH)))){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(300)); }};

	}


}
