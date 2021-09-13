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

import javafx.util.Duration;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-09-21
 */

public class JFXKeyFrame {

    private Duration duration;
    private Set<JFXKeyValue<?>> keyValues = new CopyOnWriteArraySet<>();
    private Supplier<Boolean> animateCondition = null;

    public JFXKeyFrame(Duration duration, JFXKeyValue<?>... keyValues) {
        this.duration = duration;
        for (final JFXKeyValue<?> keyValue : keyValues) {
            if (keyValue != null) {
                this.keyValues.add(keyValue);
            }
        }
    }

    private JFXKeyFrame() {

    }

    public final Duration getDuration() {
        return duration;
    }

    public final Set<JFXKeyValue<?>> getValues() {
        return keyValues;
    }

    public Supplier<Boolean> getAnimateCondition() {
        return animateCondition;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Duration duration;
        private Set<JFXKeyValue<?>> keyValues = new CopyOnWriteArraySet<>();
        private Supplier<Boolean> animateCondition = null;

        private Builder() {
        }

        public Builder setDuration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder setKeyValues(JFXKeyValue<?>... keyValues) {
            for (final JFXKeyValue<?> keyValue : keyValues) {
                if (keyValue != null) {
                    this.keyValues.add(keyValue);
                }
            }
            return this;
        }

        public Builder setAnimateCondition(Supplier<Boolean> animateCondition) {
            this.animateCondition = animateCondition;
            return this;
        }

        public JFXKeyFrame build() {
            JFXKeyFrame jFXKeyFrame = new JFXKeyFrame();
            jFXKeyFrame.duration = this.duration;
            jFXKeyFrame.keyValues = this.keyValues;
            jFXKeyFrame.animateCondition = this.animateCondition;
            return jFXKeyFrame;
        }
    }
}
