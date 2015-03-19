package com.cctintl.c3dfx.controls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;

import com.cctintl.c3dfx.skins.C3DListCellSkin;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;

public class C3DListCell<T> extends ListCell<T> {

	private StackPane cellContainer = new StackPane();
	StackPane mainContainer = new StackPane();
	private C3DRippler cellRippler;

	double ripplerInitScale = 1;
	double ripplerInitIncScale = 0.45;
	double ripplerIncScale = 0.1;
	private boolean fitRippler = true;
	private BooleanProperty expanded = new SimpleBooleanProperty(false);
	
	public boolean isExpanded(){
		return expanded.get();
	}
	public double getSubListHeight(){
		return subListHeight==-1? 0:subListHeight;
	}
	private double subListHeight = -1;
	
	private Timeline animateGap;

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

						StackPane pane = new StackPane();
						pane.getStyleClass().add("sublist-header");
						pane.getChildren().clear();
						pane.getChildren().add(new Label("Title"));
						Icon dropIcon = new Icon(AwesomeIcon.ANGLE_RIGHT, "1.2em", ";", "drop-icon");	
						pane.getChildren().add(dropIcon);
						StackPane.setAlignment(dropIcon, Pos.CENTER_RIGHT);

						StackPane content = new StackPane();
						content.getChildren().add((Node)item);
						content.setStyle("-fx-border-color:RED;");
						content.heightProperty().addListener((o,oldVal,newVal)->{
							// store the hieght of the sublist and resize it to 0 to make it hidden
							if(subListHeight == -1){
								subListHeight = newVal.doubleValue();
								Platform.runLater(()->{
									content.setMinHeight(0);
									content.setPrefHeight(0);
									content.setMaxHeight(0);	
									((C3DListView<T>)getListView()).setPrefHeight(((C3DListView<T>)getListView()).getHeight()-subListHeight);
								});	
							}
						});


						pane.setStyle("-fx-border-color:BLUE;");						
						pane.setOnMouseClicked((click)->{
							C3DListView<T> listview = ((C3DListView<T>)getListView());
							if(!expanded.get()) listview.animatedHeight.set(subListHeight);
							else listview.animatedHeight.set(-subListHeight);							
							listview.setPrefHeight(listview.getHeight() + listview.animatedHeight.get());
							expanded.set(!expanded.get());
							listview.animatedIndex.set(listview.getItems().size());
							listview.animatedIndex.set(this.getIndex());

							new Timeline(
									new KeyFrame( Duration.ZERO, 
											new KeyValue( content.minHeightProperty(), 0 ,Interpolator.EASE_BOTH),																
											new KeyValue( content.maxHeightProperty(), 0 ,Interpolator.EASE_BOTH)),
											new KeyFrame(Duration.millis(500),
													new KeyValue( content.minHeightProperty(), listview.animatedHeight.get() ,Interpolator.EASE_BOTH),																
													new KeyValue( content.maxHeightProperty(), listview.animatedHeight.get() ,Interpolator.EASE_BOTH))
									).play();

						});


						VBox contentHolder = new VBox();
						contentHolder.getChildren().add(pane);
						contentHolder.getChildren().add(content);



						cellContainer.getChildren().add(contentHolder);
						cellContainer.getStyleClass().add("c3d-list-cell-container");
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> content.fireEvent(e));
						pane.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						content.addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						cellRippler = null;
						

						//						cellContainer.getChildren().clear();
						//						cellContainer.getChildren().add((Node) item);
						//						cellContainer.getStyleClass().add("c3d-list-cell-container");
						// propagate mouse events to all children
						//						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> ((Node) item).fireEvent(e));
						//						((Node) item).addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						//						cellRippler = new C3DRippler(cellContainer);

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
//
//					((C3DListView<T>)getListView()).animatedIndex.addListener((o,oldVal,newVal)->{
//						if(this.getIndex() > newVal.intValue()){
//							double translateY = this.translateYProperty().get();
//							new Timeline(
//									new KeyFrame( Duration.ZERO, new KeyValue( this.translateYProperty(), this.translateYProperty().get() ,Interpolator.EASE_BOTH)),
//									new KeyFrame(Duration.millis(500), new KeyValue( this.translateYProperty(), translateY + ((C3DListView<T>)getListView()).animatedHeight.get()  ,Interpolator.EASE_BOTH))
//									).play();	
//						}else if(this.getIndex() == newVal.intValue()){
//
//						}
//					});


					if(cellRippler!=null){
						// propagate mouse events to parent
						if(getListView().getParent()!=null){
							cellRippler.addEventHandler(MouseEvent.ANY, (e)->{
								getListView().getParent().fireEvent(e);
							});					
						}

						// TODO: it's deprecated, however to need to test first
						cellContainer.backgroundProperty().addListener((o,oldVal,newVal)->{
							if(!Background.EMPTY.equals(newVal)){
								cellContainer.setBackground(Background.EMPTY);
								cellRippler.ripplerPane.setBackground(newVal);
							}
						});

						// scale rippler to fit the cell content
						cellContainer.heightProperty().addListener((o,oldVal,newVal)->{
							if(fitRippler){							
								double newScale = this.getHeight()/newVal.doubleValue();
								newScale = newScale > 1? newScale : 1;
								cellRippler.ripplerPane.setScaleY(newScale);
								cellRippler.ripplerPane.setScaleX(newScale);
							}
						});

						cellContainer.widthProperty().addListener((o,oldVal,newVal)->{
							if(fitRippler){
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
					cellContainer.setPadding(new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));

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
						// change the padding of the cell container
						cellContainer.setPadding(new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
					});

					((C3DListView<T>)getListView()).cellVerticalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double oldCellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double newCellInsetVgap = newVal.doubleValue();						
						if(cellRippler!=null) StackPane.setMargin(cellRippler, new Insets(newCellInsetVgap, oldCellInsetHgap, newCellInsetVgap, oldCellInsetHgap));
						// change the padding of the cell container
						cellContainer.setPadding(new Insets(newCellInsetVgap, oldCellInsetHgap, newCellInsetVgap, oldCellInsetHgap));	
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
									listview.setPrefHeight((this.getHeight() + listview.currentVerticalGapProperty().get()) * ( listview.getItems().size()  )+ listview.getCellVerticalMargin() - listview.currentVerticalGapProperty().get());
								else
									animateGap.setOnFinished((e)->{
										listview.setPrefHeight((this.getHeight() + listview.currentVerticalGapProperty().get()) * ( listview.getItems().size()  )+ listview.getCellVerticalMargin() - listview.currentVerticalGapProperty().get());
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
