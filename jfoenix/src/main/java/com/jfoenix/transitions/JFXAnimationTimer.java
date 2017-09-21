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

package com.jfoenix.transitions;

import javafx.animation.AnimationTimer;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.*;

/**
 * Custom AnimationTimer that can be created the same way as a timeline,
 * however it doesn't behave the same yet. it only animates in one direction,
 * it doesn't support animation 0 -> 1 -> 0.5
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-09-21
 */

public class JFXAnimationTimer extends AnimationTimer{

    private Set<AnimationHandler> animationHandlers = new HashSet<>();
    private HashMap<WritableValue<?>, Object> initialValuesMap = new HashMap<>();
    private long startTime = -1;
    private boolean running = false;
    private List<CacheMomento> caches = new ArrayList<>();

    public JFXAnimationTimer(JFXKeyFrame... keyFrames){
        for (JFXKeyFrame keyFrame : keyFrames) {
            Duration duration = keyFrame.getTime();
            final Set<JFXKeyValue> keyValuesSet = keyFrame.getValues();
            if(!keyValuesSet.isEmpty()){
                animationHandlers.add(new AnimationHandler(duration, keyFrame.getValues()));
            }
        }
    }

    @Override
    public void start() {
        super.start();
        running = true;
        startTime = -1;
        animationHandlers.forEach(AnimationHandler::init);
        caches.forEach(CacheMomento::cache);
    }

    @Override
    public void handle(long now) {
        startTime = startTime == -1 ? now : startTime;
        double totalElapsedMilliseconds = (now - startTime) / 1000000.0;
        boolean stop = true;
        for (AnimationHandler handler : animationHandlers) {
            handler.animate(totalElapsedMilliseconds);
            if (!handler.finished) {
                stop = false;
            }
        }
        if(stop)
            this.stop();
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
        initialValuesMap.clear();
        caches.forEach(CacheMomento::restore);
    }

    public boolean isRunning() {
        return running;
    }

    public void setCacheNodes(Node... nodesToCache){
        caches.clear();
        if(nodesToCache!=null){
            for (Node node : nodesToCache) {
                caches.add(new CacheMomento(node));
            }
        }
    }

    class AnimationHandler {
        double duration;
        Set<JFXKeyValue> keyValues;
        boolean finished = false;

        public AnimationHandler(Duration duration, Set<JFXKeyValue> keyValues) {
            this.duration = duration.toMillis();
            this.keyValues = keyValues;
        }

        public void init() {
            finished = false;
            keyValues.forEach(keyValue -> {
                if (keyValue.getTarget() != null)
                    initialValuesMap.putIfAbsent(keyValue.getTarget(), keyValue.getTarget().getValue());
            });
        }

        // now in milliseconds
        public void animate(double now){
            if(now <=  duration){
                keyValues.forEach(keyValue -> {
                    if(keyValue.isValid()) {
                        final WritableValue target = keyValue.getTarget();
                        if (target != null && !target.getValue().equals(keyValue.getEndValue()))
                            target.setValue(keyValue.getInterpolator().interpolate(initialValuesMap.get(target), keyValue.getEndValue(), now / duration));
                    }
                });
            }else{
                if(!finished) {
                    finished = true;
                    keyValues.forEach(keyValue -> {
                        if(keyValue.isValid()) {
                            final WritableValue target = keyValue.getTarget();
                            if (target != null) target.setValue(keyValue.getEndValue());
                        }
                    });
                }
            }
        }
    }
}
