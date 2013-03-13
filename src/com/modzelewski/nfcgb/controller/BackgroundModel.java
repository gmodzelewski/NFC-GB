package com.modzelewski.nfcgb.controller;

import android.util.Log;
import android.widget.Toast;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.model.*;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BackgroundModel {
    // event list for spinner
    public final List<Event> events;
    // group list for right list
    public final List<Group> groups;
    private final MainActivity mainActivity;
    // person list for left list
    public List<Person> persons;
    // selected event
    private Event currentEvent;

    public BackgroundModel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        events = new LinkedList<Event>();
        groups = new LinkedList<Group>();
        persons = new LinkedList<Person>();
        currentEvent = null;
    }

    public void addGroupMembership(int personId, int droppedInGroupPos) {
        Group groupInList = groups.get(droppedInGroupPos);
//		Toast.makeText(mainActivity, "dropped in group " + droppedInGroupPos, Toast.LENGTH_LONG).show();
        RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
        Group group = getGroupById(groupInList.id);
        Person person = getPersonById(personId);
        List<GroupMembership> gmd = null;
        try {
            gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", personId).and().eq("group_id", group.id).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(gmd.isEmpty()) {
            groupMembershipDao.create(new GroupMembership(group.id, personId));
            group.getPerson().add(person);
        } else
            Toast.makeText(mainActivity, mainActivity.getString(R.string.person_already_in_group), Toast.LENGTH_LONG).show();
        reloadGroups();
    }

    public void addPerson(Person p) {
        RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();
        personDao.create(p);

        EventMembership emd = new EventMembership(getCurrentEvent().getId(), p.getId());
        RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
        eventMembershipDao.create(emd);
        personDao.create(p);
        persons.add(p);

        reloadPersons();
    }

    public void editPerson(Person p, String newName, String newEmail) {
        persons.remove(p);

        RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();

        p.setName(newName);
        p.setEmail(newEmail);

        personDao.update(p);
        personDao.refresh(p);

        persons.add(p);

        reloadPersons();
        reloadGroups();
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public ArrayList<String> getEventList() {
        ArrayList<String> eventNames = new ArrayList<String>();
        for (Event ed : events) {
            eventNames.add(String.format("%s (%s %d)", ed.getEventname(), ed.isWintersemester() ? "WiSe" : "SoSe", ed.getYear()));
        }
        return eventNames;
    }

    public List<EventMembership> getEventMemberships(Event currentEvent) {
        RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
        List<EventMembership> eventMemberships = null;
        try {
            eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventMemberships;
    }

    public List<Event> getEvents() {
        return events;
    }

    public Group getGroupById(int id) {
        // Group group = null;
        // for(Group g : groups)
        // if(g.id == id)
        // group = g;
        // return group;
        RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDataDao();
        Group g = groupDao.queryForId(id);
        return g;
    }

    public List<GroupMembership> getGroupMemberships(Event currentEvent) {
        RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
        List<GroupMembership> groupMemberships = null;
        try {
            groupMemberships = groupMembershipDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupMemberships;
    }

    private List<GroupMembership> getGroupMemberships(Group group) {
        RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
        List<GroupMembership> groupMemberships = null;
        try {
            groupMemberships = groupMembershipDao.queryBuilder().where().eq("group_id", group.id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupMemberships;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Group> getGroups(Event currentEvent) {
        RuntimeExceptionDao<Group, Integer> groupDao = getHelper().getGroupDataDao();
        List<Group> groupResult = null;
        try {
            groupResult = groupDao.queryBuilder().where().eq("event_id", currentEvent.getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupResult;
    }

    public DatabaseHelper getHelper() {
        return mainActivity.getHelper();
    }

    public Person getPersonById(int id) {
        // Person person = null;
        // for(Person p : persons)
        // if(p.getId() == id)
        // person = p;
        // return person;

        RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();
        Person p = personDao.queryForId(id);
//        Toast.makeText(mainActivity, "Die Person ist " + p.toString(), Toast.LENGTH_LONG).show();
        return p;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<Person> getPersons(List<EventMembership> eventMemberships) {
        // ArrayList<String> personsFullNames = new ArrayList<String>();
        RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();

        for (EventMembership emd : eventMemberships) {
            Person pd = personDao.queryForId(emd.getPerson_id());
            persons.add(pd);
            // personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
        }
        return persons;
    }

    public void reloadEvents() {
        RuntimeExceptionDao<Event, Integer> eventDao = getHelper().getEventDataDao();
        setEvents(eventDao.queryForAll());
    }

    private void reloadGroups() {
        groups.clear();

        if (currentEvent == null) {
            return;
        }

        List<Group> groupsWithCurrentEvent = getGroups(currentEvent);

        RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();

        for (Group group : groupsWithCurrentEvent) {
            List<GroupMembership> groupMemberships = getGroupMemberships(group);

            for (GroupMembership groupMembership : groupMemberships) {
                // group.getPerson().add(getPersonById(groupMembership.getPersonId()));
                List<Person> p = null;
                try {
                    p = personDao.queryBuilder().where().eq("id", groupMembership.getPersonId()).query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(p.size()>1)
                    Log.i("VARIABLE p", "p = " + p.size());
                if(p.size()==1)
                    group.getPerson().add(p.get(0));
            }
            groups.add(group);
        }
    }

    public void reloadPersons() {
        persons.clear();
        if (currentEvent == null)
            return;
        List<EventMembership> eventMemberships = getEventMemberships(currentEvent);
        getPersons(eventMemberships);
    }

    public void removeGroupMembership(int personId, int groupId) {
        RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
        List<GroupMembership> gmd = null;
        try {
            gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", personId).and().eq("group_id", groupId).prepare());
            Toast.makeText(mainActivity, "person_id = " + personId + "\ngroup_id = " + groupId, Toast.LENGTH_LONG).show();
            gmd.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        groupMembershipDao.delete(gmd);
        Group group = getGroupById(groupId);
        Person person = getPersonById(personId);
        if(group.getPerson().contains(person))
            group.getPerson().remove(person);
        reloadGroups();
    }

    public void removePerson(Person p) {
        RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
        RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = getHelper().getGroupMembershipDataDao();
        List<EventMembership> emd = null;
        List<GroupMembership> gmd = null;
        try {
            emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", p.getId()).and().eq("event_id", getCurrentEvent().getId()).prepare());
            gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", p.getId()).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (GroupMembership groupMembership : gmd) {
            getGroupById(groupMembership.getGroup_id()).getPerson().remove(getPersonById(p.getId()));
        }
        eventMembershipDao.delete(emd);
        groupMembershipDao.delete(gmd);

        persons.remove(p);
        RuntimeExceptionDao<Person, Integer> personDao = getHelper().getPersonDataDao();
        personDao.delete(p);

        reloadPersons();
        reloadGroups();
    }

    public void setCurrentEvent(Event currentEvent) {
        if (this.currentEvent != currentEvent) {
            this.currentEvent = currentEvent;
            reloadPersons();
            reloadGroups();
        }
    }

    public void setEvents(List<Event> events) {
        this.events.clear();
        this.events.addAll(events);
        Collections.sort(this.events);
    }

    public void setGroups(List<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
