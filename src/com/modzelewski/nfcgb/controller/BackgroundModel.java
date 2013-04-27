package com.modzelewski.nfcgb.controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.R;
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
	public List<Event> events = new LinkedList<Event>();
	// person list for left list
	public List<Person> persons = new LinkedList<Person>();
	// group list for right list
	public List<Group> groups = new LinkedList<Group>();

	public BackgroundModel(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public DatabaseHelper getHelper() {
		return mainActivity.getHelper();
	}

	private void reloadPersons() {
		persons.clear();
		if (currentEvent == null)
			return;
		List<EventMembership> eventMemberships = getEventMemberships(currentEvent);
		getPersons(eventMemberships);
	}

	public List<Person> getPersons(List<EventMembership> eventMemberships) {
		// ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDao();

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

		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDao();

		for (Group group : groupsWithCurrentEvent) {
			List<GroupMembership> groupMemberships = getGroupMemberships(group);

			for (GroupMembership groupMembership : groupMemberships) {
				// group.getPerson().add(getPersonById(groupMembership.getPersonId()));
				group.getPerson().add(personDao.queryForId(groupMembership.getPersonId()));
			}
			groups.add(group);
		}
	}

	public void reloadEverything() {
		reloadGroups();
		reloadPersons();
	}

	/*
	 * --------------------------------------------------------------------------
	 * -------- --- Event
	 * ------------------------------------------------------------------------
	 * --
	 * ------------------------------------------------------------------------
	 * --------
	 */
	// Add, Edit, Remove
	public void addEvent(Event event) {
		RuntimeExceptionDao<Event, Integer> eventDao = getHelper().getEventDao();
		eventDao.create(event);
		if (!events.contains(event))
			events.add(event);
		setCurrentEvent(event);
		reloadPersons();
		reloadGroups();
	}

	public void addEventIfNotExists(Event event) {
		boolean exists = false;
		for (Event e : events) {
			if (e.getEventname() == event.getEventname()) {
				exists = true;
				e.setYear(event.getYear());
				e.setWintersemester(event.isWintersemester());
				e.setInfo(event.getInfo());
			}
		}
		if (!exists)
			events.add(event);
	}

	public int addEventIfNotExists(String eventName, String year, String wintersemester, String info) {
		boolean exists = false;
		int id = -1;
		for (Event e : events) {
			if (e.getEventname().equals(eventName)) {
				exists = true;
				id = e.id;
				editEvent(e, eventName, Boolean.valueOf(wintersemester), Integer.parseInt(year), info);
			}
		}
		if (!exists) {
			// Log.i("BACKGROUNDMODEL", "eventName = " + eventName);
			// Log.i("BACKGROUNDMODEL", "year = " + Integer.parseInt(year));
			// Log.i("BACKGROUNDMODEL", "ws = " +
			// Boolean.valueOf(wintersemester));
			// Log.i("BACKGROUNDMODEL", "info = " + info);
			Event event = new Event(eventName, Integer.parseInt(year), Boolean.valueOf(wintersemester), info);
			id = event.id;
			addEvent(event);
		}
		return id;
	}

	public void editEvent(Event event, String eventName, Boolean wintersemester, int year, String info) {
		RuntimeExceptionDao<Event, Integer> eventDao = getHelper().getEventDao();
		event.setEventname(eventName);
		event.setWintersemester(wintersemester);
		event.setYear(year);
		event.setInfo(info);

		this.currentEvent = event;
		eventDao.update(event);
		eventDao.refresh(event);
		reloadPersons();
		reloadGroups();
	}

	public void removeEvent(Event event) {
		currentEvent = event;
		RuntimeExceptionDao<Event, Integer> eventDao = getHelper().getEventDao();
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDao();
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDao();
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDao();
		List<Event> ed = null;
		List<Group> gd = null;
		List<EventMembership> emd = null;
		List<GroupMembership> gmd = null;

		try {
			ed = eventDao.query(eventDao.queryBuilder().where().eq("id", currentEvent.id).prepare());
			gd = groupDao.query(groupDao.queryBuilder().where().eq("event_id", currentEvent.id).prepare());
			emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.id)
					.prepare());
			// gmd goes over gd later
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (Group group : gd) {
			try {
				gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("group_id", group.id)
						.prepare());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		eventDao.delete(ed);
		groupDao.delete(gd);
		eventMembershipDao.delete(emd);
		groupMembershipDao.delete(gmd);

		persons.clear();
		groups.clear();
		events.remove(currentEvent);
		reloadGroups();
		reloadPersons();
		if (events.size() == 0) {
			setCurrentEvent(null);
			Toast.makeText(mainActivity.getApplicationContext(),
					mainActivity.getString(R.string.please_add_a_new_event), Toast.LENGTH_LONG).show();
		} else
			setCurrentEvent(events.get(0));
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

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events.clear();
		this.events.addAll(events);
		Collections.sort(this.events);
	}

	// // getList, setEvents
	// public ArrayList<String> getEventList() {
	// ArrayList<String> eventNames = new ArrayList<String>();
	// for (Event ed : events) {
	// eventNames.add(String.format("%s (%s %d)", ed.getEventname(),
	// ed.isWintersemester() ? "WiSe" : "SoSe", ed.getYear()));
	// }
	// return eventNames;
	// }
	// ----------------------------------------------------------------------------------

	/*
	 * --------------------------------------------------------------------------
	 * -------- --- Group
	 * ------------------------------------------------------------------------
	 * --
	 * ------------------------------------------------------------------------
	 * --------
	 */

	public void addGroup(String groupName) {
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDao();
		Log.i(getClass().getSimpleName(), "currentevent: " + getCurrentEvent());
		Group group = new Group(groupName, getCurrentEvent().getId());
		groupDao.create(group);
		groupDao.refresh(group);
		groups.add(group);
		reloadGroups();
	}

	public int addGroupIfNotExists(String groupName, int eventId) {
		for (Group g : groups) {
			if (g.getGroupName().equals(groupName)) {
				return g.id;
			}
		}
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDao();
		Group group = new Group(groupName, eventId);
		groupDao.create(group);
		return group.id;
	}

	public void editGroup(Group group, String name) {
		// create Object
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDao();
		Group groupInList = groups.get(groups.indexOf(group));
		groupInList.setGroupName(name);
		group.setGroupName(name);
		groupDao.update(group);
		groupDao.refresh(group);
		reloadGroups();
		reloadPersons();
	}

	public void removeGroup(Group gd) {
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDao();
		groupDao.delete(gd);
		groups.remove(gd);
		reloadGroups();
	}

	public List<Group> getGroups(Event currentEvent) {
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDao();
		List<Group> groupResult = null;
		try {
			groupResult = groupDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupResult;
	}

	public Group getGroupById(int id) {
		Group group = null;
		for (Group g : groups)
			if (g.id == id)
				group = g;
		return group;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups.clear();
		this.groups.addAll(groups);
	}

	// ----------------------------------------------------------------------------------

	/*
	 * --------------------------------------------------------------------------
	 * -------- --- Person
	 * -----------------------------------------------------------------------
	 * --
	 * ------------------------------------------------------------------------
	 * --------
	 */

	public void addPerson(String name, String email) {
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDao();
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDao();

		Person person = new Person(name, email);
		personDao.create(person);

		EventMembership emd = new EventMembership(getCurrentEvent().getId(), person.getId());
		eventMembershipDao.create(emd);

		persons.add(person);
	}

	public int addPersonIfNotExists(String name, String email) {
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDao();
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDao();

		for (Person p : persons) {
			if (p.getName().equals(name) && p.getEmail().equals(email)) {
				return p.id;
			}
		}

		Person person = new Person(name, email);
		personDao.create(person);

		EventMembership emd = new EventMembership(getCurrentEvent().getId(), person.getId());
		eventMembershipDao.create(emd);

		persons.add(person);
		return person.id;
	}

	public void removePerson(Person person) {
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDao();
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDao();
		List<EventMembership> emd = null;
		List<GroupMembership> gmd = null;

		try {
			emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", person.getId())
					.and().eq("event_id", getCurrentEvent().getId()).prepare());
			gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", person.getId())
					.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (GroupMembership groupMembership : gmd) {
			getGroupById(groupMembership.getGroup_id()).getPerson().remove(getPersonById(person.getId()));
		}
		persons.remove(person);
		eventMembershipDao.delete(emd);
		groupMembershipDao.delete(gmd);
		reloadGroups();
		reloadPersons();
	}

	public void editPerson(Person person, String name, String email) {
		RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDao();
		persons.remove(person);
		person.setName(name);
		person.setEmail(email);
		personDao.update(person);
		personDao.refresh(person);
		persons.add(person);
		reloadGroups();
		reloadPersons();
	}

	public List<Person> getPersons() {
		return persons;
	}

	Person getPersonById(int id) {
		Person person = null;
		for (Person p : persons)
			if (p.getId() == id)
				person = p;
		return person;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	// ----------------------------------------------------------------------------------

	/*
	 * --------------------------------------------------------------------------
	 * -------- --- EventMembership
	 * --------------------------------------------------------------
	 * ------------
	 * ----------------------------------------------------------------------
	 */
	// private void removeEventMemberships(EventMembership eventMembership) {
	// RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao =
	// getHelper().getEventMembershipDao();
	// eventMembershipDao.delete(eventMembership);
	// // no eventmembership representation List here, so no remove
	// }

	public List<EventMembership> getEventMemberships(Event event) {
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDao();
		List<EventMembership> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", event.id).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eventMemberships;
	}

	// is not needed because can be new generated at addPersonIfotExists
	// public void createEventMembershipsFromNdef(BackgroundModel model, byte[]
	// groupMembershipBytes, int eventId,
	// Hashtable<Integer, Integer> changedPersonIds) {
	// // read from byte array
	// ByteArrayInputStream bais = new
	// ByteArrayInputStream(groupMembershipBytes);
	// DataInputStream in = new DataInputStream(bais);
	// List<String> groupMembershipStrings = new LinkedList<String>();
	// try {
	// while (in.available() > 0) {
	// groupMembershipStrings.add(in.readUTF());
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (groupMembershipStrings != null)
	// Log.i("NFCCHECKGroupMemberships", groupMembershipStrings.toString());
	// for (String groupMembershipString : groupMembershipStrings) {
	// StringTokenizer tokens = new StringTokenizer(groupMembershipString, ",");
	//
	// int oldId = Integer.parseInt(substringAfter(tokens.nextToken(), "id="));
	// String oldEventId = substringAfter(tokens.nextToken(), "event_id=");
	// String oldPersonId = substringAfter(tokens.nextToken(), "person_id=");
	//
	// // int newId = model.addPersonIfNotExists(name, email);
	// // newId == -1 means Person already exists. Btw: only exists if both
	// // name and email are the same
	// // changedPersonIds.put(oldId, newId);
	// // int personId = model.addEventIfNotExists(eventName, year,
	// // wintersemester, info);
	// }
	//
	// }

	// ----------------------------------------------------------------------------------

	/*
	 * --------------------------------------------------------------------------
	 * -------- --- GroupMembership
	 * --------------------------------------------------------------
	 * ------------
	 * ----------------------------------------------------------------------
	 */

	public void createGroupMembershipsFromNdef(BackgroundModel model, byte[] groupMembershipBytes,
			Hashtable<Integer, Integer> changedGroupIds, Hashtable<Integer, Integer> changedPersonIds) {

		// read from byte array
		ByteArrayInputStream bais = new ByteArrayInputStream(groupMembershipBytes);
		DataInputStream in = new DataInputStream(bais);
		List<String> groupMembershipStrings = new LinkedList<String>();
		try {
			while (in.available() > 0) {
				groupMembershipStrings.add(in.readUTF());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (groupMembershipStrings != null)
			Log.i("NFCCHECKGM", groupMembershipStrings.toString());
		for (String groupMembershipString : groupMembershipStrings) {
			StringTokenizer tokens = new StringTokenizer(groupMembershipString, ",");
		
			int oldId = Integer.parseInt(substringAfter(tokens.nextToken(), "id="));
			Log.i("NFCCHECKGM-OLDID", String.valueOf(oldId));
			String oldGroupId = substringAfter(tokens.nextToken(), "group_id=");
			Log.i("NFCCHECKGM-GROUPID", oldGroupId);
			String oldPersonId = substringAfter(tokens.nextToken(), "event_id=");
			Log.i("NFCCHECK-EMAIL", oldPersonId);
			
			if(changedGroupIds.get(oldGroupId) == null || changedPersonIds.get(oldPersonId) == null){
				Toast.makeText(mainActivity, "OldPersonId not found. this shouldn't happen", Toast.LENGTH_LONG).show();
				return;
			}
			int groupId = changedGroupIds.get(oldGroupId);
			int personId = changedPersonIds.get(oldPersonId);
			model.addGroupMembership(personId, groupId);
		}
	}

	public void addGroupMembership(int personId, int groupId) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDao();

		List<GroupMembership> groupResult = null;
		try {
			groupResult = groupMembershipDao.queryBuilder().where().eq("group_id", groupId).and()
					.eq("person_id", personId).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assert groupResult != null;
		if (groupResult.isEmpty()) {
			groupMembershipDao.create(new GroupMembership(groupId, personId));
			getGroupById(groupId).getPerson().add(getPersonById(personId));
		} else {
			Toast.makeText(mainActivity.getApplicationContext(),
					mainActivity.getResources().getString(R.string.person_already_in_group), Toast.LENGTH_LONG).show();
		}

		reloadGroups();
	}

	public void removeGroupMembership(Group group, Person person) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDao();

		List<GroupMembership> groupResult = null;
		try {
			groupResult = groupMembershipDao.queryBuilder().where().eq("group_id", group.id).and()
					.eq("person_id", person.id).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		groupMembershipDao.delete(groupResult);
		group.getPerson().remove(person);
		reloadGroups();
	}

	public List<GroupMembership> getGroupMemberships(Group group) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDao();
		List<GroupMembership> groupMemberships = null;
		try {
			groupMemberships = groupMembershipDao.queryBuilder().where().eq("group_id", group.id).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupMemberships;
	}

	// public List<GroupMembership> getGroupMemberships(Event currentEvent) {
	// RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao =
	// getHelper().getGroupMembershipDao();
	// List<GroupMembership> groupMemberships = null;
	// try {
	// groupMemberships =
	// groupMembershipDao.queryBuilder().where().eq("event_id",
	// currentEvent.getId()).query();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// return groupMemberships;
	// }

	// ----------------------------------------------------------------------------------
	/**
	 * Returns the substring after the first occurrence of a delimiter. The
	 * delimiter is not part of the result.
	 * 
	 * @param string
	 *            String to get a substring from.
	 * @param delimiter
	 *            String to search for.
	 * @return Substring after the last occurrence of the delimiter.
	 */
	public static String substringAfter(String string, String delimiter) {
		int pos = string.indexOf(delimiter);

		return pos >= 0 ? string.substring(pos + delimiter.length()) : "";
	}

}
