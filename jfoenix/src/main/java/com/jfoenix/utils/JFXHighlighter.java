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

package com.jfoenix.utils;

import com.jfoenix.concurrency.JFXUtilities;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLine;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * JFXHighlighter is used to highlight Text and LabeledText nodes
 * (in a specific Pane) that matches the user query.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-03-22
 */
public class JFXHighlighter {

    private Pane pane;
    private HashMap<Node, List<Rectangle>> boxes = new HashMap<>();
    private ObjectProperty<Paint> paint = new SimpleObjectProperty<>(Color.rgb(255, 0, 0, 0.4));

    private Method textLayoutMethod;
    {
        try {
            textLayoutMethod = Text.class.getDeclaredMethod("getTextLayout");
            textLayoutMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * highlights the matching text in the specified pane
     * @param pane node to search into its text
     * @param query search text
     */
    public synchronized void highlight(Pane pane, String query) {
        if (this.pane != null && !boxes.isEmpty()) {
            clear();
        }
        if(query.isEmpty()) return;

        this.pane = pane;

        Set<Node> nodes = getTextNodes(pane);

        ArrayList<Rectangle> allRectangles = new ArrayList<>();
        nodes.forEach(node -> {
            Text text = ((Text) node);
            final int beginIndex = text.getText().toLowerCase().indexOf(query.toLowerCase());
            if (node.isVisible() && beginIndex > -1) {
                ArrayList<Bounds> boundingBoxes = getMatchingBounds(query, node, text);
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
        });

        Platform.runLater(()-> pane.getChildren().addAll(allRectangles));
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
            if(boxes.get(text)!=null && !boxes.get(text).isEmpty())
                Platform.runLater(() -> pane.getChildren().removeAll(boxes.get(text)));
        }
    }

    private Set<Node> getTextNodes(Pane pane) {
        Set<Node> labeledTextNodes = pane.lookupAll("LabeledText");
        Set<Node> textNodes = pane.lookupAll("Text");
        Set<Node> nodes = new HashSet<>();
        nodes.addAll(labeledTextNodes);
        nodes.addAll(textNodes);
        return nodes;
    }

    private ArrayList<Bounds> getMatchingBounds(String query, Node node, Text text) {

        Bounds sceneBounds = node.localToScene(text.getLayoutBounds());
        Bounds parentSceneBounds = pane.localToScene(pane.getLayoutBounds());

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

                rectBounds.add(new BoundingBox(sceneBounds.getMinX() + startX - parentSceneBounds.getMinX(),
                    sceneBounds.getMinY() - parentSceneBounds.getMinY() + startY,
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
        List<Rectangle> allBoxes = new ArrayList<>();
        boxes.values().forEach(allBoxes::addAll);
        boxes.clear();
        if(pane!=null) JFXUtilities.runInFX(()-> pane.getChildren().removeAll(allBoxes));
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
