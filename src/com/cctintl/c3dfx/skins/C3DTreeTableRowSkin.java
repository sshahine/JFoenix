package com.cctintl.c3dfx.skins;

import java.util.Map;
import java.util.WeakHashMap;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.scene.control.skin.TreeTableRowSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

/**
 * @author sshahine
 *
 * @param <T>
 */

public class C3DTreeTableRowSkin<T> extends TreeTableRowSkin<T> {


	static final Map<Control, Double> maxDisclosureWidthMap = new WeakHashMap<Control, Double>();

	private C3DRippler rippler;
	private int oldselectedIndex = -1;
	private int maxChildIndex = -1;
	private EventHandler<MouseEvent> ripplerEventPropagator = (event)-> {
		/*
		 * fixed the issue of rippler is being stuck at the pressed state while collapsing
		 * a group that has one of its items selected
		 */
		if(getSkinnable().getTreeItem()!=null && !getSkinnable().getTreeItem().isLeaf()){
			if(!(oldselectedIndex > getSkinnable().getIndex() && oldselectedIndex  < maxChildIndex))
				rippler.fireEventProgrammatically(event);	
			oldselectedIndex = getSkinnable().getTreeTableView().getSelectionModel().getSelectedIndex();
		}else{
			rippler.fireEventProgrammatically(event);
		}
	};

	// this vairable is used to hold the expanded/collapsed row index
	private static int expandedIndex = -1; 
	// this variable is used to hold the rippler while expanding/collapsing a row
	private static C3DTreeTableRowSkin<?> oldSkin = null;
	// this variable indicates whether an expand/collapse operation is triggered
	private boolean expandTriggered = false;


	private ChangeListener<Boolean> expandedListener = (o,oldVal,newVal)->{
		if(getSkinnable().getTreeItem()!=null && !getSkinnable().getTreeItem().isLeaf()){
			expandedIndex = getSkinnable().getIndex();
			oldSkin = this;
			expandTriggered = true;
		}
	};
	private Timeline collapsedAnimation;
	private Animation expandedAnimation;

	public C3DTreeTableRowSkin(TreeTableRow<T> control) {
		super(control);
		getSkinnable().indexProperty().addListener((o,oldVal,newVal)->{
			if(newVal.intValue() != -1){
				if(newVal.intValue() == expandedIndex){
					expandTriggered = true;
					expandedIndex = -1;
				}else{
					expandTriggered = false;
				}
			}
		});


		/*
		 * fixed the issue of rippler is being stuck at the pressed state while collapsing
		 * a group that has one of its items selected
		 */
		getSkinnable().getTreeTableView().getSelectionModel().selectedIndexProperty().addListener((o,oldVal,newVal)->{
			oldselectedIndex = oldVal.intValue();		
		});
		getSkinnable().addEventFilter(MouseEvent.MOUSE_PRESSED, (press)->{
			TreeItem<T> temp = getSkinnable().getTreeItem();
			while(temp!=null && temp.nextSibling()==null) temp = temp.getParent();		
			maxChildIndex = temp != null ? getSkinnable().getTreeTableView().getRow(temp.nextSibling()) : getSkinnable().getTreeTableView().getExpandedItemCount();
		});
	}


	@Override protected void updateChildren() {
		super.updateChildren();				
		if(getSkinnable().getIndex() > -1){
			if(oldSkin != this){
				if( (!(expandedIndex == getSkinnable().getIndex() || expandTriggered) || rippler == null)
						|| !getSkinnable().isSelected()){
					rippler = new C3DRippler(new StackPane());
				}else{
					this.rippler = oldSkin.rippler;
				}	
			}else{
				if(!expandTriggered){
					rippler = new C3DRippler(new StackPane());
				}else if(!getSkinnable().isSelected()){
					rippler = new C3DRippler(new StackPane());
				}
			}
			getChildren().add(0,rippler);
		}
	}


	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {		
		// allow custom skin to grouped rows
		getSkinnable().getStyleClass().remove("tree-table-row-group");
		if(getSkinnable().getTreeItem() != null && getSkinnable().getTreeItem().getValue() instanceof RecursiveTreeObject && getSkinnable().getTreeItem().getValue().getClass() == RecursiveTreeObject.class)
			getSkinnable().getStyleClass().add("tree-table-row-group");

		if(getSkinnable().getIndex() > -1 && getSkinnable().getTreeTableView().getTreeItem(getSkinnable().getIndex()) != null){
			super.layoutChildren(x, y, w, h);

			//add rippler effects to each row in the table
			rippler.resize(w, h);
			for (int i = 1; i < getChildren().size(); i++) {
				getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_PRESSED, ripplerEventPropagator);
				getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_RELEASED, ripplerEventPropagator);
				getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED, ripplerEventPropagator);

				getChildren().get(i).addEventHandler(MouseEvent.MOUSE_PRESSED, ripplerEventPropagator);
				getChildren().get(i).addEventHandler(MouseEvent.MOUSE_RELEASED, ripplerEventPropagator);
				getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, ripplerEventPropagator);
			}

			// disclosure row case
			if(getSkinnable().getTreeItem()!=null && !getSkinnable().getTreeItem().isLeaf()){

				// register the width of disclosure node
				Control c = getVirtualFlowOwner();
				final double defaultDisclosureWidth = maxDisclosureWidthMap.containsKey(c) ? maxDisclosureWidthMap.get(c) : 0;
				double disclosureWidth =  getDisclosureNode().prefWidth(h);
				if (disclosureWidth > defaultDisclosureWidth) {
					maxDisclosureWidthMap.put(c, disclosureWidth);
					// RT-36359: The recorded max width of the disclosure node
					// has increased. We need to go back and request all
					// earlier rows to update themselves to take into account
					// this increased indentation.
					Parent p = getSkinnable();
					while (p != null) {
						if (p instanceof VirtualFlow)
							break;
						p = p.getParent();
					}
					if(p!=null){
						final VirtualFlow<?> flow = (VirtualFlow<?>) p;
						for (int i = flow.getFirstVisibleCell().getIndex() ; i <= flow.getLastVisibleCell().getIndex(); i++) {
							IndexedCell<?> cell = flow.getCell(i);							
							if (cell == null || cell.isEmpty() || cell.getIndex() >= getSkinnable().getIndex()) continue;
							cell.requestLayout();
							cell.layout();
						}
					}
				}


				// relocating the disclosure node according to the grouping column
				Node arrow = ((Parent)getDisclosureNode()).getChildrenUnmodifiable().get(0);
				Node col = getChildren().get((getSkinnable().getTreeTableView().getTreeItemLevel(getSkinnable().getTreeItem())+1));
				if(getSkinnable().getItem() instanceof RecursiveTreeObject){
					if(((RecursiveTreeObject<?>)getSkinnable().getItem()).getGroupedColumn()!=null){
						int index = getSkinnable().getTreeTableView().getColumns().indexOf(((RecursiveTreeObject<?>)getSkinnable().getItem()).getGroupedColumn());
						//						getSkinnable().getTreeTableView().getColumns().get(index).getText();
						col = getChildren().get(index+2);
					}
				}								
				arrow.getParent().setTranslateX(col.getBoundsInParent().getMinX());
				arrow.getParent().setLayoutX(0);


				// add disclosure node animation 	
				if(expandedAnimation == null || !expandedAnimation.getStatus().equals(Status.RUNNING)){
					expandedAnimation = new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(arrow.rotateProperty(), 90, Interpolator.EASE_BOTH)));
					expandedAnimation.setOnFinished((finish)->arrow.setRotate(90));
				}
				if(collapsedAnimation == null || !collapsedAnimation.getStatus().equals(Status.RUNNING)){
					collapsedAnimation = new Timeline(new KeyFrame(Duration.millis(160), new KeyValue(arrow.rotateProperty(), 0, Interpolator.EASE_BOTH)));
					collapsedAnimation.setOnFinished((finish)->arrow.setRotate(0));
				}
				getSkinnable().getTreeItem().expandedProperty().removeListener(expandedListener);
				getSkinnable().getTreeItem().expandedProperty().addListener(expandedListener);

				if(expandTriggered){
					if(getSkinnable().getTreeTableView().getTreeItem(getSkinnable().getIndex()).isExpanded()){
						arrow.setRotate(0);
						expandedAnimation.play();
					}else{
						arrow.setRotate(90);
						collapsedAnimation.play();
					}
					expandTriggered = false;
				}else{
					if(getSkinnable().getTreeTableView().getTreeItem(getSkinnable().getIndex()).isExpanded()){
						if(!expandedAnimation.getStatus().equals(Status.RUNNING))
							arrow.setRotate(90);
					}else{
						if(!collapsedAnimation.getStatus().equals(Status.RUNNING))
							arrow.setRotate(0);
					}	
				}
			}
		}

	}

}
