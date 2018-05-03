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

package com.jfoenix.skins;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.events.JFXAutoCompleteEvent;
import javafx.animation.Animation.Status;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * JFXAutoCompletePopup is an animated popup list view that allow filtering
 * suggestions according to some predicate.
 *
 * @author Shadi Shaheen
 * @version 1.0.0
 * @since 2018-02-01
 */
public class JFXAutoCompletePopupSkin<T> implements Skin<JFXAutoCompletePopup<T>> {

    private JFXAutoCompletePopup<T> control;
    private ListView<T> suggestionList;
    private final StackPane pane = new StackPane();
    private Scale scale;
    private Timeline showTransition;

    public JFXAutoCompletePopupSkin(JFXAutoCompletePopup<T> control) {
        this.control = control;
        suggestionList = new ListView<T>(control.getFilteredSuggestions());
        suggestionList.setFixedCellSize(control.getFixedCellSize());
        control.fixedCellSizeProperty().addListener(observable -> suggestionList.setFixedCellSize(control.getFixedCellSize()));
        suggestionList.getItems().addListener((InvalidationListener) observable -> updateListHeight());
        suggestionList.getStyleClass().add("autocomplete-list");
        control.suggestionsCellFactoryProperty().addListener((o, oldVal, newVal) -> {
            if (newVal != null) {
                suggestionList.setCellFactory(newVal);
            }
        });
        if (control.getSuggestionsCellFactory() != null) {
            suggestionList.setCellFactory(control.getSuggestionsCellFactory());
        }
        pane.getChildren().add(new Group(suggestionList));
        pane.getStyleClass().add("autocomplete-container");
        suggestionList.prefWidthProperty().bind(control.prefWidthProperty());
        suggestionList.maxWidthProperty().bind(control.maxWidthProperty());
        suggestionList.minWidthProperty().bind(control.minWidthProperty());
        registerEventListener();
    }

    private void registerEventListener() {
        suggestionList.setOnMouseClicked(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                selectItem();
                getSkinnable().hide();
            }
        });
        control.showingProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                animate();
            }
        });

        suggestionList.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    selectItem();
                    getSkinnable().hide();
                    break;
                case ESCAPE:
                    getSkinnable().hide();
                    break;
                default:
                    break;
            }
        });
    }

    public void animate() {
        updateListHeight();
        if (showTransition == null || showTransition.getStatus().equals(Status.STOPPED)) {
            if (scale == null) {
                scale = new Scale(1, 0);
                pane.getTransforms().add(scale);
            }
            scale.setY(0);
            suggestionList.setOpacity(0);
            scale.setPivotX(pane.getLayoutBounds().getWidth() / 2);
            showTransition = new Timeline(new KeyFrame(Duration.millis(120),
                new KeyValue(scale.yProperty(), 1, Interpolator.EASE_BOTH)));
            showTransition.setOnFinished((finish) -> {
                Group vf = (Group) suggestionList.lookup(".sheet");
                ParallelTransition trans = new ParallelTransition();
                for (int i = 0; i < vf.getChildren().size(); i++) {
                    ListCell<T> cell = (ListCell<T>) vf.getChildren().get(i);
                    int index = cell.getIndex();
                    if(index > -1){
                        cell.setOpacity(0);
                        cell.setTranslateY(- suggestionList.getFixedCellSize() / 8);
                        Timeline f = new Timeline(new KeyFrame(Duration.millis(120),
                            end ->{
                                cell.setOpacity(1);
                                cell.setTranslateY(0);
                            },
                            new KeyValue(cell.opacityProperty(), 1, Interpolator.EASE_BOTH),
                            new KeyValue(cell.translateYProperty(), 0, Interpolator.EASE_BOTH)));
                        f.setDelay(Duration.millis(index * 20));
                        trans.getChildren().add(f);
                    }
                }
                suggestionList.setOpacity(1);
                trans.play();
            });
            showTransition.play();
        }
    }

    private void updateListHeight() {
        final double height = Math.min(suggestionList.getItems().size(), getSkinnable().getCellLimit()) * suggestionList.getFixedCellSize();
        suggestionList.setPrefHeight(height + suggestionList.getFixedCellSize() / 2);
    }

    private void selectItem() {
        T item = suggestionList.getSelectionModel().getSelectedItem();
        if (item == null) {
            try {
                suggestionList.getSelectionModel().select(0);
                item = suggestionList.getSelectionModel().getSelectedItem();
            } catch (Exception e) {
            }
        }
        if (item != null) {
            control.getSelectionHandler().handle(new JFXAutoCompleteEvent<T>(JFXAutoCompleteEvent.SELECTION, item));
        }
    }

    @Override
    public Node getNode() {
        return pane;
    }

    @Override
    public JFXAutoCompletePopup<T> getSkinnable() {
        return control;
    }

    @Override
    public void dispose() {
        this.control = null;
        if (showTransition != null) {
            showTransition.stop();
            showTransition.getKeyFrames().clear();
            showTransition = null;
        }
    }
}
