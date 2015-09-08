package com.jfoenix.controls.events;

import javafx.event.Event;
import javafx.event.EventType;


public class JFXDialogEvent extends Event{
	
	private static final long serialVersionUID = 1L;

	public JFXDialogEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public static final EventType<JFXDialogEvent> CLOSED =
			new EventType<JFXDialogEvent> (Event.ANY, "DIALOG_CLOSED");
	
	public static final EventType<JFXDialogEvent> OPENED =
			new EventType<JFXDialogEvent> (Event.ANY, "DIALOG_OPENED");
	
	
}
