package com.modzelewski.nfcgb.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.modzelewski.nfcgb.model.Event;

public class EventManager {
	// selected event
	private Event currentEvent = null;
	// event list for spinner
	public final List<Event> events = new LinkedList<Event>();

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public ArrayList<String> getEventList() {
		ArrayList<String> eventNames = new ArrayList<String>();
		for (Event ed : events) {
			eventNames.add(String.format("%s (%s %d)", ed.getEventname(), ed.isWintersemester() ? "WiSe" : "SoSe", ed.getYear()));
		}
		return eventNames;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setCurrentEvent(Event currentEvent) {
		if (this.currentEvent != currentEvent) {
			this.currentEvent = currentEvent;
			// model.reloadPersons();
			// model.reloadGroups();
		}
	}
	public void setEvents(List<Event> events) {
		this.events.clear();
		this.events.addAll(events);
		Collections.sort(this.events);
	}
}
