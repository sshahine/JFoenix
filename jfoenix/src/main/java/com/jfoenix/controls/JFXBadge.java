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

import javafx.animation.FadeTransition;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

@DefaultProperty(value = "control")
public class JFXBadge extends StackPane {

    private Group badge;
    protected Node control;
    private boolean enabled = true;

    public JFXBadge() {
        this(null);
    }

    public JFXBadge(Node control) {
        this(control, Pos.TOP_RIGHT);
    }

    public JFXBadge(Node control, Pos pos) {
        initialize();
        setPosition(pos);
        setControl(control);
        position.addListener((o, oldVal, newVal) -> StackPane.setAlignment(badge, newVal));
    }

    /***************************************************************************
     * * Setters / Getters * *
     **************************************************************************/

    public void setControl(Node control) {
        if (control != null) {
            this.control = control;
            this.badge = new Group();
            this.getChildren().add(control);
            this.getChildren().add(badge);

            // if the control got resized the badge must be rest
            if (control instanceof Region) {
                ((Region) control).widthProperty().addListener((o, oldVal, newVal) -> refreshBadge());
                ((Region) control).heightProperty().addListener((o, oldVal, newVal) -> refreshBadge());
            }
            text.addListener((o, oldVal, newVal) -> refreshBadge());
        }
    }

    public Node getControl() {
        return this.control;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    public void refreshBadge() {
        badge.getChildren().clear();
        if (enabled) {


            Label labelControl = new Label(text.getValue());

            StackPane badgePane = new StackPane();
            badgePane.getStyleClass().add("badge-pane");
            badgePane.getChildren().add(labelControl);
            //Adding a clip would avoid overlap but this does not work as intended
            //badgePane.setClip(clip);
            badge.getChildren().add(badgePane);
            StackPane.setAlignment(badge, getPosition());

            FadeTransition ft = new FadeTransition(Duration.millis(666), badge);
            ft.setFromValue(0);
            ft.setToValue(1.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(true);
            ft.play();
        }
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-badge";

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    protected ObjectProperty<Pos> position = new SimpleObjectProperty<>();

    public Pos getPosition() {
        return position == null ? Pos.TOP_RIGHT : position.get();
    }

    public ObjectProperty<Pos> positionProperty() {
        return this.position;
    }

    public void setPosition(Pos position) {
        this.position.set(position);
    }

    private SimpleStringProperty text = new SimpleStringProperty();

    public final String getText() {
        return text.get();
    }

    public final void setText(String value) {
        text.set(value);
    }

    public final StringProperty textProperty() {
        return text;
    }
}
