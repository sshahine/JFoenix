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

package com.jfoenix.controls.pannable;

import com.jfoenix.bindings.CustomBidirectionalBinding;
import com.jfoenix.bindings.base.IPropertyConverter;
import com.jfoenix.controls.pannable.base.IPannablePane;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.function.Function;

/**
 * Used to add scroll functionality to {@link PannablePane}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2020-04-30
 */
public class PannableScrollPane extends Pane {

    private ScrollBar vBar = new ScrollBar();
    private ScrollBar hBar = new ScrollBar();

    private void init() {
        getStyleClass().add("pannable-scroll-pane");
        getChildren().addAll(vBar, hBar);
        vBar.setManaged(false);
        vBar.setOrientation(Orientation.VERTICAL);
        hBar.setManaged(false);
        hBar.setOrientation(Orientation.HORIZONTAL);
    }

    private static final double SCROLL_PAD = 20;

    public <P extends Region & IPannablePane> PannableScrollPane(P pane) {
        super(pane);
        init();
        bindScrollBar(vBar, pane, pane.translateYProperty(), (p) -> p.heightProperty());
        bindScrollBar(hBar, pane, pane.translateXProperty(), (p) -> p.widthProperty());
    }

    <P extends Region & IPannablePane> void bindScrollBar(ScrollBar bar, P pane, Property<Number> trans, Function<Region, DoubleExpression> propFun) {
        CustomBidirectionalBinding<Number, Number> binding = new CustomBidirectionalBinding<>(
            trans
            , bar.valueProperty()
            , new IPropertyConverter<Number, Number>() {
            @Override
            public Number to(Number number) {
                return number.doubleValue() * -1;
            }

            @Override
            public Number from(Number number) {
                return number.doubleValue() * -1;
            }
        });
        binding.bindBi();
        bar.minProperty().bind(pane.scaleProperty().negate());
        bar.maxProperty().bind(propFun.apply(pane).add(SCROLL_PAD).multiply(pane.scaleProperty()).subtract(propFun.apply(this)));
        bar.visibleProperty().bind(bar.maxProperty().greaterThan(0));
    }

    public PannableScrollPane() {
        init();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double w = getWidth();
        double h = getHeight();
        Insets insets = getInsets();
        final double prefWidth = vBar.prefWidth(-1);
        vBar.resizeRelocate(w - prefWidth - insets.getRight(), insets.getTop(), prefWidth, h - insets.getTop() - insets.getBottom());

        final double prefHeight = hBar.prefHeight(-1);
        hBar.resizeRelocate(insets.getLeft(), h - prefHeight - insets.getBottom(), w - insets.getLeft() - insets.getRight(), prefHeight);
    }
}
