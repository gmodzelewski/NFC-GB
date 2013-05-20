package com.modzelewski.nfcgb.persistence;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.model.Event;

import java.sql.SQLException;

public class DatabasePopulator {
    private final String LOG_TAG = getClass().getSimpleName();

	public void fillDatabase(DatabaseHelper dbh, Context context, BackgroundModel model) {
        DatabaseHelper databaseHelper = dbh;
		// delete all Data daos
		// Reset here database every time; remove at release
		ConnectionSource connectionSource = databaseHelper.getConnectionSource();
		Log.i(LOG_TAG, "-------------------------------------------------------------------");
		Log.i(LOG_TAG, "--- Processing Database drop ---");
		Log.i(LOG_TAG, "-------------------------------------------------------------------");
		try {
			for (Class<?> c : DatabaseConfigUtil.classes) {
				TableUtils.dropTable(connectionSource, c, true);
				TableUtils.createTable(connectionSource, c);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Log.i(LOG_TAG, "-------------------------------------------------------------------");
		Log.i(LOG_TAG, "--- Database is empty now. Processing Database Population ---");
		Log.i(LOG_TAG, "-------------------------------------------------------------------");

		// populate empty database
		DatabasePopulation.populateEventDAO(databaseHelper.getEventDao());
		DatabasePopulation.populatePersonDAO(databaseHelper.getPersonDao());
		DatabasePopulation.populateGroupDAO(databaseHelper.getGroupDao());
		DatabasePopulation.populateEventMembershipDao(databaseHelper.getEventMembershipDao());
        DatabasePopulation.populateGroupMembershipDao(databaseHelper.getGroupMembershipDao());
		Log.i(LOG_TAG, "-------------------------------------------------------------------");
		Log.i(LOG_TAG, "--- Database is new populated ---");
		Toast.makeText(context, "--- Database is new populated ---", Toast.LENGTH_SHORT).show();
		Log.i(LOG_TAG, "-------------------------------------------------------------------");

		// try {
		// Thread.sleep(5);
		// } catch (InterruptedException e) {
		// // ignore
		// }

		// load events from database
		RuntimeExceptionDao<Event, Integer> eventDao = databaseHelper.getEventDao();
		model.setEvents(eventDao.queryForAll());
		model.reloadEverything();
//		createSpinner();
//		createListView();
//		create();
	}
}
