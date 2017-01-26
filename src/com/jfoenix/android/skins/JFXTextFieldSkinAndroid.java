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
package com.jfoenix.android.skins;

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.skins.JFXTextFieldSkin;
import com.jfoenix.transitions.CachedTransition;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.sun.javafx.scene.control.skin.TextFieldSkinAndroid;
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
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.lang.reflect.Field;

/**
 * <h1>Material Design TextField Skin for android</h1>
 * The JFXTextFieldSkinAndroid implements material design text field for android
 * when porting JFoenix to android using JavaFXPorts 
 * <p>
 * <b>Note:</b> the implementation is a copy of the original {@link JFXTextFieldSkin} 
 * however it extends the JavaFXPorts text field android skin.
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2017-01-25
 */
public class JFXTextFieldSkinAndroid extends TextFieldSkinAndroid{

	private boolean invalid = true;

	private StackPane line = new StackPane();
	private StackPane focusedLine = new StackPane();

	private Label errorLabel = new Label();
	private StackPane errorIcon = new StackPane();
	private HBox errorContainer;
	private Pane textPane;

	private double initScale = 0.05;
	private double oldErrorLabelHeight = -1;
	private double initYLayout = -1;
	private double initHeight = -1;
	private boolean errorShown = false;
	private double currentFieldHeight = -1;
	private double errorLabelInitHeight = 0;

	private boolean heightChanged = false;
	private StackPane promptContainer;
	private Text promptText;

	private ParallelTransition transition;
	private Timeline hideErrorAnimation;
	private CachedTransition promptTextUpTransition;
	private CachedTransition promptTextDownTransition;
	private CachedTransition promptTextColorTransition;

	private Scale scale = new Scale(initScale,1);
	private Timeline linesAnimation = new Timeline(
			new KeyFrame(Duration.ZERO,
					new KeyValue(scale.xProperty(), initScale, Interpolator.EASE_BOTH),
					new KeyValue(focusedLine.opacityProperty(), 0, Interpolator.EASE_BOTH)),
			new KeyFrame(Duration.millis(1),
					new KeyValue(focusedLine.opacityProperty(), 1, Interpolator.EASE_BOTH)),
			new KeyFrame(Duration.millis(160),
					new KeyValue(scale.xProperty(), 1, Interpolator.EASE_BOTH))
	);

	private Paint oldPromptTextFill;
	private BooleanBinding usePromptText = Bindings.createBooleanBinding(()-> usePromptText(), getSkinnable().textProperty(), getSkinnable().promptTextProperty());

	public JFXTextFieldSkinAndroid(JFXTextField field) {
		super(field);
		// initial styles
		field.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

		errorLabel.getStyleClass().add("errorLabel");
		errorLabel.setPadding(new Insets(4,0,0,0));
		errorLabel.setWrapText(true);
		errorIcon.setTranslateY(3);

		AnchorPane errorLabelContainer = new AnchorPane();
		errorLabelContainer.getChildren().add(errorLabel);

		line.getStyleClass().add("textfield-line");
		getChildren().add(line);
		focusedLine.getStyleClass().add("textfield-focused-line");
		getChildren().add(focusedLine);

		promptContainer = new StackPane();
		getChildren().add(promptContainer);

		errorContainer = new HBox();
		errorContainer.getChildren().setAll(errorLabelContainer, errorIcon);
		HBox.setHgrow(errorLabelContainer, Priority.ALWAYS);
		errorContainer.setSpacing(10);
		errorContainer.setVisible(false);
		errorContainer.setOpacity(0);
		getChildren().add(errorContainer);

		// add listeners to show error label
		errorLabel.heightProperty().addListener((o,oldVal,newVal)->{
			if(errorShown){
				if(oldErrorLabelHeight == -1)
					oldErrorLabelHeight = errorLabelInitHeight = oldVal.doubleValue();
				heightChanged = true;
				double newHeight = this.getSkinnable().getHeight() - oldErrorLabelHeight +  newVal.doubleValue();
				// show the error
				Timeline errorAnimation = new Timeline(
						new KeyFrame(Duration.ZERO, new KeyValue(getSkinnable().minHeightProperty(), currentFieldHeight,  Interpolator.EASE_BOTH)),
						new KeyFrame(Duration.millis(160),
								// text pane animation
								new KeyValue(textPane.translateYProperty(), (initYLayout + textPane.getMaxHeight()/2) - newHeight/2, Interpolator.EASE_BOTH),
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
			if(newVal) JFXUtilities.runInFX(()->createFloatingLabel());
			else promptText.visibleProperty().bind(usePromptText);
			createFocusTransition();
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

		field.focusColorProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null) {
				focusedLine.setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
				if(((JFXTextField)getSkinnable()).isLabelFloat()){
					promptTextColorTransition = new CachedTransition(textPane,  new Timeline(
							new KeyFrame(Duration.millis(1300),
									new KeyValue(promptTextFill, newVal, Interpolator.EASE_BOTH))))
					{
						{setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(160));}
						protected void starting() {super.starting(); oldPromptTextFill = promptTextFill.get();}
					};
					// reset transition
					transition = null;
				}
			}
		});
		field.unFocusColorProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null)
				line.setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
		});

		// handle animation on focus gained/lost event
		field.focusedProperty().addListener((o,oldVal,newVal) -> {
			if (newVal) focus();
			else unFocus();
		});

		// handle text changing at runtime
		field.textProperty().addListener((o,oldVal,newVal)->{
			if(!getSkinnable().isFocused() && ((JFXTextField)getSkinnable()).isLabelFloat()){
				if(newVal == null || newVal.isEmpty()) animateFloatingLabel(false);
				else animateFloatingLabel(true);
			}
		});

		field.disabledProperty().addListener((o,oldVal,newVal) -> {
			line.setBorder(newVal ? new Border(new BorderStroke(((JFXTextField)getSkinnable()).getUnFocusColor(),
					BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(line.getHeight()))) : Border.EMPTY);
			line.setBackground(new Background(new BackgroundFill( newVal? Color.TRANSPARENT : ((JFXTextField)getSkinnable()).getUnFocusColor(),
					CornerRadii.EMPTY, Insets.EMPTY)));
		});

		// prevent setting prompt text fill to transparent when text field is focused (override java transparent color if the control was focused)
		promptTextFill.addListener((o,oldVal,newVal)->{
			if(Color.TRANSPARENT.equals(newVal) && ((JFXTextField)getSkinnable()).isLabelFloat())
				promptTextFill.set(oldVal);
		});

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
			// bind error container
			errorLabel.maxWidthProperty().bind(Bindings.createDoubleBinding(()->textPane.getWidth()/1.14, textPane.widthProperty()));

			// draw lines
			line.setPrefHeight(1);
			line.setTranslateY(1); // translate = prefHeight + init_translation
			line.setBackground(new Background(new BackgroundFill(((JFXTextField)getSkinnable()).getUnFocusColor(),
					CornerRadii.EMPTY, Insets.EMPTY)));
			if(getSkinnable().isDisabled()) {
				line.setBorder(new Border(new BorderStroke(((JFXTextField) getSkinnable()).getUnFocusColor(),
						BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(1))));
				line.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
						CornerRadii.EMPTY, Insets.EMPTY)));
			}

			// focused line
			focusedLine.setPrefHeight(2);
			focusedLine.setTranslateY(0); // translate = prefHeight + init_translation(-1)
			focusedLine.setBackground(new Background(new BackgroundFill(((JFXTextField)getSkinnable()).getFocusColor(),
					CornerRadii.EMPTY, Insets.EMPTY)));
			focusedLine.setOpacity(0);
			focusedLine.getTransforms().add(scale);

			// create floating label
			createFloatingLabel();

			// update validation container
			if(((JFXTextField)getSkinnable()).getActiveValidator()!=null) updateValidationError();

			// focus
			createFocusTransition();
			if(getSkinnable().isFocused()) focus();
		}

		focusedLine.resizeRelocate(x, getSkinnable().getHeight(), w, focusedLine.prefHeight(-1));
		line.resizeRelocate(x, getSkinnable().getHeight(), w, line.prefHeight(-1));
		errorContainer.relocate(x, getSkinnable().getHeight() + focusedLine.getHeight());
		scale.setPivotX(w/2);
	}

	private void updateValidationError() {
		if(hideErrorAnimation!=null && hideErrorAnimation.getStatus().equals(Status.RUNNING))
			hideErrorAnimation.stop();
		hideErrorAnimation = new Timeline(
				new KeyFrame(Duration.millis(160),
						new KeyValue(errorContainer.opacityProperty(), 0, Interpolator.EASE_BOTH)));
		hideErrorAnimation.setOnFinished(finish->{
			errorContainer.setVisible(false);
			showError(((JFXTextField)getSkinnable()).getActiveValidator());
		});
		hideErrorAnimation.play();
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
							new KeyValue(promptText.scaleYProperty(),0.85 , Interpolator.EASE_BOTH)))){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(240)); }};

			promptTextColorTransition = new CachedTransition(textPane,  new Timeline(
					new KeyFrame(Duration.millis(1300),
							new KeyValue(promptTextFill, ((JFXTextField)getSkinnable()).getFocusColor(), Interpolator.EASE_BOTH))))
			{
				{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(160)); }
				protected void starting() {super.starting(); oldPromptTextFill = promptTextFill.get();};
			};

			promptTextDownTransition = new CachedTransition(textPane, new Timeline(
					new KeyFrame(Duration.millis(1300),
							new KeyValue(promptText.translateYProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(promptText.translateXProperty(), 0, Interpolator.EASE_BOTH),
							new KeyValue(promptText.scaleXProperty(),1 , Interpolator.EASE_BOTH),
							new KeyValue(promptText.scaleYProperty(),1 , Interpolator.EASE_BOTH))))
			{{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(240));}};
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
		 * this is bug is reported while editing TreeTableView cells
		 */
		if(textPane == null){
			Platform.runLater(()->focus());
		}else{
			// create the focus animations
			if(transition == null) createFocusTransition();
			transition.play();
		}
	}

	private void createFocusTransition() {
		transition = new ParallelTransition();
		if(((JFXTextField)getSkinnable()).isLabelFloat()){
			transition.getChildren().add(promptTextUpTransition);
			transition.getChildren().add(promptTextColorTransition);
		}
		transition.getChildren().add(linesAnimation);
	}

	private void unFocus() {
		if(transition!=null) transition.stop();
		scale.setX(initScale);
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
	private void animateFloatingLabel(boolean up){
		if(promptText == null){
			Platform.runLater(()-> animateFloatingLabel(up));
		}else{
			if(transition!=null){
				transition.stop();
				transition.getChildren().remove(promptTextUpTransition);
				transition = null;
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
		if(initYLayout == -1){
			textPane.setMaxHeight(textPane.getHeight());
			initYLayout = textPane.getBoundsInParent().getMinY();
			initHeight = getSkinnable().getHeight();
			currentFieldHeight = initHeight;
		}
		errorContainer.setVisible(true);
		errorShown = true;
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
		errorShown = false;
	}
}
