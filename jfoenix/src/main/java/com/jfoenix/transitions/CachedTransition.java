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

import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * applies animation on a cached node to improve the performance
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class CachedTransition extends Transition {
    protected final Node node;
    protected ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
    private CacheMemento[] mementos = new CacheMemento[0];

    public CachedTransition(final Node node, final Timeline timeline) {
        this.node = node;
        this.timeline.set(timeline);
        mementos = node == null ? mementos : new CacheMemento[]{new CacheMemento(node)};
        statusProperty().addListener(observable -> {
            switch (getStatus()) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });
    }

    public CachedTransition(final Node node, final Timeline timeline, CacheMemento... cacheMomentos) {
        this.node = node;
        this.timeline.set(timeline);
        mementos = new CacheMemento[(node == null ? 0 : 1) + cacheMomentos.length];
        if (node != null) {
            mementos[0] = new CacheMemento(node);
        }
        System.arraycopy(cacheMomentos, 0, mementos, node == null ? 0 : 1, cacheMomentos.length);
        statusProperty().addListener(observable -> {
            switch (getStatus()) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });
    }

    /**
     * Called when the animation is starting
     */
    protected void starting() {
        if (mementos != null) {
            for (int i = 0; i < mementos.length; i++) {
                mementos[i].cache();
            }
        }
    }

    /**
     * Called when the animation is stopping
     */
    protected void stopping() {
        if (mementos != null) {
            for (int i = 0; i < mementos.length; i++) {
                mementos[i].restore();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void interpolate(double d) {
        timeline.get().playFrom(Duration.seconds(d));
        timeline.get().stop();
    }
}
