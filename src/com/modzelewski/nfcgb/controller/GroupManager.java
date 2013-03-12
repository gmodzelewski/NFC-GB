package com.modzelewski.nfcgb.controller;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class GroupManager {
	// group list for right list
	public final List<Group> groups = new LinkedList<Group>();

	private void reloadGroups(DatabaseHelper databaseHelper, Event currentEvent) {
		groups.clear();

		List<Group> groupsWithCurrentEvent = getGroups(databaseHelper, currentEvent);

		GroupMembershipManager gmm = new GroupMembershipManager();
		PersonManager pm = new PersonManager();
		
		for (Group group : groupsWithCurrentEvent) {

			List<GroupMembership> groupMemberships = gmm.getGroupMemberships(databaseHelper, group);

			for (GroupMembership groupMembership : groupMemberships) {
				// group.getPerson().add(getPersonById(groupMembership.getPersonId()));
				group.getPerson().add(pm.getPersonByIdFromDatabase(databaseHelper, groupMembership.getPersonId()));
			}
			groups.add(group);
		}
	}

	public List<Group> getGroups(DatabaseHelper databaseHelper, Event event) {
		RuntimeExceptionDao<Group, Integer> groupDao = databaseHelper.getGroupDataDao();
		List<Group> groupResult = null;
		try {
			groupResult = groupDao.queryBuilder().where().eq("event_id", event.getId()).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupResult;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public Group getGroupById(int id) {
		Group group = null;
		for (Group g : groups)
			if (g.id == id)
				group = g;
		return group;
	}

	public void setGroups(List<Group> groups) {
		this.groups.clear();
		this.groups.addAll(groups);
	}

}
