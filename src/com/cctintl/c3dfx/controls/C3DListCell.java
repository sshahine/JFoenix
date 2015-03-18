package com.cctintl.c3dfx.controls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import com.cctintl.c3dfx.skins.C3DListCellSkin;

public class C3DListCell<T> extends ListCell<T> {

	private StackPane cellContainer = new StackPane();
	StackPane mainContainer = new StackPane();
	private C3DRippler cellRippler;

	double ripplerInitScale = 1;
	double ripplerInitIncScale = 0.45;
	double ripplerIncScale = 0.1;
	private boolean fitRippler = true;


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
						cellContainer.getChildren().clear();
						cellContainer.getChildren().add((Node) item);
						cellContainer.getStyleClass().add("c3d-list-cell-container");
						((Region) item).requestLayout();
						// propagate mouse events to all children
						cellContainer.addEventHandler(MouseEvent.ANY, (e)-> ((Node) item).fireEvent(e));
						((Node) item).addEventHandler(MouseEvent.ANY, (e)-> e.consume());
						cellRippler = new C3DRippler(cellContainer);

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
						if(cellContainer.getChildren().get(0) instanceof C3DListView<?>){
							System.out.println(this.getHeight() +  "container height" + newVal.doubleValue());
						}
						if(fitRippler){							
							double newScale = this.getHeight()/newVal.doubleValue();
							newScale = newScale > 1? newScale : 1;
							cellRippler.ripplerPane.setScaleY(newScale);
							cellRippler.ripplerPane.setScaleX(newScale);
						}
					});					
					cellContainer.widthProperty().addListener((o,oldVal,newVal)->{
						if(cellContainer.getChildren().get(0) instanceof C3DListView<?>){
							System.out.println(this.getWidth() +  "container width" + newVal.doubleValue());
						}
						if(fitRippler){
							double newScale = this.getWidth()/newVal.doubleValue();
							newScale = newScale > 1? newScale : 1;
							cellRippler.ripplerPane.setScaleY(newScale);
							cellRippler.ripplerPane.setScaleX(newScale);
						}
					});
					
					
					// initialize the gaps between cells
					double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
					double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
					StackPane.setMargin(cellRippler, new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));
					cellContainer.setPadding(new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));
					// check if the list is in expanded mode 
					if(this.getIndex() > 0 && ((C3DListView<T>)getListView()).isExpanded()) 
						this.translateYProperty().set(((C3DListView<T>)getListView()).getVerticalGap()*this.getIndex());

					
					// add listeners to gaps properties 
					((C3DListView<T>)getListView()).cellHorizontalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double newCellInsetHgap = newVal.doubleValue();
						double oldCellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
						StackPane.setMargin(cellRippler, new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
						// change the padding of the cell container
						cellContainer.setPadding(new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
					});

					((C3DListView<T>)getListView()).cellVerticalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double oldCellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double newCellInsetVgap = newVal.doubleValue();						
						StackPane.setMargin(cellRippler, new Insets(newCellInsetVgap, oldCellInsetHgap, newCellInsetVgap, oldCellInsetHgap));
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
					mainContainer.getChildren().add(cellRippler);
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
