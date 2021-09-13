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

package com.jfoenix.utils;

import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLine;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * JFXHighlighter is used to highlight Text and LabeledText nodes
 * (in a specific {@link Parent}) that matches the user query.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-03-22
 */
public class JFXHighlighter {

    private Parent parent;
    private HashMap<Node, List<Rectangle>> boxes = new HashMap<>();
    private ObjectProperty<Paint> paint = new SimpleObjectProperty<>(Color.rgb(255, 0, 0, 0.4));

    private Method textLayoutMethod;
    private Field parentChildrenField;
    {
        try {
            textLayoutMethod = Text.class.getDeclaredMethod("getTextLayout");
            textLayoutMethod.setAccessible(true);
            parentChildrenField = Parent.class.getDeclaredField("children");
            parentChildrenField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * highlights the matching text in the specified pane
     * @param pane node to search into its text
     * @param query search text
     */
    public synchronized void highlight(Parent pane, String query) {
        if (this.parent != null && !boxes.isEmpty()) {
            clear();
        }
        if(query == null || query.isEmpty()) return;

        this.parent = pane;

        Set<Node> nodes = getTextNodes(pane);

        ArrayList<Rectangle> allRectangles = new ArrayList<>();
        for (Node node : nodes) {
            Text text = ((Text) node);
            final int beginIndex = text.getText().toLowerCase().indexOf(query.toLowerCase());
            if (beginIndex > -1 && node.impl_isTreeVisible()) {
                ArrayList<Bounds> boundingBoxes = getMatchingBounds(query, text);
                ArrayList<Rectangle> rectangles = new ArrayList<>();
                for (Bounds boundingBox : boundingBoxes) {
                    HighLightRectangle rect = new HighLightRectangle(text);
                    rect.setCacheHint(CacheHint.SPEED);
                    rect.setCache(true);
                    rect.setMouseTransparent(true);
                    rect.setBlendMode(BlendMode.MULTIPLY);
                    rect.fillProperty().bind(paintProperty());
                    rect.setManaged(false);
                    rect.setX(boundingBox.getMinX());
                    rect.setY(boundingBox.getMinY());
                    rect.setWidth(boundingBox.getWidth());
                    rect.setHeight(boundingBox.getHeight());
                    rectangles.add(rect);
                    allRectangles.add(rect);
                }
                boxes.put(node, rectangles);
            }
        }

        JFXUtilities.runInFXAndWait(()-> getParentChildren(pane).addAll(allRectangles));
    }

    private class HighLightRectangle extends Rectangle{
        // add listener to remove the current rectangle if text was changed
        private InvalidationListener listener;

        public HighLightRectangle(Text text) {
            listener = observable -> clear(text);
            text.textProperty().addListener(new WeakInvalidationListener(listener));
            text.localToSceneTransformProperty().addListener(new WeakInvalidationListener(listener));
        }

        private void clear(Text text) {
            final List<Rectangle> rectangles = boxes.get(text);
            if(rectangles != null && !rectangles.isEmpty())
                Platform.runLater(() -> getParentChildren(parent).removeAll(rectangles));
        }
    }

    private Set<Node> getTextNodes(Parent pane) {
        Set<Node> labeledTextNodes = pane.lookupAll("LabeledText");
        Set<Node> textNodes = pane.lookupAll("Text");
        Set<Node> nodes = new HashSet<>();
        nodes.addAll(labeledTextNodes);
        nodes.addAll(textNodes);
        return nodes;
    }

    private ObservableList<Node> getParentChildren(Parent parent){
        try {
            return (ObservableList<Node>) parentChildrenField.get(parent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Bounds> getMatchingBounds(String query, Text text) {
        // find local text bounds in parent
        Bounds textBounds = parent.sceneToLocal(text.localToScene(text.getBoundsInLocal()));

        ArrayList<Bounds> rectBounds = new ArrayList<>();

        TextLayout textLayout = null;
        try {
            textLayout = (TextLayout) textLayoutMethod.invoke(text);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        int queryLength = query.length();
        TextLine[] lines = textLayout.getLines();
        // handle matches in all lines
        for (int i = 0; i < lines.length; i++) {
            TextLine line = lines[i];
            String lineText = text.getText().substring(line.getStart(), line.getStart() + line.getLength());

            final String lineTextLow = lineText.toLowerCase();
            final String queryLow = query.toLowerCase();
            int beginIndex = lineTextLow.indexOf(queryLow);
            if (beginIndex == -1) {
                continue;
            }

            RectBounds lineBounds = (line.getBounds());

            // compute Y layout
            double height = Math.round(lineBounds.getMaxY()) - Math.round(lineBounds.getMinY());
            double startY = height * i;

            // handle multiple matches in one line
            while (beginIndex != -1) {
                // compute X layout
                Text temp = new Text(lineText.substring(beginIndex, beginIndex + queryLength));
                temp.setFont(text.getFont());
                temp.applyCss();
                double width = temp.getLayoutBounds().getWidth();
                temp.setText(lineText.substring(0, beginIndex + queryLength));
                temp.applyCss();
                double maxX = temp.getLayoutBounds().getMaxX();
                double startX = maxX - width;

                rectBounds.add(new BoundingBox(textBounds.getMinX() + startX,
                    textBounds.getMinY() + startY,
                    width, temp.getLayoutBounds().getHeight()));

                beginIndex = lineTextLow.indexOf(queryLow, beginIndex + queryLength);
            }
        }

        return rectBounds;
    }

    /**
     * clear highlights
     */
    public synchronized void clear() {
        List<Rectangle> flatBoxes = new ArrayList<>();
        final Collection<List<Rectangle>> boxesCollection = boxes.values();
        for (List<Rectangle> box : boxesCollection) {
            flatBoxes.addAll(box);
        }
        boxes.clear();
        if(parent!=null) JFXUtilities.runInFXAndWait(()-> getParentChildren(parent).removeAll(flatBoxes));
    }

    public Paint getPaint() {
        return paint.get();
    }

    public ObjectProperty<Paint> paintProperty() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint.set(paint);
    }
}
