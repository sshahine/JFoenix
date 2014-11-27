package com.cctintl.c3dfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cctintl.c3dfx.converters.ButtonTypeConverter;
import com.cctintl.c3dfx.skins.C3DButtonSkin;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class C3DButton extends Button {
	
	
	public C3DButton() {
		super();
		initialize();
	}	
		
	public C3DButton(String text){
		super(text);
		initialize();
	}
	public C3DButton(String text, Node graphic){
		super(text, graphic);
		initialize();
	}

    private void initialize() {
    	this.getStyleClass().add("c3d-button");
    }
    
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DButtonSkin(this);
	}
	
	public static enum ButtonType{FLAT, RAISED};
	
	
	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	private StyleableObjectProperty<ButtonType> buttonType = new SimpleStyleableObjectProperty<ButtonType>(StyleableProperties.BUTTON_TYPE, C3DButton.this, "buttonType", ButtonType.FLAT );

	public ButtonType getButtonType(){
		return buttonType == null ? ButtonType.FLAT : buttonType.get();
	}
	public StyleableObjectProperty<ButtonType> buttonTypeProperty(){		
		return this.buttonType;
	}
	public void setButtonType(ButtonType type){
		this.buttonType.set(type);
	}


	private static class StyleableProperties {
		private static final CssMetaData< C3DButton, ButtonType> BUTTON_TYPE =
				new CssMetaData< C3DButton, ButtonType>("-fx-button-type",
						ButtonTypeConverter.getInstance(), ButtonType.FLAT) {
			@Override
			public boolean isSettable(C3DButton control) {
				return control.buttonType == null || !control.buttonType.isBound();
			}
			@Override
			public StyleableProperty<ButtonType> getStyleableProperty(C3DButton control) {
				return control.buttonTypeProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					BUTTON_TYPE
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
