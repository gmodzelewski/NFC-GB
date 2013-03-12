package com.modzelewski.nfcgb.controller;

import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class PersonManager {
	// person list for left list
	public List<Person> persons = new LinkedList<Person>();

	private void reloadPersons(DatabaseHelper databaseHelper, Event currentEvent) {
		persons.clear();
		if (currentEvent == null)
			return;

		EventMembershipManager eventMembershipManager = new EventMembershipManager();
		List<EventMembership> eventMemberships = eventMembershipManager.getEventMemberships(databaseHelper, currentEvent);
		this.persons = getPersons(databaseHelper, eventMemberships);
	}

	public List<Person> getPersons(DatabaseHelper databaseHelper, List<EventMembership> eventMemberships) {
		// ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<Person, Integer> personDao = databaseHelper.getPersonDataDao();

		for (EventMembership emd : eventMemberships) {
			Person pd = personDao.queryForId(emd.getPerson_id());
			persons.add(pd);
			// personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
		}
		return persons;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public Person getPersonById(int id) {
		Person person = null;
		for (Person p : persons)
			if (p.getId() == id)
				person = p;
		return person;
	}
	
	public Person getPersonByIdFromDatabase(DatabaseHelper databaseHelper, int id) {
		RuntimeExceptionDao<Person, Integer> personDao = databaseHelper.getPersonDataDao();
		return personDao.queryForId(id);
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

}
