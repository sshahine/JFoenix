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

import com.jfoenix.controls.base.IFXValidatableControl;
import com.jfoenix.skins.JFXDatePickerSkin;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.PaintConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXDatePicker is the material design implementation of a date picker.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDatePicker extends DatePicker implements IFXValidatableControl {

    /**
     * {@inheritDoc}
     */
    public JFXDatePicker() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXDatePicker(LocalDate localDate) {
        super(localDate);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        try {
            editorProperty();
            Field editorField = DatePicker.class.getDeclaredField("editor");
            editorField.setAccessible(true);
            ReadOnlyObjectWrapper<TextField> editor = (ReadOnlyObjectWrapper<TextField>) editorField.get(this);
            final FakeFocusJFXTextField editorNode = new FakeFocusJFXTextField();
            this.focusedProperty().addListener((obj, oldVal, newVal) -> {
                if (getEditor() != null) {
                    editorNode.setFakeFocus(newVal);
                }
            });
            editorNode.activeValidatorWritableProperty().bind(activeValidatorProperty());
            editor.set(editorNode);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFXDatePicker.class.getResource("/css/controls/jfx-date-picker.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXDatePickerSkin(this);
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

    private ValidationControl validationControl = new ValidationControl(this);

    @Override
    public ValidatorBase getActiveValidator() {
        return validationControl.getActiveValidator();
    }

    @Override
    public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty() {
        return validationControl.activeValidatorProperty();
    }

    @Override
    public ObservableList<ValidatorBase> getValidators() {
        return validationControl.getValidators();
    }

    @Override
    public void setValidators(ValidatorBase... validators) {
        validationControl.setValidators(validators);
    }

    @Override
    public boolean validate() {
        return validationControl.validate();
    }

    @Override
    public void resetValidation() {
        validationControl.resetValidation();
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
    private static final String DEFAULT_STYLE_CLASS = "jfx-date-picker";

    /**
     * show the popup as an overlay using JFXDialog
     * NOTE: to show it properly the scene root must be StackPane, or the user must set
     * the dialog parent manually using the property {{@link #dialogParentProperty()}
     */
    private StyleableBooleanProperty overLay = new SimpleStyleableBooleanProperty(StyleableProperties.OVERLAY,
        JFXDatePicker.this,
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
        JFXDatePicker.this,
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
        private static final CssMetaData<JFXDatePicker, Paint> DEFAULT_COLOR =
            new CssMetaData<JFXDatePicker, Paint>("-jfx-default-color",
                PaintConverter.getInstance(), Color.valueOf("#009688")) {
                @Override
                public boolean isSettable(JFXDatePicker control) {
                    return control.defaultColor == null || !control.defaultColor.isBound();
                }

                @Override
                public StyleableProperty<Paint> getStyleableProperty(JFXDatePicker control) {
                    return control.defaultColorProperty();
                }
            };

        private static final CssMetaData<JFXDatePicker, Boolean> OVERLAY =
            new CssMetaData<JFXDatePicker, Boolean>("-jfx-overlay",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXDatePicker control) {
                    return control.overLay == null || !control.overLay.isBound();
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXDatePicker control) {
                    return control.overLayProperty();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(DatePicker.getClassCssMetaData());
            Collections.addAll(styleables,
                DEFAULT_COLOR,
                OVERLAY);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }

}
