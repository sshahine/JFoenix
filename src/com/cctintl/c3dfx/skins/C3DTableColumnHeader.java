package com.cctintl.c3dfx.skins;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

/**
 * @author sshahine
 *
 */

public class C3DTableColumnHeader extends TableColumnHeader {

	private StackPane container;
	private GridPane arrowPane;
	private Region arrow;
	private Timeline arrowAnimation;

	public C3DTableColumnHeader(TableViewSkinBase skin, TableColumnBase tc) {
		super(skin, tc);

	}

	@Override protected void layoutChildren() {
		super.layoutChildren();

		double w = snapSize(getWidth()) - (snappedLeftInset() + snappedRightInset());

		container = new StackPane();
		container.resizeRelocate(snappedLeftInset(), 0, w, getHeight());

		for(int i = 0 ; i < getChildren().size();){
			Node child = getChildren().get(i);
			container.getChildren().add(child);
		}

		// add animation to sorting arrow
		
		if(container.getChildren().size() > 1){
			arrowPane = (GridPane) container.getChildren().get(1);
			arrow = (Region) arrowPane.getChildren().get(0);			
			arrowPane.maxWidthProperty().bind(arrow.widthProperty());
			StackPane.setAlignment(arrowPane, Pos.CENTER_RIGHT);
			StackPane.setMargin(arrowPane, new Insets(0,10,0,0));

			if(arrowAnimation!=null && arrowAnimation.getStatus().equals(Status.RUNNING)) arrowAnimation.stop();
			if(arrow.getRotate() == 180){
				arrowPane.setOpacity(0);
				arrowPane.setTranslateY(getHeight()/4);	
				arrowAnimation = new Timeline(new KeyFrame(Duration.millis(320),
						new KeyValue(arrowPane.opacityProperty(), 1, Interpolator.EASE_BOTH),
						new KeyValue(arrowPane.translateYProperty(), 0, Interpolator.EASE_BOTH)));
			}else if(arrow.getRotate() == 0){				 
				arrowPane.setRotate(-180);	
				arrowPane.setTranslateX(-3);
				arrowPane.setTranslateY(1);
				arrowAnimation = new Timeline(new KeyFrame(Duration.millis(160),
						new KeyValue(arrowPane.translateXProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(arrowPane.translateYProperty(), -1, Interpolator.EASE_BOTH),
						new KeyValue(arrowPane.rotateProperty(), 0, Interpolator.EASE_BOTH)));						
			}
			arrowAnimation.play();
		}

		if(arrowPane!=null && container.getChildren().size() == 1){
			if(arrowAnimation!=null && arrowAnimation.getStatus().equals(Status.RUNNING)) arrowAnimation.stop();
			arrowPane.setVisible(true);
			container.getChildren().add(arrowPane);
			arrowAnimation = new Timeline(new KeyFrame(Duration.millis(320),
					new KeyValue(arrowPane.opacityProperty(), 0, Interpolator.EASE_BOTH),
					new KeyValue(arrowPane.translateYProperty(), getHeight()/4, Interpolator.EASE_BOTH)));
			arrowAnimation.setOnFinished((finish)->arrowPane.setVisible(false));
			arrowAnimation.play();
		}

		getChildren().add(container);
	}

}
