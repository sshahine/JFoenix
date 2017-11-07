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

import com.jfoenix.skins.JFXTimePickerSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.property.*;
import javafx.css.*;
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

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * <h1>Material Design Time Picker control</h1>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-03-01
 */
public class JFXTimePicker extends ComboBoxBase<LocalTime> {

    /**
     * {@inheritDoc}
     */
    public JFXTimePicker() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXTimePicker(LocalTime localTime) {
        setValue(localTime);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setAccessibleRole(AccessibleRole.DATE_PICKER);
        setEditable(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("/css/controls/jfx-time-picker.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
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
    public final ObjectProperty<StringConverter<LocalTime>> converterProperty() {
        return converter;
    }

    private ObjectProperty<StringConverter<LocalTime>> converter =
        new SimpleObjectProperty<>(this, "converter", null);

    public final void setConverter(StringConverter<LocalTime> value) {
        converterProperty().set(value);
    }

    public final StringConverter<LocalTime> getConverter() {
        StringConverter<LocalTime> converter = converterProperty().get();
        if (converter != null) {
            return converter;
        } else {
            return defaultConverter;
        }
    }

    private StringConverter<LocalTime> defaultConverter = new LocalTimeStringConverter(FormatStyle.SHORT,
        Locale.ENGLISH);

    private BooleanProperty _24HourView = new SimpleBooleanProperty(false);

    public final BooleanProperty _24HourViewProperty() {
        return this._24HourView;
    }

    public final boolean is24HourView() {
        return _24HourViewProperty().get();
    }

    public final void setIs24HourView(final boolean value) {
        _24HourViewProperty().setValue(value);
    }


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
            editor = new ReadOnlyObjectWrapper<>(this, "editor");
            final FakeFocusJFXTextField editorNode = new FakeFocusJFXTextField();
            this.focusedProperty().addListener((obj, oldVal, newVal) -> {
                if (getEditor() != null) {
                    editorNode.setFakeFocus(newVal);
                }
            });
            editor.set(editorNode);
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
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-time-picker";

    /**
     * show the popup as an overlay using JFXDialog
     * NOTE: to show it properly the scene root must be StackPane, or the user must set
     * the dialog parent manually using the property {{@link #dialogParentProperty()}
     */
    private StyleableBooleanProperty overLay = new SimpleStyleableBooleanProperty(StyleableProperties.OVERLAY,
        JFXTimePicker.this,
        "overLay",
        false);

    public final StyleableBooleanProperty overLayProperty() {
        return this.overLay;
    }

    public final boolean isOverLay() {
        return overLay != null && this.overLayProperty().get();
    }

    public final void setOverLay(final boolean overLay) {
        this.overLayProperty().set(overLay);
    }

    /**
     * the default color used in the data picker content
     */
    private StyleableObjectProperty<Paint> defaultColor = new SimpleStyleableObjectProperty<>(StyleableProperties.DEFAULT_COLOR,
        JFXTimePicker.this,
        "defaultColor",
        Color.valueOf(
            "#009688"));

    public Paint getDefaultColor() {
        return defaultColor == null ? Color.valueOf("#009688") : defaultColor.get();
    }

    public StyleableObjectProperty<Paint> defaultColorProperty() {
        return this.defaultColor;
    }

    public void setDefaultColor(Paint color) {
        this.defaultColor.set(color);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXTimePicker, Paint> DEFAULT_COLOR =
            new CssMetaData<JFXTimePicker, Paint>("-jfx-default-color",
                PaintConverter.getInstance(), Color.valueOf("#009688")) {
                @Override
                public boolean isSettable(JFXTimePicker control) {
                    return control.defaultColor == null || !control.defaultColor.isBound();
                }

                @Override
                public StyleableProperty<Paint> getStyleableProperty(JFXTimePicker control) {
                    return control.defaultColorProperty();
                }
            };

        private static final CssMetaData<JFXTimePicker, Boolean> OVERLAY =
            new CssMetaData<JFXTimePicker, Boolean>("-jfx-overlay",
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
                new ArrayList<>(Control.getClassCssMetaData());
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
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Control.getClassCssMetaData());
            styleables.addAll(getClassCssMetaData());
            styleables.addAll(Control.getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }


}
