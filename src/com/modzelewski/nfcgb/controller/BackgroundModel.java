package com.modzelewski.nfcgb.controller;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

	private void reloadPersons() {
		persons.clear();
		if (currentEvent == null)
			return;
		List<EventMembership> eventMemberships = getEventMemberships(currentEvent);
		getPersons(eventMemberships);
	}

	public List<Person> getPersons(List<EventMembership> eventMemberships) {
		// ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<Person, Integer> personDao = getHelper()
				.getPersonDao();

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

		RuntimeExceptionDao<Person, Integer> personDao = getHelper()
				.getPersonDao();

		for (Group group : groupsWithCurrentEvent) {
			List<GroupMembership> groupMemberships = getGroupMemberships(group);

			for (GroupMembership groupMembership : groupMemberships) {
				// group.getPerson().add(getPersonById(groupMembership.getPersonId()));
				group.getPerson().add(
						personDao.queryForId(groupMembership.getPersonId()));
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
		RuntimeExceptionDao<Event, Integer> eventDao = getHelper()
				.getEventDataDao();
		eventDao.create(event);
		if (!events.contains(event))
			;
		events.add(event);
		setCurrentEvent(event);
		reloadPersons();
		reloadGroups();
	}

	public void editEvent(Event event, String eventName,
			Boolean wintersemester, int year, String info) {
		RuntimeExceptionDao<Event, Integer> eventDao = getHelper()
				.getEventDataDao();
		event.setEventname(eventName);
		event.setWintersemester(wintersemester);
		event.setYear(year);
		event.setInfo(info);

		this.currentEvent = event;
		eventDao.update(event);
		eventDao.refresh(event);
	}

	public void removeEvent(Event event) {
		currentEvent = event;
		RuntimeExceptionDao<Event, Integer> eventDao = getHelper()
				.getEventDataDao();
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper()
				.getGroupDataDao();
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper()
				.getEventMembershipDataDao();
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper()
				.getGroupMembershipDataDao();
		List<Event> ed = null;
		List<Group> gd = null;
		List<EventMembership> emd = null;
		List<GroupMembership> gmd = null;

		try {
			ed = eventDao.query(eventDao.queryBuilder().where()
					.eq("id", currentEvent.id).prepare());
			gd = groupDao.query(groupDao.queryBuilder().where()
					.eq("event_id", currentEvent.id).prepare());
			emd = eventMembershipDao.query(eventMembershipDao.queryBuilder()
					.where().eq("event_id", currentEvent.id).prepare());
			// gmd goes over gd later
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (Group group : gd) {
			try {
				gmd = groupMembershipDao.query(groupMembershipDao
						.queryBuilder().where().eq("group_id", group.id)
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
					mainActivity.getString(R.string.please_add_a_new_event),
					Toast.LENGTH_LONG).show();
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
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper()
				.getGroupDataDao();
		Log.i(getClass().getSimpleName(), "currentevent: " + getCurrentEvent());
		Group group = new Group(groupName, getCurrentEvent().getId());
		groupDao.create(group);
		groupDao.refresh(group);
		groups.add(group);
		reloadGroups();
	}

	public void editGroup(Group group, String name) {
		// create Object
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper()
				.getGroupDataDao();
		Group groupInList = groups.get(groups.indexOf(group));
		groupInList.setGroupName(name);
		group.setGroupName(name);
		groupDao.update(group);
		groupDao.refresh(group);
		reloadGroups();
		reloadPersons();
	}

	public void removeGroup(Group gd) {
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper()
				.getGroupDataDao();
		groupDao.delete(gd);
		groups.remove(gd);
		reloadGroups();
	}

	public List<Group> getGroups(Event currentEvent) {
		RuntimeExceptionDao<Group, Integer> groupDao = getHelper()
				.getGroupDataDao();
		List<Group> groupResult = null;
		try {
			groupResult = groupDao.queryBuilder().where()
					.eq("event_id", currentEvent.getId()).query();
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
		RuntimeExceptionDao<Person, Integer> personDao = getHelper()
				.getPersonDao();
		Person person = new Person(name, email);
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper()
				.getEventMembershipDataDao();
		personDao.create(person);
		EventMembership emd = new EventMembership(getCurrentEvent().getId(),
				person.getId());
		eventMembershipDao.create(emd);
		persons.add(person);
	}

	public void removePerson(Person person) {
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper()
				.getEventMembershipDataDao();
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper()
				.getGroupMembershipDataDao();
		List<EventMembership> emd = null;
		List<GroupMembership> gmd = null;

		try {
			emd = eventMembershipDao.query(eventMembershipDao.queryBuilder()
					.where().eq("person_id", person.getId()).and()
					.eq("event_id", getCurrentEvent().getId()).prepare());
			gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder()
					.where().eq("person_id", person.getId()).prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (GroupMembership groupMembership : gmd) {
			getGroupById(groupMembership.getGroup_id()).getPerson().remove(
					getPersonById(person.getId()));
		}
		persons.remove(person);
		eventMembershipDao.delete(emd);
		groupMembershipDao.delete(gmd);
		reloadGroups();
		reloadPersons();
	}

	public void editPerson(Person person, String name, String email) {
		RuntimeExceptionDao<Person, Integer> personDao = getHelper()
				.getPersonDao();
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
	// getHelper().getEventMembershipDataDao();
	// eventMembershipDao.delete(eventMembership);
	// // no eventmembership representation List here, so no remove
	// }

	public List<EventMembership> getEventMemberships(Event currentEvent) {
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper()
				.getEventMembershipDataDao();
		List<EventMembership> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where()
					.eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eventMemberships;
	}

	// ----------------------------------------------------------------------------------

	/*
	 * --------------------------------------------------------------------------
	 * -------- --- GroupMembership
	 * --------------------------------------------------------------
	 * ------------
	 * ----------------------------------------------------------------------
	 */

	public void addGroupMembership(int personId, int groupId) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper()
				.getGroupMembershipDataDao();

		List<GroupMembership> groupResult = null;
		try {
			groupResult = groupMembershipDao.queryBuilder().where()
					.eq("group_id", groupId).and().eq("person_id", personId)
					.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assert groupResult != null;
		if (groupResult.isEmpty()) {
			groupMembershipDao.create(new GroupMembership(groupId, personId));
			getGroupById(groupId).getPerson().add(getPersonById(personId));
		} else {
			Toast.makeText(
					mainActivity.getApplicationContext(),
					mainActivity.getResources().getString(
							R.string.person_already_in_group),
					Toast.LENGTH_LONG).show();
		}

		reloadGroups();
	}

	public void removeGroupMembership(Group group, Person person) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper()
				.getGroupMembershipDataDao();

		List<GroupMembership> groupResult = null;
		try {
			groupResult = groupMembershipDao.queryBuilder().where()
					.eq("group_id", group.id).and().eq("person_id", person.id)
					.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		groupMembershipDao.delete(groupResult);
		group.getPerson().remove(person);
		reloadGroups();
	}

	private List<GroupMembership> getGroupMemberships(Group group) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper()
				.getGroupMembershipDataDao();
		List<GroupMembership> groupMemberships = null;
		try {
			groupMemberships = groupMembershipDao.queryBuilder().where()
					.eq("group_id", group.id).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupMemberships;
	}

	// public List<GroupMembership> getGroupMemberships(Event currentEvent) {
	// RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao =
	// getHelper().getGroupMembershipDataDao();
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

}
