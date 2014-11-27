package com.cctintl.c3dfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.cctintl.c3dfx.converters.DialogTransitionConverter;
import com.fxexperience.javafx.animation.CachedTimelineTransition;


public class C3DDialog extends StackPane {

	public static enum C3DDialogLayout{PLAIN, HEADING, ACTIONS, BACKDROP};
	public static enum C3DDialogAnimation{CENTER, TOP, RIGHT, BOTTOM, LEFT};

	private C3DDialogAnimation animationType = C3DDialogAnimation.CENTER;
	private Transition transition;
	private StackPane contentHolder;
	private StackPane overlayPane;
	private double offsetX = 0;
	private double offsetY = 0;

	private Pane dialogContainer;
	private Region content;

	public C3DDialog(){
		this(null,null,C3DDialogAnimation.CENTER);
	}

	public C3DDialog(Pane dialogContainer, Region content, C3DDialogAnimation transitionType) {	

		this.setVisible(false);
		
		setContent(content);
		setDialogContainer(dialogContainer);

		this.transitionType.set(transitionType);		
		transition = getShowAnimation(transitionType);
		this.transitionType.addListener((o,oldVal,newVal)->{
			transition = getShowAnimation(newVal);			
		});
	}



	public Pane getDialogContainer() {
		return dialogContainer;
	}

	public void setDialogContainer(Pane dialogContainer) {
		if(dialogContainer!=null){
			this.dialogContainer = dialogContainer;
			// close the dialog if clicked on the overlay pane
			overlayPane.setOnMouseClicked((e)->dialogContainer.getChildren().remove(overlayPane));
			this.dialogContainer.getChildren().add(overlayPane);
			offsetX = (overlayPane.getParent().getBoundsInLocal().getWidth()/2 + content.getPrefWidth());
			offsetY = (overlayPane.getParent().getBoundsInLocal().getHeight()/2 + content.getPrefHeight());
		}
	}

	public Region getContent() {
		return content;
	}

	public void setContent(Region content) {
		if(content!=null){
			this.content = content;	
			contentHolder = new StackPane();
			contentHolder.getChildren().add(content);
			contentHolder.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
			DepthManager.setDepth(contentHolder, 4);
			// ensure stackpane is never resized beyond it's preferred size
			contentHolder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			overlayPane = new StackPane();
			overlayPane.getChildren().add(contentHolder);
			StackPane.setAlignment(contentHolder, Pos.CENTER);
			overlayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), null, null)));						
		}
	}

	public void setAnimationType(C3DDialogAnimation animationType){
		this.animationType = animationType;
	}

	public C3DDialogAnimation getAnimationType(){
		return this.animationType;
	}


	public void show(Pane dialogContainer){
		this.setDialogContainer(dialogContainer);
		transition.play();
	}

	public void show(){
		transition.play();
	}

	private Transition getShowAnimation(C3DDialogAnimation transitionType){
		Transition animation = null;
		if(contentHolder!=null){
			switch (transitionType) {		
			case LEFT:	
				contentHolder.setTranslateX(-offsetX);
				animation = new LeftTransition();
				break;
			case RIGHT:			
				contentHolder.setTranslateX(offsetX);
				animation = new RightTransition();
				break;
			case TOP:	
				contentHolder.setTranslateY(-offsetY);
				animation = new TopTransition();
				break;
			case BOTTOM:			
				contentHolder.setTranslateY(offsetY);
				animation = new BottomTransition();
				break;
			default:
				contentHolder.setScaleX(0);
				contentHolder.setScaleY(0);
				animation = new CenterTransition();
				break;
			}
		}
		return animation;
	}


	private class LeftTransition extends CachedTimelineTransition {
		public LeftTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateXProperty(), -offsetX ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateXProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class RightTransition extends CachedTimelineTransition {
		public RightTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateXProperty(), offsetX ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateXProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class TopTransition extends CachedTimelineTransition {
		public TopTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateYProperty(), -offsetY ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateYProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class BottomTransition extends CachedTimelineTransition {
		public BottomTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(contentHolder.translateYProperty(), offsetY ,Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.millis(1000), new KeyValue(contentHolder.translateYProperty(), 0,Interpolator.EASE_BOTH))
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}

	private class CenterTransition extends CachedTimelineTransition {
		public CenterTransition() {
			super(contentHolder, new Timeline(
					new KeyFrame(Duration.ZERO, 
							new KeyValue(contentHolder.scaleXProperty(), 0 ,Interpolator.EASE_BOTH),
							new KeyValue(contentHolder.scaleYProperty(), 0 ,Interpolator.EASE_BOTH)							
							),
							new KeyFrame(Duration.millis(1000), 							
									new KeyValue(contentHolder.scaleXProperty(), 1 ,Interpolator.EASE_BOTH),
									new KeyValue(contentHolder.scaleYProperty(), 1 ,Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private StyleableObjectProperty<C3DDialogAnimation> transitionType = new SimpleStyleableObjectProperty<C3DDialogAnimation>(StyleableProperties.DIALOG_TRANSITION, C3DDialog.this, "dialogTransition", C3DDialogAnimation.CENTER );

	public C3DDialogAnimation getTransitionType(){
		return transitionType == null ? C3DDialogAnimation.CENTER : transitionType.get();
	}
	public StyleableObjectProperty<C3DDialogAnimation> transitionTypeProperty(){		
		return this.transitionType;
	}
	public void setTransitionType(C3DDialogAnimation transition){
		this.transitionType.set(transition);
	}


	private static class StyleableProperties {
		private static final CssMetaData< C3DDialog, C3DDialogAnimation> DIALOG_TRANSITION =
				new CssMetaData< C3DDialog, C3DDialogAnimation>("-fx-dialog-transition",
						DialogTransitionConverter.getInstance(), C3DDialogAnimation.CENTER) {
			@Override
			public boolean isSettable(C3DDialog control) {
				return control.transitionType == null || !control.transitionType.isBound();
			}
			@Override
			public StyleableProperty<C3DDialogAnimation> getStyleableProperty(C3DDialog control) {
				return control.transitionTypeProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					DIALOG_TRANSITION
					);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		if(STYLEABLES == null){
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.CHILD_STYLEABLES;
	}



}

