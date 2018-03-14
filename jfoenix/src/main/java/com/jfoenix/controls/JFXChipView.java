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

import com.jfoenix.skins.JFXChipViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * JFXChipArea is the material design implementation of chip Input.
 * An easy way to manage chips in a text area component with an x to
 * omit the chip.
 *
 * @author Shadi Shaheen & Gerard Moubarak
 * @version 1.0.0
 * @since 2018-02-01
 */
public class JFXChipView<T> extends Control {

    /***************************************************************************
     *                                                                         *
     * Static properties and methods                                           *
     *                                                                         *
     **************************************************************************/

    private static <T> StringConverter<T> defaultStringConverter() {
        return new StringConverter<T>() {
            @Override
            public String toString(T t) {
                return t == null ? null : t.toString();
            }

            @Override
            public T fromString(String string) {
                return (T) string;
            }
        };
    }

    public JFXChipView() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-tag-area'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-chip-view";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("/css/controls/jfx-chip-view.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXChipViewSkin<T>(this);
    }


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    // chip factory to customize chip ui
    private ObjectProperty<BiFunction<JFXChipView<T>, T, JFXChip<T>>> chipFactory;

    public BiFunction<JFXChipView<T>, T, JFXChip<T>> getChipFactory() {
        return chipFactory == null ? null : chipFactory.get();
    }

    public ObjectProperty<BiFunction<JFXChipView<T>, T, JFXChip<T>>> chipFactoryProperty() {
        if (chipFactory == null) {
            chipFactory = new SimpleObjectProperty<>(this, "chipFactory");
        }
        return chipFactory;
    }

    public void setChipFactory(BiFunction<JFXChipView<T>, T, JFXChip<T>> chipFactory) {
        chipFactoryProperty().set(chipFactory);
    }

    private JFXAutoCompletePopup<T> autoCompletePopup = new JFXChipViewSkin.ChipsAutoComplete<T>();

    public JFXAutoCompletePopup<T> getAutoCompletePopup() {
        return autoCompletePopup;
    }

    public ObservableList<T> getSuggestions() {
        return autoCompletePopup.getSuggestions();
    }

    public void setSuggestionsCellFactory(Callback<ListView<T>, ListCell<T>> factory) {
        autoCompletePopup.setSuggestionsCellFactory(factory);
    }

    private ObjectProperty<BiPredicate<T, String>> predicate = new SimpleObjectProperty<>(
        (item, text) -> {
            StringConverter<T> converter = getConverter();
            String itemString = converter != null ? converter.toString(item) : item.toString();
            return itemString.toLowerCase().contains(text.toLowerCase());
        }
    );

    public BiPredicate<T, String> getPredicate() {
        return predicate.get();
    }

    public ObjectProperty<BiPredicate<T, String>> predicateProperty() {
        return predicate;
    }

    public void setPredicate(BiPredicate<T, String> predicate) {
        this.predicate.set(predicate);
    }

    // --- chips
    /**
     * The list of selected chips.
     */
    private ObservableList<T> chips = FXCollections.observableArrayList();

    public ObservableList<T> getChips() {
        return chips;
    }

    // --- string converter

    /**
     * Converts the user-typed input (when the ChipArea is
     * editable to an object of type T, such that
     * the input may be retrieved via the property.
     */

    private ObjectProperty<StringConverter<T>> converter =
        new SimpleObjectProperty<StringConverter<T>>(this, "converter", JFXChipView.defaultStringConverter());

    public ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }
}
