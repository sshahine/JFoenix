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

	private boolean fitRippler = true;

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

					// build the Cell node and its rippler
					if(item instanceof C3DRippler){
						// build cell container from exisiting rippler
						Node content = ((C3DRippler)item).getControl();						
						cellContainer.getChildren().add(content);
						cellContainer.getStyleClass().add("c3d-list-cell-container");
						// propagate mouse events to all children
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> content.fireEvent(e));
						content.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						cellRippler = new C3DRippler(cellContainer);
						cellRippler.ripplerFillProperty().bind(((C3DRippler)item).ripplerFillProperty());
						cellRippler.maskTypeProperty().bind(((C3DRippler)item).maskTypeProperty());
						cellRippler.positionProperty().bind(((C3DRippler)item).positionProperty());
					}else if(item instanceof C3DListView<?>){
						// build the sublist
						this.getStyleClass().add("sublist-item");
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

						C3DRippler groupRippler = new C3DRippler(group);
						// scale rippler to fit the cell content
						group.heightProperty().addListener((o,oldVal,newVal)->{
							if(fitRippler){							
								double newScale = (this.getHeight()-subListHeight)/newVal.doubleValue();
								newScale = newScale > 1 ? newScale : 1;
								groupRippler.ripplerPane.setScaleY(newScale);
								groupRippler.ripplerPane.setScaleX(newScale);
							}
						});
						group.widthProperty().addListener((o,oldVal,newVal)->{
							if(fitRippler){
								double newScale = this.getWidth()/newVal.doubleValue();
								newScale = newScale > 1? newScale : 1;
								groupRippler.ripplerPane.setScaleY(newScale);
								groupRippler.ripplerPane.setScaleX(newScale);
							}
						});


						StackPane content = new StackPane();
						content.getStyleClass().add("sublist-container");
						content.getChildren().add((Node)item);
						content.setOpacity(0);							
						content.heightProperty().addListener((o,oldVal,newVal)->{
							// store the hieght of the sublist and resize it to 0 to make it hidden
							if(subListHeight == -1){
								subListHeight = newVal.doubleValue();
								totalSubListsHeight += subListHeight;
								// set the parent list 
								Platform.runLater(()->{
									content.setMinHeight(0);
									content.setPrefHeight(0);
									content.setMaxHeight(0);									
									((C3DListView<T>)getListView()).setPrefHeight(((C3DListView<T>)getListView()).getHeight()-totalSubListsHeight);
								});	
							}
						});
						
						// create container of group title and the sublist
						VBox contentHolder = new VBox();
						
						contentHolder.getChildren().add(groupRippler);
						contentHolder.getChildren().add(content);
						cellContainer.getChildren().add(contentHolder);
						cellContainer.getStyleClass().add("c3d-list-cell-container");
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> content.fireEvent(e));
						group.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						content.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						cellRippler = null;

						
						// Animate sublist
						this.heightProperty().addListener((o,oldVal,newVal)->{
							if(!this.isExpanded() && (expandAnimation == null || expandAnimation.getStatus().equals(Status.STOPPED))){
								double borderWidth = 0;
								if(this.getBorder()!=null) borderWidth += this.getBorder().getStrokes().get(0).getWidths().getTop();
								if(content.getBorder()!=null) borderWidth += content.getBorder().getStrokes().get(0).getWidths().getTop();
								if(group.getBorder()!=null) borderWidth += group.getBorder().getStrokes().get(0).getWidths().getTop();
								if(contentHolder.getBorder()!=null) borderWidth += contentHolder.getBorder().getStrokes().get(0).getWidths().getTop();
								content.setTranslateY((this.getHeight() - group.getHeight())/2 + 1);
							}
						});
												
						group.setOnMouseClicked((click)->{
							C3DListView<T> listview = ((C3DListView<T>)getListView());
							// invert the expand property 
							expandedProperty.set(!expandedProperty.get());

							// change the list height
							animatedHeight = subListHeight;
							if(!expandedProperty.get()) animatedHeight = -animatedHeight;
 
							// stop the animation or change the list height 
							if(expandAnimation!=null && expandAnimation.getStatus().equals(Status.RUNNING)) expandAnimation.stop();								
							else if(expandedProperty.get()) listview.setPrefHeight(listview.getHeight() + animatedHeight);
							
							
							// animate showing/hiding the sublist
							double initMin,initMax;
							initMin = initMax = !expandedProperty.get()? subListHeight : 0.0;
							int opacity = !expandedProperty.get()? 0 : 1;
							expandAnimation = new Timeline(new KeyFrame(Duration.millis(320),
											new KeyValue( content.minHeightProperty(), initMin + animatedHeight ,Interpolator.EASE_BOTH),																
											new KeyValue( content.maxHeightProperty(), initMax + animatedHeight ,Interpolator.EASE_BOTH),
											new KeyValue( content.opacityProperty(), opacity ,Interpolator.EASE_BOTH)));							
							if(!expandedProperty.get()) expandAnimation.setOnFinished((finish)->listview.setPrefHeight(listview.getHeight() + animatedHeight));
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
						cellContainer.getStyleClass().add("c3d-list-cell-container");
						// propagate mouse events to all children
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> ((Node) item).fireEvent(e));
						((Node) item).addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						cellRippler = new C3DRippler(cellContainer);
					}

					// change the padding of the cell container
					cellContainer.paddingProperty().bind(Bindings.createObjectBinding(()->{
						double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
						return new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap);
					}, ((C3DListView<T>)getListView()).cellHorizontalMarginProperty(), ((C3DListView<T>)getListView()).cellVerticalMarginProperty()));

					
					if(cellRippler!=null){
						// propagate mouse events to parent
						if(getListView().getParent()!=null){
							cellRippler.addEventHandler(MouseEvent.ANY, (e)->{
								getListView().getParent().fireEvent(e);
							});					
						}

						// set the background color to the rippler instead of the cell when the cell is selected
						cellContainer.backgroundProperty().addListener((o,oldVal,newVal)->{
							if(!Background.EMPTY.equals(newVal)){
								cellContainer.setBackground(Background.EMPTY);
								if(cellRippler!=null)
									cellRippler.ripplerPane.setBackground(newVal);
							}
						});

						// scale rippler to fit the cell content
						cellContainer.heightProperty().addListener((o,oldVal,newVal)->{
							if(fitRippler && cellRippler!=null){							
								double newScale = this.getHeight()/newVal.doubleValue();
								newScale = newScale > 1? newScale : 1;
								cellRippler.ripplerPane.setScaleY(newScale);
								cellRippler.ripplerPane.setScaleX(newScale);
							}
						});

						cellContainer.widthProperty().addListener((o,oldVal,newVal)->{
							if(fitRippler && cellRippler!=null){
								double newScale = this.getWidth()/newVal.doubleValue();
								newScale = newScale > 1? newScale : 1;
								cellRippler.ripplerPane.setScaleY(newScale);
								cellRippler.ripplerPane.setScaleX(newScale);
							}
						});
					}



					// initialize the gaps between cells
					double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
					double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
					if(cellRippler!=null) StackPane.setMargin(cellRippler, new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));

					// check if the list is in expanded mode 
					// TODO : need to be changed as the cell might not have the same height
					if(this.getIndex() > 0 && ((C3DListView<T>)getListView()).isExpanded()) 
						this.translateYProperty().set(((C3DListView<T>)getListView()).getVerticalGap()*this.getIndex());


					// add listeners to gaps properties 
					((C3DListView<T>)getListView()).cellHorizontalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double newCellInsetHgap = newVal.doubleValue();
						double oldCellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
						if(cellRippler!=null) StackPane.setMargin(cellRippler, new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
					});

					((C3DListView<T>)getListView()).cellVerticalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double oldCellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double newCellInsetVgap = newVal.doubleValue();						
						if(cellRippler!=null) StackPane.setMargin(cellRippler, new Insets(newCellInsetVgap, oldCellInsetHgap, newCellInsetVgap, oldCellInsetHgap));
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

					// set the cotent of the cell
					if(cellRippler != null) mainContainer.getChildren().add(cellRippler);
					else mainContainer.getChildren().add(cellContainer);
					setGraphic(mainContainer);
					setText(null);
				}
			}
		}
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
	private static double totalSubListsHeight = 15;



	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-list-cell";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
	}

	//	private class C3DListCellRippler extends C3DRippler{
	//		public C3DListCellRippler(Node control) {
	//			super(control);
	//		}
	//	}

}
