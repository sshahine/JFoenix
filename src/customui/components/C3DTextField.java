package customui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.sun.javafx.css.converters.PaintConverter;

import customui.skins.C3DTextFieldSkin;
import customui.validation.base.ValidatorBase;

public class C3DTextField extends TextField {

	public C3DTextField() {
		super();
		initialize();
	}
	
	public C3DTextField(String text) {
		super(text);
		initialize();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DTextFieldSkin(this);
	}
	
	private void initialize() {
		this.getStyleClass().add("c3d-text-field");  
	}


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
	
	private ReadOnlyObjectWrapper<ValidatorBase> activeValidator = new ReadOnlyObjectWrapper<ValidatorBase>();
	
	public ValidatorBase getActiveValidator(){
		return activeValidator == null ? null : activeValidator.get();
	}
	public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty(){		
		return this.activeValidator.getReadOnlyProperty();
	}
	
	
	private ObservableList<ValidatorBase> validators = FXCollections.observableArrayList();
	
    public ObservableList<ValidatorBase> getValidators() {
		return validators;
	}

	public void setValidators(ObservableList<ValidatorBase> validators) {
		this.validators = validators;
	}
	
	public boolean validate(){
		for(ValidatorBase validator : validators){
			if(validator.getSrcControl() == null)
				validator.setSrcControl(this);
			validator.validate();
			if(validator.getHasErrors()){
				activeValidator.set(validator);
				return false;
			}
		}
		activeValidator.set(null);
		return true;
	}
	

	/***************************************************************************
     *                                                                         *
     * styleable Properties                                                    *
     *                                                                         *
     **************************************************************************/
	private StyleableObjectProperty<Paint> unFocusColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNFOCUS_COLOR, C3DTextField.this, "unFocusColor", Color.rgb(77,77,77));

	public Paint getUnFocusColor(){
		return unFocusColor == null ? Color.rgb(77,77,77) : unFocusColor.get();
	}
	public StyleableObjectProperty<Paint> unFocusColorProperty(){		
		return this.unFocusColor;
	}
	public void setUnFocusColor(Paint color){
		this.unFocusColor.set(color);
	}


	private StyleableObjectProperty<Paint> focusColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.FOCUS_COLOR, C3DTextField.this, "focusColor", Color.valueOf("#4059A9"));

	public Paint getFocusColor(){
		return focusColor == null ? Color.valueOf("#4059A9") : focusColor.get();
	}
	public StyleableObjectProperty<Paint> focusColorProperty(){		
		return this.focusColor;
	}
	public void setFocusColor(Paint color){
		this.focusColor.set(color);
	}

	private static class StyleableProperties {
		private static final CssMetaData< C3DTextField, Paint> UNFOCUS_COLOR =
				new CssMetaData< C3DTextField, Paint>("-fx-unfocus-color",
						PaintConverter.getInstance(), Color.rgb(77,77,77)) {
			@Override
			public boolean isSettable(C3DTextField control) {
				return control.unFocusColor == null || !control.unFocusColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DTextField control) {
				return control.unFocusColorProperty();
			}
		};
		private static final CssMetaData< C3DTextField, Paint> FOCUS_COLOR =
				new CssMetaData< C3DTextField, Paint>("-fx-focus-color",
						PaintConverter.getInstance(),  Color.valueOf("#4059A9")) {
			@Override
			public boolean isSettable(C3DTextField control) {
				return control.focusColor == null || !control.focusColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DTextField control) {
				return control.focusColorProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					UNFOCUS_COLOR,
					FOCUS_COLOR
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
