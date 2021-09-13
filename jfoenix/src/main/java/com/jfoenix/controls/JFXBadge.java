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
