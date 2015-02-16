package com.cctintl.c3dfx.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DPasswordField;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

public class C3DPasswordFieldSkin extends TextFieldSkin{

	private StackPane effectsPane  = new StackPane();
	private AnchorPane cursorPane = new AnchorPane();
	
	private Line line = new Line();
	private Line focusedLine = new Line();
	private Label errorLabel = new Label();
	private StackPane errorIcon = new StackPane();
	
	private double endX;
	private double startX;
	private double mid ;

	private boolean invalid = true;

	public C3DPasswordFieldSkin(C3DPasswordField field) {
		super(field);
		
		// initial styles
//		field.setStyle("-fx-background-color: transparent ;-fx-font-weight: BOLD;-fx-prompt-text-fill: #808080;-fx-alignment: top-left ;");
		field.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		field.setAlignment(Pos.TOP_LEFT);
		
		
		effectsPane.getChildren().add(line);
		effectsPane.getChildren().add(focusedLine);
		effectsPane.setAlignment(Pos.BOTTOM_CENTER);
		StackPane.setMargin(line, new Insets(0,0,1,0));
		
		effectsPane.getChildren().add(cursorPane);
		StackPane.setAlignment(cursorPane, Pos.CENTER_LEFT);
		StackPane.setMargin(cursorPane, new Insets(0,0,5,40));
		
		errorLabel.getStyleClass().add("errorLabel");
		errorLabel.setStyle("-fx-text-fill : #D34336;-fx-font-size: 0.75em;");
		effectsPane.getChildren().add(errorLabel);
		
		StackPane.setAlignment(errorLabel, Pos.BOTTOM_LEFT);
		StackPane.setMargin(errorLabel, new Insets(0,0,-14,1));
		
		effectsPane.getChildren().add(errorIcon);
		
		field.focusedProperty().addListener((o,oldVal,newVal) -> {
			if (newVal) focus();
			else focusedLine.setVisible(false);
		});
		
		field.activeValidatorProperty().addListener((o,oldVal,newVal)->{
			if(newVal!=null){
				errorLabel.setText(newVal.getMessage());
				Node awsomeIcon = newVal.getAwsomeIcon();
				errorIcon.getChildren().add(awsomeIcon);
				StackPane.setAlignment(awsomeIcon, Pos.BOTTOM_RIGHT);
				StackPane.setMargin(awsomeIcon, new Insets(0,1,-14,0));
			}else{
				errorLabel.setText(null);
				errorIcon.getChildren().clear();
			}
			invalid = true;
		});
		field.prefWidthProperty().addListener((o,oldVal,newVal)-> {
			field.setMaxWidth(newVal.doubleValue());
			invalid = true;	
		});
	}

	@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefHeight(width, topInset, rightInset, bottomInset + 5, leftInset);
	}

	@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMaxHeight(width, topInset, rightInset, bottomInset + 5, leftInset);
	}
	@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMinHeight(width, topInset, rightInset, bottomInset + 1, leftInset);
	}


	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		super.layoutChildren(x, y, w, h);
		if(invalid){
			startX = getSkinnable().getBoundsInLocal().getMinX() ;
			endX = getSkinnable().getWidth() - getSkinnable().getBaselineOffset();
			
			line.setStartX( startX );
			line.setEndX(endX);
			line.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
			line.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
			line.setStroke(((C3DPasswordField)getSkinnable()).getUnFocusColor());
			line.setStrokeWidth(1);
			line.setStrokeType(StrokeType.CENTERED);
			if(getSkinnable().isDisabled()) line.getStrokeDashArray().addAll(2d);
			getSkinnable().disabledProperty().addListener((o,oldVal,newVal) -> {
				line.getStrokeDashArray().clear();
				if(newVal)
					line.getStrokeDashArray().addAll(2d);
			});
			
			mid = (endX - startX )/2;			
			focusedLine.setStartX(mid);
			focusedLine.setEndX(mid);
			focusedLine.setStartY(getSkinnable().getBoundsInLocal().getMaxY() );
			focusedLine.setEndY(getSkinnable().getBoundsInLocal().getMaxY() );
			focusedLine.setStroke(((C3DPasswordField)getSkinnable()).getFocusColor());
			focusedLine.setStrokeWidth(2);
			focusedLine.setStrokeType(StrokeType.CENTERED);
			focusedLine.setVisible(false);

			//			cursorPane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
			cursorPane.setMaxSize(40, getSkinnable().getHeight());
			cursorPane.setBackground(new Background(new BackgroundFill(((C3DPasswordField)getSkinnable()).getFocusColor(), CornerRadii.EMPTY, Insets.EMPTY)));
			cursorPane.setOpacity(0);
			
			this.getChildren().remove(effectsPane);
			this.getChildren().add(effectsPane);
		
			layoutInArea(effectsPane, x, y, w, h, -1, HPos.CENTER, VPos.BOTTOM);
			
			invalid = false;
		}		
		
	}

	private void focus(){
		Timeline linesAnimation = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(focusedLine.startXProperty(), mid ,Interpolator.EASE_BOTH),
						new KeyValue(focusedLine.visibleProperty(), false ,Interpolator.EASE_BOTH),									
						new KeyValue(focusedLine.endXProperty(), mid ,Interpolator.EASE_BOTH)
						),
						new KeyFrame(
								Duration.millis(5),
								new KeyValue(focusedLine.visibleProperty(), true ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(
										Duration.millis(150),
										new KeyValue(focusedLine.startXProperty(), startX ,Interpolator.EASE_BOTH),
										new KeyValue(focusedLine.endXProperty(), endX ,Interpolator.EASE_BOTH)
										)

				);

		Timeline cursorAnimation = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(cursorPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
						new KeyValue(cursorPane.scaleXProperty(), 1 ,Interpolator.EASE_BOTH),
						new KeyValue(cursorPane.translateXProperty(), 0 ,Interpolator.EASE_BOTH),
						new KeyValue(cursorPane.opacityProperty(), 0.75 ,Interpolator.EASE_BOTH)
						),
						new KeyFrame(
								Duration.millis(5),
								new KeyValue(cursorPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(
										Duration.millis(150),
										new KeyValue(cursorPane.scaleXProperty(), 1/cursorPane.getWidth() ,Interpolator.EASE_BOTH),
										new KeyValue(cursorPane.translateXProperty(), -65 ,Interpolator.EASE_BOTH),
										new KeyValue(cursorPane.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
										)

				);
		ParallelTransition transition = new ParallelTransition();
		transition.getChildren().add(linesAnimation);
		if(getSkinnable().getText().length() == 0)
			transition.getChildren().add(cursorAnimation);
		transition.play();
	}

}
