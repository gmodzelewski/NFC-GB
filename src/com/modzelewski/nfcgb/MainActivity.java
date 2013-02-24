package com.modzelewski.nfcgb;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.controller.DragEventListener;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.*;
import com.modzelewski.nfcgb.nearfieldcommunication.Nfc;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;
import com.modzelewski.nfcgb.persistence.DatabasePopulator;
import com.modzelewski.nfcgb.view.*;

import java.sql.SQLException;
import java.util.List;

/**
 * MainActivity
 * 
 * @author Georg
 */
public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements CreateNdefMessageCallback {
	private final String LOG_TAG = getClass().getSimpleName();

    private DatabaseHelper databaseHelper = null;
	protected NfcAdapter nfcAdapter;

	private static final int request_Code = 1;
	private final Context context = this;

	private final BackgroundModel model = new BackgroundModel(this);

	// some gui elements
    private Spinner spinner;
	private ListView personsLV;
	private ExpandableListView eventExpLV;
	// gui adapter
    private EventAdapter ea;
	private PersonAdapter pa;
	private GroupAdapter ga;
	private AboutDialog aboutDialog;
	private EventDialogInterface eventDialog;
	private GroupDialogInterface groupDialog;
	private PersonDialogInterface personDialog;

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

		pa = new PersonAdapter(this, model.getPersons());
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
	 * Create spinner referencing at events in background model.
	 */
	private void createSpinner() {
		// assign Data as single items
		ea = new EventAdapter(this, model.getEvents());
		spinner.setAdapter(ea);
		registerForContextMenu(spinner);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				setCurrentEvent(model.getEvents().get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
            }
		});
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
			groupDialog.addGroup(databaseHelper, model, ga);
			return true;
		case R.id.cm_group_edit:
			groupDialog.editGroup(databaseHelper, model, ga, item);
			return true;
		case R.id.cm_group_remove:
			groupDialog.removeGroup(databaseHelper, model, ga, item);
			return true;
		case R.id.cm_group_email:
			groupDialog.emailGroup(model, item);
			return true;
		case R.id.cm_person_edit:
			personDialog.editPerson(databaseHelper, model, item, pa, ga);
			refreshListViews();
			return true;
		case R.id.cm_person_remove:
			personDialog.removePerson(databaseHelper, model, item, pa, ga);
			refreshListViews();
			// Log.i("DEFAULT", "Bin drin, ItemID " + item.getItemId());
			return true;
		case R.id.cm_event_edit:
			eventDialog.editEvent(databaseHelper, model, ea);
			return true;
		case R.id.cm_event_remove:
			eventDialog.removeEvent(databaseHelper, model, ea);

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		databaseHelper = model.getHelper();
		aboutDialog = new AboutDialog();
		eventDialog = new EventDialog(context);
		groupDialog = new GroupDialog(context);
		personDialog = new PersonDialog(context);

		if (nfcAdapter != null) {
			// Check to see that the Activity started due to an Android Beam
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
				Nfc nfc = new Nfc();
				nfc.processIntent(context, getIntent());

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

        String LISTVIEW_TAG = "ListView";
        personsLV.setTag(LISTVIEW_TAG);
        String EXPLISTVIEW_TAG = "ELV";
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
			aboutDialog.menuAbout(context);
			return true;
		case R.id.om_add_event:
			EventDialogInterface eventDialog = new EventDialog(context);
			eventDialog.addEvent(databaseHelper, model, spinner, ea);
			return true;
		case R.id.om_add_group:
			groupDialog.addGroup(databaseHelper, model, ga);
			return true;
		case R.id.om_add_person:
			personDialog.addPerson(databaseHelper, model, pa);
			refreshListViews();
			return true;
		case R.id.om_nfc:
			Nfc nfc = new Nfc();
			nfc.menuNfcCheck(context);
			return true;
		case R.id.om_repop:
			DatabasePopulator dp = new DatabasePopulator();
			dp.doDatabaseStuff(databaseHelper, context, model);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	void refreshListViews() {
		pa.notifyDataSetChanged();
		ga.notifyDataSetChanged();
	}

	void setCurrentEvent(EventData ed) {
		EventData current_event = model.getCurrentEvent();
		if (current_event == null || ed.getId() != current_event.getId()) {
			model.setCurrentEvent(ed);
			refreshListViews();
		}
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		return null;
	}
}