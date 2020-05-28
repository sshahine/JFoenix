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
