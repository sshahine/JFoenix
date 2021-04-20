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

package com.jfoenix.transitions;

import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.concurrent.atomic.AtomicBoolean;

public class CacheMemento {
    private boolean cache;
    private boolean cacheShape;
    private boolean snapToPixel;
    private CacheHint cacheHint = CacheHint.DEFAULT;
    private Node node;
    private AtomicBoolean isCached = new AtomicBoolean(false);

    public CacheMemento(Node node) {
        this.node = node;
    }

    /**
     * this method will cache the node only if it wasn't cached before
     */
    public void cache() {
        if (!isCached.getAndSet(true)) {
            this.cache = node.isCache();
            this.cacheHint = node.getCacheHint();
            node.setCache(true);
            node.setCacheHint(CacheHint.SPEED);
            if (node instanceof Region) {
                this.cacheShape = ((Region) node).isCacheShape();
                this.snapToPixel = ((Region) node).isSnapToPixel();
                ((Region) node).setCacheShape(true);
                ((Region) node).setSnapToPixel(true);
            }
        }
    }

    public void restore() {
        if (isCached.getAndSet(false)) {
            node.setCache(cache);
            node.setCacheHint(cacheHint);
            if (node instanceof Region) {
                ((Region) node).setCacheShape(cacheShape);
                ((Region) node).setSnapToPixel(snapToPixel);
            }
        }
    }
}
