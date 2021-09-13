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

package com.jfoenix.controls.pannable.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implementation of observer pattern using weak references
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2020-04-30
 */
public class ResilientObservable<L> implements IObservableObject<L> {

    private CopyOnWriteArrayList<WeakObject<L>> listeners = new CopyOnWriteArrayList<>();

    public ResilientObservable() {

    }

    public boolean addListener(L observer) {
        return listeners.add(new WeakObject<>(observer));
    }

    public boolean removeListener(L observer) {
        return listeners.remove(new WeakObject<>(observer));
    }

    public void fireEvent(Consumer<L> listenerConsumer) {
        ArrayList<WeakObject<L>> toBeRemoved = new ArrayList<>();
        for (Iterator<WeakObject<L>> itr = listeners.iterator(); itr.hasNext(); ) {
            WeakObject<L> ref = itr.next();
            try {
                // notify
                L listener = ref.get();
                if (listener != null) {
                    listenerConsumer.accept(listener);
                } else {
                    toBeRemoved.add(ref);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                toBeRemoved.add(ref);
            }
        }
        // remove null / invalid references
        listeners.removeAll(toBeRemoved);
    }

    public Collection<L> listeners() {
        return Collections.unmodifiableCollection(
            listeners.stream().map(Reference::get).collect(Collectors.toList()));
    }

    private static class WeakObject<T> extends WeakReference<T> {
        private WeakObject(T referent) {
            super(referent);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof WeakObject)) {
                return false;
            }
            return ((WeakObject) obj).get() == this.get();
        }
    }
}
