package com.modzelewski.nfcgb.model;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

	public BackgroundModel(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public DatabaseHelper getHelper() {
		return mainActivity.getHelper();
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

	// TODO: OPTIMIZE! OPTIMIZE! OPTIMIZE!
	private void reloadPersons() {
		persons.clear();

		if(currentEvent == null)
			return;
		
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
		List<EventMembership> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();


		for (EventMembership emd : eventMemberships) {
			Person pd = personDao.queryForId(emd.getPerson_id());
			persons.add(pd);
			// personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
		}
	}
	
	private void reloadGroups() {
		groups.clear();

		if(currentEvent == null) {
			return;
		}
		
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDataDao();
		List<Group> groupResult = null;
		try {
			groupResult = groupDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();
		
		for (Group gd : groupResult) {
			List<GroupMembership> groupMemberships = null;
			try {
				groupMemberships = groupMembershipDao.queryBuilder().where().eq("group_id", gd.id).query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			for (GroupMembership groupMembership : groupMemberships) {
				gd.getPerson().add(personDao.queryForId(groupMembership.person_id));
			}
			
			groups.add(gd);
		}
	}

	public List<Event> getEvents() {
		return events;
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
		Collections.sort(this.events);
	}

	public List<Person> getPersons() {
		return persons;
	}
	
	public Person getPersonById(int id) {
		Person person = null;
		for(Person p : persons)
			if(p.getId() == id)
				person = p;
		return person;
	}

	public Group getGroupById(int id) {
		Group group = null;
		for(Group g : groups)
			if(g.id == id)
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
