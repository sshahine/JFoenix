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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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

	private boolean invalid = true;

	private Timeline animateGap;

	public C3DListCell() {
		super();
		initialize();
		this.setCache(true);
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
				if(invalid){
					cellContainer.getChildren().clear();
					cellContainer.getChildren().add((Node) item);
					cellContainer.getStyleClass().add("c3d-list-cell-container");
					cellContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));				
					cellRippler = new C3DListCellRippler(cellContainer);
					
					double cellInsetHgap = ((C3DListView<T>)getListView()).getCellHorizontalMargin().doubleValue();
					double cellInsetVgap = ((C3DListView<T>)getListView()).getCellVerticalMargin().doubleValue();

					mainContainer.getChildren().add(cellRippler);
					StackPane.setMargin(cellRippler, new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));
					cellContainer.setPadding(new Insets(cellInsetVgap, cellInsetHgap, cellInsetVgap, cellInsetHgap));
					if(fitRippler){
						cellRippler.ripplerPane.setScaleX(getRipplerScale(cellInsetHgap));
						cellRippler.ripplerPane.setScaleY(getRipplerScale(cellInsetVgap));
					}
					
					if(this.getIndex() > 0 && ((C3DListView<T>)getListView()).isExpanded())
						this.translateYProperty().set(((C3DListView<T>)getListView()).getVerticalGap()*this.getIndex());
					
					setGraphic(mainContainer);
					setText(null);
					invalid = false;
				}

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
					if(this.getIndex() > 0){
												
						
						if(animateGap!=null) animateGap.stop();
						animateGap = new Timeline(
								new KeyFrame(
										Duration.ZERO,       
										new KeyValue( this.translateYProperty(), this.translateYProperty().get() ,Interpolator.EASE_BOTH)
										),
										new KeyFrame(Duration.millis(500),
												new KeyValue( this.translateYProperty(), newVal.doubleValue()*this.getIndex()  ,Interpolator.EASE_BOTH)
												)
								);	
						
						if(oldVal.doubleValue()<newVal.doubleValue()){
							C3DListView<T> listview = ((C3DListView<T>)getListView());
							listview.setPrefHeight((this.getHeight()+ listview.getCellVerticalMargin() + listview.currentVerticalGapProperty().get()) * listview.getItems().size());
						}else{
							animateGap.setOnFinished((e)->{
								C3DListView<T> listview = ((C3DListView<T>)getListView());
								listview.setPrefHeight((this.getHeight()+ listview.getCellVerticalMargin() + listview.currentVerticalGapProperty().get()) * listview.getItems().size());
							});
						}
						
						animateGap.play();	
					}
				});
			}
		}
	}


	private double getRipplerScale(double gap){
		return ripplerInitScale  + ripplerInitIncScale + (gap / 5) * ripplerIncScale;
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
