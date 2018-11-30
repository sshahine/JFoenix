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
