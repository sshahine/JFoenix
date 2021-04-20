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

import javafx.animation.Interpolator;
import javafx.beans.value.WritableValue;

import java.util.function.Supplier;

/**
 * Wrapper for JFXDrawer animation key value
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-05-03
 */
public class JFXDrawerKeyValue<T> {

    private WritableValue<T> target;
    private Supplier<T> closeValueSupplier;
    private Supplier<T> openValueSupplier;
    private Interpolator interpolator;
    private Supplier<Boolean> animateCondition = () -> true;

    public WritableValue<T> getTarget() {
        return target;
    }

    public Supplier<T> getCloseValueSupplier() {
        return closeValueSupplier;
    }

    public Supplier<T> getOpenValueSupplier() {
        return openValueSupplier;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public boolean isValid() {
        return animateCondition == null ? true : animateCondition.get();
    }

    public static JFXDrawerKeyValueBuilder builder() {
        return new JFXDrawerKeyValueBuilder();
    }

    public void applyOpenValues() {
        target.setValue(getOpenValueSupplier().get());
    }

    public void applyCloseValues(){
        target.setValue(getCloseValueSupplier().get());
    }

    public static final class JFXDrawerKeyValueBuilder<T> {
        private WritableValue<T> target;
        private Interpolator interpolator = Interpolator.EASE_BOTH;
        private Supplier<Boolean> animateCondition = () -> true;
        private Supplier<T> closeValueSupplier;
        private Supplier<T> openValueSupplier;

        private JFXDrawerKeyValueBuilder() {
        }

        public static JFXDrawerKeyValueBuilder aJFXDrawerKeyValue() {
            return new JFXDrawerKeyValueBuilder();
        }

        public JFXDrawerKeyValueBuilder setTarget(WritableValue<T> target) {
            this.target = target;
            return this;
        }

        public JFXDrawerKeyValueBuilder setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public JFXDrawerKeyValueBuilder setAnimateCondition(Supplier<Boolean> animateCondition) {
            this.animateCondition = animateCondition;
            return this;
        }

        public JFXDrawerKeyValueBuilder setCloseValue(T closeValue) {
            this.closeValueSupplier = () -> closeValue;
            return this;
        }

        public JFXDrawerKeyValueBuilder setCloseValueSupplier(Supplier<T> closeValueSupplier) {
            this.closeValueSupplier = closeValueSupplier;
            return this;
        }

        public JFXDrawerKeyValueBuilder setOpenValueSupplier(Supplier<T> openValueSupplier) {
            this.openValueSupplier = openValueSupplier;
            return this;
        }

        public JFXDrawerKeyValueBuilder setOpenValue(T openValue) {
            this.openValueSupplier = () -> openValue;
            return this;
        }

        public JFXDrawerKeyValue<T> build() {
            JFXDrawerKeyValue<T> jFXDrawerKeyValue = new JFXDrawerKeyValue();
            jFXDrawerKeyValue.openValueSupplier = this.openValueSupplier;
            jFXDrawerKeyValue.closeValueSupplier = this.closeValueSupplier;
            jFXDrawerKeyValue.target = this.target;
            jFXDrawerKeyValue.interpolator = this.interpolator;
            jFXDrawerKeyValue.animateCondition = this.animateCondition;
            return jFXDrawerKeyValue;
        }
    }
}
