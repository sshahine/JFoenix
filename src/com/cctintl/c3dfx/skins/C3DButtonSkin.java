package com.cctintl.c3dfx.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DButton.ButtonType;
import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.DepthManager;
import com.cctintl.c3dfx.jidefx.CachedTimelineTransition;
import com.sun.javafx.scene.control.skin.ButtonSkin;
import com.sun.javafx.scene.control.skin.LabeledText;

public class C3DButtonSkin extends ButtonSkin {

	private final AnchorPane main = new AnchorPane();
	private StackPane buttonComponents = new StackPane();
	private C3DRippler buttonRippler;

	private Transition clickedAnimation ;

	private final Color disabledColor = Color.valueOf("#EAEAEA");

	private boolean invalid = true;
	private Rectangle buttonRect;

	public C3DButtonSkin(C3DButton button) {
		super(button);
		if(button.getBackground()==null)
			button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

		// create button
		buttonRect = new Rectangle();
		buttonRect.setArcHeight(7);
		buttonRect.setArcWidth(7);		
		buttonRect.setFill(Color.WHITE);

		if(button.isDisabled()) buttonRect.setFill(disabledColor);

		buttonComponents.getChildren().add(buttonRect);
		buttonRippler = new C3DRippler(buttonComponents){
			@Override protected Shape getMask(){
				Rectangle mask = new Rectangle(buttonRect.getWidth() - 0.1,buttonRect.getHeight() - 0.1); // -0.1 to prevent resizing the anchor pane
				mask.setArcHeight(buttonRect.getArcHeight());
				mask.setArcWidth(buttonRect.getArcWidth());					
				return mask;
			}
			@Override protected void initListeners(){
				ripplerPane.setOnMousePressed((event) -> {
					createRipple(event.getX(),event.getY());
				});
			}
		};
		main.getChildren().add(buttonRippler);


		// add listeners to the button
		button.widthProperty().addListener((o,oldVal,newVal)->buttonRect.setWidth(newVal.doubleValue()));
		button.heightProperty().addListener((o,oldVal,newVal)->buttonRect.setHeight(newVal.doubleValue()+1));
		button.buttonTypeProperty().addListener((o,oldVal,newVal)->updateButtonType(newVal));
		button.backgroundProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null){
				buttonRect.setFill(newVal.getFills().get(0).getFill());
				double radius = 7;
				if(newVal.getFills().get(0).getRadii().getTopLeftHorizontalRadius() > radius)
					radius = newVal.getFills().get(0).getRadii().getTopLeftHorizontalRadius();
				buttonRect.setArcHeight(radius);
				buttonRect.setArcWidth(radius);
			}
		});

		button.setOnMousePressed((e)->{
			if(clickedAnimation!=null){
				clickedAnimation.setRate(1);
				clickedAnimation.play();	
			}
		});
		button.setOnMouseReleased((e)->{
			if(clickedAnimation!=null){
				clickedAnimation.setRate(-1);
				clickedAnimation.play();
			}
		});

		updateButtonType(button.getButtonType());
		updateChildren();
	}

	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (main != null) {
			getChildren().add(main);
		}
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if(invalid){
			if(getSkinnable().getBackground()!=null){
				buttonRect.setFill(getSkinnable().getBackground().getFills().get(0).getFill());
				double radius = 7;
				if(getSkinnable().getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() > radius)
					radius = getSkinnable().getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius();
				buttonRect.setArcHeight(radius);
				buttonRect.setArcWidth(radius);
			}
			if(getChildren().get(0) instanceof LabeledText){
				buttonRippler.setRipplerFill(((LabeledText)getChildren().get(0)).getFill());			
				((LabeledText)getChildren().get(0)).fillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
			}else if(getChildren().get(0) instanceof Label){
				buttonRippler.setRipplerFill(((Label)getChildren().get(0)).getTextFill());			
				((Label)getChildren().get(0)).textFillProperty().addListener((o,oldVal,newVal)-> buttonRippler.setRipplerFill(newVal));
			}
			buttonComponents.getChildren().add(getChildren().get(0));
			invalid = false;
		}
		layoutLabelInArea(x, y, w, h);
	}

	private void updateButtonType(ButtonType type){
		switch (type) {
		case RAISED:
			DepthManager.setDepth(buttonRippler, 2);
			clickedAnimation = new ButtonClickTransition(); 
			break;
		default:
			buttonRippler.setEffect(null);
			break;
		}
	}


	private class ButtonClickTransition extends CachedTimelineTransition {

		public ButtonClickTransition() {
			super(buttonRippler, new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(((DropShadow)buttonRippler.getEffect()).radiusProperty(), DepthManager.getShadowAt(2).radiusProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonRippler.getEffect()).spreadProperty(), DepthManager.getShadowAt(2).spreadProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonRippler.getEffect()).offsetXProperty(), DepthManager.getShadowAt(2).offsetXProperty().get(), Interpolator.EASE_BOTH),
							new KeyValue(((DropShadow)buttonRippler.getEffect()).offsetYProperty(), DepthManager.getShadowAt(2).offsetYProperty().get(), Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(1000),
									new KeyValue(((DropShadow)buttonRippler.getEffect()).radiusProperty(), DepthManager.getShadowAt(5).radiusProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonRippler.getEffect()).spreadProperty(), DepthManager.getShadowAt(5).spreadProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonRippler.getEffect()).offsetXProperty(), DepthManager.getShadowAt(5).offsetXProperty().get(), Interpolator.EASE_BOTH),
									new KeyValue(((DropShadow)buttonRippler.getEffect()).offsetYProperty(), DepthManager.getShadowAt(5).offsetYProperty().get(), Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.2));
			setDelay(Duration.seconds(0));
		}

	}


}
