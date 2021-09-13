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

package com.jfoenix.svg;

import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Node that is used to show svg images
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class SVGGlyph extends Pane {
    private static final String DEFAULT_STYLE_CLASS = "jfx-svg-glyph";

    private final int glyphId;
    private final String name;
    private static final int DEFAULT_PREF_SIZE = 64;
    private double widthHeightRatio = 1;
    private ObjectProperty<Paint> fill = new SimpleObjectProperty<>();

    public SVGGlyph() {
        this(null);
    }

    public SVGGlyph(@NamedArg("svgPathContent") String svgPathContent) {
        this(-1, "UNNAMED", svgPathContent, Color.BLACK);
    }

    public SVGGlyph(@NamedArg("svgPathContent") String svgPathContent, @NamedArg("fill") Paint fill) {
        this(-1, "UNNAMED", svgPathContent, fill);
    }

    /**
     * Constructs SVGGlyph node for a specified svg content and color
     * <b>Note:</b> name and glyphId is not needed when creating a single SVG image,
     * they have been used in {@link SVGGlyphLoader} to load icomoon svg font.
     *
     * @param glyphId        integer represents the glyph id
     * @param name           glyph name
     * @param svgPathContent svg content
     * @param fill           svg color
     */
    public SVGGlyph(int glyphId, String name, String svgPathContent, Paint fill) {
        this.glyphId = glyphId;
        this.name = name;
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.fill.addListener((observable) -> setBackground(new Background(
            new BackgroundFill(getFill() == null ? Color.BLACK : getFill(), null, null))));

        shapeProperty().addListener(observable -> {
            Shape shape = getShape();
            if (getShape() != null) {
                widthHeightRatio = shape.prefWidth(-1) / shape.prefHeight(-1);
                if (getSize() != Region.USE_COMPUTED_SIZE) {
                    setSizeRatio(getSize());
                }
            }
        });

        if (svgPathContent != null && !svgPathContent.isEmpty()) {
            SVGPath shape = new SVGPath();
            shape.setContent(svgPathContent);
            setShape(shape);
            setFill(fill);
        }

        setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
    }

    /**
     * @return current svg id
     */
    public int getGlyphId() {
        return glyphId;
    }

    /**
     * @return current svg name
     */
    public String getName() {
        return name;
    }

    /**
     * svg color property
     */
    public void setFill(Paint fill) {
        this.fill.setValue(fill);
    }

    public ObjectProperty<Paint> fillProperty() {
        return fill;
    }

    public Paint getFill() {
        return fill.getValue();
    }

    /**
     * resize the svg to a certain width and height
     *
     * @param width
     * @param height
     */
    public void setSize(double width, double height) {
        this.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        this.setPrefSize(width, height);
        this.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
    }

    /**
     * resize the svg to this size while keeping the width/height ratio
     *
     * @param size in pixel
     */
    private void setSizeRatio(double size) {
        double width = widthHeightRatio * size;
        double height = size / widthHeightRatio;
        if (width <= size) {
            setSize(width, size);
        } else if (height <= size) {
            setSize(size, height);
        } else {
            setSize(size, size);
        }
    }

    /**
     * resize the svg to certain width while keeping the width/height ratio
     *
     * @param width in pixel
     */
    public void setSizeForWidth(double width) {
        double height = width / widthHeightRatio;
        setSize(width, height);
    }

    /**
     * resize the svg to certain width while keeping the width/height ratio
     *
     * @param height in pixel
     */
    public void setSizeForHeight(double height) {
        double width = height * widthHeightRatio;
        setSize(width, height);
    }

    /**
     * specifies the radius of the spinner node, by default it's set to -1 (USE_COMPUTED_SIZE)
     */
    private StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(StyleableProperties.SIZE,
        SVGGlyph.this,
        "size",
        Region.USE_COMPUTED_SIZE) {
        @Override
        public void invalidated() {
            setSizeRatio(getSize());
        }
    };

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    private static class StyleableProperties {
        private static final CssMetaData<SVGGlyph, Number> SIZE =
            new CssMetaData<SVGGlyph, Number>("-jfx-size",
                SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
                @Override
                public boolean isSettable(SVGGlyph control) {
                    return control.size == null || !control.size.isBound();
                }

                @Override
                public StyleableDoubleProperty getStyleableProperty(SVGGlyph control) {
                    return control.size;
                }
            };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Pane.getClassCssMetaData());
            Collections.addAll(styleables,
                SIZE
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return SVGGlyph.StyleableProperties.CHILD_STYLEABLES;
    }
}
