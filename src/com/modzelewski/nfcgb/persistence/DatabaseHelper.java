package com.modzelewski.nfcgb.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.model.*;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "nfcgb.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 20;

	// the DAO object we use to access the eventData table
	private Dao<EventData, Integer> eventDao = null;
	private Dao<PersonData, Integer> personDao = null;
	private Dao<GroupData, Integer> groupDao = null;
	private Dao<EventMembershipData, Integer> eventMembershipDao = null;
	private Dao<GroupMembershipData, Integer> groupMembershipDao = null;

	private RuntimeExceptionDao<EventData, Integer> eventRuntimeDao = null;
	private RuntimeExceptionDao<PersonData, Integer> personRuntimeDao = null;
	private RuntimeExceptionDao<GroupData, Integer> groupRuntimeDao = null;
	private RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipRuntimeDao = null;
	private RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipRuntimeDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
//			Log.i(DatabaseHelper.class.getName(), "onCreate");
			// create tables
			TableUtils.createTable(connectionSource, com.modzelewski.nfcgb.model.EventData.class);
			TableUtils.createTable(connectionSource, com.modzelewski.nfcgb.model.GroupData.class);
			TableUtils.createTable(connectionSource, com.modzelewski.nfcgb.model.PersonData.class);
			TableUtils.createTable(connectionSource, com.modzelewski.nfcgb.model.EventMembershipData.class);
			TableUtils.createTable(connectionSource, com.modzelewski.nfcgb.model.GroupMembershipData.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

		// here we try inserting data in the on-create as a test
//		RuntimeExceptionDao<EventData, Integer> daoE = getEventDataDao();
//		RuntimeExceptionDao<PersonData, Integer> daoP = getPersonDataDao();
//		RuntimeExceptionDao<GroupData, Integer> daoG = getGroupDataDao();
//		long millis = System.currentTimeMillis();
//		// create some entries in the onCreate
//		EventData event = new EventData(millis);
//		PersonData person = new PersonData(millis);
//		daoE.create(event);
//		daoP.create(person);
//		event = new EventData(millis + 1);
//		person = new PersonData(millis + 1);
//		daoE.create(event);
//		daoP.create(person);
//		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
//			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, EventData.class, true);
			TableUtils.dropTable(connectionSource, GroupData.class, true);
			TableUtils.dropTable(connectionSource, PersonData.class, true);
			TableUtils.dropTable(connectionSource, EventMembershipData.class, true);
			TableUtils.dropTable(connectionSource, GroupMembershipData.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our eventData class. It will
	 * create it or just give the cached value.
	 */
	public Dao<EventData, Integer> getDaoE() throws SQLException {
		if (eventDao == null) {
			eventDao = getDao(EventData.class);
		}
		return eventDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our personData class. It
	 * will create it or just give the cached value.
	 */
	public Dao<PersonData, Integer> getDaoP() throws SQLException {
		if (personDao == null) {
			personDao = getDao(PersonData.class);
		}
		return personDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our groupData class. It
	 * will create it or just give the cached value.
	 */
	public Dao<GroupData, Integer> getDaoG() throws SQLException {
		if (groupDao == null) {
			groupDao = getDao(GroupData.class);
		}
		return groupDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our groupData class. It
	 * will create it or just give the cached value.
	 */
	public Dao<EventMembershipData, Integer> getDaoEM() throws SQLException {
		if (eventMembershipDao == null) {
			eventMembershipDao = getDao(EventMembershipData.class);
		}
		return eventMembershipDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our groupData class. It
	 * will create it or just give the cached value.
	 */
	public Dao<GroupMembershipData, Integer> getDaoGM() throws SQLException {
		if (groupMembershipDao == null) {
			groupMembershipDao = getDao(GroupMembershipData.class);
		}
		return groupMembershipDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
	 * for our eventData class. It will create it or just give the cached value.
	 * RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<EventData, Integer> getEventDataDao() {
		if (eventRuntimeDao == null) {
			eventRuntimeDao = getRuntimeExceptionDao(EventData.class);
		}
		return eventRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
	 * for our personData class. It will create it or just give the cached
	 * value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<PersonData, Integer> getPersonDataDao() {
		if (personRuntimeDao == null) {
			personRuntimeDao = getRuntimeExceptionDao(PersonData.class);
		}
		return personRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
	 * for our groupData class. It will create it or just give the cached
	 * value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<GroupData, Integer> getGroupDataDao() {
		if (groupRuntimeDao == null) {
			groupRuntimeDao = getRuntimeExceptionDao(GroupData.class);
		}
		return groupRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
	 * for our groupData class. It will create it or just give the cached
	 * value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<EventMembershipData, Integer> getEventMembershipDataDao() {
		if (eventMembershipRuntimeDao == null) {
			eventMembershipRuntimeDao = getRuntimeExceptionDao(EventMembershipData.class);
		}
		return eventMembershipRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
	 * for our groupData class. It will create it or just give the cached
	 * value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<GroupMembershipData, Integer> getGroupMembershipDataDao() {
		if (groupMembershipRuntimeDao == null) {
			groupMembershipRuntimeDao = getRuntimeExceptionDao(GroupMembershipData.class);
		}
		return groupMembershipRuntimeDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		eventDao = null;
		eventRuntimeDao = null;
		personDao = null;
		personRuntimeDao = null;
		groupDao = null;
		groupRuntimeDao = null;
		eventMembershipDao = null;
		eventMembershipRuntimeDao = null;
		groupMembershipDao = null;
		groupMembershipRuntimeDao = null;
	}
}
