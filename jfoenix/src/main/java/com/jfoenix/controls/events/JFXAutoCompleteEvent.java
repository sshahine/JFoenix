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

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Shadi Shaheen
 * @version 1.0.0
 * @since 2018-02-01
 */
public class JFXAutoCompleteEvent<T> extends Event {

	private T object;

	public JFXAutoCompleteEvent(EventType<? extends Event> eventType, T object) {
		super(eventType);
		this.object = object;
	}

	public T getObject(){
		return object;
	}

	//TODO: more events to be added
	public static final EventType<JFXAutoCompleteEvent> SELECTION =
			new EventType<JFXAutoCompleteEvent>(Event.ANY, "JFX_AUTOCOMPLETE_SELECTION");
}
