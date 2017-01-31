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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfoenix.converters.base.NodeConverter;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * JFXComboBox is the material design implementation of a combobox. 
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXComboBox<T> extends ComboBox<T> {

	/**
	 * {@inheritDoc}
	 */
	public JFXComboBox() {
		super();
		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public JFXComboBox(ObservableList<T> items) {
		super(items);
		initialize();
	}

	private void initialize() {
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
			@Override
			public ListCell<T> call(ListView<T> listView) {
				return new JFXListCell<T>();
			}
		});
		this.setConverter(new StringConverter<T>() {
			@Override
			public String toString(T object) {
				if(object == null) return null;
				if(object instanceof Label) return ((Label)object).getText();
				return object.toString();				
			}
			@SuppressWarnings("unchecked")
			@Override
			public T fromString(String string) {
				return (T) string;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override protected Skin<?> createDefaultSkin() {
		return new JFXComboBoxListViewSkin<T>(this);
	}

	/**
	 * Initialize the style class to 'jfx-combo-box'.
	 *
	 * This is the selector class from which CSS can be used to style
	 * this control.
	 */
	private static final String DEFAULT_STYLE_CLASS = "jfx-combo-box";

	/***************************************************************************
	 *                                                                         *
	 * Node Converter Property                                                 *
	 *                                                                         *
	 **************************************************************************/
	/**
	 * Converts the user-typed input (when the ComboBox is 
	 * {@link #editableProperty() editable}) to an object of type T, such that 
	 * the input may be retrieved via the  {@link #valueProperty() value} property.
	 */
	public ObjectProperty<NodeConverter<T>> nodeConverterProperty() { return nodeConverter; }
	private ObjectProperty<NodeConverter<T>> nodeConverter =  new SimpleObjectProperty<NodeConverter<T>>(this, "nodeConverter", JFXComboBox.<T>defaultNodeConverter());
	public final void setNodeConverter(NodeConverter<T> value) { nodeConverterProperty().set(value); }
	public final NodeConverter<T> getNodeConverter() {return nodeConverterProperty().get(); }

	private static <T> NodeConverter<T> defaultNodeConverter() {
		return new NodeConverter<T>() {
			@Override public Node toNode(T object) {
				if(object == null) return null;
				StackPane selectedValueContainer = new StackPane();
				selectedValueContainer.getStyleClass().add("combo-box-selected-value-container");
				selectedValueContainer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
				Label selectedValueLabel;
				if(object instanceof Label) selectedValueLabel = new Label(((Label)object).getText());	
				else selectedValueLabel = new Label(object.toString());
				selectedValueLabel.setTextFill(Color.BLACK);
				selectedValueContainer.getChildren().add(selectedValueLabel);
				StackPane.setAlignment(selectedValueLabel, Pos.CENTER_LEFT);
				StackPane.setMargin(selectedValueLabel, new Insets(0,0,0,5));
				return selectedValueContainer;
			}
			@SuppressWarnings("unchecked")
			@Override public T fromNode(Node node) {
				return (T) node;
			}
			@Override
			public String toString(T object) {
				if(object == null) return null;
				if(object instanceof Label) return ((Label)object).getText();
				return object.toString();
			}
		};
	}
	
	/***************************************************************************
	 *                                                                         *
	 * styleable Properties                                                    *
	 *                                                                         *
	 **************************************************************************/
	
	/**
	 * set true to show a float the prompt text when focusing the field
	 */
	private StyleableBooleanProperty labelFloat = new SimpleStyleableBooleanProperty(StyleableProperties.LABEL_FLOAT, JFXComboBox.this, "lableFloat", false);
	
	public final StyleableBooleanProperty labelFloatProperty() {
		return this.labelFloat;
	}

	public final boolean isLabelFloat() {
		return this.labelFloatProperty().get();
	}

	public final void setLabelFloat(final boolean labelFloat) {
		this.labelFloatProperty().set(labelFloat);
	}
	
	/**
	 * default color used when the field is unfocused
	 */
	private StyleableObjectProperty<Paint> unFocusColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.UNFOCUS_COLOR, JFXComboBox.this, "unFocusColor", Color.rgb(77, 77, 77));

	public Paint getUnFocusColor() {
		return unFocusColor == null ? Color.rgb(77, 77, 77) : unFocusColor.get();
	}

	public StyleableObjectProperty<Paint> unFocusColorProperty() {
		return this.unFocusColor;
	}

	public void setUnFocusColor(Paint color) {
		this.unFocusColor.set(color);
	}

	/**
	 * default color used when the field is focused
	 */
	private StyleableObjectProperty<Paint> focusColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.FOCUS_COLOR, JFXComboBox.this, "focusColor", Color.valueOf("#4059A9"));

	public Paint getFocusColor() {
		return focusColor == null ? Color.valueOf("#4059A9") : focusColor.get();
	}

	public StyleableObjectProperty<Paint> focusColorProperty() {
		return this.focusColor;
	}

	public void setFocusColor(Paint color) {
		this.focusColor.set(color);
	}
	
	
	private static class StyleableProperties {
		private static final CssMetaData<JFXComboBox<?>, Paint> UNFOCUS_COLOR = new CssMetaData<JFXComboBox<?>, Paint>("-fx-unfocus-color", PaintConverter.getInstance(), Color.valueOf("#A6A6A6")) {
			@Override
			public boolean isSettable(JFXComboBox<?> control) {
				return control.unFocusColor == null || !control.unFocusColor.isBound();
			}

			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXComboBox<?> control) {
				return control.unFocusColorProperty();
			}
		};
		private static final CssMetaData<JFXComboBox<?>, Paint> FOCUS_COLOR = new CssMetaData<JFXComboBox<?>, Paint>("-fx-focus-color", PaintConverter.getInstance(), Color.valueOf("#3f51b5")) {
			@Override
			public boolean isSettable(JFXComboBox<?> control) {
				return control.focusColor == null || !control.focusColor.isBound();
			}

			@Override
			public StyleableProperty<Paint> getStyleableProperty(JFXComboBox<?> control) {
				return control.focusColorProperty();
			}
		};
		private static final CssMetaData<JFXComboBox<?>, Boolean> LABEL_FLOAT = new CssMetaData<JFXComboBox<?>, Boolean>("-fx-label-float", BooleanConverter.getInstance(), false) {
			@Override
			public boolean isSettable(JFXComboBox<?> control) {
				return control.labelFloat == null || !control.labelFloat.isBound();
			}

			@Override
			public StyleableBooleanProperty getStyleableProperty(JFXComboBox<?> control) {
				return control.labelFloatProperty();
			}
		};
		

		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables, UNFOCUS_COLOR, FOCUS_COLOR, LABEL_FLOAT);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		if (STYLEABLES == null) {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
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
