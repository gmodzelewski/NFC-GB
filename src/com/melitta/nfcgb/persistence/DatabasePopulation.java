package com.melitta.nfcgb.persistence;

import java.util.Date;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.melitta.nfcgb.PersonData;

public class DatabasePopulation {
	public static void populatePersonDAO(RuntimeExceptionDao<PersonData, Integer> personDao) {
		personDao.create(new PersonData("Dwight D.", "Eisenhower", new Date(System.currentTimeMillis()), "dwight@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("John F.", "Kennedy", new Date(System.currentTimeMillis()), "john@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Lyndon B.", "Johnson", new Date(System.currentTimeMillis()), "lyndon@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Richard", "Nixon", new Date(System.currentTimeMillis()), "richard@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Gerald", "Ford", new Date(System.currentTimeMillis()), "gerald@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Jimmy", "Carter", new Date(System.currentTimeMillis()), "jimmy@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Ronald", "Reagan", new Date(System.currentTimeMillis()), "ronald@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("George H. W.", "Bush", new Date(System.currentTimeMillis()), "george56@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Bill", "Clinton", new Date(System.currentTimeMillis()), "bill@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("George W.", "Bush", new Date(System.currentTimeMillis()), "george2u@gmail.com", "Medieninformatik2"));
		personDao.create(new PersonData("Barack", "Obama", new Date(System.currentTimeMillis()), "barack@gmail.com", "Medieninformatik2"));
		
	}
}
