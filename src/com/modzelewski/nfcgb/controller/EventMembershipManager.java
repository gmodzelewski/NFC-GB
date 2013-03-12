package com.modzelewski.nfcgb.controller;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.EventMembership;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class EventMembershipManager {
	public List<EventMembership> getEventMemberships(DatabaseHelper databaseHelper, Event currentEvent) {
		RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
		List<EventMembership> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eventMemberships;
	}
}
