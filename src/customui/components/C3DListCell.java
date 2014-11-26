package customui.components;

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
import customui.skins.C3DListCellSkin;

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
				if (currentNode == null || ! currentNode.equals(newNode)) {

					cellContainer.getChildren().clear();
					cellContainer.getChildren().add((Node) item);
					cellContainer.getStyleClass().add("c3d-list-cell-container");
					// propagate mouse events to all children
					cellContainer.addEventHandler(MouseEvent.ANY, (e)->{
						e.consume();
						((Node) item).fireEvent(e);
					});
					
					cellRippler = new C3DListCellRippler(cellContainer);
					// propagate mouse events to parent
					if(getListView().getParent()!=null){
						cellRippler.addEventHandler(MouseEvent.ANY, (e)->{
							getListView().getParent().fireEvent(e);
						});
					}
					
					double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
					double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();

					mainContainer.getChildren().add(cellRippler);
					StackPane.setMargin(cellRippler, new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));
					cellContainer.setPadding(new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));
					if(fitRippler){
						cellRippler.ripplerPane.setScaleX(getRipplerScale(cellInsetHgap));
						cellRippler.ripplerPane.setScaleY(getRipplerScale(cellInsetVgap));
					}
					
					cellContainer.backgroundProperty().addListener((o,oldVal,newVal)->{
						cellContainer.setBackground(Background.EMPTY);
						cellRippler.ripplerPane.setBackground(newVal);
					});
					
					
					if(this.getIndex() > 0 && ((C3DListView<T>)getListView()).isExpanded())
						this.translateYProperty().set(((C3DListView<T>)getListView()).getVerticalGap()*this.getIndex());
					setGraphic(mainContainer);
					setText(null);

					// add listeners
					((C3DListView<T>)getListView()).cellHorizontalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double newCellInsetHgap = newVal.doubleValue();
						double oldCellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();
						if(fitRippler) cellRippler.ripplerPane.setScaleX(getRipplerScale(newCellInsetHgap));
						StackPane.setMargin(cellRippler, new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
						// change the padding of the cell container
						cellContainer.setPadding(new Insets(oldCellInsetVgap, newCellInsetHgap, oldCellInsetVgap, newCellInsetHgap));
					});

					((C3DListView<T>)getListView()).cellVerticalMarginProperty().addListener((o,oldVal,newVal)-> {
						// fit the rippler into the cell bounds
						double oldCellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
						double newCellInsetVgap = newVal.doubleValue();
						if(fitRippler) cellRippler.ripplerPane.setScaleY(getRipplerScale(newCellInsetVgap));
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
				}
			}
		}
	}


	// FIXME : need to be changed according to the gap 
	private double getRipplerScale(double gap){
		return ripplerInitScale  + ripplerInitIncScale + (gap / 4) * ripplerIncScale;
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

	private class C3DListCellRippler extends C3DRippler{
		public C3DListCellRippler(Node control) {
			super(control);
		}
	}

}
