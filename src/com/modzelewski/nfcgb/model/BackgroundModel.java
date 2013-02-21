package com.modzelewski.nfcgb.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.MainActivity;
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

	public BackgroundModel(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public DatabaseHelper getHelper() {
		return mainActivity.getHelper();
	}

	public EventData getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(EventData currentEvent) {
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
		
		RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
		List<EventMembershipData> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();


		for (EventMembershipData emd : eventMemberships) {
			PersonData pd = personDao.queryForId(emd.getPerson_id());
			persons.add(pd);
			// personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
		}
	}
	
	private void reloadGroups() {
		groups.clear();

		if(currentEvent == null) {
			return;
		}
		
		RuntimeExceptionDao<GroupData, Integer> groupDao = getHelper().getGroupDataDao();
		List<GroupData> groupResult = null;
		try {
			groupResult = groupDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();
		
		for (GroupData gd : groupResult) {
			List<GroupMembershipData> groupMemberships = null;
			try {
				groupMemberships = groupMembershipDao.queryBuilder().where().eq("group_id", gd.id).query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			for (GroupMembershipData groupMembershipData : groupMemberships) {
				gd.getPerson().add(personDao.queryForId(groupMembershipData.person_id));
			}
			
			groups.add(gd);
		}
	}

	public List<EventData> getEvents() {
		return events;
	}

	public ArrayList<String> getEventList() {
		ArrayList<String> eventNames = new ArrayList<String>();
		for (EventData ed : events) {
			eventNames.add(String.format("%s (%s %d)", ed.getEventname(), ed.isWintersemester() ? "WiSe" : "SoSe", ed.getYear()));
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
	
	public PersonData getPersonById(int id) {
		PersonData person = null;
		for(PersonData p : persons)
			if(p.getId() == id)
				person = p;
		return person;
	}

	public GroupData getGroupById(int id) {
		GroupData group = null;
		for(GroupData g : groups)
			if(g.id == id)
				group = g;
		return group;
	}

	public void setPersons(List<PersonData> persons) {
		this.persons = persons;
	}

	public List<GroupData> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupData> groups) {
		this.groups.clear();
		this.groups.addAll(groups);
	}
}
