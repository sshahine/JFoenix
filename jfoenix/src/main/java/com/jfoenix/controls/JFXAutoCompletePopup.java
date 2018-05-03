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

import com.jfoenix.controls.events.JFXAutoCompleteEvent;
import com.jfoenix.skins.JFXAutoCompletePopupSkin;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * JFXAutoCompletePopup is an animated popup list view that allow filtering
 * suggestions according to some predicate.
 *
 * @author Shadi Shaheen
 * @version 1.1.0
 * @since 2018-02-01
 */
public class JFXAutoCompletePopup<T> extends PopupControl {

    private final ObservableList<T> suggestions = FXCollections.observableArrayList();
    private final ObjectProperty<EventHandler<JFXAutoCompleteEvent<T>>> selectionHandler = new SimpleObjectProperty<>();
    private final FilteredList<T> filteredData = new FilteredList<T>(suggestions, s -> true);
    private final ObjectProperty<Callback<ListView<T>, ListCell<T>>> suggestionsCellFactory = new SimpleObjectProperty<Callback<ListView<T>, ListCell<T>>>();

    private static final String DEFAULT_STYLE_CLASS = "jfx-autocomplete-popup";

    public JFXAutoCompletePopup() {
        super();
        bridge = new CSSBridge();
        getContent().setAll(bridge);
        setAutoFix(true);
        setAutoHide(true);
        setHideOnEscape(true);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXAutoCompletePopupSkin<T>(this);
    }

    public void show(Node node){
        if(!isShowing()){
            if(node.getScene() == null || node.getScene().getWindow() == null)
                throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
            Window parent = node.getScene().getWindow();
            this.show(parent, parent.getX() + node.localToScene(0, 0).getX() +
                              node.getScene().getX(),
                parent.getY() + node.localToScene(0, 0).getY() +
                node.getScene().getY() + ((Region)node).getHeight());
            ((JFXAutoCompletePopupSkin<T>)getSkin()).animate();
        }
    }

    public ObservableList<T> getSuggestions() {
        return suggestions;
    }

    public void filter(Predicate<T> predicate){
        filteredData.setPredicate(predicate);
    }

    public ObservableList<T> getFilteredSuggestions() {
        return filteredData;
    }

    public EventHandler<JFXAutoCompleteEvent<T>> getSelectionHandler() {
        return selectionHandler.get();
    }

    public void setSelectionHandler(EventHandler<JFXAutoCompleteEvent<T>> selectionHandler){
        this.selectionHandler.set(selectionHandler);
    }

    public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> suggestionsCellFactoryProperty() {
        return this.suggestionsCellFactory;
    }


    public final Callback<ListView<T>, ListCell<T>> getSuggestionsCellFactory() {
        return this.suggestionsCellFactoryProperty().get();
    }


    public final void setSuggestionsCellFactory(
        final javafx.util.Callback<ListView<T>, ListCell<T>> suggestionsCellFactory) {
        this.suggestionsCellFactoryProperty().set(suggestionsCellFactory);
    }

    /**
     * limits the number of cells to be shown, used to compute the list size
     */
    private IntegerProperty cellLimit = new SimpleStyleableIntegerProperty(StyleableProperties.CELL_LIMIT,
        JFXAutoCompletePopup.this, "cellLimit", 10);
    public final void setCellLimit(int value) {
        cellLimitProperty().set(value);
    }
    public final int getCellLimit() { return cellLimitProperty().get(); }
    public final IntegerProperty cellLimitProperty() { return cellLimit; }

    /**
     * cell size, used to compute the list size
     */
    private DoubleProperty fixedCellSize = new SimpleStyleableDoubleProperty(StyleableProperties.FIXED_CELL_SIZE,
        JFXAutoCompletePopup.this, "fixedCellSize", 24d);

    public final void setFixedCellSize(double value) {
        fixedCellSizeProperty().set(value);
    }
    public final double getFixedCellSize() { return fixedCellSizeProperty().get(); }
    public final DoubleProperty fixedCellSizeProperty() { return fixedCellSize; }


    private final class CSSBridge extends PopupControl.CSSBridge {
        private JFXAutoCompletePopup popup = JFXAutoCompletePopup.this;
        CSSBridge() {
            super();
        }
    }


    private static class StyleableProperties {
        private static final CssMetaData<JFXAutoCompletePopup.CSSBridge,Number> FIXED_CELL_SIZE =
            new CssMetaData<JFXAutoCompletePopup.CSSBridge, Number>("-fx-fixed-cell-size",
                SizeConverter.getInstance(), 24) {
                @Override public Double getInitialValue(JFXAutoCompletePopup.CSSBridge bridge) {
                    return bridge.popup.getFixedCellSize();
                }
                @Override public boolean isSettable(JFXAutoCompletePopup.CSSBridge bridge) {
                    return bridge.popup.fixedCellSize == null || !bridge.popup.fixedCellSize.isBound();
                }
                @Override public StyleableProperty<Number> getStyleableProperty(JFXAutoCompletePopup.CSSBridge bridge) {
                    return (StyleableProperty<Number>) bridge.popup.fixedCellSizeProperty();
                }
            };
        private static final CssMetaData<JFXAutoCompletePopup.CSSBridge, Number> CELL_LIMIT =
            new CssMetaData<JFXAutoCompletePopup.CSSBridge, Number>("-jfx-cell-limit",
                SizeConverter.getInstance(), 10) {
                @Override public Number getInitialValue(JFXAutoCompletePopup.CSSBridge bridge) {
                    return bridge.popup.getCellLimit();
                }
                @Override public boolean isSettable(JFXAutoCompletePopup.CSSBridge bridge) {
                    return bridge.popup.cellLimit == null || !bridge.popup.cellLimit.isBound();
                }
                @Override public StyleableProperty<Number> getStyleableProperty(JFXAutoCompletePopup.CSSBridge bridge) {
                    return (StyleableProperty<Number>) bridge.popup.cellLimitProperty();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<CssMetaData<? extends Styleable, ?>>(PopupControl.getClassCssMetaData());
            styleables.add(FIXED_CELL_SIZE);
            styleables.add(CELL_LIMIT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }


    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
