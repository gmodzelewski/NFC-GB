package com.modzelewski.nfcgb.persistence;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.model.EventData;
import com.modzelewski.nfcgb.model.EventMembershipData;
import com.modzelewski.nfcgb.model.GroupData;
import com.modzelewski.nfcgb.model.PersonData;

public class DatabasePopulation {
	
	public static void populatePersonDAO(RuntimeExceptionDao<PersonData, Integer> personDao) {
		personDao.create(new PersonData("Dwight D. Eisenhower", "dwight@gmail.com"));
		personDao.create(new PersonData("John F. Kennedy", "john@gmail.com"));
		personDao.create(new PersonData("Lyndon B. Johnson", "lyndon@gmail.com"));
		personDao.create(new PersonData("Richard Nixon",  "richard@gmail.com"));
		personDao.create(new PersonData("Gerald Ford",  "gerald@gmail.com"));
		personDao.create(new PersonData("Jimmy Carter",  "jimmy@gmail.com"));
		personDao.create(new PersonData("Ronald Reagan",  "ronald@gmail.com"));
		personDao.create(new PersonData("George H. W. Bush",  "george56@gmail.com"));
		personDao.create(new PersonData("Bill Clinton",  "bill@gmail.com"));
		personDao.create(new PersonData("George W. Bush",  "george2u@gmail.com"));
		personDao.create(new PersonData("Barack Obama",  "barack@gmail.com"));
	}
	
	public static void populateEventDAO(RuntimeExceptionDao<EventData, Integer> eventDao) {
		eventDao.create(new EventData("Ich AG", 2005, true, "Peter", "ich@auchhier.de", "here be dragons"));
		eventDao.create(new EventData("Medieninformatik 1 - Tutorium 2", 2011, false, "Hans Meiser", "ich@hier.de", ""));
		eventDao.create(new EventData("Medieninformatik 2 - Tutorium 5", 2012, true, "Mr. McScuzzypants", "ich@hier.de", ""));
		eventDao.create(new EventData("Mobile digitale Kommunikation - Tutorium 5", 2012, true, "Batman Supaman", "ich@hier.de", "here be dragons"));
	}
	
	public static void populateEventMembershipDao(RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao) {
		eventMembershipDao.create(new EventMembershipData(1, 1));
		eventMembershipDao.create(new EventMembershipData(1, 2));
		eventMembershipDao.create(new EventMembershipData(1, 3));
		eventMembershipDao.create(new EventMembershipData(1, 4));
		eventMembershipDao.create(new EventMembershipData(1, 5));
		eventMembershipDao.create(new EventMembershipData(1, 6));
		eventMembershipDao.create(new EventMembershipData(2, 5));
		eventMembershipDao.create(new EventMembershipData(2, 6));
		eventMembershipDao.create(new EventMembershipData(2, 7));
		eventMembershipDao.create(new EventMembershipData(2, 8));
		eventMembershipDao.create(new EventMembershipData(3, 9));
		eventMembershipDao.create(new EventMembershipData(3, 1));
		eventMembershipDao.create(new EventMembershipData(3, 4));
		eventMembershipDao.create(new EventMembershipData(4, 11));
		eventMembershipDao.create(new EventMembershipData(4, 2));
		eventMembershipDao.create(new EventMembershipData(4, 5));
		eventMembershipDao.create(new EventMembershipData(4, 9));
	}
	
	public static void populateGroupDAO(RuntimeExceptionDao<GroupData, Integer> groupDao) {
		groupDao.create(new GroupData("Gruppe 1", 1));
		groupDao.create(new GroupData("Gruppe 2", 1));
		groupDao.create(new GroupData("Gruppe 3", 1));
		groupDao.create(new GroupData("Gruppe 4", 1));
		groupDao.create(new GroupData("Gruppe 1", 2));
		groupDao.create(new GroupData("Gruppe 2", 2));
		groupDao.create(new GroupData("Gruppe 3", 2));
		groupDao.create(new GroupData("Gruppe 4", 2));
		groupDao.create(new GroupData("Gruppe 1", 3));
		groupDao.create(new GroupData("Gruppe 2", 3));
		groupDao.create(new GroupData("Gruppe 3", 3));
		groupDao.create(new GroupData("Gruppe 4", 3));
		groupDao.create(new GroupData("Gruppe 1", 4));
		groupDao.create(new GroupData("Gruppe 2", 4));
		groupDao.create(new GroupData("Gruppe 3", 4));
		groupDao.create(new GroupData("Gruppe 4", 4));
		groupDao.create(new GroupData("Die Flitzer", 1));
		groupDao.create(new GroupData("Die Medieninformatik Gefahr", 2));
		groupDao.create(new GroupData("wir aus hier", 4));
	}
}
