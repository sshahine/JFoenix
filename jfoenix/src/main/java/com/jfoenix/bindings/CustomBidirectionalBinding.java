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

package com.jfoenix.bindings;

import com.jfoenix.bindings.base.IBiBinder;
import com.jfoenix.bindings.base.IPropertyConverter;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Custom bidirectional binder, used for bidirectional binding between properties of different types
 * or with different accessibility methods (e.g {@link ReadOnlyProperty} with setter method)
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2020-04-30
 */
public class CustomBidirectionalBinding<A, B> implements IBiBinder {

    private final WeakReference<ReadOnlyProperty<A>> propertyRef1;
    private final WeakReference<ReadOnlyProperty<B>> propertyRef2;
    private final Consumer<A> propertyRef1Setter;
    private final Consumer<B> propertyRef2Setter;
    private HashMap<ReadOnlyProperty<?>, ChangeListener> listeners = new HashMap<>();
    private IPropertyConverter<A, B> converter;

    public CustomBidirectionalBinding(Property<A> a, Property<B> b, IPropertyConverter<A, B> converter) {
        this(a, value -> a.setValue(value),
            b, value -> b.setValue(value),
            converter);
    }

    public CustomBidirectionalBinding(ReadOnlyProperty<A> a, Consumer<A> propertyRef1Setter,
                                      ReadOnlyProperty<B> b, Consumer<B> propertyRef2Setter,
                                      IPropertyConverter<A, B> converter) {
        this.propertyRef1 = new WeakReference<>(a);
        this.propertyRef2 = new WeakReference<>(b);
        this.propertyRef1Setter = propertyRef1Setter;
        this.propertyRef2Setter = propertyRef2Setter;
        this.converter = converter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void unbindBi() {
        listeners.entrySet().forEach(entry -> entry.getKey().removeListener(entry.getValue()));
    }

    @Override
    public void bindBi() {
        addFlaggedChangeListener(propertyRef1.get(), propertyRef1Setter, propertyRef2.get(), propertyRef2Setter, param -> converter.to(param));
        addFlaggedChangeListener(propertyRef2.get(), propertyRef2Setter, propertyRef1.get(), propertyRef1Setter, param -> converter.from(param));
        propertyRef2Setter.accept(converter.to(propertyRef1.get().getValue()));
    }

    private <a, b> void addFlaggedChangeListener(ReadOnlyProperty<a> a, Consumer<a> aConsumer,
                                                 ReadOnlyProperty<b> b, Consumer<b> bConsumer,
                                                 Callback<a, b> updateB) {
        ChangeListener<a> listener = new ChangeListener<a>() {
            private boolean alreadyCalled = false;

            @Override
            public void changed(ObservableValue<? extends a> observable, a oldValue, a newValue) {
                if (alreadyCalled) {
                    return;
                }
                try {
                    alreadyCalled = true;
                    bConsumer.accept(updateB.call(newValue));
                } finally {
                    alreadyCalled = false;
                }
            }
        };
        listeners.put(a, listener);
        a.addListener(listener);
    }
}
