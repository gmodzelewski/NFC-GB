package com.modzelewski.nfcgb.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class BackgroundModel {
	private final MainActivity mainActivity;
	// selected event
	private Event currentEvent = null;
	// event list for spinner
	public final List<Event> events = new LinkedList<Event>();
	// person list for left list
	public List<Person> persons = new LinkedList<Person>();
	// group list for right list
	public final List<Group> groups = new LinkedList<Group>();
	private DatabaseHelper databaseHelper;

	public BackgroundModel(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public DatabaseHelper getHelper() {
		databaseHelper = mainActivity.getHelper();
		return databaseHelper;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event currentEvent) {
		if (this.currentEvent != currentEvent) {
			this.currentEvent = currentEvent;
			reloadPersons();
			reloadGroups();
		}
	}

	private void reloadPersons() {
		persons.clear();
		// if(currentEvent == null)
		// return;
		List<EventMembership> eventMemberships = getEventMemberships(currentEvent);
		getPersons(eventMemberships);
	}

	public List<Person> getPersons(List<EventMembership> eventMemberships) {
		// ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();

		for (EventMembership emd : eventMemberships) {
			Person pd = personDao.queryForId(emd.getPerson_id());
			persons.add(pd);
			// personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
		}
		return persons;
	}

	private void reloadGroups() {
		groups.clear();

		if (currentEvent == null) {
			return;
		}

		List<Group> groupsWithCurrentEvent = getGroups(currentEvent);

		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();

		for (Group group : groupsWithCurrentEvent) {
			List<GroupMembership> groupMemberships = getGroupMemberships(group);

			for (GroupMembership groupMembership : groupMemberships) {
				// group.getPerson().add(getPersonById(groupMembership.getPersonId()));
				group.getPerson().add(personDao.queryForId(groupMembership.getPersonId()));
			}
			groups.add(group);
		}
	}

	private List<GroupMembership> getGroupMemberships(Group group) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
		List<GroupMembership> groupMemberships = null;

		try {
			groupMemberships = groupMembershipDao.queryBuilder().where().eq("group_id", group.id).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return groupMemberships;
	}

	public List<Group> getGroups(Event currentEvent) {
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDataDao();
		List<Group> groupResult = null;
		try {
			groupResult = groupDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return groupResult;
	}

	public List<Event> getEvents() {
		return events;
	}

	public List<EventMembership> getEventMemberships(Event currentEvent) {
		this.currentEvent = currentEvent;
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
		List<EventMembership> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eventMemberships;
	}

	public List<GroupMembership> getGroupMemberships(Event currentEvent) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
		List<GroupMembership> groupMemberships = null;
		try {
			groupMemberships = groupMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupMemberships;
	}

	public ArrayList<String> getEventList() {
		ArrayList<String> eventNames = new ArrayList<String>();
		for (Event ed : events) {
			eventNames.add(String.format("%s (%s %d)", ed.getEventname(), ed.isWintersemester() ? "WiSe" : "SoSe", ed.getYear()));
		}
		return eventNames;
	}

	public void setEvents(List<Event> events) {
		this.events.clear();
		this.events.addAll(events);
		setCurrentEvent(events.get(0));
		Collections.sort(this.events);
	}

	public List<Person> getPersons() {
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();
		List<EventMembership> eventMemberships = getEventMemberships(getCurrentEvent());
		for (EventMembership eventMembership : eventMemberships) {
			persons.add(personDao.queryForId(eventMembership.getPerson_id()));
		}
		return persons;
	}

	public Person getPersonById(int id) {
		// Person person = null;
		// for(Person p : persons)
		// if(p.getId() == id)
		// person = p;
		// return person;
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();
		return personDao.queryForId(id);
	}

	public Group getGroupById(int id) {
		Group group = null;
		for (Group g : groups)
			if (g.id == id)
				group = g;
		return group;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups.clear();
		this.groups.addAll(groups);
	}
}
