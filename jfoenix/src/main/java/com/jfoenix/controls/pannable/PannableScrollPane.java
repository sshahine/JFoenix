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
