package com.modzelewski.nfcgb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class BackgroundModel {
	private MainActivity mainActivity;
	// selected event
	private EventData currentEvent = null;
	// event list for spinner
	public List<EventData> events = new LinkedList<EventData>();
	// person list for left list
	public List<PersonData> persons = new LinkedList<PersonData>();
	// group list for right list
	public List<GroupData> groups = new LinkedList<GroupData>();

	// List<EventMembershipData> memberships;

	public BackgroundModel(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	private DatabaseHelper getHelper() {
		return mainActivity.getHelper();
	}

	public EventData getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(EventData currentEvent) {
		if (this.currentEvent != currentEvent) {
			this.currentEvent = currentEvent;
			reloadPersons();
		}
	}

	// TODO: OPTIMIZE! OPTIMIZE! OPTIMIZE!
	private void reloadPersons() {
		// TODO: cross-select
		RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
		List<EventMembershipData> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.id).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();

		persons.clear();

		for (EventMembershipData emd : eventMemberships) {
			PersonData pd = personDao.queryForId(emd.person_id);
			persons.add(pd);
			personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
		}
	}

	public List<EventData> getEvents() {
		return events;
	}

	public ArrayList<String> getEventList() {
		ArrayList<String> eventNames = new ArrayList<String>();
		for (EventData ed : events) {
			eventNames.add(String.format("%s (%s %d)", ed.eventname, ed.wintersemester ? "WiSe" : "SoSe", ed.year));
		}
		return eventNames;
	}

	public void setEvents(List<EventData> events) {
		this.events.clear();
		this.events.addAll(events);
		Collections.sort(this.events);
	}

	public List<PersonData> getPersons() {
		return persons;
	}

	public void setPersons(List<PersonData> persons) {
		this.persons = persons;
	}

	public List<GroupData> getGroups() {
	  return groups;
  }
}
