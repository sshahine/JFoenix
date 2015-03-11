package com.cctintl.c3dfx.controls.events;

import javafx.event.Event;
import javafx.event.EventType;


public class C3DDialogEvent extends Event{
	
	private static final long serialVersionUID = 1L;

	public C3DDialogEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public static final EventType<C3DDialogEvent> CLOSED =
			new EventType<C3DDialogEvent> (Event.ANY, "CLOSED");
	
	public static final EventType<C3DDialogEvent> OPENED =
			new EventType<C3DDialogEvent> (Event.ANY, "OPENED");
	
	
}
