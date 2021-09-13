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

package com.jfoenix.cache;

import com.jfoenix.transitions.CacheMemento;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.WeakHashMap;

public interface CachePolicy<T extends Node> {

    void cache(T node);

    void restore(T node);

    CachePolicy<Node> CACHE = new CachePolicy<Node>() {

        private WeakHashMap<Node, CacheMemento> cache = new WeakHashMap<>();

        @Override
        public void cache(Node node) {
            if (!cache.containsKey(node)) {
                final CacheMemento cacheMemento = new CacheMemento(node);
                cache.put(node, cacheMemento);
                cacheMemento.cache();
            }
        }

        @Override
        public void restore(Node node) {
            CacheMemento cacheMemento = cache.remove(node);
            if (cacheMemento != null) {
                cacheMemento.restore();
            }
        }
    };

    CachePolicy<Node> NONE = new CachePolicy<Node>() {
        @Override
        public void cache(Node node) {
            // do nothing
        }

        @Override
        public void restore(Node node) {
            // do nothing
        }
    };


    CachePolicy<Pane> IMAGE = new CachePolicy<Pane>() {

        private WeakHashMap<Node, ArrayList<Node>> cache = new WeakHashMap<>();

        @Override
        public void cache(Pane node) {
            if (!cache.containsKey(node)) {
                SnapshotParameters snapShotparams = new SnapshotParameters();
                snapShotparams.setFill(Color.TRANSPARENT);
                WritableImage temp = node.snapshot(snapShotparams,
                    new WritableImage((int) node.getLayoutBounds().getWidth(),
                        (int) node.getLayoutBounds().getHeight()));
                ImageView tempImage = new ImageView(temp);
                tempImage.setCache(true);
                tempImage.setCacheHint(CacheHint.SPEED);
                cache.put(node, new ArrayList<>(node.getChildren()));
                node.getChildren().setAll(tempImage);
            }
        }

        @Override
        public void restore(Pane node) {
            ArrayList<Node> children = cache.remove(node);
            if (children != null) {
                node.getChildren().setAll(children);
            }
        }
    };

}
