package com.modzelewski.nfcgb;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Switch;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.modzelewski.nfcgb.persistence.DatabaseConfigUtil;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;
import com.modzelewski.nfcgb.persistence.DatabasePopulation;

/**
 * MainActivity
 * 
 * @author Georg
 */
public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements CreateNdefMessageCallback {
	private final String LOG_TAG = getClass().getSimpleName();
	private final String ADD_PERSON = "ADD PERSON";
	private final String EDIT_PERSON = "EDIT PERSON";
	private final String ADD_GROUP = "ADD GROUP";
	private final String EDIT_GROUP = "EDIT GROUP";
	private NfcAdapter nfcAdapter;

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
	/**
	 * Create expandable list referencing at groups in background model.
	 */
	private void createExpandableListView() {
		ga = new GroupAdapter(this, model.getGroups());
		eventExpLV.setAdapter((ExpandableListAdapter) ga);
		registerForContextMenu(eventExpLV);
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

	/**
	 * Create list referencing at persons in background model.
	 */
	private void createListView() {
		pa = new PersonAdapter(this, android.R.layout.simple_list_item_1, model.getPersons());
		personsLV.setAdapter(pa);
		registerForContextMenu(personsLV);
	}

	/**
	 * Creates a custom MIME type encapsulated in an NDEF record
	 */
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String text = ("Beam me up, Android!\n\n" + "Beam Time: " + System.currentTimeMillis());
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.modzelewski.nfcgb", text.getBytes())
				/**
				 * The Android Application Record (AAR) is commented out. When a device
				 * receives a push with an AAR in it, the application specified in the
				 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
				 * You can add it back in to guarantee that this activity starts when
				 * receiving a beamed message. For now, this code uses the tag dispatch
				 * system.
				 */
				// ,NdefRecord.createApplicationRecord("com.modzelewski.nfcgb")
		});
		return msg;
	}

	/**
	 * Create spinner referencing at events in background model.
	 */
	private void createSpinner() {
		// assign Data as single items
		ea = new EventAdapter(this, android.R.layout.simple_spinner_item, model.getEvents());
		spinner.setAdapter(ea);

		spinner.setOnLongClickListener(new OnLongClickListener() {
			private void eventdialog(final EventData ed) {
				LayoutInflater inflater = LayoutInflater.from(spinner.getContext());
				final RuntimeExceptionDao<EventData, Integer> eventDao = getHelper().getEventDataDao();

				final View eventView = inflater.inflate(R.layout.event_dialog, null);

				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				EditText year = (EditText) eventView.findViewById(R.id.ed_year);
				EditText tutor = (EditText) eventView.findViewById(R.id.ed_tutor);
				EditText tutoremail = (EditText) eventView.findViewById(R.id.ed_tutor_email);
				EditText info = (EditText) eventView.findViewById(R.id.ed_info);

				eventname.setText(ed.eventname);
				wintersemester.setChecked(ed.wintersemester);
				year.setText(String.valueOf(ed.year));
				tutor.setText(ed.tutor);
				tutoremail.setText(ed.tutoremail);
				info.setText(ed.info);

				AlertDialog.Builder adb = new AlertDialog.Builder(spinner.getContext());
				String title = getString(R.string.edit_event);
				adb.setTitle(title);
				adb.setView(eventView);
				adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
						Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
						EditText year = (EditText) eventView.findViewById(R.id.ed_year);
						EditText tutor = (EditText) eventView.findViewById(R.id.ed_tutor);
						EditText tutoremail = (EditText) eventView.findViewById(R.id.ed_tutor_email);
						EditText info = (EditText) eventView.findViewById(R.id.ed_info);

						ed.eventname = eventname.getText().toString();
						ed.wintersemester = wintersemester.isChecked();
						ed.year = Integer.valueOf(year.getText().toString());
						ed.tutor = tutor.getText().toString();
						ed.tutoremail = tutoremail.getText().toString();
						ed.info = info.getText().toString();

						eventDao.update(ed);
						eventDao.refresh(ed);

						ea.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// ignore, just dismiss
					}
				}).show();
			}

			@Override
			public boolean onLongClick(View v) {
				eventdialog(model.getCurrentEvent());
				return true;
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

	void menuAbout() {
		DialogFragment newFragment = AboutDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "dialog");
	}

	private void menuAddEvent() {

		LayoutInflater inflater = LayoutInflater.from(this);
		final View eventView = inflater.inflate(R.layout.event_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getString(R.string.add_event));
		adb.setView(eventView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				EditText year = (EditText) eventView.findViewById(R.id.ed_year);
				EditText tutor = (EditText) eventView.findViewById(R.id.ed_tutor);
				EditText tutoremail = (EditText) eventView.findViewById(R.id.ed_tutor_email);
				EditText info = (EditText) eventView.findViewById(R.id.ed_info);

				EventData ed = new EventData();

				ed.eventname = eventname.getText().toString();
				ed.wintersemester = wintersemester.isChecked();
				ed.year = Integer.valueOf(year.getText().toString());
				ed.tutor = tutor.getText().toString();
				ed.tutoremail = tutoremail.getText().toString();
				ed.info = info.getText().toString();

				RuntimeExceptionDao<EventData, Integer> eventDao = getHelper().getEventDataDao();
				eventDao.create(ed);
				model.events.add(ed);
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();

	}

	private void menuGroup(final String task, final MenuItem item) {
		final EventData currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(this);
		final View groupView = inflater.inflate(R.layout.group_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		String title = null;
		if (task == ADD_GROUP)
			title = getString(R.string.add_group);
		if (task == EDIT_GROUP) {
			title = getString(R.string.edit_group);

			EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			final AdapterView.AdapterContextMenuInfo pInfo = info;
			final GroupData gd = model.groups.get(pInfo.position);
			groupNameET.setText(gd.groupName);
		}
		adb.setTitle(title);
		adb.setView(groupView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
				String groupName = groupNameET.getText().toString();
				GroupData group = new GroupData(groupName, currentEvent.id);

				// create Object
				RuntimeExceptionDao<GroupData, Integer> groupDao = getHelper().getGroupDataDao();
				if (task == ADD_GROUP) {
					groupDao.create(group);
					model.groups.add(group);
				}
				if (task == EDIT_GROUP) {
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
					final AdapterView.AdapterContextMenuInfo pInfo = info;
					final GroupData gd = model.groups.get(pInfo.position);
					gd.groupName = groupName;
					groupDao.update(gd);
					groupDao.refresh(gd);
				}
				refreshListViews();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).show();
	}

	private void menuGroupRemove(MenuItem item) {
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.remove_group);
		adb.setMessage(R.string.remove_group_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		final ExpandableListView.ExpandableListContextMenuInfo pInfo = info;
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO: Is the id here right?
				GroupData gd = model.groups.get((int) pInfo.id);
				model.groups.remove(gd);
				refreshListViews();
			}
		});
		adb.show();
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
			@Override
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
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();
	}

	private void menuPersonRemove(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		final AdapterView.AdapterContextMenuInfo pInfo = info;
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PersonData pd = model.persons.get(pInfo.position);
				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = getHelper().getEventMembershipDataDao();
				List<EventMembershipData> emd = null;
				try {
					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", pd.id).and().eq("event_id", model.getCurrentEvent().id).prepare());
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == request_Code) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {
		case R.id.cm_group_add:
			menuGroup(ADD_GROUP, item);
			return true;
		case R.id.cm_group_edit:
			menuGroup(EDIT_GROUP, item);
			return true;
		case R.id.cm_group_remove:
			menuGroupRemove(item);
			return true;
		case R.id.cm_person_edit:
			menuPerson(EDIT_PERSON, item);
			return true;
		case R.id.cm_person_remove:
			menuPersonRemove(item);
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content and cache some important objects
		setContentView(R.layout.activity_main);
		spinner = (Spinner) findViewById(R.id.events_spinner);
		personsLV = (ListView) findViewById(R.id.personsLV);
		eventExpLV = (ExpandableListView) findViewById(R.id.groupsExpLV);
		// TODO?: check, if all elements are available?

		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
		doDatabaseStuff();

		createSpinner();
		createListView();
		createExpandableListView();

		// Check for available NFC Adapter
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		} else
			Toast.makeText(this, "NFC is available. Yeyy", Toast.LENGTH_LONG).show();
		// Register callback
		nfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.personsLV) {
			getMenuInflater().inflate(R.menu.context_menu_person, menu);
		}

		// //TODO Context Menu for Spinner? Not so nice...
		// if (v.getId() == R.id.events_spinner) {
		// getMenuInflater().inflate(R.menu.context_menu_, menu);
		// }

		if (v.getId() == R.id.groupsExpLV) {
			getMenuInflater().inflate(R.menu.context_menu_group, menu);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.om_about:
			menuAbout();
			return true;
		case R.id.om_add_event:
			menuAddEvent();
			return true;
		case R.id.om_add_group:
			menuGroup(ADD_GROUP, item);
			return true;
		case R.id.om_add_person:
			menuPerson(ADD_PERSON, item);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	/**
	 * Parses the NDEF Message from the intent and prints to a Toast
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
				NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		Toast.makeText(this, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
	}

	protected void refreshListViews() {
		pa.notifyDataSetChanged();
		ga.notifyDataSetChanged();
	}

	private void setCurrentEvent(EventData ed) {
		EventData current_event = model.getCurrentEvent();
		if (current_event == null || ed.id != current_event.id) {
			model.setCurrentEvent(ed);
			refreshListViews();
		}
	}
}