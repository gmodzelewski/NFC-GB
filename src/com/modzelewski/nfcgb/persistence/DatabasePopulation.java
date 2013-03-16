package com.modzelewski.nfcgb.persistence;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.model.*;

class DatabasePopulation {

    /*
    Ich AG
        Gruppe 1
            Dwight D. Eisenhower
        Gruppe 2
            John F. Kennedy
            Lyndon B. Johnson
        Gruppe 3
            Lyndon B. Johnson
            Richard Nixon
            Gerald Ford
        Gruppe 4
        Die Flitzer
        KEINE GRUPPE
            Jimmy Carter

    Medieninformatik 1 - Tutorium 2
        Gruppe 5
        Gruppe 6
        Gruppe 7
        Gruppe 8
        Die Medieninformatik Gefahr
        KEINE GRUPPE
            Ronald Reagan
            George H. W. Bush
    Medieninformatik 2 - Tutorium 5
        Gruppe 9
        Gruppe 10
        Gruppe 11
        Gruppe 12
        KEINE GRUPPE
            Bill Clinton
            George W. Bush
            Barack Obama
    */

    public static void populatePersonDAO(RuntimeExceptionDao<Person, Integer> personDao) {
        personDao.create(new Person("Dwight D. Eisenhower", "dwight@gmail.com"));
        personDao.create(new Person("John F. Kennedy", "john@gmail.com"));
        personDao.create(new Person("Lyndon B. Johnson", "lyndon@gmail.com"));
        personDao.create(new Person("Richard Nixon", "richard@gmail.com"));
        personDao.create(new Person("Gerald Ford", "gerald@gmail.com"));
        personDao.create(new Person("Jimmy Carter", "jimmy@gmail.com"));
        personDao.create(new Person("Ronald Reagan", "ronald@gmail.com"));
        personDao.create(new Person("George H. W. Bush", "george56@gmail.com"));
        personDao.create(new Person("Bill Clinton", "bill@gmail.com"));
        personDao.create(new Person("George W. Bush", "george2u@gmail.com"));
        personDao.create(new Person("Barack Obama", "barack@gmail.com"));
    }

    public static void populateEventDAO(RuntimeExceptionDao<Event, Integer> eventDao) {
        eventDao.create(new Event("Ich AG", 2005, true, "here be dragons"));
        eventDao.create(new Event("Medieninformatik 1 - Tutorium 2", 2011, false, ""));
        eventDao.create(new Event("Medieninformatik 2 - Tutorium 5", 2012, true, ""));
    }

    public static void populateEventMembershipDao(RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao) {
        eventMembershipDao.create(new EventMembership(1, 1));
        eventMembershipDao.create(new EventMembership(1, 2));
        eventMembershipDao.create(new EventMembership(1, 3));
        eventMembershipDao.create(new EventMembership(1, 4));
        eventMembershipDao.create(new EventMembership(1, 5));
        eventMembershipDao.create(new EventMembership(1, 6));
        eventMembershipDao.create(new EventMembership(2, 7));
        eventMembershipDao.create(new EventMembership(3, 8));
        eventMembershipDao.create(new EventMembership(3, 9));
        eventMembershipDao.create(new EventMembership(3, 10));
        eventMembershipDao.create(new EventMembership(3, 11));
    }

    public static void populateGroupMembershipDao(RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao) {
        groupMembershipDao.create(new GroupMembership(1, 1));
        groupMembershipDao.create(new GroupMembership(2, 2));
        groupMembershipDao.create(new GroupMembership(2, 3));
        groupMembershipDao.create(new GroupMembership(3, 3));
        groupMembershipDao.create(new GroupMembership(3, 4));
        groupMembershipDao.create(new GroupMembership(3, 5));
    }

    public static void populateGroupDAO(RuntimeExceptionDao<Group, Integer> groupDao) {
        groupDao.create(new Group("Gruppe 1", 1));
        groupDao.create(new Group("Gruppe 2", 1));
        groupDao.create(new Group("Gruppe 3", 1));
        groupDao.create(new Group("Gruppe 4", 1));
        groupDao.create(new Group("Gruppe 5", 2));
        groupDao.create(new Group("Gruppe 6", 2));
        groupDao.create(new Group("Gruppe 7", 2));
        groupDao.create(new Group("Gruppe 8", 2));
        groupDao.create(new Group("Gruppe 9", 3));
        groupDao.create(new Group("Gruppe 10", 3));
        groupDao.create(new Group("Gruppe 11", 3));
        groupDao.create(new Group("Gruppe 12", 3));
        groupDao.create(new Group("Die Flitzer", 1));
        groupDao.create(new Group("Die Medieninformatik Gefahr", 2));
        groupDao.create(new Group("wir aus hier", 3));
    }
}
