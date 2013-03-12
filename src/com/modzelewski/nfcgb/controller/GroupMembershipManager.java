package com.modzelewski.nfcgb.controller;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class GroupMembershipManager {
	List<GroupMembership> getGroupMemberships(DatabaseHelper databaseHelper, Group group) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
		List<GroupMembership> groupMemberships = null;
		try {
			groupMemberships = groupMembershipDao.queryBuilder().where().eq("group_id", group.id).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupMemberships;
	}

	public List<GroupMembership> getGroupMemberships(DatabaseHelper databaseHelper, Event currentEvent) {
		RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
		List<GroupMembership> groupMemberships = null;
		try {
			groupMemberships = groupMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupMemberships;
	}

}
