/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import com.jfoenix.skins.JFXListViewSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.SizeConverter;

public class JFXListView<T> extends ListView<T> {

	public JFXListView() {
		super();
		// bug : to prevent selection when focusing the list
		this.setFocusTraversable(false);
		this.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
			@Override
			public ListCell<T> call(ListView<T> listView) {
				return new JFXListCell<T>();
			}
		});
		initialize();    	
	}	 

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new JFXListViewSkin<T>(this);
	}

	private ObjectProperty<Integer> depthProperty = new SimpleObjectProperty<Integer>(0);
	public ObjectProperty<Integer> depthProperty(){
		return depthProperty;
	}
	public int getDepthProperty(){
		return depthProperty.get();
	}
	public void setDepthProperty(int depth){
		depthProperty.set(depth);
	}	

	private DoubleProperty currentVerticalGapProperty = new SimpleDoubleProperty();

	public DoubleProperty currentVerticalGapProperty(){
		return currentVerticalGapProperty;
	}
	public double getCurrentVerticalGap(){
		return currentVerticalGapProperty.get();
	}
	public void setCurrentVerticalGap(double gap){
		currentVerticalGapProperty.set(gap);
	}

	private void expand(){
		currentVerticalGapProperty.set(verticalGap.get());
		expanded.set(true);
	}

	private void collapse(){	
		currentVerticalGapProperty.set(0);
		expanded.set(false);
	}
	
	
	/***************************************************************************
	 *                                                                         *
	 * SubList Properties                                                      *
	 *                                                                         *
	 **************************************************************************/
	
	private ObjectProperty<Node> groupnode = new SimpleObjectProperty<Node>(new Label("GROUP"));

	public Node getGroupnode(){
		return groupnode.get();
	}
	public void setGroupnode(Node node){
		this.groupnode.set(node);
	}

	// sublists property
	private ObjectProperty<ObservableList<JFXListView<?>>> sublistsProperty = new SimpleObjectProperty<ObservableList<JFXListView<?>>>(FXCollections.observableArrayList());	
	private LinkedHashMap<Integer, JFXListView<?>> sublistsIndices = new LinkedHashMap<Integer, JFXListView<?>>();
	public void addSublist(JFXListView<?> subList, int index){
		if(!sublistsProperty.get().contains(subList)){
			sublistsProperty.get().add(subList);
			sublistsIndices.put(index, subList);
			subList.getSelectionModel().selectedIndexProperty().addListener((o,oldVal,newVal)->{
				if(newVal.intValue() != -1){
					getOverAllSelectedIndex();
				}
			});
		}
	}

	// selection property across list and its sublists
	private ReadOnlyObjectWrapper<Integer> overAllIndexProperty = new ReadOnlyObjectWrapper<Integer>(-1);

	public ReadOnlyObjectProperty<Integer> overAllIndexProperty(){
		return overAllIndexProperty.getReadOnlyProperty();
	}

	private void getOverAllSelectedIndex(){
		// if item from the list is selected
		if(this.getSelectionModel().getSelectedIndex() != -1 ){
			int selectedIndex = this.getSelectionModel().getSelectedIndex();
			Iterator<Integer> itr = sublistsIndices.keySet().iterator();
			int preItemsSize = 0;
			while(itr.hasNext()){
				Integer key = itr.next();
				if(key < selectedIndex) preItemsSize += sublistsIndices.get(key).getItems().size()-1;
			}
//			int preItemsSize = sublistsIndices.keySet().stream().filter(key-> key < selectedIndex).mapToInt(key->sublistsIndices.get(key).getItems().size()-1).sum();
			overAllIndexProperty.set(selectedIndex + preItemsSize);
		}else{
			Iterator<Integer> itr = sublistsIndices.keySet().iterator();
			ArrayList<Object> selectedList = new ArrayList<>();
			while(itr.hasNext()){
				Integer key = itr.next();
				if(sublistsIndices.get(key).getSelectionModel().getSelectedIndex() != -1){
					selectedList.add(key);
				}
			}
			if(selectedList.size() > 0){
				itr = sublistsIndices.keySet().iterator();
				int preItemsSize = 0;
				while(itr.hasNext()){
					Integer key = itr.next();
					if(key < ((Integer)selectedList.get(0))){
						preItemsSize += sublistsIndices.get(key).getItems().size()-1;
					}
				}
				overAllIndexProperty.set(preItemsSize + (Integer)selectedList.get(0)+ sublistsIndices.get(selectedList.get(0)).getSelectionModel().getSelectedIndex());
			}else{
				overAllIndexProperty.set(-1);
			}
//			Object[] selectedList = sublistsIndices.keySet().stream().filter(key-> sublistsIndices.get(key).getSelectionModel().getSelectedIndex() != -1).toArray();
//			if(selectedList.length > 0){			
//				int preItemsSize = sublistsIndices.keySet().stream().filter(key-> key < ((Integer)selectedList[0])).mapToInt(key-> sublistsIndices.get(key).getItems().size()-1).sum();
//				overAllIndexProperty.set(preItemsSize + (Integer)selectedList[0] + sublistsIndices.get(selectedList[0]).getSelectionModel().getSelectedIndex());
//			}else{ 
//				overAllIndexProperty.set(-1);
//			}
		}		
	}
	

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "jfx-list-view";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		expanded.addListener((o,oldVal,newVal)->{
			if(newVal) expand();
			else collapse();
		});

		// handle selection model on the list ( FOR NOW : we only support single selection on the list if it contains sublists)
		sublistsProperty.get().addListener( (ListChangeListener.Change<? extends JFXListView<?>> c)->{ 
			while (c.next()) {
				if(c.wasAdded() || c.wasUpdated() || c.wasReplaced()){
					if( sublistsProperty.get().size() == 1) {
						this.getSelectionModel().selectedItemProperty().addListener((o,oldVal,newVal)->clearSelection(this));
						// prevent selecting the sublist item by clicking the right mouse button						
						this.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
					}
					c.getAddedSubList().forEach(item -> item.getSelectionModel().selectedItemProperty().addListener((o,oldVal,newVal)->clearSelection(item)));
				}
			}
		});		

		// listen to index changes
		this.getSelectionModel().selectedIndexProperty().addListener((o,oldVal,newVal)->{
			if(newVal.intValue() != -1){
				getOverAllSelectedIndex();	
			}
		});
	}


	// allow single selection across the list and all sublits
	private boolean allowClear = true;
	private void clearSelection(JFXListView<?> selectedList){
		if(allowClear){
			allowClear = false;
			if(this != selectedList) this.getSelectionModel().clearSelection();
			for(int i =0 ; i < sublistsProperty.get().size();i++)
				if(sublistsProperty.get().get(i) != selectedList)
					sublistsProperty.get().get(i).getSelectionModel().clearSelection();
//			sublistsProperty.get().stream().filter(list-> list!=selectedList).forEach(list->list.getSelectionModel().clearSelection());
			allowClear = true;
		}
	}

	// propagate mouse events to the parent node ( e.g. to allow dragging while clicking on the list)
	public void propagateMouseEventsToParent(){
		this.addEventHandler(MouseEvent.ANY, (e)->{
			e.consume();
			this.getParent().fireEvent(e);
		});
	}

	private StyleableDoubleProperty cellHorizontalMargin = new SimpleStyleableDoubleProperty(StyleableProperties.CELL_HORIZONTAL_MARGIN, JFXListView.this, "cellHorizontalMargin",  0.0);

	public Double getCellHorizontalMargin(){
		return cellHorizontalMargin == null ? 0 : cellHorizontalMargin.get();
	}
	public StyleableDoubleProperty cellHorizontalMarginProperty(){		
		return this.cellHorizontalMargin;
	}
	public void setCellHorizontalMargin(Double margin){
		this.cellHorizontalMargin.set(margin);
	}

	private StyleableDoubleProperty cellVerticalMargin = new SimpleStyleableDoubleProperty(StyleableProperties.CELL_VERTICAL_MARGIN, JFXListView.this, "cellVerticalMargin",  4.0 );

	public Double getCellVerticalMargin(){
		return cellVerticalMargin == null ? 4 : cellVerticalMargin.get();
	}
	public StyleableDoubleProperty cellVerticalMarginProperty(){		
		return this.cellVerticalMargin;
	}
	public void setCellVerticalMargin(Double margin){
		this.cellVerticalMargin.set(margin);
	}

	private StyleableDoubleProperty verticalGap = new SimpleStyleableDoubleProperty(StyleableProperties.VERTICAL_GAP, JFXListView.this, "verticalGap",  0.0 );

	public Double getVerticalGap(){
		return verticalGap == null ? 0 : verticalGap.get();
	}
	public StyleableDoubleProperty verticalGapProperty(){		
		return this.verticalGap;
	}
	public void setVerticalGap(Double gap){
		this.verticalGap.set(gap);
	}

	private StyleableBooleanProperty expanded = new SimpleStyleableBooleanProperty(StyleableProperties.EXPANDED, JFXListView.this, "expanded",  false );

	public Boolean isExpanded(){
		return expanded == null ? false : expanded.get();
	}
	public StyleableBooleanProperty expandedProperty(){		
		return this.expanded;
	}
	public void setExpanded(Boolean expanded){
		this.expanded.set(expanded);
	}

	private static class StyleableProperties {
		private static final CssMetaData< JFXListView<?>, Number> CELL_HORIZONTAL_MARGIN =
				new CssMetaData< JFXListView<?>, Number>("-fx-cell-horizontal-margin",
						SizeConverter.getInstance(), 0) {
			@Override
			public boolean isSettable(JFXListView<?> control) {
				return control.cellHorizontalMargin == null || !control.cellHorizontalMargin.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXListView<?> control) {
				return control.cellHorizontalMarginProperty();
			}
		};
		private static final CssMetaData< JFXListView<?>, Number> CELL_VERTICAL_MARGIN =
				new CssMetaData< JFXListView<?>, Number>("-fx-cell-vertical-margin",
						SizeConverter.getInstance(), 4) {
			@Override
			public boolean isSettable(JFXListView<?> control) {
				return control.cellVerticalMargin == null || !control.cellVerticalMargin.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXListView<?> control) {
				return control.cellVerticalMarginProperty();
			}
		};
		private static final CssMetaData< JFXListView<?>, Number> VERTICAL_GAP =
				new CssMetaData< JFXListView<?>, Number>("-fx-vertical-gap",
						SizeConverter.getInstance(), 0) {
			@Override
			public boolean isSettable(JFXListView<?> control) {
				return control.verticalGap == null || !control.verticalGap.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXListView<?> control) {
				return control.verticalGapProperty();
			}
		};
		private static final CssMetaData< JFXListView<?>, Boolean> EXPANDED =
				new CssMetaData< JFXListView<?>, Boolean>("-fx-expanded",
						BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(JFXListView<?> control) {
				// it's only settable if the List is not shown yet
				return control.getHeight() == 0 && ( control.expanded == null || !control.expanded.isBound() );
			}
			@Override
			public StyleableBooleanProperty getStyleableProperty(JFXListView<?> control) {
				return control.expandedProperty();
			}
		};
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					CELL_HORIZONTAL_MARGIN,
					CELL_VERTICAL_MARGIN,
					VERTICAL_GAP,
					EXPANDED
					);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
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
