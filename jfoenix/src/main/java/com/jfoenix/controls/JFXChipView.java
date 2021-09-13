/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.controls;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.skins.JFXChipViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import java.util.function.Function;

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
        return JFoenixResources.load("css/controls/jfx-chip-view.css").toExternalForm();
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

    private ObjectProperty<Function<T, T>> selectionHandler;

    public Function<T, T> getSelectionHandler() {
        return selectionHandler == null ? null : selectionHandler.get();
    }

    public ObjectProperty<Function<T, T>> selectionHandlerProperty() {
        if (selectionHandler == null) {
            selectionHandler = new SimpleObjectProperty<>(this, "selectionHandler");
        }
        return selectionHandler;
    }

    public void setSelectionHandler(Function<T, T> selectionHandler) {
        selectionHandlerProperty().set(selectionHandler);
    }


    /**
     * The prompt text to display in the TextArea.
     */
    private StringProperty promptText = new SimpleStringProperty(this, "promptText", "");

    public final StringProperty promptTextProperty() {
        return promptText;
    }

    public final String getPromptText() {
        return promptText.get();
    }

    public final void setPromptText(String value) {
        promptText.set(value);
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
