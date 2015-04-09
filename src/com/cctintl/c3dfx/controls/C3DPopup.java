package com.cctintl.c3dfx.controls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import com.cctintl.c3dfx.jidefx.CachedTimelineTransition;

@DefaultProperty(value="content")
public class C3DPopup extends StackPane {

	public static enum C3DPopupHPosition{ RIGHT, LEFT};
	public static enum C3DPopupVPosition{ TOP, BOTTOM};

	private AnchorPane contentHolder;
	private AnchorPane overlayPane;

	private Scale scaleTransform = new Scale(0,0,0,0);
	private double offsetX = -1;
	private double offsetY = -1;

	private Pane popupContainer;
	private Region content;
	private Transition animation;
	private Node source;

	public C3DPopup(){
		this(null,null);
	}

	public C3DPopup(Pane popupContainer, Region content) {
		initialize();
		setContent(content);
		setPopupContainer(popupContainer);
	}


	/***************************************************************************
	 *                                                                         *
	 * Setters / Getters                                                       *
	 *                                                                         *
	 **************************************************************************/

	public Pane getPopupContainer() {
		return popupContainer;
	}

	public void setPopupContainer(Pane popupContainer) {
		if(popupContainer!=null){
			this.popupContainer = popupContainer;
			// close the popup if clicked on the overlay pane
			overlayPane.setOnMouseClicked((e)->{ if(e.isStillSincePress())close(); });
			this.popupContainer.getChildren().remove(overlayPane);
			this.popupContainer.getChildren().add(overlayPane);
			animation = new PopupTransition();
		}
	}

	public Region getContent() {
		return content;
	}

	public void setContent(Region content) {
		if(content!=null){
			this.content = content;
			contentHolder = new AnchorPane();
			contentHolder.getChildren().add(this.content);
			// bind the content holder size to its content
			contentHolder.prefWidthProperty().bind(this.content.prefWidthProperty());
			contentHolder.prefHeightProperty().bind(this.content.prefHeightProperty());
			contentHolder.getStyleClass().add("c3d-popup-holder");
			contentHolder.getTransforms().add(scaleTransform);			
			DepthManager.setDepth(contentHolder, 4);
			// to allow closing he popup when clicking on the shadowed area
			contentHolder.setPickOnBounds(false);
			
			// ensure stackpane is never resized beyond it's preferred size
			overlayPane = new AnchorPane();
			overlayPane.getChildren().add(contentHolder);
			overlayPane.getStyleClass().add("c3d-popup-overlay-pane");
			overlayPane.setVisible(false);			
			// prevent propagating the events to overlay pane
			contentHolder.addEventHandler(MouseEvent.ANY, (e)->e.consume());
		}
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	public void show(C3DPopupVPosition vAlign, C3DPopupHPosition hAlign, Pane popupContainer){
		this.setPopupContainer(popupContainer);
		this.show(vAlign, hAlign);
	}

	public void show(C3DPopupVPosition vAlign, C3DPopupHPosition hAlign ){
		this.show(vAlign, hAlign, 0, 0);
	}

	public void show(C3DPopupVPosition vAlign, C3DPopupHPosition hAlign, double initOffsetX, double initOffsetY ){

		offsetX = 0;
		offsetY = 0;

		// compute the position of the popup
		Node tempSource = this.source;
		Bounds bound = tempSource.localToParent(tempSource.getBoundsInLocal());
		offsetX = bound.getMinX() + initOffsetX;
		offsetY = bound.getMinY() + initOffsetY;
		
		// set the scene root as popup container if it's not set by the user
		if(popupContainer == null) this.setPopupContainer((Pane) this.source.getScene().getRoot());
		
		while(!tempSource.getParent().equals(popupContainer)){
			tempSource = tempSource.getParent();
			bound = tempSource.localToParent(tempSource.getBoundsInLocal());
			// handle scroll pane case
			if(tempSource.getClass().getName().contains("ScrollPaneSkin")){
				offsetX += bound.getMinX();
				offsetY += bound.getMinY();
			}if(tempSource instanceof C3DTabPane){
				offsetX -= bound.getWidth() * ((C3DTabPane)tempSource).getSelectionModel().getSelectedIndex();				
			}else{				
				if(bound.getMinX() > 0) offsetX += bound.getMinX();
				if(bound.getMinY() > 0) offsetY += bound.getMinY();	
			}
		}
	
		// postion the popup according to its animation
		if(hAlign.equals(C3DPopupHPosition.RIGHT)){
			scaleTransform.pivotXProperty().bind(content.widthProperty());
			contentHolder.translateXProperty().bind(Bindings.createDoubleBinding(()-> -content.getWidth() + source.getBoundsInLocal().getWidth()  + offsetX , content.widthProperty(),source.boundsInLocalProperty()));
		}else {
			scaleTransform.pivotXProperty().unbind();
			contentHolder.translateXProperty().unbind();
			scaleTransform.setPivotX(0);
			contentHolder.setTranslateX(offsetX);
		}

		if(vAlign.equals(C3DPopupVPosition.BOTTOM)){
			scaleTransform.pivotYProperty().bind(content.heightProperty());
			contentHolder.translateYProperty().bind(Bindings.createDoubleBinding(()-> -content.getHeight() + source.getBoundsInLocal().getHeight()  + offsetY , content.heightProperty(),source.boundsInLocalProperty()));
		}else {
			scaleTransform.pivotYProperty().unbind();
			contentHolder.translateYProperty().unbind();
			scaleTransform.setPivotY(0);
			contentHolder.setTranslateY(offsetY);
		}

		animation.setRate(1);
		animation.setOnFinished((e)->{});
		animation.play();
	}



	public void close(){
		animation.setRate(-1);
		animation.play();
		animation.setOnFinished((e)->{
			resetProperties();
		});

	}

	/***************************************************************************
	 *                                                                         *
	 * Animations                                                             *
	 *                                                                         *
	 **************************************************************************/


	private void resetProperties(){
		overlayPane.setVisible(false);
		scaleTransform.setX(0);
		scaleTransform.setY(0);
	}


	private class PopupTransition extends CachedTimelineTransition {

		public PopupTransition() {
			super(C3DPopup.this, new  Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(overlayPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
							new KeyValue(content.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(10),
									new KeyValue(overlayPane.visibleProperty(), true ,Interpolator.EASE_BOTH),
									new KeyValue(overlayPane.opacityProperty(), 0 ,Interpolator.EASE_BOTH),
									new KeyValue(scaleTransform.xProperty(), 0,Interpolator.EASE_BOTH),
									new KeyValue(scaleTransform.yProperty(), 0,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(700),
											new KeyValue(content.opacityProperty(), 0 ,Interpolator.EASE_BOTH),
											new KeyValue(scaleTransform.xProperty(), 1,Interpolator.EASE_BOTH)
											),		
											new KeyFrame(Duration.millis(1000),
													new KeyValue(content.opacityProperty(), 1 ,Interpolator.EASE_BOTH),
													new KeyValue(overlayPane.opacityProperty(), 1 ,Interpolator.EASE_BOTH),
													new KeyValue(scaleTransform.yProperty(), 1  ,Interpolator.EASE_BOTH)

													)
					)
					);
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}

	}

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-popup";

	private void initialize() {
		this.setVisible(false);
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);        
	}


}
