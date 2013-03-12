package com.modzelewski.nfcgb.controller;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BackgroundModel {
	private final MainActivity mainActivity;
	private DatabaseHelper databaseHelper;
	
	private EventManager eventManager;
	private GroupManager groupManager;
	private PersonManager personManager;
	private GroupMembershipManager groupMembershipManager;
	private EventMembershipManager eventMembershipManager;
	

	public BackgroundModel(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		eventManager = new EventManager();
		groupManager = new GroupManager();
		personManager = new PersonManager();
		groupMembershipManager = new GroupMembershipManager();
		eventMembershipManager = new EventMembershipManager();
	}

	// public DatabaseHelper getHelper() {
	// return mainActivity.getHelper();
	// }

	public DatabaseHelper createHelper() {
		databaseHelper = mainActivity.getHelper();
		return databaseHelper;
	}

	public void closeDatabaseHelper() {
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}
	}
	
	public void initiateEvents() {
		RuntimeExceptionDao<Event, Integer> eventDao = databaseHelper.getEventDataDao();
		eventManager.setEvents(eventDao.queryForAll());
	}

}
