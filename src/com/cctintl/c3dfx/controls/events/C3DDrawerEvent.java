package com.cctintl.c3dfx.controls.events;

import javafx.event.Event;
import javafx.event.EventType;

public class C3DDrawerEvent extends Event {
	
	private static final long serialVersionUID = 1L;

	public C3DDrawerEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public static final EventType<C3DDrawerEvent> CLOSED =
			new EventType<C3DDrawerEvent> (Event.ANY, "CLOSED");
	
	public static final EventType<C3DDrawerEvent> OPENED =
			new EventType<C3DDrawerEvent> (Event.ANY, "OPENED");
	
	
}
