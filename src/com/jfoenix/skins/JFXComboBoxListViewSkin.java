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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.converters.base.NodeConverter;
import com.jfoenix.transitions.CachedTransition;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * <h1>Material Design ComboBox Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */

public class JFXComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/    

	private boolean invalid = true;

	private StackPane customPane;
	private StackPane line = new StackPane();
	private StackPane focusedLine = new StackPane();
	private Text promptText = new Text();

	private double initScale = 0.05;
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

	private ParallelTransition transition;
	private CachedTransition promptTextUpTransition;
	private CachedTransition promptTextDownTransition;
	private CachedTransition promptTextColorTransition;
	private Scale promptTextScale = new Scale(1,1,0,0);
	private Paint oldPromptTextFill;
	protected final ObjectProperty<Paint> promptTextFill = new SimpleObjectProperty<Paint>(Color.valueOf("#B2B2B2")); 

	private BooleanBinding usePromptText = Bindings.createBooleanBinding(()-> usePromptText(), ((JFXComboBox<?>)getSkinnable()).valueProperty(), getSkinnable().promptTextProperty());


	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/   

	public JFXComboBoxListViewSkin(final JFXComboBox<T> comboBox) {

		super(comboBox);
		// customize combox box
		arrowButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

		// create my custom pane for the prompt node
		promptText.textProperty().bind(comboBox.promptTextProperty());
		promptText.fillProperty().bind(promptTextFill);
		promptText.getStyleClass().addAll("text","prompt-text");
		promptText.getTransforms().add(promptTextScale);
		if(!comboBox.isLabelFloat()) promptText.visibleProperty().bind(usePromptText);

		customPane = new StackPane();
		customPane.setMouseTransparent(true);
		getSkinnable().backgroundProperty().addListener((o,oldVal,newVal)-> customPane.setBackground(newVal));
		customPane.getStyleClass().add("combo-box-button-container");
		customPane.backgroundProperty().bindBidirectional(getSkinnable().backgroundProperty());
		customPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		customPane.getChildren().add(promptText);
		getChildren().add(0,customPane);
		StackPane.setAlignment(promptText, Pos.CENTER_LEFT);

		// add lines
		getChildren().add(line);
		getChildren().add(focusedLine);

		comboBox.setButtonCell(new ListCell<T>(){
			protected void updateItem(T item, boolean empty) {
				updateDisplayText(this,item, empty);
				this.setVisible(item!=null || !empty);
			};
		});

		if(comboBox.isEditable()){
			comboBox.getEditor().setStyle("-fx-background-color:TRANSPARENT;-fx-padding: 4 0 4 0");
			comboBox.getEditor().promptTextProperty().unbind();
			comboBox.getEditor().setPromptText(null);
			comboBox.getEditor().textProperty().addListener((o,oldVal,newVal)-> usePromptText.invalidate());
			
			comboBox.getEditor().textProperty().addListener((o,oldVal,newVal)->{
				comboBox.setValue(getConverter().fromString(newVal));
			});
		}

		comboBox.labelFloatProperty().addListener((o,oldVal,newVal)->{
			if(newVal) {
				promptText.visibleProperty().unbind();
				JFXUtilities.runInFX(()->createFloatingAnimation());
			}
			else promptText.visibleProperty().bind(usePromptText);
			createFocusTransition();
		});

		comboBox.focusColorProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null) {
				focusedLine.setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
				if(((JFXComboBox<?>)getSkinnable()).isLabelFloat()){
					promptTextColorTransition = new CachedTransition(customPane,  new Timeline(
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

		comboBox.unFocusColorProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null)
				line.setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
		});

		comboBox.disabledProperty().addListener((o,oldVal,newVal) -> {
			line.setBorder(newVal ? new Border(new BorderStroke(((JFXComboBox<?>)getSkinnable()).getUnFocusColor(),
					BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(line.getHeight()))) : Border.EMPTY);
			line.setBackground(new Background(new BackgroundFill( newVal? Color.TRANSPARENT : ((JFXComboBox<?>)getSkinnable()).getUnFocusColor(),
					CornerRadii.EMPTY, Insets.EMPTY)));
		});

		// handle animation on focus gained/lost event
		comboBox.focusedProperty().addListener((o,oldVal,newVal) -> {
			if (newVal) focus();
			else unFocus();
		});

		// handle animation on value changed
		comboBox.valueProperty().addListener((o,oldVal,newVal)->{
			if(((JFXComboBox<?>)getSkinnable()).isLabelFloat()){
				if(newVal == null || newVal.toString().isEmpty()) animateFloatingLabel(false);
				else animateFloatingLabel(true);
			}
		});

	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/  

	@Override protected void layoutChildren(final double x, final double y,
			final double w, final double h) {
		super.layoutChildren(x,y,w,h);		 
		customPane.resizeRelocate(x, y, w , h);
		if(invalid){
			invalid = false;

			line.setPrefHeight(1);
			line.setTranslateY(1); // translate = prefHeight + init_translation
			line.setBackground(new Background(new BackgroundFill(((JFXComboBox<?>)getSkinnable()).getUnFocusColor(),
					CornerRadii.EMPTY, Insets.EMPTY)));
			if(getSkinnable().isDisabled()) {
				line.setBorder(new Border(new BorderStroke(((JFXComboBox<?>) getSkinnable()).getUnFocusColor(),
						BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(1))));
				line.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
						CornerRadii.EMPTY, Insets.EMPTY)));
			}

			// focused line
			focusedLine.setPrefHeight(2);
			focusedLine.setTranslateY(0); // translate = prefHeight + init_translation(-1)
			focusedLine.setBackground(new Background(new BackgroundFill(((JFXComboBox<?>)getSkinnable()).getFocusColor(),
					CornerRadii.EMPTY, Insets.EMPTY)));
			focusedLine.setOpacity(0);
			focusedLine.getTransforms().add(scale);

			// create floating label
			createFloatingAnimation();
		}
		focusedLine.resizeRelocate(x, getSkinnable().getHeight(), w, focusedLine.prefHeight(-1));
		line.resizeRelocate(x, getSkinnable().getHeight(), w, line.prefHeight(-1));
		scale.setPivotX(w/2);
	}

	private void createFloatingAnimation() {
		// TODO: the 6.05 should be computed, for now its hard coded to keep the alignment with other controls
		promptTextUpTransition = new CachedTransition(customPane, new Timeline(
				new KeyFrame(Duration.millis(1300),
						new KeyValue(promptText.translateYProperty(), -customPane.getHeight() + 6.05 , Interpolator.EASE_BOTH),
						new KeyValue(promptTextScale.xProperty(), 0.85 , Interpolator.EASE_BOTH),
						new KeyValue(promptTextScale.yProperty(), 0.85 , Interpolator.EASE_BOTH)))){{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(240)); }};

						promptTextColorTransition = new CachedTransition(customPane,  new Timeline(
								new KeyFrame(Duration.millis(1300),
										new KeyValue(promptTextFill, ((JFXComboBox<?>)getSkinnable()).getFocusColor(), Interpolator.EASE_BOTH))))
						{
							{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(160)); }
							protected void starting() {super.starting(); oldPromptTextFill = promptTextFill.get();};
						};

						promptTextDownTransition = new CachedTransition(customPane, new Timeline(
								new KeyFrame(Duration.millis(1300),
										new KeyValue(promptText.translateYProperty(), 0, Interpolator.EASE_BOTH),
										new KeyValue(promptTextScale.xProperty(), 1 , Interpolator.EASE_BOTH),
										new KeyValue(promptTextScale.yProperty(), 1 , Interpolator.EASE_BOTH))))
						{{ setDelay(Duration.millis(0)); setCycleDuration(Duration.millis(240));}};
						promptTextDownTransition.setOnFinished((finish)->{
							promptText.setTranslateY(0);
							promptTextScale.setX(1);
							promptTextScale.setY(1);
						});
	}

	private void focus(){
		// create the focus animations
		if(transition == null) createFocusTransition();
		transition.play();
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
				transition.getChildren().remove(promptTextColorTransition);
				transition = null;
			}
			if(up && promptText.getTranslateY() == 0){
				promptTextDownTransition.stop();
				promptTextUpTransition.play();
				if(getSkinnable().isFocused()) promptTextColorTransition.play();
			}else if(!up){
				promptTextUpTransition.stop();
				if(getSkinnable().isFocused()) promptTextFill.set(oldPromptTextFill);
				promptTextDownTransition.play();
			}
		}
	}

	private void createFocusTransition() {
		transition = new ParallelTransition();
		if(((JFXComboBox<?>)getSkinnable()).isLabelFloat()){
			transition.getChildren().add(promptTextUpTransition);
			transition.getChildren().add(promptTextColorTransition);
		}
		transition.getChildren().add(linesAnimation);
	}

	private void unFocus() {
		if(transition!=null) transition.stop();
		scale.setX(initScale);
		focusedLine.setOpacity(0);
		if(((JFXComboBox<?>)getSkinnable()).isLabelFloat() && oldPromptTextFill != null){
			promptTextFill.set(oldPromptTextFill);
			if(usePromptText()) promptTextDownTransition.play();
		}
	}

	private boolean usePromptText() {
		Object txt = ((JFXComboBox<?>)getSkinnable()).getValue();
		String promptTxt = getSkinnable().getPromptText();
		boolean hasPromptText = (txt == null || txt.toString().isEmpty())  && promptTxt != null && !promptTxt.isEmpty() && !promptTextFill.get().equals(Color.TRANSPARENT);
		return hasPromptText;
	}


	private boolean updateDisplayText(ListCell<T> cell, T item, boolean empty) {
		if (empty) {
			// create empty cell
			if (cell == null) return true;
			cell.setGraphic(null);
			cell.setText(null);
			return true;
		} else if (item instanceof Node) {
			Node currentNode = cell.getGraphic();
			Node newNode = (Node) item;
			/*
			 *  create a node from the selected node of the listview
			 *  using JFXComboBox {@link #nodeConverterProperty() NodeConverter}) 
			 */
			NodeConverter<T> nc = ((JFXComboBox<T>)getSkinnable()).getNodeConverter();
			Node node = nc == null? null : nc.toNode(item);
			if (currentNode == null || ! currentNode.equals(newNode)) {
				cell.setText(null);
				cell.setGraphic(node==null? newNode : node);
			}
			return node == null;
		} else {
			// run item through StringConverter if it isn't null
			StringConverter<T> c = ((JFXComboBox<T>)getSkinnable()).getConverter();
			String s = item == null ? getSkinnable().getPromptText() : (c == null ? item.toString() : c.toString(item));
			cell.setText(s);
			cell.setGraphic(null);
			return s == null || s.isEmpty();
		}
	}
}
