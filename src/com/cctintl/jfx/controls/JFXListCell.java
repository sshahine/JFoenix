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

package com.cctintl.jfx.controls;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import com.cctintl.jfx.skins.JFXListCellSkin;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;

public class JFXListCell<T> extends ListCell<T> {

	private StackPane cellContainer = new StackPane();
	private StackPane mainContainer = new StackPane();
	private JFXRippler cellRippler;

	private Node cellContent;

	private Timeline animateGap;
	private Timeline expandAnimation;
	private double animatedHeight = 0;


	public JFXListCell() {
		super();
		initialize();
	}

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXListCellSkin<T>(this);
	}

	@Override
	public void updateItem(T item, boolean empty){
		super.updateItem(item,empty);
		if(empty){
			setText(null);
			setGraphic(null);
		}else{
			if(item != null) {


				Node currentNode = getGraphic();
				
				Node newNode;
				if((item instanceof Region || item instanceof Control))  newNode = (Node) item;
				else newNode = new Label(item.toString());
				
				boolean bindRippler = false;
				boolean addCellRippler = true;

				if (currentNode == null || !currentNode.equals(newNode)) {
					// clear nodes
					mainContainer.getChildren().clear();
					cellContainer.getChildren().clear();
					cellContent = newNode;

					// build the Cell node and its rippler					
					// RIPPLER ITEM : in case if the list item has its own rippler bind the list rippler and item rippler properties
					if(newNode instanceof JFXRippler){
						bindRippler = true;
						// build cell container from exisiting rippler
						cellContent = ((JFXRippler)newNode).getControl();						
						cellContainer.getChildren().add(cellContent);
					}

					// SUBLIST ITEM : build the Cell node as sublist the sublist
					else if(newNode instanceof JFXListView<?>){
						// add the sublist to the parent and style the cell as sublist item
						((JFXListView<?>)getListView()).addSublist((JFXListView<?>) newNode, this.getIndex());						
						this.getStyleClass().add("sublist-item");
						addCellRippler = false;

						// First build the group item used to expand / hide the sublist
						StackPane group = new StackPane();						
						group.getStyleClass().add("sublist-header");
						group.getChildren().clear();						
						group.getChildren().add(((JFXListView<?>)newNode).getGroupnode());
						Icon dropIcon = new Icon(AwesomeIcon.ANGLE_RIGHT, "1.2em", ";", "drop-icon");
						group.getChildren().add(dropIcon);
						// the margin is needed when rotating the angle
						StackPane.setMargin(dropIcon, new Insets(0,7,0,0));
						StackPane.setAlignment(dropIcon, Pos.CENTER_RIGHT);						
						group.paddingProperty().bind(Bindings.createObjectBinding(()->{
							double cellInsetHgap = ((JFXListView<T>)getListView()).getCellHorizontalMargin().doubleValue() + cellContainer.getPadding().getLeft();
							double cellInsetVgap = ((JFXListView<T>)getListView()).getCellVerticalMargin().doubleValue() + cellContainer.getPadding().getTop();
							return new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap);
						}, ((JFXListView<T>)getListView()).cellHorizontalMarginProperty(), ((JFXListView<T>)getListView()).cellVerticalMarginProperty()));
						// add group item rippler
						JFXRippler groupRippler = new JFXRippler(group);

						// FIXME : NEED TO CHECK THE HEIGHT OF THE CELLS (as it's being changed )
						cellContainer.setPadding(new Insets(0));

						// Second build the sublist container
						StackPane sublistContainer = new StackPane();
						sublistContainer.getStyleClass().add("sublist-container");
						sublistContainer.getChildren().add(cellContent);
						sublistContainer.setTranslateY(1);
						sublistContainer.setOpacity(0);	
						
						sublistContainer.heightProperty().addListener((o,oldVal,newVal)->{
							// store the hieght of the sublist and resize it to 0 to make it hidden
							if(subListHeight == -1){
								subListHeight = newVal.doubleValue();
								totalSubListsHeight += subListHeight;
								// set the parent list 
								Platform.runLater(()->{
									sublistContainer.setMinHeight(0);
									sublistContainer.setPrefHeight(0);
									sublistContainer.setMaxHeight(0);
									double currentHeight = ((JFXListView<T>)getListView()).getHeight();
									// FIXME : THIS SHOULD ONLY CALLED ONCE ( NOW ITS BEING CALLED FOR EVERY SUBLIST)
									updateListViewHeight(currentHeight - totalSubListsHeight);
								});	
							}
						});


						// Third, create container of group title and the sublist
						VBox contentHolder = new VBox();
						contentHolder.getChildren().add(groupRippler);
						contentHolder.getChildren().add(sublistContainer);
						cellContainer.getChildren().add(contentHolder);
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> contentHolder.fireEvent(e));
						contentHolder.addEventHandler(MouseEvent.ANY, (e)-> e.consume());


						// Finally, add sublist animation						
						group.setOnMouseClicked((click)->{
							JFXListView<T> listview = ((JFXListView<T>)getListView());
							// invert the expand property 
							expandedProperty.set(!expandedProperty.get());

							// change the list height
							animatedHeight = subListHeight;
							if(!expandedProperty.get()) animatedHeight = -animatedHeight;

							// stop the animation or change the list height 
							if(expandAnimation!=null && expandAnimation.getStatus().equals(Status.RUNNING)) expandAnimation.stop();								
							else if(expandedProperty.get()) updateListViewHeight(listview.getHeight() + animatedHeight);


							// animate showing/hiding the sublist
							double initMin,initMax;
							initMin = initMax = !expandedProperty.get()? subListHeight : 0.0;
							int opacity = !expandedProperty.get()? 0 : 1;
							expandAnimation = new Timeline(new KeyFrame(Duration.millis(320),
									new KeyValue( sublistContainer.minHeightProperty(), initMin + animatedHeight ,Interpolator.EASE_BOTH),																
									new KeyValue( sublistContainer.maxHeightProperty(), initMax + animatedHeight ,Interpolator.EASE_BOTH),
									new KeyValue( sublistContainer.opacityProperty(), opacity ,Interpolator.EASE_BOTH)));							
							if(!expandedProperty.get()) expandAnimation.setOnFinished((finish)->updateListViewHeight(listview.getHeight() + animatedHeight));
							expandAnimation.play();
						});

						// animate arrow
						expandedProperty.addListener((o,oldVal,newVal)->{
							if(newVal) new Timeline(new KeyFrame(Duration.millis(160),new KeyValue( dropIcon.rotateProperty(),90 ,Interpolator.EASE_BOTH))).play();
							else new Timeline(new KeyFrame(Duration.millis(160),new KeyValue( dropIcon.rotateProperty(), 0 ,Interpolator.EASE_BOTH))).play();
						});
					}

					// DEFAULT BUILD  : build cell container and rippler if the cell has no rippler
					else{
						cellContainer.getChildren().clear();
						cellContainer.getChildren().add(newNode);						
					}

					if(addCellRippler){
						// initialize the gaps between cells
						double cellInsetHgap = ((JFXListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double cellInsetVgap = ((JFXListView<T>)getListView()).getCellVerticalMargin().doubleValue();
						if(cellContainer!=null) StackPane.setMargin(cellContainer, new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));

						// add listeners to gaps properties 
						((JFXListView<T>)getListView()).cellHorizontalMarginProperty().addListener((o,oldVal,newVal)-> {
							// fit the rippler into the cell bounds
							double newCellInsetHgap = newVal.doubleValue();
							double oldCellInsetVgap = ((JFXListView<T>)getListView()).getCellVerticalMargin().doubleValue();
							if(cellContainer!=null) StackPane.setMargin(cellContainer, new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
						});
						((JFXListView<T>)getListView()).cellVerticalMarginProperty().addListener((o,oldVal,newVal)-> {
							// fit the rippler into the cell bounds
							double oldCellInsetHgap = ((JFXListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
							double newCellInsetVgap = newVal.doubleValue();						
							if(cellContainer!=null) StackPane.setMargin(cellContainer, new Insets(newCellInsetVgap, oldCellInsetHgap, newCellInsetVgap, oldCellInsetHgap));
						});
					}

					// check if the list is in expanded mode 
					if(this.getIndex() > 0 && ((JFXListView<T>)getListView()).isExpanded()) 
						this.translateYProperty().set(((JFXListView<T>)getListView()).getVerticalGap()*this.getIndex());

					((JFXListView<T>)getListView()).currentVerticalGapProperty().addListener((o,oldVal,newVal)->{
						// validate changing gap operation
						JFXListView<T> listview = ((JFXListView<T>)getListView());
						double borderWidth = 0;
						if(listview.getPadding()!=null){
							borderWidth += listview.getPadding().getTop();
							borderWidth += listview.getPadding().getBottom();
						}
						double newHeight = (this.getHeight() + listview.currentVerticalGapProperty().get()) * listview.getItems().size() + borderWidth - listview.currentVerticalGapProperty().get();
						if(listview.getMaxHeight() == -1 || (listview.getMaxHeight() > 0 && newHeight <= listview.getMaxHeight())){
							if(this.getIndex() > 0 && this.getIndex() < listview.getItems().size()){
								// stop the previous animation 
								if(animateGap!=null) animateGap.stop();
								// create new animation
								animateGap = new Timeline(
										new KeyFrame( Duration.ZERO, new KeyValue( this.translateYProperty(), this.translateYProperty().get() ,Interpolator.EASE_BOTH)),
										new KeyFrame(Duration.millis(500), new KeyValue( this.translateYProperty(), newVal.doubleValue()*this.getIndex()  ,Interpolator.EASE_BOTH))
										);	
								// change the height of the list view
								if(oldVal.doubleValue()<newVal.doubleValue())
									listview.setPrefHeight(newHeight);
								else
									animateGap.setOnFinished((e)->{
										listview.setPrefHeight(newHeight);
									});

								animateGap.play();	
							}
						}
					});

					// set the content of the cell
					mainContainer.getChildren().add(cellContainer);
					if(addCellRippler){
						cellRippler = new JFXRippler(mainContainer);
						// if the item passed to the list is C3D Rippler then we bind its color mask and position properties to the cell rippler
						if(bindRippler){
							cellRippler.ripplerFillProperty().bind(((JFXRippler)newNode).ripplerFillProperty());
							cellRippler.maskTypeProperty().bind(((JFXRippler)newNode).maskTypeProperty());
							cellRippler.positionProperty().bind(((JFXRippler)newNode).positionProperty());
						}
						setGraphic(cellRippler);
					}else{
						setGraphic(mainContainer);	
					}
					setText(null);

					// propagate mouse events to all children
					mainContainer.addEventHandler(MouseEvent.ANY, (e)-> cellContent.fireEvent(e));
					cellContent.addEventHandler(MouseEvent.ANY, (e)->e.consume());
				}
			}
		}
	}


	private void updateListViewHeight(double newHeight){
		((JFXListView<T>)getListView()).setPrefHeight(newHeight);
		((JFXListView<T>)getListView()).setMaxHeight(newHeight);
		((JFXListView<T>)getListView()).setMinHeight(newHeight);
	}


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	// indicate whether the sub list is expanded or not
	private BooleanProperty expandedProperty = new SimpleBooleanProperty(false);	

	public BooleanProperty expandedProperty(){
		return expandedProperty;
	}
	public void setExpanded(boolean expand){
		expandedProperty.set(expand);
	}	
	public boolean isExpanded(){
		return expandedProperty.get();
	}


	// hold the height of the sub list if existed
	private double subListHeight = -1;

	// FIXME : this value must be computed instead of fixed
	private static double totalSubListsHeight = -34;



	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "jfx-list-cell";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		// set default classes
		mainContainer.getStyleClass().add("jfx-list-cell-container");
		cellContainer.getStyleClass().add("jfx-list-cell-content-container");
		cellContainer.setPadding(new Insets(4,8,4,8));
		this.setPadding(new Insets(0));
		totalSubListsHeight = -34;
	}

}
