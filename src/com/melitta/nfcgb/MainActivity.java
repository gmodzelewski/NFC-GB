package com.melitta.nfcgb;

import java.sql.SQLException;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.melitta.nfcgb.persistence.DatabaseConfigUtil;
import com.melitta.nfcgb.persistence.DatabaseHelper;
import com.melitta.nfcgb.persistence.DatabasePopulation;

/**
 * MainActivity
 * 
 * @author Georg
 */
public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private final String LOG_TAG = getClass().getSimpleName();
	private final String ADD_PERSON = "ADD PERSON";
	private final String EDIT_PERSON = "EDIT PERSON";

	private static final int request_Code = 1;

	BackgroundModel model = new BackgroundModel(this);

	// some gui elements
	Spinner spinner;
	ListView personsLV;
	ExpandableListView eventExpLV;
	// gui adapter
	EventAdapter ea;
	PersonAdapter pa;
	GroupAdapter ga;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content and cache some important objects
		setContentView(R.layout.activity_main);
		spinner = (Spinner) findViewById(R.id.events_spinner);
		personsLV = (ListView) findViewById(R.id.personsLV);
		eventExpLV = (ExpandableListView) findViewById(R.id.eventExpLV);
		// TODO?: check, if all elements are available?

		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
		doDatabaseStuff();

		createSpinner();
		createListView();
		createExpandableListView();
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

		// load events from database
		RuntimeExceptionDao<EventData, Integer> eventDao = getHelper().getEventDataDao();
		model.setEvents(eventDao.queryForAll());
	}

	/**
	 * Create spinner referencing at events in background model.
	 */
	private void createSpinner() {
		// assign Data as single items
		ea = new EventAdapter(this, android.R.layout.simple_spinner_item, model.getEvents());
		spinner.setAdapter(ea);

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
				setCurrentEvent(model.getEvents().get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				return;
			}
		});
	}

	/**
	 * Create list referencing at persons in background model.
	 */
	private void createListView() {
		pa = new PersonAdapter(this, android.R.layout.simple_list_item_1, model.getPersons());
		personsLV.setAdapter(pa);
		registerForContextMenu(personsLV);
	}

	/**
	 * Create expandable list referencing at groups in background model.
	 */
	private void createExpandableListView() {
		ga = new GroupAdapter(this, model.getGroups());
		eventExpLV.setAdapter((ExpandableListAdapter) ga);

		// List<Map<String, String>> groupData = new ArrayList<Map<String,
		// String>>();
		// List<List<Map<String, String>>> childData = new
		// ArrayList<List<Map<String, String>>>();
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
		// expLVAdapter = new SimpleExpandableListAdapter(this, groupData,
		// android.R.layout.simple_expandable_list_item_1,
		// new String[] { NAME, IS_EVEN }, new int[] { android.R.id.text1,
		// android.R.id.text2 }, childData,
		// android.R.layout.simple_expandable_list_item_2, new String[] { NAME,
		// IS_EVEN }, new int[] { android.R.id.text1,
		// android.R.id.text2 });

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
		/*
		 * for (int i = 0; i < 20; i++) { Map<String, String> curGroupMap = new
		 * HashMap<String, String>(); groupData.add(curGroupMap);
		 * curGroupMap.put(NAME, "Group " + i); curGroupMap.put(IS_EVEN, (i % 2
		 * == 0) ? "This group is even" : "This group is odd"); List<Map<String,
		 * String>> children = new ArrayList<Map<String, String>>(); // TODO:
		 * add children childData.add(children); } // Set up our adapter
		 * expLVAdapter = new SimpleExpandableListAdapter(this, groupData,
		 * android.R.layout.simple_expandable_list_item_1, new String[] { NAME,
		 * IS_EVEN }, new int[] { android.R.id.text1, android.R.id.text2 },
		 * childData, android.R.layout.simple_expandable_list_item_2, new
		 * String[] { NAME, IS_EVEN }, new int[] { android.R.id.text1,
		 * android.R.id.text2 }); eventExpLV.setAdapter(expLVAdapter);
		 */
	}

	private void setCurrentEvent(EventData ed) {
		EventData current_event = model.getCurrentEvent();
		if (current_event == null || ed.id != current_event.id) {
			model.setCurrentEvent(ed);
			refreshListViews();
		}
	}

	protected void refreshListViews() {
		pa.notifyDataSetChanged();
		ga.notifyDataSetChanged();
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
		case R.id.menu_about:
			menuAbout();
			return true;
		case R.id.menu_add_person:
			// menuAddPerson();
			menuPerson(ADD_PERSON, item);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void menuPerson(final String task, final MenuItem item) {
		final EventData currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(this);
		final View personView = inflater.inflate(R.layout.person_dialog, null);

		String title = null;
		String addPerson = getString(R.string.add_person_in) + " " + currentEvent.eventname;
		String editPerson = getString(R.string.edit_person_in) + " " + currentEvent.eventname;
		if (task == ADD_PERSON)
			title = addPerson;
		if (task == EDIT_PERSON) {
			title = editPerson;

			EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
			EditText emailET = (EditText) personView.findViewById(R.id.pd_email);

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			final AdapterView.AdapterContextMenuInfo pInfo = info;
			final PersonData pd = model.persons.get(pInfo.position);
			nameET.setText(pd.name);
			emailET.setText(pd.email);
		}

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setView(personView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
				EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
				String name = nameET.getText().toString();
				String email = emailET.getText().toString();
				RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();

				if (task == ADD_PERSON) {
					PersonData person = new PersonData(currentEvent.id, name, email);
					RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();

					// create Object
					personDao.create(person);
					EventMembershipData emd = new EventMembershipData(currentEvent.id, person.id, 0);
					eventMembershipDao.create(emd);
					model.persons.add(person);
				}

				if (task == EDIT_PERSON) {
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
					final AdapterView.AdapterContextMenuInfo pInfo = info;
					final PersonData pd = model.persons.get(pInfo.position);
					pd.name = name;
					pd.email = email;

					personDao.update(pd);
					personDao.refresh(pd);

				}

				refreshListViews();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		// String[] menuItems =
		// getResources().getStringArray(R.array.persons_context_menu);
		// String menuItemName = menuItems[menuItemIndex];
		if (menuItemIndex == 0)
			menuPerson(EDIT_PERSON, item);

		if (menuItemIndex == 1) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.context_menu_remove_title);
			adb.setMessage(R.string.context_menu_remove_message);
			adb.setNegativeButton(R.string.cancel_button, null);
			final AdapterView.AdapterContextMenuInfo pInfo = info;
			adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					PersonData pd = model.persons.get(pInfo.position);
					RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
					List<EventMembershipData> emd = null;
					try {
						emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", pd.id).and()
								.eq("event_id", model.getCurrentEvent().id).prepare());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// TODO: outsource
					eventMembershipDao.delete(emd);
					model.persons.remove(pd);
					refreshListViews();
				}
			});
			adb.show();
		}
		return true;
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
