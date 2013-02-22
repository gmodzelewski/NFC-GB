package com.modzelewski.nfcgb;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.modzelewski.nfcgb.controller.DragEventListener;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.model.EventData;
import com.modzelewski.nfcgb.model.EventMembershipData;
import com.modzelewski.nfcgb.model.GroupData;
import com.modzelewski.nfcgb.model.GroupMembershipData;
import com.modzelewski.nfcgb.model.PersonData;
import com.modzelewski.nfcgb.persistence.DatabaseConfigUtil;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;
import com.modzelewski.nfcgb.persistence.DatabasePopulation;
import com.modzelewski.nfcgb.persistence.DatabasePopulator;
import com.modzelewski.nfcgb.view.OptionsMenuFactory;

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

	// Create and set the tags for the Buttons
	final String LISTVIEW_TAG = "ListView";
	final String EXPLISTVIEW_TAG = "ELV";
	final String TARGETLAYOUT_TAG = "targetLayout";

	public DatabaseHelper databaseHelper = null;
	private NfcAdapter nfcAdapter;

	private static final int request_Code = 1;
	private final Context context = this;

	BackgroundModel model = new BackgroundModel(this);

	// some gui elements
	Spinner spinner;
	ListView personsLV;
	ExpandableListView eventExpLV;
	// gui adapter
	EventAdapter ea;
	PersonAdapter pa;
	GroupAdapter ga;
	private OptionsMenuFactory omf;

	/**
	 * Create expandable list referencing at groups in background model.
	 */
	private void createExpandableListView() {
		ga = new GroupAdapter(this, model.getGroups());
		eventExpLV.setAdapter(ga);
		// Menu on long Click
		registerForContextMenu(eventExpLV);

		// Removing of Child Items Menu on short Click
		eventExpLV.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				final GroupData group = (GroupData) ga.getGroup(groupPosition);
				final PersonData person = (PersonData) ga.getChild(groupPosition, childPosition);
				AlertDialog.Builder builder = new AlertDialog.Builder(eventExpLV.getContext());
				builder.setMessage(R.string.remove_person_from_group);
				builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						List<GroupMembershipData> gmd = null;
						RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
						try {
							gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", person.getId()).and().eq("group_id", group.id).prepare());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						groupMembershipDao.delete(gmd);
						model.getGroupById(group.id).getPerson().remove(model.getPersonById(person.getId()));
						ga.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();
				
				return true;
			}
		});
	}

	/**
	 * Create list referencing at persons in background model.
	 */
	private void createListView() {

		pa = new PersonAdapter(this, android.R.layout.simple_list_item_1, model.getPersons());
		personsLV.setAdapter(pa);

		// On long Click: Initiate Drag
		personsLV.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
				PersonData person = model.persons.get(position);
				ClipData dragData = ClipData.newPlainText(person.getClass().getSimpleName(), String.valueOf(person.getId()));
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
				l.startDrag(dragData, // the data to be dragged
						shadowBuilder, // the drag shadow builder
						null, // no need to use local data
						0 // flags (not currently used, set to 0)
				);
				return false;
			}
		});

		// On normal Click: Context Menu
		personsLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				registerForContextMenu(l);
				openContextMenu(v);
				unregisterForContextMenu(l);
			}
		});
	}

	/**
	 * Creates a custom MIME type encapsulated in an NDEF record
	 */
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {

		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// String text = ("Beam me up, Android!\n\n" + "Beam Time: " +
		// System.currentTimeMillis());
		PersonData person1 = new PersonData("Hans", "hans@email.de");
		// PersonData person2 = new PersonData("Peter", "peter@email.de");
		String person1Name = person1.getName();
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.modzelewski.nfcgb", person1Name.getBytes())
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

	public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		byte[] textBytes = payload.getBytes(utfEncoding);
		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		return record;
	}

	/**
	 * Create spinner referencing at events in background model.
	 */
	private void createSpinner() {
		// assign Data as single items
		ea = new EventAdapter(this, android.R.layout.simple_spinner_item, model.getEvents());
		spinner.setAdapter(ea);
		registerForContextMenu(spinner);

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



//	private void menuAddEvent() {
//
//		LayoutInflater inflater = LayoutInflater.from(this);
//		final View eventView = inflater.inflate(R.layout.event_dialog, null);
//
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		adb.setTitle(getString(R.string.add_event));
//		adb.setView(eventView);
//		NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
//		year.setMinValue(1940);
//		year.setMaxValue(2300);
//		Calendar c = Calendar.getInstance();
//		year.setValue(c.get(Calendar.YEAR));
//		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
//				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
//				NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
//				EditText info = (EditText) eventView.findViewById(R.id.ed_info);
//
//				EventData ed = new EventData();
//
//				ed.setEventname(eventname.getText().toString());
//				ed.setWintersemester(wintersemester.isChecked());
//				ed.setYear(year.getValue());
//				ed.setInfo(info.getText().toString());
//
//				RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
//				eventDao.create(ed);
//				model.events.add(ed);
//				model.setCurrentEvent(ed);
//				setCurrentEvent(ed);
//				spinner.setSelection(ea.getPosition(ed));
//				ea.notifyDataSetChanged();
//			}
//		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int whichButton) {
//				// ignore, just dismiss
//			}
//		}).show();
//
//	}

	private void menuGroup(final String task, final MenuItem item) {
		final EventData currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(this);
		final View groupView = inflater.inflate(R.layout.group_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
		groupNameET.requestFocus();

		String title = null;
		if (task == ADD_GROUP)
			title = getString(R.string.add_group);

		if (task == EDIT_GROUP) {
			title = getString(R.string.edit_group);
			final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
			final GroupData gd = model.groups.get((int) pInfo.id);
			groupNameET.setText(gd.getGroupName());
		}

		adb.setTitle(title);
		adb.setView(groupView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
				String groupName = groupNameET.getText().toString().trim();
				GroupData group = new GroupData(groupName, currentEvent.getId());

				// create Object
				RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
				if (task == ADD_GROUP) {
					groupDao.create(group);
					model.groups.add(group);
				}
				if (task == EDIT_GROUP) {
					final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
					final GroupData gd = model.groups.get((int) pInfo.id);
					gd.setGroupName(groupName);
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

	private void menuGroupEmail(MenuItem item) {
		ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
		GroupData gd = model.groups.get((int) pInfo.id);
		List<PersonData> persons = gd.getPerson();
		String emailAddresses = "";
		for (PersonData person : persons) {
			if (person.getEmail().contains("@")) {
				emailAddresses += ", " + person.getEmail();
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.incorrect_mail) + person.getName(), Toast.LENGTH_LONG).show();
			}
		}

		final Intent i = new Intent(android.content.Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddresses });
		try {
			startActivity(Intent.createChooser(i, getString(R.string.about_email_chooser)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getBaseContext(), getString(R.string.about_no_email_apps), Toast.LENGTH_SHORT).show();
		}
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
				GroupData gd = model.groups.get((int) pInfo.id);
				RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
				groupDao.delete(gd);
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
		String addPerson = getString(R.string.add_person_in) + " " + currentEvent.getEventname();
		String editPerson = getString(R.string.edit_person_in) + " " + currentEvent.getEventname();
		if (task == ADD_PERSON)
			title = addPerson;
		if (task == EDIT_PERSON) {
			title = editPerson;

			EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
			EditText emailET = (EditText) personView.findViewById(R.id.pd_email);

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			final AdapterView.AdapterContextMenuInfo pInfo = info;
			final PersonData pd = model.persons.get(pInfo.position);
			nameET.setText(pd.getName());
			emailET.setText(pd.getEmail());
		}

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setView(personView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText nameET = (EditText) personView.findViewById(R.id.pd_name);
				EditText emailET = (EditText) personView.findViewById(R.id.pd_email);
				String name = nameET.getText().toString().trim();
				String email = emailET.getText().toString().trim();
				RuntimeExceptionDao<PersonData, Integer> personDao = databaseHelper.getPersonDataDao();

				if (task == ADD_PERSON) {
					PersonData person = new PersonData(name, email);
					RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
					// RuntimeExceptionDao<GroupMembershipData, Integer>
					// groupMembershipDao =
					// databaseHelper.getGroupMembershipDataDao();

					personDao.create(person);
					EventMembershipData emd = new EventMembershipData(currentEvent.getId(), person.getId());
					eventMembershipDao.create(emd);
					model.persons.add(person);
				}

				if (task == EDIT_PERSON) {
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
					final AdapterView.AdapterContextMenuInfo pInfo = info;
					final PersonData pd = model.persons.get(pInfo.position);

					pd.setName(name);
					pd.setEmail(email);

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
				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
				RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
				List<EventMembershipData> emd = null;
				List<GroupMembershipData> gmd = null;

				PersonData pd = model.persons.get(pInfo.position);
				try {
					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("person_id", pd.getId()).and().eq("event_id", model.getCurrentEvent().getId()).prepare());
					gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("person_id", pd.getId()).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}

				for (GroupMembershipData groupMembershipData : gmd) {
					model.getGroupById(groupMembershipData.getGroup_id()).getPerson().remove(model.getPersonById(pd.getId()));
				}
				model.persons.remove(pd);
				eventMembershipDao.delete(emd);
				groupMembershipDao.delete(gmd);

				refreshListViews();
			}
		});
		adb.show();
	}

	private void menuEvent(final MenuItem item) {
		// TODO menuAddEvent hier rein
		final EventData currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(spinner.getContext());
		final RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
		final View eventView = inflater.inflate(R.layout.event_dialog, null);
		EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
		Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
		NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
		EditText info = (EditText) eventView.findViewById(R.id.ed_info);
		eventname.setText(currentEvent.getEventname());
		wintersemester.setChecked(currentEvent.isWintersemester());
		year.setMinValue(1940);
		year.setMaxValue(2300);
		year.setValue(currentEvent.getYear());
		info.setText(currentEvent.getInfo());

		AlertDialog.Builder adb = new AlertDialog.Builder(spinner.getContext());
		String title = getString(R.string.edit_event);
		adb.setTitle(title);
		adb.setView(eventView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
				EditText info = (EditText) eventView.findViewById(R.id.ed_info);

				if (eventname.toString().trim().isEmpty())
					currentEvent.setEventname(getString(R.string.unknown_eventname));
				else
					currentEvent.setEventname(eventname.getText().toString().trim());
				currentEvent.setWintersemester(wintersemester.isChecked());
				currentEvent.setYear(year.getValue());
				currentEvent.setInfo(info.getText().toString());

				eventDao.update(currentEvent);
				eventDao.refresh(currentEvent);

				ea.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();

	}

	private void menuEventRemove(final MenuItem item) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EventData currentEvent = model.getCurrentEvent();
				RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
				RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = databaseHelper.getEventMembershipDataDao();
				RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = databaseHelper.getGroupMembershipDataDao();
				List<EventData> ed = null;
				List<GroupData> gd = null;
				List<EventMembershipData> emd = null;
				List<GroupMembershipData> gmd = null;

				try {
					ed = eventDao.query(eventDao.queryBuilder().where().eq("id", model.getCurrentEvent().getId()).prepare());
					gd = groupDao.query(groupDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().getId()).prepare());
					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().getId()).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}

				for (GroupData groupData : gd) {
					try {
						gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("group_id", groupData.id).prepare());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				eventDao.delete(ed);
				groupDao.delete(gd);
				eventMembershipDao.delete(emd);
				groupMembershipDao.delete(gmd);

				model.persons.clear();
				model.groups.clear();
				model.events.remove(currentEvent);
				if (model.events.contains(currentEvent))
					Toast.makeText(getBaseContext(), "ist noch drin", Toast.LENGTH_LONG).show();

				ea.notifyDataSetChanged();
				refreshListViews();

				if (!model.getEvents().isEmpty())
					setCurrentEvent(model.getEvents().get(0));
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
		case R.id.cm_group_email:
			menuGroupEmail(item);
			return true;
		case R.id.cm_person_edit:
			menuPerson(EDIT_PERSON, item);
			return true;
		case R.id.cm_person_remove:
			menuPersonRemove(item);
			// Log.i("DEFAULT", "Bin drin, ItemID " + item.getItemId());
			return true;
		case R.id.cm_event_edit:
			menuEvent(item);
			return true;
		case R.id.cm_event_remove:
			menuEventRemove(item);

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		databaseHelper = model.getHelper();
		omf = new OptionsMenuFactory();
		
		if(nfcAdapter != null){
				// Check to see that the Activity started due to an Android Beam
				if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
					processIntent(getIntent());
	
				// Register callback
				nfcAdapter.setNdefPushMessageCallback(this, this);
			}
		}
		// set content and cache some important objects
		setContentView(R.layout.activity_main);
		spinner = (Spinner) findViewById(R.id.events_spinner);
		personsLV = (ListView) findViewById(R.id.personsLV);
		eventExpLV = (ExpandableListView) findViewById(R.id.groupsExpLV);
		// TODO?: check, if all elements are available?

		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());

		// load events from database
		RuntimeExceptionDao<EventData, Integer> eventDao = databaseHelper.getEventDataDao();
		model.setEvents(eventDao.queryForAll());

		createSpinner();
		createListView();
		createExpandableListView();

		// --- Drag and Drop init
		DragEventListener dragEL = new DragEventListener(getBaseContext(), model);
		eventExpLV.setOnDragListener(dragEL);
		personsLV.setOnDragListener(dragEL);

		personsLV.setTag(LISTVIEW_TAG);
		eventExpLV.setTag(EXPLISTVIEW_TAG);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.personsLV) {
			getMenuInflater().inflate(R.menu.context_menu_person, menu);
		}

		if (v.getId() == R.id.events_spinner) {
			if (model.getEvents().isEmpty())
				Toast.makeText(this, getString(R.string.add_new_event), Toast.LENGTH_SHORT).show();
			else
				getMenuInflater().inflate(R.menu.context_menu_event, menu);
		}

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
	protected void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}
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
			omf.menuAbout(context);
			return true;
		case R.id.om_add_event:
			omf.menuAddEvent(context, databaseHelper, model, spinner, ea);
			return true;
		case R.id.om_add_group:
			menuGroup(ADD_GROUP, item);
			return true;
		case R.id.om_add_person:
			menuPerson(ADD_PERSON, item);
			return true;
		case R.id.om_nfc:
			menuNfcCheck();
			return true;
		case R.id.om_repop:
			DatabasePopulator dp = new DatabasePopulator();
			dp.doDatabaseStuff(databaseHelper, context, model);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void menuNfcCheck() {
		if (nfcAdapter == null) {
			Toast.makeText(this, getString(R.string.nfc_not_available), Toast.LENGTH_LONG).show();
			finish();
			return;
		} else {
			Toast.makeText(this, getString(R.string.nfc_available), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * Parses the NDEF Message from the intent and prints to a Toast
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		Toast.makeText(this, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
	}

	protected void refreshListViews() {
		pa.notifyDataSetChanged();
		ga.notifyDataSetChanged();
	}

	protected void setCurrentEvent(EventData ed) {
		EventData current_event = model.getCurrentEvent();
		if (current_event == null || ed.getId() != current_event.getId()) {
			model.setCurrentEvent(ed);
			refreshListViews();
		}
	}
}