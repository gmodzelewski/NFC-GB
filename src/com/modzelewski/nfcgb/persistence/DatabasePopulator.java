package com.modzelewski.nfcgb.persistence;

import java.sql.SQLException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.model.EventData;

public class DatabasePopulator {
	private DatabaseHelper databaseHelper;
	private final String LOG_TAG = getClass().getSimpleName();

	public void doDatabaseStuff(DatabaseHelper dbh, Context context, BackgroundModel model) {
		databaseHelper = dbh;
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.i(LOG_TAG, "-------------------------------------------------------------------");
		Log.i(LOG_TAG, "--- Database is empty now. Processing Database Population ---");
		Log.i(LOG_TAG, "-------------------------------------------------------------------");

		// populate empty database
		DatabasePopulation.populateEventDAO(databaseHelper.getEventDataDao());
		DatabasePopulation.populatePersonDAO(databaseHelper.getPersonDataDao());
		DatabasePopulation.populateGroupDAO(databaseHelper.getGroupDataDao());
		DatabasePopulation.populateEventMembershipDao(databaseHelper.getEventMembershipDataDao());

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
		RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
		model.setEvents(eventDao.queryForAll());

//		createSpinner();
//		createListView();
//		createExpandableListView();
	}
}
