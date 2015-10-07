/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.controls.events;

import javafx.event.Event;
import javafx.event.EventType;

public class JFXDrawerEvent extends Event {
	
	private static final long serialVersionUID = 1L;

	public JFXDrawerEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public static final EventType<JFXDrawerEvent> CLOSED =
			new EventType<JFXDrawerEvent> (Event.ANY, "DRAWER_CLOSED");
	
	public static final EventType<JFXDrawerEvent> OPENED =
			new EventType<JFXDrawerEvent> (Event.ANY, "DRAWER_OPENED");
	
	
}
