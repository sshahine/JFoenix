package com.jfoenix.controls;

import com.jfoenix.svg.SVGGlyph;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFXTreeViewPath extends ScrollPane {

    private HBox container = new HBox();
    private double lastX;

    public JFXTreeViewPath(TreeView<?> treeView) {

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        container.getStyleClass().add("buttons-container");

        setContent(container);
        setPannable(true);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true);
        treeView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            TreeItem temp = treeView.getSelectionModel().getSelectedItem();
            if (temp != null) {
                container.getChildren().clear();
                while (temp != null) {
                    if (temp.getParent() != null) {
                        Button button = null;
                        if (temp.getParent().getParent() == null) {
                            button = createFirstButton(temp);
                        } else if (temp.isLeaf()) {
                            button = createLastButton(temp);
                        } else {
                            button = createNextButton(temp);
                        }
                        final TreeItem node = temp;
                        button.setOnAction(action -> treeView.scrollTo(treeView.getRow(node)));
                        final StackPane container = new StackPane(button);
                        container.setPickOnBounds(false);

                        if (temp.getParent().getParent() != null) {
                            container.setTranslateX(-getOffset());
                        }
                        if (!temp.isLeaf()) {
                            final SVGGlyph arrow = new SVGGlyph("M366 698l196-196-196-196 60-60 256 256-256 256z", Color.BLACK);
                            arrow.setWidthSizeRatio(4);
                            arrow.setMouseTransparent(true);
                            StackPane.setAlignment(arrow, Pos.CENTER_RIGHT);
                            container.getChildren().add(arrow);
                        }
                        this.container.getChildren().add(0, container);
                    }
                    temp = temp.getParent();
                }
                setHvalue(1);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("/css/controls/jfx-tree-view-path.css").toExternalForm();
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

    private JFXButton createLastButton(TreeItem temp) {
        return new JFXButton(temp.getValue().toString()) {
            {
                setPadding(new Insets(getOffset(), getOffset(), getOffset(), 2 * getOffset()));
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
                    getOffset(), height / 2});
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
                new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                OFFSET
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(ScrollPane.getClassCssMetaData());
            styleables.addAll(getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }


}
