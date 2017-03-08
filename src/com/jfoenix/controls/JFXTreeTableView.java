/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.skins.JFXTreeTableViewSkin;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

/**
 * JFXTreeTableView is the material design implementation of table view.
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 * DOC: not completed
 */
public class JFXTreeTableView<S extends RecursiveTreeObject<S>> extends TreeTableView<S> {

	private TreeItem<S> originalRoot;

	/**
	 * {@inheritDoc}
	 */
	public JFXTreeTableView() {
		super();
		init();
	}

	/**
	 * {@inheritDoc}
	 */
	public JFXTreeTableView(TreeItem<S> root) {
		super(root);
		originalRoot = root;
		init();
	}

	/**
	 * propagate any mouse event on the tree table view to its parent
	 */	
	public void propagateMouseEventsToParent(){
		this.addEventHandler(MouseEvent.ANY, (e)->{
			e.consume();
			this.getParent().fireEvent(e);
		});
	}

	/** {@inheritDoc} */
	@Override protected Skin<?> createDefaultSkin() {
		return new JFXTreeTableViewSkin<S>(this);
	}

	protected void init(){
		this.setRowFactory(new Callback<TreeTableView<S>, TreeTableRow<S>>() {
			@Override
			public TreeTableRow<S> call(TreeTableView<S> param) {
				return new JFXTreeTableRow<S>();
			}
		});	

		this.getSelectionModel().selectedItemProperty().addListener((o,oldVal,newVal)->{
			if(newVal != null && newVal.getValue() != null)
				itemWasSelected = true;
		});

		this.predicate.addListener((o,oldVal,newVal)-> filter(newVal));

		this.rootProperty().addListener((o,oldVal,newVal)->{
			if(newVal != null){
				setCurrentItemsCount(count(getRoot()));
			}
		});

		// compute the current items count
		setCurrentItemsCount(count(getRoot()));

		//		getGroupOrder().addListener((Change<? extends TreeTableColumn<S, ?>> c) ->{
		//			group();
		//		});
	}

	@Override
	public int getTreeItemLevel(TreeItem<?> node) {
		final TreeItem<?> root = getRoot();
		if (node == null) return -1;
		if (node == root) return 0;
		int level = 0;
		TreeItem<?> parent = node.getParent();
		while (parent != null) {
			level++;
			if (parent == root ) {
				break;
			}
			// handle group nodes
			if(parent.getValue() !=null
			&& parent.getValue() instanceof RecursiveTreeObject
			&& ((RecursiveTreeObject<?>)parent.getValue()).getGroupedColumn()!=null)
				level--;
			parent = parent.getParent();
		}
		return level;
	}

	/*
	 * clear selection before sorting as its bugged in java
	 */
	private boolean itemWasSelected = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sort(){
		getSelectionModel().clearSelection();
		super.sort();
		if(itemWasSelected)
			getSelectionModel().select(0);
	}


	// Allows for multiple column Grouping based on the order of the TreeTableColumns
	// in this observableArrayList.
	//TODO: treat group order as sort order
	private ObservableList<TreeTableColumn<S,?>> groupOrder = FXCollections.observableArrayList();

	final ObservableList<TreeTableColumn<S,?>> getGroupOrder() {
		return groupOrder;
	}

	// semaphore is used to force mutual exclusion while group/ungroup operation 
	private Semaphore groupingSemaphore = new Semaphore(1);

	// this method will regroup the treetableview according to columns group order
	public void group(TreeTableColumn<S, ?>... treeTableColumns){
		// init groups map
		if(groupingSemaphore.tryAcquire()){
			if(groupOrder.size() == 0) groups = new HashMap<>();
			try{
				if(originalRoot == null) originalRoot = getRoot();
				for (TreeTableColumn<S, ?> treeTableColumn : treeTableColumns) 
					groups = group(treeTableColumn, groups, null, (RecursiveTreeItem<S>) originalRoot);			
				groupOrder.addAll(treeTableColumns);
				// update table ui
				buildGroupedRoot(groups, null, 0);
			}catch(Exception e){
				e.printStackTrace();
			}
			groupingSemaphore.release();
		}
	}

	private void refreshGroups(List<TreeTableColumn<S, ?>> groupColumns){
		groups = new HashMap<>();
		for (TreeTableColumn<S, ?> treeTableColumn : groupColumns) 
			groups = group(treeTableColumn, groups, null, (RecursiveTreeItem<S>) originalRoot);
		groupOrder.addAll(groupColumns);
		// update table ui
		buildGroupedRoot(groups, null, 0);
	}

	public void unGroup(TreeTableColumn<S, ?>... treeTableColumns){
		if(groupingSemaphore.tryAcquire()){
			try {
				if(groupOrder.size() > 0){
					groupOrder.removeAll(treeTableColumns);
					List<TreeTableColumn<S, ?>> grouped = new ArrayList<TreeTableColumn<S, ?>>();
					grouped.addAll(groupOrder);
					groupOrder.clear();
					JFXUtilities.runInFXAndWait(()->{
						ArrayList<TreeTableColumn<S, ?>> sortOrder = new ArrayList<>();
						sortOrder.addAll(getSortOrder());
						// needs to reset the children in order to update the parent
						List children = Arrays.asList(originalRoot.getChildren().toArray());
						originalRoot.getChildren().clear();
						originalRoot.getChildren().setAll(children);
						// reset the original root
						setRoot(originalRoot);
						getSelectionModel().select(0);	
						getSortOrder().addAll(sortOrder);
						if(grouped.size() != 0)
							refreshGroups(grouped);						
					});	
				}		
			} catch (Exception e) {
				e.printStackTrace();
			}
			groupingSemaphore.release();
		}
	}

	private Map group(TreeTableColumn<S, ?> column, Map parentGroup , Object key, RecursiveTreeItem<S> root){
		if(parentGroup.isEmpty()){
			parentGroup = groupByFunction(root.filteredItems, column);
			return parentGroup;
		}
		Object value = parentGroup.get(key);
		if(value instanceof List){
			Object newGroup = groupByFunction((List) value, column);
			parentGroup.put(key, newGroup);
			return parentGroup;
		}else if(value instanceof Map){
			for (Object childKey : ((Map)value).keySet()) 
				value = group(column,(Map)value,childKey, root);
			parentGroup.put(key, value);
			return parentGroup;
		}else if(key == null){
			for (Object childKey : parentGroup.keySet()) 
				parentGroup = group(column, parentGroup,childKey, root);
			return parentGroup;
		}
		return parentGroup;
	}

	/*
	 * stream implementation is faster regarding the performance 
	 * however its not compatable when porting to mobile
	 */
	//	protected Map groupByFunction(List<TreeItem<S>> items, TreeTableColumn<S, ?> column){
	//		return items.stream().collect(Collectors.groupingBy(child-> column.getCellData((TreeItem<S>)child)));
	//	}

	protected Map groupByFunction(List<TreeItem<S>> items, TreeTableColumn<S, ?> column){
		Map<Object, List<TreeItem<S>>> map = new HashMap<Object, List<TreeItem<S>>>();
		for (TreeItem<S> child : items) {
			Object key = column.getCellData(child);
			if (map.get(key) == null) {
				map.put(key, new ArrayList<TreeItem<S>>());
			}
			map.get(key).add(child);
		}
		return map;
	}

	/*
	 * this method is used to update tree items and set the new root 
	 * after grouping the data model
	 */
	private void buildGroupedRoot(Map groupedItems, RecursiveTreeItem parent , int groupIndex){
		boolean setRoot = false;
		if(parent == null){
			parent = new RecursiveTreeItem<>(new RecursiveTreeObject(), RecursiveTreeObject::getChildren);
			setRoot = true;
		}

		for(Object key : groupedItems.keySet()){
			RecursiveTreeObject groupItem = new RecursiveTreeObject<>();
			groupItem.setGroupedValue(key);
			groupItem.setGroupedColumn(groupOrder.get(groupIndex));

			RecursiveTreeItem node = new RecursiveTreeItem<>(groupItem, RecursiveTreeObject::getChildren);
			// TODO: need to be removed once the selection issue is fixed
			node.expandedProperty().addListener((o,oldVal,newVal)->{
				getSelectionModel().clearSelection();
			});

			parent.originalItems.add(node);
			parent.getChildren().add(node);			

			Object children = groupedItems.get(key);
			if(children instanceof List){
				node.originalItems.addAll((List)children);
				node.getChildren().addAll((List)children);
			}else if(children instanceof Map){
				buildGroupedRoot((Map)children, node, groupIndex+1);
			}
		}

		// update ui
		if(setRoot){
			final RecursiveTreeItem<S> newParent = parent;
			JFXUtilities.runInFX(()->{
				ArrayList<TreeTableColumn<S, ?>> sortOrder = new ArrayList<>();
				sortOrder.addAll(getSortOrder());
				setRoot(newParent);		
				getSortOrder().addAll(sortOrder);
				getSelectionModel().select(0);
			});
		}
	}


	/*
	 * this method will filter the treetable and it  
	 */

	private Timer t;

	private final void filter(Predicate<TreeItem<S>> predicate){
		if(originalRoot == null) originalRoot = getRoot();
		if(t!=null){
			t.cancel();
			t.purge();
		}
		t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				/*
				 *  filter the original root and regroup the data
				 */
				new Thread(()->{
					// filter the ungrouped root
					((RecursiveTreeItem) originalRoot).setPredicate(predicate);
					// regroup the data
					reGroup();
					Platform.runLater(()->{
						getSelectionModel().select(0);	
						setCurrentItemsCount(count(getRoot()));
					});
				}).start();
			}
		},  500);
	}

	public void reGroup() {
		if(!groupOrder.isEmpty()){
			ArrayList<TreeTableColumn<S, ?>> tempGroups = new ArrayList<>(groupOrder);
			groupOrder.clear();
			group(tempGroups.toArray(new TreeTableColumn[tempGroups.size()]));	
		}
	}

	private ObjectProperty<Predicate<TreeItem<S>>> predicate = new SimpleObjectProperty<Predicate<TreeItem<S>>>((TreeItem<S> t) -> true);

	public final ObjectProperty<Predicate<TreeItem<S>>> predicateProperty() {
		return this.predicate;
	}

	public final Predicate<TreeItem<S>> getPredicate() {
		return this.predicateProperty().get();
	}

	public final void setPredicate(final Predicate<TreeItem<S>> predicate) {
		this.predicateProperty().set(predicate);
	}

	private IntegerProperty currentItemsCount = new SimpleIntegerProperty(0);
	private Map<Object, Map<Object, ?>> groups;

	/**
	 * @return the initial tree items count ( add / remove items should be handled manually for now )
	 */
	public final IntegerProperty currentItemsCountProperty() {
		return this.currentItemsCount;
	}

	/**
	 * @return the initial tree items count ( add / remove items should be handled manually for now )
	 */
	public final int getCurrentItemsCount() {
		return this.currentItemsCountProperty().get();
	}

	/**
	 * sets the current items count
	 * @param currentItemsCount
	 */
	public final void setCurrentItemsCount(final int currentItemsCount) {
		this.currentItemsCountProperty().set(currentItemsCount);
	}

	private int count(TreeItem<?> node){
		if(node == null ) return 0;

		int count = 1;
		if(node.getValue() == null ||  (node.getValue() != null && node.getValue().getClass().equals(RecursiveTreeObject.class))) count = 0;
		for (TreeItem<?> child : node.getChildren()) {
			count += count(child);
		}
		return count;
	}
}
