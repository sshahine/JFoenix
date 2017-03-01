package com.jfoenix.controls;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.jfoenix.skins.JFXTimePickerSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;

public class JFXTimePicker extends ComboBoxBase<LocalTime>  {


	/**
	 * {@inheritDoc}
	 */
	public JFXTimePicker() {
		super();		
		initialize();
	}
	
	
	public JFXTimePicker(LocalTime localTime) {
		setValue(localTime);
		initialize();
	}
	
	
	private void initialize() {
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		setAccessibleRole(AccessibleRole.DATE_PICKER);
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		setEditable(true);
		
//		valueProperty().addListener(observable -> {
//            LocalTime date = getValue();
//            converter
//            if (validateDate(chrono, date)) {
//                lastValidDate = date;
//            } else {
//                System.err.println("Restoring value to " +
//                            ((lastValidDate == null) ? "null" : getConverter().toString(lastValidDate)));
//                setValue(lastValidDate);
//            }
//        });
	}
    
	/**
	 * {@inheritDoc}
	 */
    @Override protected Skin<?> createDefaultSkin() {
        return new JFXTimePickerSkin(this);
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
	 * the parent node used when showing the data picker content as an overlay,
	 * intead of a popup
	 */
	private ObjectProperty<StackPane> dialogParent = new SimpleObjectProperty<>(null);
	public final ObjectProperty<StackPane> dialogParentProperty() {
		return this.dialogParent;
	}
	public final StackPane getDialogParent() {
		return this.dialogParentProperty().get();
	}
	public final void setDialogParent(final StackPane dialogParent) {
		this.dialogParentProperty().set(dialogParent);
	}
	
    /**
     * Converts the input text to an object of type LocalTime and vice
     * versa.
     */
	public final ObjectProperty<StringConverter<LocalTime>> converterProperty() { return converter; }
    private ObjectProperty<StringConverter<LocalTime>> converter =
            new SimpleObjectProperty<StringConverter<LocalTime>>(this, "converter", null);
    public final void setConverter(StringConverter<LocalTime> value) { converterProperty().set(value); }
    public final StringConverter<LocalTime> getConverter() {
        StringConverter<LocalTime> converter = converterProperty().get();
        if (converter != null) {
            return converter;
        } else {
            return defaultConverter;
        }
    }
    private StringConverter<LocalTime> defaultConverter = new LocalTimeStringConverter(FormatStyle.SHORT, Locale.ENGLISH);


    /**
     * The editor for the JFXTimePicker.
     *
     * @see javafx.scene.control.ComboBox#editorProperty
     */
    private ReadOnlyObjectWrapper<TextField> editor;
    public final TextField getEditor() {
        return editorProperty().get();
    }
    public final ReadOnlyObjectProperty<TextField> editorProperty() {
        if (editor == null) {
            editor = new ReadOnlyObjectWrapper<TextField>(this, "editor");
            editor.set(new ComboBoxListViewSkin.FakeFocusTextField());
        }
        return editor.getReadOnlyProperty();
    }

	/***************************************************************************
	 *                                                                         *
	 * Stylesheet Handling                                                     *
	 *                                                                         *
	 **************************************************************************/
	
	/**
     * Initialize the style class to 'jfx-date-picker'.
     *
     * This is the selector class from which CSS can be used to style
     * this control.
     */
	private static String DEFAULT_STYLE_CLASS = "jfx-time-picker";
    
	/**
	 * show the popup as an overlay using JFXDialog
	 * NOTE: to show it properly the scene root must be StackPane, or the user must set
	 * the dialog parent manually using the property {{@link #dialogParentProperty()}
	 */
	private StyleableBooleanProperty overLay = new SimpleStyleableBooleanProperty(StyleableProperties.OVERLAY, JFXTimePicker.this, "overLay", false);
	
	public final StyleableBooleanProperty overLayProperty() {
		return this.overLay;
	}
	public final boolean isOverLay() {
		return overLay == null ? false : this.overLayProperty().get();
	}
	public final void setOverLay(final boolean overLay) {
		this.overLayProperty().set(overLay);
	}
	
	/**
	 * the default color used in the data picker content
	 */
	private StyleableObjectProperty<Paint> defaultColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.DEFAULT_COLOR, JFXTimePicker.this, "defaultColor", Color.valueOf("#009688"));

	public Paint getDefaultColor(){
		return defaultColor == null ? Color.valueOf("#009688") : defaultColor.get();
	}
	public StyleableObjectProperty<Paint> defaultColorProperty(){		
		return this.defaultColor;
	}
	public void setDefaultColor(Paint color){
		this.defaultColor.set(color);
	}
    
	private static class StyleableProperties {
		private static final CssMetaData< JFXTimePicker, Paint> DEFAULT_COLOR =
				new CssMetaData< JFXTimePicker, Paint>("-jfx-default-color",
						PaintConverter.getInstance(), Color.valueOf("#5A5A5A")) {
			@Override
			public boolean isSettable(JFXTimePicker control) {
				return control.defaultColor == null || !control.defaultColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXTimePicker control) {
				return control.defaultColorProperty();
			}
		};

		private static final CssMetaData< JFXTimePicker, Boolean> OVERLAY =
				new CssMetaData< JFXTimePicker, Boolean>("-jfx-overlay",
						BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(JFXTimePicker control) {
				return control.overLay == null || !control.overLay.isBound();
			}
			@Override
			public StyleableBooleanProperty getStyleableProperty(JFXTimePicker control) {
				return control.overLayProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					DEFAULT_COLOR,
					OVERLAY);
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
