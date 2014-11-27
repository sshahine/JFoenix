package com.cctintl.c3dfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.cctintl.c3dfx.skins.C3DCheckBoxSkin;
import com.sun.javafx.css.converters.PaintConverter;

public class C3DCheckBox extends CheckBox {

	public C3DCheckBox(String label){
		super(label);
		initialize();
	}

	public C3DCheckBox(){
		super();
		initialize();
	}

	private void initialize() {
		this.getStyleClass().add("c3d-check-box");        
	}

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DCheckBoxSkin(this);
	}


	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableObjectProperty<Paint> checkedColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.CHECKED_COLOR, C3DCheckBox.this, "checkedColor", Color.valueOf("#0F9D58"));

	public Paint getCheckedColor(){
		return checkedColor == null ? Color.valueOf("#0F9D58") : checkedColor.get();
	}
	public StyleableObjectProperty<Paint> checkedColorProperty(){		
		return this.checkedColor;
	}
	public void setCheckedColor(Paint color){
		this.checkedColor.set(color);
	}


	private StyleableObjectProperty<Paint> unCheckedColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNCHECKED_COLOR, C3DCheckBox.this, "unCheckedColor", Color.valueOf("#5A5A5A"));

	public Paint getUnCheckedColor(){
		return unCheckedColor == null ? Color.valueOf("#5A5A5A") : unCheckedColor.get();
	}
	public StyleableObjectProperty<Paint> unCheckedColorProperty(){		
		return this.unCheckedColor;
	}
	public void setUnCheckedColor(Paint color){
		this.unCheckedColor.set(color);
	}


	private static class StyleableProperties {
		private static final CssMetaData< C3DCheckBox, Paint> CHECKED_COLOR =
				new CssMetaData< C3DCheckBox, Paint>("-fx-checked-color",
						PaintConverter.getInstance(), Color.valueOf("#0F9D58")) {
			@Override
			public boolean isSettable(C3DCheckBox control) {
				return control.checkedColor == null || !control.checkedColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DCheckBox control) {
				return control.checkedColorProperty();
			}
		};
		private static final CssMetaData< C3DCheckBox, Paint> UNCHECKED_COLOR =
				new CssMetaData< C3DCheckBox, Paint>("-fx-unchecked-color",
						PaintConverter.getInstance(), Color.valueOf("#5A5A5A")) {
			@Override
			public boolean isSettable(C3DCheckBox control) {
				return control.unCheckedColor == null || !control.unCheckedColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DCheckBox control) {
				return control.unCheckedColorProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					CHECKED_COLOR,
					UNCHECKED_COLOR
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