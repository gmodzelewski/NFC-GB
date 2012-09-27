package com.melitta.nfcgb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.melitta.nfcgb.persistence.DatabaseConfigUtil;
import com.melitta.nfcgb.persistence.DatabaseHelper;
import com.melitta.nfcgb.persistence.DatabasePopulation;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	int request_Code = 1;
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";
	private ExpandableListAdapter expLVAdapter;
	private final String LOG_TAG = getClass().getSimpleName();

	// TODO: maybe save db-query-result visible and filter in Java
	List<EventData> events;
	List<PersonData> persons = new LinkedList<PersonData>();

	// List<EventMembershipData> memberships;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
		doDatabaseStuff();

		createSpinner();

		EventData currentSpinnerEvent = getCurrentEvent();
		createListView(currentSpinnerEvent);
		createExpandableListView(currentSpinnerEvent);
	}

	private void doDatabaseStuff() {
		// delete all Data daos
		// TODO: reset here database every time; remove at release
		ConnectionSource connectionSource = getHelper().getConnectionSource();
		try {
			for (Class<?> c : DatabaseConfigUtil.classes) {
				TableUtils.dropTable(connectionSource, c, true);
				TableUtils.createTable(connectionSource, c);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
		Log.i(LOG_TAG, "Should be empty now\n");
		Log.i(LOG_TAG, "-------------------------------------------------------------------\n");

		// populate empty database
		DatabasePopulation.populateEventDAO(getHelper().getEventDataDao());
		DatabasePopulation.populatePersonDAO(getHelper().getPersonDataDao());
		DatabasePopulation.populateGroupDAO(getHelper().getGroupDataDao());
		DatabasePopulation.populateEventMembershipDao(getHelper().getEventMembershipDataDao());

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Fills spinner with events
	 */
	private void createSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.events_spinner);
		// load events from database
		RuntimeExceptionDao<EventData, Integer> eventDao = getHelper().getEventDataDao();
		events = eventDao.queryForAll();
		Collections.sort(events);

		// ArrayList<String> eventNames = new ArrayList<String>();
		// CloseableIterator<EventData> eventIterator =
		// eventDao.closeableIterator();
		// try {
		// while (eventIterator.hasNext()) {
		// EventData ed = eventIterator.next();
		// eventNames.add(String.format("%s (%s %d)", ed.eventname,
		// ed.wintersemester ? "WiSe" : "SoSe", ed.year));
		// }
		// } finally {
		// // close it at the end to close underlying SQL statement
		// eventIterator.close();
		// }

		ArrayList<String> eventNames = new ArrayList<String>();
		for (EventData ed : events) {
			eventNames.add(String.format("%s (%s %d)", ed.eventname, ed.wintersemester ? "WiSe" : "SoSe", ed.year));
		}
		// assign Data as single items
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eventNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				startEventActivity(v);
				return false;
			}
		});
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// refresh all user
				createListView(events.get(position));
				createExpandableListView(events.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				return;
			}
		});
	}

	/**
	 * Get selected event
	 * 
	 * @return null, if no spinner or no item selected else EventData
	 */
	private EventData getCurrentEvent() {
		// load persons and groups for selected event
		Spinner spinner = (Spinner) findViewById(R.id.events_spinner);

		// update content, if there's a pre-selected event
		if (spinner == null)
			return null;

		// selected?
		int position = spinner.getSelectedItemPosition();
		if (position == AdapterView.INVALID_POSITION)
			return null;

		return events.get(position);
	}

	protected void refreshListViews() {
		ExpandableListView eventExpLV = (ExpandableListView) findViewById(R.id.eventExpLV);
		ListView personsLV = (ListView) findViewById(R.id.personsLV);
		eventExpLV.invalidateViews();
		personsLV.invalidateViews();
	}

	/**
	 * Create expandable list view with groups and their member of selected
	 * event.
	 * 
	 * @param currentSpinnerEvent
	 */
	private void createExpandableListView(EventData currentSpinnerEvent) {

		ExpandableListView eventExpLV = (ExpandableListView) findViewById(R.id.eventExpLV);
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		// // TODO: load groups
		//
		// // TODO: cross-select
		// RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao
		// = getHelper().getEventMembershipDataDao();
		// List<EventMembershipData> eventMemberships = null;
		// try {
		// eventMemberships =
		// eventMembershipDao.queryBuilder().where().eq("event_id",
		// currentSpinnerEvent.id).query();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		//
		// ArrayList<String> groupNames = new ArrayList<String>();
		// RuntimeExceptionDao<GroupData, Integer> groupDao =
		// getHelper().getGroupDataDao();
		// for (EventMembershipData emd : eventMemberships) {
		// GroupData gd = groupDao.queryForId(emd.group_id);
		// groupNames.add(gd.groupName);
		// }
		//
		expLVAdapter = new SimpleExpandableListAdapter(this, groupData, android.R.layout.simple_expandable_list_item_1, new String[] { NAME, IS_EVEN }, new int[] { android.R.id.text1,
				android.R.id.text2 }, childData, android.R.layout.simple_expandable_list_item_2, new String[] { NAME, IS_EVEN }, new int[] { android.R.id.text1, android.R.id.text2 });

		/*
		 * for (int i = 0; i < 20; i++) { Map<String, String> curGroupMap = new
		 * HashMap<String, String>(); groupData.add(curGroupMap);
		 * curGroupMap.put(NAME, "Group " + i); curGroupMap.put(IS_EVEN, (i % 2
		 * == 0) ? "This group is even" : "This group is odd"); List<Map<String,
		 * String>> children = new ArrayList<Map<String, String>>(); // TODO:
		 * load member of groups for (int j = 0; j < 15; j++) { Map<String,
		 * String> curChildMap = new HashMap<String, String>();
		 * children.add(curChildMap); curChildMap.put(NAME, "Child " + j);
		 * curChildMap.put(IS_EVEN, (j % 2 == 0) ? "This child is even" :
		 * "This child is odd"); } childData.add(children); }
		 */
		// TODO: query for groups
		// TODO: load persons
		for (int i = 0; i < 20; i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, "Group " + i);
			curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even" : "This group is odd");
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			// TODO: add children
			childData.add(children);
		}
		// Set up our adapter
		expLVAdapter = new SimpleExpandableListAdapter(this, groupData, android.R.layout.simple_expandable_list_item_1, new String[] { NAME, IS_EVEN }, new int[] { android.R.id.text1,
				android.R.id.text2 }, childData, android.R.layout.simple_expandable_list_item_2, new String[] { NAME, IS_EVEN }, new int[] { android.R.id.text1, android.R.id.text2 });
		eventExpLV.setAdapter(expLVAdapter);

	}

	/**
	 * Create list with names and mail.
	 * 
	 * @param currentSpinnerEvent
	 */
	// TODO: OPTIMIZE! OPTIMIZE! OPTIMIZE!
	private void createListView(EventData currentSpinnerEvent) {
		// Log.i(NAME, "currentItemId ist " + currentSpinnerEvent.id);

		// TODO: cross-select
		RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
		List<EventMembershipData> eventMemberships = null;
		try {
			eventMemberships = eventMembershipDao.queryBuilder().where().eq("event_id", currentSpinnerEvent.id).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ArrayList<String> personsFullNames = new ArrayList<String>();
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();

		persons.clear();

		for (EventMembershipData emd : eventMemberships) {
			PersonData pd = personDao.queryForId(emd.person_id);
			persons.add(pd);
			personsFullNames.add(String.format("%s\n%s", pd.name, pd.email));
		}

		// TODO: individual layout for individual design
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, personsFullNames);
		ListView personsLV = (ListView) findViewById(R.id.personsLV);
		personsLV.setAdapter(adapter);
		registerForContextMenu(personsLV);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.personsLV) {
			String[] menuItems = getResources().getStringArray(R.array.persons_context_menu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_info:
			menuInfo();
			return true;
		case R.id.menu_about:
			menuAbout();
			return true;
		case R.id.menu_add_person:
			menuAddPerson();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void menuAddPerson() {
		{
			final EventData currentEvent = getCurrentEvent();
			LayoutInflater inflater = LayoutInflater.from(this);
			final View addView = inflater.inflate(R.layout.person_dialog, null);

			new AlertDialog.Builder(this).setTitle(currentEvent.eventname).setView(addView).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText nameET = (EditText) addView.findViewById(R.id.pd_name);
					EditText emailET = (EditText) addView.findViewById(R.id.pd_email);
					String name = nameET.getText().toString();
					String email = emailET.getText().toString();
					
					PersonData person = new PersonData(currentEvent.id, name, email);
					
					//group_id == 0 means no group assigned
					
					// TODO: Add Person to Database
					RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();
					RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
					RuntimeExceptionDao<EventData, Integer> eventDao = getHelper().getEventDataDao();

					//create Object
					personDao.create(person);
					EventMembershipData emd = new EventMembershipData(currentEvent.id, person.id, 0);
					eventMembershipDao.create(emd);

					createListView(currentEvent);
				}
			}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// ignore, just dismiss
				}
			}).show();
		}

		// startActivityForResult(new Intent("com.melitta.PersonActivity"),
		// request_Code);
		// if(request_Code == RESULT_OK)
		// createListView(getCurrentEvent());
		// mache irgendwas

		// adb.setPositiveButton(R.string.ok_button, new
		// AlertDialog.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// PersonData pd = persons.get(pInfo.position);
		// RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao
		// = getHelper().getEventMembershipDataDao();
		// List<EventMembershipData> emd = null;
		// try {
		// emd =
		// eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id",
		// pd.id).and().eq("event_id", getCurrentEvent().id).prepare());
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// eventMembershipDao.delete(emd);
		// persons.remove(pd);
		// createListView(getCurrentEvent());
		// }
		// });

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.persons_context_menu);
		String menuItemName = menuItems[menuItemIndex];
		if (menuItemIndex == 0) {
			CharSequence text = menuItemName + " not yet implemented";
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			toast.show();
		}
		if (menuItemIndex == 1) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.context_menu_remove_title);
			adb.setMessage(R.string.context_menu_remove_message);
			adb.setNegativeButton(R.string.cancel_button, null);
			final AdapterView.AdapterContextMenuInfo pInfo = info;
			adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					PersonData pd = persons.get(pInfo.position);
					RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
					List<EventMembershipData> emd = null;
					try {
						emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", pd.id).and().eq("event_id", getCurrentEvent().id).prepare());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					eventMembershipDao.delete(emd);
					persons.remove(pd);
					createListView(getCurrentEvent());
				}
			});
			adb.show();
		}
		return true;
	}

	private void menuInfo() {
		startActivity(new Intent(this, MyInfo.class));
	}

	void menuAbout() {
		DialogFragment newFragment = AboutDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void startEventActivity(View view) {
		startActivityForResult(new Intent("com.melitta.EventActivity"), request_Code);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == request_Code) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
}
