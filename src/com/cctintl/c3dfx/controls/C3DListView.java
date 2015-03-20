package com.cctintl.c3dfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import com.cctintl.c3dfx.skins.C3DListViewSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.SizeConverter;

public class C3DListView<T> extends ListView<T> {

	public C3DListView() {
		super();
		this.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
			@Override
			public ListCell<T> call(ListView<T> listView) {
				return new C3DListCell<T>();
			}
		});
		initialize();    	
	}	 

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DListViewSkin<T>(this);
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
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "c3d-list-view";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
		expanded.addListener((o,oldVal,newVal)->{
			if(newVal) expand();
			else collapse();
		});
		
	}

	public void propagateMouseEventsToParent(){
		this.addEventHandler(MouseEvent.ANY, (e)->{
			e.consume();
			this.getParent().fireEvent(e);
		});
	}
	
	private StyleableDoubleProperty cellHorizontalMargin = new SimpleStyleableDoubleProperty(StyleableProperties.CELL_HORIZONTAL_MARGIN, C3DListView.this, "cellHorizontalMargin",  0.0);

	public Double getCellHorizontalMargin(){
		return cellHorizontalMargin == null ? 0 : cellHorizontalMargin.get();
	}
	public StyleableDoubleProperty cellHorizontalMarginProperty(){		
		return this.cellHorizontalMargin;
	}
	public void setCellHorizontalMargin(Double margin){
		this.cellHorizontalMargin.set(margin);
	}

	private StyleableDoubleProperty cellVerticalMargin = new SimpleStyleableDoubleProperty(StyleableProperties.CELL_VERTICAL_MARGIN, C3DListView.this, "cellVerticalMargin",  4.0 );

	public Double getCellVerticalMargin(){
		return cellVerticalMargin == null ? 4 : cellVerticalMargin.get();
	}
	public StyleableDoubleProperty cellVerticalMarginProperty(){		
		return this.cellVerticalMargin;
	}
	public void setCellVerticalMargin(Double margin){
		this.cellVerticalMargin.set(margin);
	}

	private StyleableDoubleProperty verticalGap = new SimpleStyleableDoubleProperty(StyleableProperties.VERTICAL_GAP, C3DListView.this, "verticalGap",  0.0 );

	public Double getVerticalGap(){
		return verticalGap == null ? 0 : verticalGap.get();
	}
	public StyleableDoubleProperty verticalGapProperty(){		
		return this.verticalGap;
	}
	public void setVerticalGap(Double gap){
		this.verticalGap.set(gap);
	}

	private StyleableBooleanProperty expanded = new SimpleStyleableBooleanProperty(StyleableProperties.EXPANDED, C3DListView.this, "expanded",  false );

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
		private static final CssMetaData< C3DListView<?>, Number> CELL_HORIZONTAL_MARGIN =
				new CssMetaData< C3DListView<?>, Number>("-fx-cell-horizontal-margin",
						SizeConverter.getInstance(), 0) {
			@Override
			public boolean isSettable(C3DListView<?> control) {
				return control.cellHorizontalMargin == null || !control.cellHorizontalMargin.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(C3DListView<?> control) {
				return control.cellHorizontalMarginProperty();
			}
		};
		private static final CssMetaData< C3DListView<?>, Number> CELL_VERTICAL_MARGIN =
				new CssMetaData< C3DListView<?>, Number>("-fx-cell-vertical-margin",
						SizeConverter.getInstance(), 4) {
			@Override
			public boolean isSettable(C3DListView<?> control) {
				return control.cellVerticalMargin == null || !control.cellVerticalMargin.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(C3DListView<?> control) {
				return control.cellVerticalMarginProperty();
			}
		};
		private static final CssMetaData< C3DListView<?>, Number> VERTICAL_GAP =
				new CssMetaData< C3DListView<?>, Number>("-fx-vertical-gap",
						SizeConverter.getInstance(), 0) {
			@Override
			public boolean isSettable(C3DListView<?> control) {
				return control.verticalGap == null || !control.verticalGap.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(C3DListView<?> control) {
				return control.verticalGapProperty();
			}
		};
		private static final CssMetaData< C3DListView<?>, Boolean> EXPANDED =
				new CssMetaData< C3DListView<?>, Boolean>("-fx-expanded",
						BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(C3DListView<?> control) {
				// it's only settable if the List is not shown yet
				return control.getHeight() == 0 && ( control.expanded == null || !control.expanded.isBound() );
			}
			@Override
			public StyleableBooleanProperty getStyleableProperty(C3DListView<?> control) {
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
