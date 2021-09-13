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

package com.jfoenix.utils;

import com.sun.javafx.collections.UnmodifiableListSet;
import com.sun.javafx.scene.traversal.Direction;
import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-02-11
 */
public class JFXNodeUtils {

    public static final EventHandler<KeyEvent> TRAVERSE_HANDLER = event -> {
        if (event.getCode() == KeyCode.TAB) {
            event.consume();
            ((Region) event.getTarget()).impl_traverse(event.isShiftDown() ? Direction.PREVIOUS : Direction.NEXT);
        }
    };

    public static void updateBackground(Background newBackground, Region nodeToUpdate) {
        updateBackground(newBackground, nodeToUpdate, Color.BLACK);
    }

    public static void updateBackground(Background newBackground, Region nodeToUpdate, Paint fill) {
        if (newBackground != null && !newBackground.getFills().isEmpty()) {
            final BackgroundFill[] fills = new BackgroundFill[newBackground.getFills().size()];
            for (int i = 0; i < newBackground.getFills().size(); i++) {
                BackgroundFill bf = newBackground.getFills().get(i);
                fills[i] = new BackgroundFill(fill, bf.getRadii(), bf.getInsets());
            }
            nodeToUpdate.setBackground(new Background(fills));
        }
    }

    public static String colorToHex(Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255)).toUpperCase();
        } else {
            return null;
        }
    }

    public static void addPressAndHoldHandler(Node node, Duration holdTime,
                                              EventHandler<MouseEvent> handler) {
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    public static void addPressAndHoldFilter(Node node, Duration holdTime,
                                             EventHandler<MouseEvent> handler) {
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        });
        node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventFilter(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    public static <T> InvalidationListener addDelayedPropertyInvalidationListener(ObservableValue<T> property,
                                                                                  Duration delayTime,
                                                                                  Consumer<T> consumer) {
        Wrapper<T> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(delayTime);
        holdTimer.setOnFinished(event -> consumer.accept(eventWrapper.content));
        final InvalidationListener invalidationListener = observable -> {
            eventWrapper.content = property.getValue();
            holdTimer.playFromStart();
        };
        property.addListener(invalidationListener);
        return invalidationListener;
    }

    public static <T> InvalidationListener addDelayedPropertyInvalidationListener(ObservableValue<T> property,
                                                                                  Duration delayTime,
                                                                                  BiConsumer<T, InvalidationListener> consumer) {
        Wrapper<T> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(delayTime);
        final InvalidationListener invalidationListener = observable -> {
            eventWrapper.content = property.getValue();
            holdTimer.playFromStart();
        };
        holdTimer.setOnFinished(event -> consumer.accept(eventWrapper.content, invalidationListener));
        property.addListener(invalidationListener);
        return invalidationListener;
    }


    public static <T> InvalidationListener addDelayedPropertyInvalidationListener(ObservableValue<T> property,
                                                                                  Duration delayTime,
                                                                                  Consumer<T> justInTimeConsumer,
                                                                                  Consumer<T> delayedConsumer) {
        Wrapper<T> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(delayTime);
        holdTimer.setOnFinished(event -> delayedConsumer.accept(eventWrapper.content));
        final InvalidationListener invalidationListener = observable -> {
            eventWrapper.content = property.getValue();
            justInTimeConsumer.accept(eventWrapper.content);
            holdTimer.playFromStart();
        };
        property.addListener(invalidationListener);
        return invalidationListener;
    }


    public static <T extends Event> EventHandler<? super T> addDelayedEventHandler(Node control, Duration delayTime,
                                                                                   final EventType<T> eventType,
                                                                                   final EventHandler<? super T> eventHandler) {
        Wrapper<T> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(delayTime);
        holdTimer.setOnFinished(finish -> eventHandler.handle(eventWrapper.content));
        final EventHandler<? super T> eventEventHandler = event -> {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        };
        control.addEventHandler(eventType, eventEventHandler);
        return eventEventHandler;
    }

    private static class Wrapper<T> {
        T content;
    }

    public static <T> Set<T> getAllChildren(Node root, Class<T> childClass) {
        final List<T> selectedChildren = new ArrayList<>();
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node head = queue.poll();
            if (childClass.isInstance(head)) {
                selectedChildren.add((T) head);
            } else if (Region.class.isInstance(head)) {
                ((Region) head).getChildrenUnmodifiable().forEach(queue::offer);
            }
        }
        return new UnmodifiableListSet<>(selectedChildren);
    }


}
