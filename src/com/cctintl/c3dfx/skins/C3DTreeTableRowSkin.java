package com.cctintl.c3dfx.skins;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DRippler;
import com.sun.javafx.scene.control.skin.TreeTableRowSkin;

/**
 * @author sshahine
 *
 * @param <T>
 */

public class C3DTreeTableRowSkin<T> extends TreeTableRowSkin<T> {

	private C3DRippler rippler;
	EventHandler<MouseEvent> ripplerEventPropagator = (event)-> rippler.fireEventProgrammatically(event);

	private Timeline expandedAnimation;
	private Timeline collapsedAnimation;

	private ChangeListener<Boolean> expandedListener = (o,oldVal,newVal)->{
		if(getSkinnable().getTreeItem()!=null && !getSkinnable().getTreeItem().isLeaf()){
			if(getSkinnable().getPseudoClassStates().toString().contains("expanded")) expandedAnimation.play();
			else collapsedAnimation.play();	
		}
	};

	public C3DTreeTableRowSkin(TreeTableRow<T> control) {
		super(control);
	}

	@Override protected void updateChildren() {
		super.updateChildren();
		StackPane container = new StackPane();	
		rippler = new C3DRippler(container);
		getChildren().add(0,rippler);
	}


	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		super.layoutChildren(x, y, w, h);		

		if(getSkinnable().getIndex() < getSkinnable().getTreeTableView().getExpandedItemCount()){	
			rippler.resize(w, h);
			for (int i = 1; i < getChildren().size(); i++) {
				getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_PRESSED, ripplerEventPropagator);
				getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_RELEASED, ripplerEventPropagator);
				getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED, ripplerEventPropagator);

				getChildren().get(i).addEventHandler(MouseEvent.MOUSE_PRESSED, ripplerEventPropagator);
				getChildren().get(i).addEventHandler(MouseEvent.MOUSE_RELEASED, ripplerEventPropagator);
				getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, ripplerEventPropagator);
			}


			// add arrow animation 	
			if(getSkinnable().getTreeItem()!=null && !getSkinnable().getTreeItem().isLeaf()){

				int arrowIndex = getChildren().size() - 1;
				StackPane arrow = (StackPane) ((StackPane) getChildren().get(arrowIndex)).lookup(".arrow");

				// add animation behavtion to the arrow 
				collapsedAnimation = new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(arrow.rotateProperty(), 0, Interpolator.EASE_BOTH)));
				expandedAnimation = new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(arrow.rotateProperty(), 90, Interpolator.EASE_BOTH)));
				expandedAnimation.setOnFinished((finish)->arrow.setRotate(90));
				collapsedAnimation.setOnFinished((finish)->arrow.setRotate(0));

				getSkinnable().getTreeItem().expandedProperty().removeListener(expandedListener);
				getSkinnable().getTreeItem().expandedProperty().addListener(expandedListener);

				Platform.runLater(()->{
					if(getSkinnable().getPseudoClassStates().toString().contains("expanded")){
						if(expandedAnimation.getStatus().equals(Status.STOPPED))
							arrow.setRotate(90);
					}else{
						if(collapsedAnimation.getStatus().equals(Status.STOPPED))
							arrow.setRotate(0);
					}	
				});

			}
		}
	}

}
