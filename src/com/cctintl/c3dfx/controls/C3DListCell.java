package com.cctintl.c3dfx.controls;

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
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import com.cctintl.c3dfx.skins.C3DListCellSkin;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;

public class C3DListCell<T> extends ListCell<T> {

	private StackPane cellContainer = new StackPane();
	StackPane mainContainer = new StackPane();
	private C3DRippler cellRippler;

	private Node cellContent;

	private Timeline animateGap;
	private Timeline expandAnimation;
	private double animatedHeight = 0;

	
	public C3DListCell() {
		super();
		initialize();
	}

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DListCellSkin<T>(this);
	}

	@Override
	public void updateItem(T item, boolean empty){
		super.updateItem(item,empty);
		if(empty){
			setText(null);
			setGraphic(null);
		}else{
			if(item != null && (item instanceof Region || item instanceof Control)) {


				Node currentNode = getGraphic();
				Node newNode = (Node) item;
				if (currentNode == null || !currentNode.equals(newNode)) {
					// clear nodes
					mainContainer.getChildren().clear();
					cellContainer.getChildren().clear();
					cellContent = (Node) item;
					// build the Cell node and its rippler
					if(item instanceof C3DRippler){
						// build cell container from exisiting rippler
						cellContent = ((C3DRippler)item).getControl();						
						cellContainer.getChildren().add(cellContent);
						
						cellRippler = new C3DRippler(cellContainer);
						cellRippler.ripplerFillProperty().bind(((C3DRippler)item).ripplerFillProperty());
						cellRippler.maskTypeProperty().bind(((C3DRippler)item).maskTypeProperty());
						cellRippler.positionProperty().bind(((C3DRippler)item).positionProperty());
					}else if(item instanceof C3DListView<?>){
						// build the sublist
						this.getStyleClass().add("sublist-item");
						
						// First build the group item used to expand / hide the sublist
						StackPane group = new StackPane();						
						group.getStyleClass().add("sublist-header");
						group.getChildren().clear();						
						group.getChildren().add(((C3DListView<?>)item).getGroupnode());
						Icon dropIcon = new Icon(AwesomeIcon.ANGLE_RIGHT, "1.2em", ";", "drop-icon");
						group.getChildren().add(dropIcon);
						// the margin is needed when rotating the angle
						StackPane.setMargin(dropIcon, new Insets(0,7,0,0));
						StackPane.setAlignment(dropIcon, Pos.CENTER_RIGHT);
						
						group.paddingProperty().bind(Bindings.createObjectBinding(()->{
							double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
							double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
							return new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap);
						}, ((C3DListView<T>)getListView()).cellHorizontalMarginProperty(), ((C3DListView<T>)getListView()).cellVerticalMarginProperty()));

						// add group item rippler
						C3DRippler groupRippler = new C3DRippler(group);
						// scale rippler to fit the cell content
//						group.heightProperty().addListener((o,oldVal,newVal)->{
//							if(fitRippler){							
//								double newScale = (this.getHeight()-subListHeight)/newVal.doubleValue();
//								newScale = newScale > 1 ? newScale : 1;
//								groupRippler.ripplerPane.setScaleY(newScale);
//								groupRippler.ripplerPane.setScaleX(newScale);
//							}
//						});
//						group.widthProperty().addListener((o,oldVal,newVal)->{
//							if(fitRippler){
//								double newScale = this.getWidth()/newVal.doubleValue();
//								newScale = newScale > 1? newScale : 1;
//								groupRippler.ripplerPane.setScaleY(newScale);
//								groupRippler.ripplerPane.setScaleX(newScale);
//							}
//						});


						// Second build the sublist container
						StackPane sublistContainer = new StackPane();
						sublistContainer.getStyleClass().add("sublist-container");
						sublistContainer.getChildren().add(cellContent);
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
									double currentHeight = ((C3DListView<T>)getListView()).getHeight();
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
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> sublistContainer.fireEvent(e));
						
						group.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						sublistContainer.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						cellRippler = null;
						
						// Finally, add sublist animation 
						this.heightProperty().addListener((o,oldVal,newVal)->{
							if(!this.isExpanded() && (expandAnimation == null || expandAnimation.getStatus().equals(Status.STOPPED))){
//								double borderWidth = 0;
//								if(this.getBorder()!=null) borderWidth += this.getBorder().getStrokes().get(0).getWidths().getTop();
//								if(content.getBorder()!=null) borderWidth += content.getBorder().getStrokes().get(0).getWidths().getTop();
//								if(group.getBorder()!=null) borderWidth += group.getBorder().getStrokes().get(0).getWidths().getTop();
//								if(contentHolder.getBorder()!=null) borderWidth += contentHolder.getBorder().getStrokes().get(0).getWidths().getTop();
								sublistContainer.setTranslateY((this.getHeight() - group.getHeight())/2 + 1);
							}
						});
						// animate sublist						
						group.setOnMouseClicked((click)->{
							C3DListView<T> listview = ((C3DListView<T>)getListView());
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
						

					}else{
						// build cell container and rippler if the cell has no rippler
						cellContainer.getChildren().clear();
						cellContainer.getChildren().add((Node) item);						
					}

					// change the padding of the cell container
//					cellContainer.paddingProperty().bind(Bindings.createObjectBinding(()->{
//						double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
//						double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
//						return new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap);
//					}, ((C3DListView<T>)getListView()).cellHorizontalMarginProperty(), ((C3DListView<T>)getListView()).cellVerticalMarginProperty()));
					
//					if(cellRippler!=null){
//						// propagate mouse events to parent
//						if(getListView().getParent()!=null){
//							cellRippler.addEventHandler(MouseEvent.ANY, (e)->{
//								getListView().getParent().fireEvent(e);
//							});					
//						}
//
//						// set the background color to the rippler instead of the cell when the cell is selected
//						cellContainer.backgroundProperty().addListener((o,oldVal,newVal)->{
//							if(!Background.EMPTY.equals(newVal)){
//								cellContainer.setBackground(Background.EMPTY);
//								if(cellRippler!=null)
//									cellRippler.ripplerPane.setBackground(newVal);
//							}
//						});
//
//					
//						// scale rippler to fit the cell content
//						cellContainer.heightProperty().addListener((o,oldVal,newVal)->{
//							if(fitRippler && cellRippler!=null){						
//								double newScale = this.getHeight()/newVal.doubleValue();
//								newScale = newScale > 1? newScale : 1;
//								cellRippler.ripplerPane.setScaleY(newScale);								
//								cellRippler.ripplerPane.setScaleX(newScale);
//							}
//						});
//
//						cellContainer.widthProperty().addListener((o,oldVal,newVal)->{
//							if(fitRippler && cellRippler!=null){
//								double newScale = this.getWidth()/newVal.doubleValue();
//								newScale = newScale > 1? newScale : 1;
//								cellRippler.ripplerPane.setScaleY(newScale);
//								cellRippler.ripplerPane.setScaleX(newScale);
//							}
//						});
//					}



					// initialize the gaps between cells
					double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
					double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
					if(cellContainer!=null) StackPane.setMargin(cellContainer, new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));

					// check if the list is in expanded mode 
					// TODO : need to be changed as the cell might not have the same height
					if(this.getIndex() > 0 && ((C3DListView<T>)getListView()).isExpanded()) 
						this.translateYProperty().set(((C3DListView<T>)getListView()).getVerticalGap()*this.getIndex());


					// add listeners to gaps properties 
					((C3DListView<T>)getListView()).cellHorizontalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double newCellInsetHgap = newVal.doubleValue();
						double oldCellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
						if(cellContainer!=null) StackPane.setMargin(cellContainer, new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
					});

					((C3DListView<T>)getListView()).cellVerticalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double oldCellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double newCellInsetVgap = newVal.doubleValue();						
						if(cellContainer!=null) StackPane.setMargin(cellContainer, new Insets(newCellInsetVgap, oldCellInsetHgap, newCellInsetVgap, oldCellInsetHgap));
					});

					((C3DListView<T>)getListView()).currentVerticalGapProperty().addListener((o,oldVal,newVal)->{
						// validate changing gap operation
						C3DListView<T> listview = ((C3DListView<T>)getListView());
						double newHeight = (this.getHeight() + listview.currentVerticalGapProperty().get()) * ( listview.getItems().size()  )+ listview.getCellVerticalMargin() - listview.currentVerticalGapProperty().get();
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
									listview.setPrefHeight((this.getHeight() + listview.currentVerticalGapProperty().get()) * (listview.getItems().size())+ listview.getCellVerticalMargin() - listview.currentVerticalGapProperty().get());
								else
									animateGap.setOnFinished((e)->{
										listview.setPrefHeight((this.getHeight() + listview.currentVerticalGapProperty().get()) * (listview.getItems().size())+ listview.getCellVerticalMargin() - listview.currentVerticalGapProperty().get());
									});

								animateGap.play();	
							}
						}
					});
					
					// set the content of the cell
					mainContainer.getChildren().add(cellContainer);
					cellRippler = new C3DRippler(mainContainer);
					setGraphic(cellRippler);
					setText(null);
					
					// propagate mouse events to all children
					mainContainer.addEventHandler(MouseEvent.ANY, (e)-> cellContent.fireEvent(e));
					cellContent.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
				}
			}
		}
	}

	
	private void updateListViewHeight(double newHeight){
		((C3DListView<T>)getListView()).setPrefHeight(newHeight);
		((C3DListView<T>)getListView()).setMaxHeight(newHeight);
		((C3DListView<T>)getListView()).setMinHeight(newHeight);
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
	private static double totalSubListsHeight = 17;



	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-list-cell";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		// set default classes
		mainContainer.getStyleClass().add("c3d-list-cell-container");
		cellContainer.getStyleClass().add("c3d-list-cell-content-container");
		cellContainer.setPadding(new Insets(4,8,4,8));
		this.setPadding(new Insets(0));
	}

}
