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
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-02-11
 */
public class JFXTreeViewPath extends ScrollPane {

    private PseudoClass firstClass = PseudoClass.getPseudoClass("first");
    private PseudoClass nextClass = PseudoClass.getPseudoClass("next");
    private PseudoClass lastClass = PseudoClass.getPseudoClass("last");
    private Region clip = new Region();

    private HBox container = new HBox();

    private double lastX;

    public JFXTreeViewPath(TreeView<?> treeView) {

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        setClip(clip);
        clip.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(3), Insets.EMPTY)));
        backgroundProperty().addListener(observable -> JFXNodeUtils.updateBackground(getBackground(), clip));

        container.getStyleClass().add("buttons-container");
        container.getChildren().add(new Label("Selection Path..."));
        container.setAlignment(Pos.CENTER_LEFT);
        container.widthProperty().addListener(observable -> setHvalue(getHmax()));
        setContent(container);
        setPannable(true);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true);
        treeView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            TreeItem selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeItem temp = selectedItem;
            int level = treeView.getTreeItemLevel(temp) - (treeView.isShowRoot() ? 0 : 1);
            if (temp != null) {
                List<Node> newPath = new ArrayList<>();
                while (temp != null) {
                    TreeItem parent = treeView.isShowRoot() ? temp : temp.getParent();
                    if (parent != null) {
                        Button button = null;
                        if (temp.isLeaf()) {
                            button = createLastButton(temp, parent.getParent());
                            button.pseudoClassStateChanged(lastClass, true);
                        } else if (parent.getParent() == null) {
                            button = createFirstButton(temp);
                            button.pseudoClassStateChanged(firstClass, true);
                        } else {
                            button = createNextButton(temp);
                            button.pseudoClassStateChanged(nextClass, true);
                        }
                        final TreeItem node = temp;
                        button.setOnAction(action -> treeView.scrollTo(treeView.getRow(node)));
                        final StackPane container = new StackPane(button);
                        container.setPickOnBounds(false);

                        if (parent.getParent() != null) {
                            container.setTranslateX((-getOffset()-1) * level--);
                        }
                        if (temp!=selectedItem) {
                            final SVGGlyph arrow = new SVGGlyph("M366 698l196-196-196-196 60-60 256 256-256 256z", Color.BLACK);
                            arrow.setSizeForWidth(6);
                            arrow.setMouseTransparent(true);
                            StackPane.setAlignment(arrow, Pos.CENTER_RIGHT);
                            container.getChildren().add(arrow);
                        }
                        newPath.add(0, container);
                    }
                    temp = temp.getParent();
                }
                container.getChildren().setAll(newPath);
            }
        });

        container.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            lastX = event.getX();
        });
        container.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            double dx = lastX - event.getX();
            if (Math.abs(dx) > 0.5) {
                double newHVal = (getHvalue() + dx / (container.getWidth()));
                setHvalue(newHVal);
            }
        });

        JFXScrollPane.smoothHScrolling(this);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        clip.resizeRelocate(0,0,getWidth(), getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResources.load("css/controls/jfx-tree-view-path.css").toExternalForm();
    }

    @Override
    protected double computeMinHeight(double width) {
        return super.computePrefHeight(width);
    }

    private JFXButton createNextButton(TreeItem temp) {
        return new JFXButton(temp.getValue().toString()) {
            {
                setPadding(new Insets(getOffset(), 1.5 * getOffset(), getOffset(), 2 * getOffset()));
                setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                double width = getWidth();
                Polygon polygon = new Polygon();
                final double height = getHeight();
                polygon.getPoints().addAll(new Double[] {
                    0.0, 0.0,
                    width - getOffset(), 0.0,
                    width, height / 2,
                    width - getOffset(), height,
                    0.0, height,
                    getOffset(), height / 2});
                setClip(polygon);

            }
        };
    }

    public JFXButton createFirstButton(TreeItem temp) {
        return new JFXButton(temp.getValue().toString()) {
            {
                setPadding(new Insets(getOffset(), 1.5 * getOffset(), getOffset(), getOffset()));
                setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                double width = getWidth();
                Polygon polygon = new Polygon();
                final double height = getHeight();
                polygon.getPoints().addAll(new Double[] {
                    0.0, 0.0,
                    width - getOffset(), 0.0,
                    width, height / 2,
                    width - getOffset(), height,
                    0.0, height});
                setClip(polygon);
            }
        };
    }

    private JFXButton createLastButton(TreeItem temp, TreeItem parent) {
        return new JFXButton(temp.getValue().toString()) {
            private boolean noParent = parent == null;
            {
                setPadding(new Insets(getOffset(), getOffset(), getOffset(), (noParent? 1 : 2) * getOffset()));
                setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                double width = getWidth();
                Polygon polygon = new Polygon();
                final double height = getHeight();
                polygon.getPoints().addAll(new Double[] {
                    0.0, 0.0,
                    width, 0.0,
                    width, height,
                    0.0, height,
                    noParent ? 0 : getOffset(), noParent ? 0 : height / 2});
                setClip(polygon);
            }
        };
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-tree-view-path";

    private StyleableDoubleProperty offset = new SimpleStyleableDoubleProperty(
        StyleableProperties.OFFSET,
        JFXTreeViewPath.this,
        "offset",
        10.0);

    public double getOffset() {
        return offset.get();
    }

    public StyleableDoubleProperty offsetProperty() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset.set(offset);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXTreeViewPath, Number> OFFSET =
            new CssMetaData<JFXTreeViewPath, Number>("-jfx-offset",
                SizeConverter.getInstance(), 10.0) {
                @Override
                public boolean isSettable(JFXTreeViewPath control) {
                    return control.offset == null || !control.offset.isBound();
                }

                @Override
                public StyleableDoubleProperty getStyleableProperty(JFXTreeViewPath control) {
                    return control.offsetProperty();
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(ScrollPane.getClassCssMetaData());
            Collections.addAll(styleables, OFFSET);
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
