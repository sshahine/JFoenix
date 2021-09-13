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

package com.jfoenix.controls.events;

import com.jfoenix.controls.JFXDrawer;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * JFXDrawer events, used exclusively by the following methods:
 * <ul>
 * <li>{@link JFXDrawer#open()}
 * <li>{@link JFXDrawer#close()}
 * </ul>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDrawerEvent extends Event {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new JFXDrawer {@code Event} with the specified event type
     *
     * @param eventType the event type
     */
    public JFXDrawerEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * This event occurs when a JFXDrawer is closed, no longer visible to the user
     * ( after the exit animation ends )
     */
    public static final EventType<JFXDrawerEvent> CLOSED =
        new EventType<>(Event.ANY, "JFX_DRAWER_CLOSED");

    /**
     * This event occurs when a JFXDrawer is drawn, visible to the user
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDrawerEvent> OPENED =
        new EventType<>(Event.ANY, "JFX_DRAWER_OPENED");

    /**
     * This event occurs when a JFXDrawer is being drawn, visible to the user
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDrawerEvent> OPENING =
        new EventType<>(Event.ANY, "JFX_DRAWER_OPENING");


    /**
     * This event occurs when a JFXDrawer is being closed, will become invisible to the user
     * at the end of the animation
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDrawerEvent> CLOSING =
        new EventType<>(Event.ANY, "JFX_DRAWER_CLOSING");


}
