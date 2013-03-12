package com.modzelewski.nfcgb;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.DragEventListener;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.Event;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.nfc.Nfc;
import com.modzelewski.nfcgb.nfc.NfcCheck;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;
import com.modzelewski.nfcgb.persistence.DatabasePopulator;
import com.modzelewski.nfcgb.view.AboutDialog;
import com.modzelewski.nfcgb.view.EventDialog;
import com.modzelewski.nfcgb.view.EventDialogInterface;
import com.modzelewski.nfcgb.view.EventSpinner;
import com.modzelewski.nfcgb.view.GroupDialog;
import com.modzelewski.nfcgb.view.GroupDialogInterface;
import com.modzelewski.nfcgb.view.GroupExpandableListView;
import com.modzelewski.nfcgb.view.PersonDialog;
import com.modzelewski.nfcgb.view.PersonDialogInterface;
import com.modzelewski.nfcgb.view.PersonListView;

/**
 * MainActivity
 * 
 * @author Georg
 */
public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private final String LOG_TAG = getClass().getSimpleName();

	private DatabaseHelper databaseHelper = null;
	protected NfcAdapter nfcAdapter;

	private static final int request_Code = 1;
	private final Context context = this;

	private final BackgroundModel model = new BackgroundModel(this);

	// some gui elements
	private Spinner eventSpinner;
	private ListView personsLV;
	private ExpandableListView groupsExpLV;
	// gui adapter

	private AboutDialog aboutDialog;
	private EventDialogInterface eventDialog;
	private GroupDialogInterface groupDialog;
	private PersonDialogInterface personDialog;

	private GroupAdapter groupAdapter;
	private PersonAdapter personAdapter;
	private EventAdapter eventAdapter;

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
			groupDialog.addGroup(databaseHelper, model, groupAdapter);
			return true;
		case R.id.cm_group_edit:
			groupDialog.editGroup(databaseHelper, model, groupAdapter, item);
			return true;
		case R.id.cm_group_remove:
			groupDialog.removeGroup(databaseHelper, model, groupAdapter, item);
			return true;
		case R.id.cm_group_email:
			groupDialog.emailGroup(model, item);
			return true;
		case R.id.cm_person_edit:
			personDialog.editPerson(databaseHelper, model, item, personAdapter, groupAdapter);
			refreshListViews();
			return true;
		case R.id.cm_person_remove:
			personDialog.removePerson(databaseHelper, model, item, personAdapter, groupAdapter);
			refreshListViews();
			// Log.i("DEFAULT", "Bin drin, ItemID " + item.getItemId());
			return true;
		case R.id.cm_event_edit:
			eventDialog.editEvent(databaseHelper, model, eventAdapter);
			return true;
		case R.id.cm_event_remove:
			eventDialog.removeEvent(databaseHelper, model, eventAdapter);

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
		// set content and cache some important objects
		setContentView(R.layout.activity_main);

		// get DatabaseHelper
		databaseHelper = model.getHelper();
		// load events from database
		RuntimeExceptionDao<Event, Integer> eventDao = databaseHelper.getEventDataDao();
		model.setEvents(eventDao.queryForAll());

		// Dialog Constructors
		aboutDialog = new AboutDialog();
		eventDialog = new EventDialog(context);
		groupDialog = new GroupDialog(context);
		personDialog = new PersonDialog(context);

		// get Adapters
		groupAdapter = new GroupAdapter(context, model.getGroups());
		personAdapter = new PersonAdapter(context, model.getPersons());
		eventAdapter = new EventAdapter(context, model.getEvents());
		nfcAdapter = NfcAdapter.getDefaultAdapter(context);

		EventSpinner es = new EventSpinner((Spinner) findViewById(R.id.events_spinner));
		PersonListView plv = new PersonListView((ListView) findViewById(R.id.personsLV));
		GroupExpandableListView glv = new GroupExpandableListView((ExpandableListView) findViewById(R.id.groupsExpLV));

		eventSpinner = es.create(model, context, eventAdapter, groupAdapter, personAdapter);
		personsLV = plv.create(model, context, databaseHelper, personAdapter);
		groupsExpLV = glv.create(model, context, databaseHelper, groupAdapter);

		registerForContextMenu(eventSpinner);
		registerForContextMenu(groupsExpLV);
		// Workaround for short click function: Context Menu
		personsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				registerForContextMenu(l);
				openContextMenu(v);
				unregisterForContextMenu(l);
			}
		});

		// --- Drag and Drop init
		DragEventListener dragEL = new DragEventListener(getBaseContext(), model);
		groupsExpLV.setOnDragListener(dragEL);
		personsLV.setOnDragListener(dragEL);

		String LISTVIEW_TAG = "ListView";
		personsLV.setTag(LISTVIEW_TAG);
		String EXPLISTVIEW_TAG = "ELV";
		groupsExpLV.setTag(EXPLISTVIEW_TAG);

		// NFC Callback init
		if (nfcAdapter != null) {
			// Check to see that the Activity started due to an Android Beam
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
				Nfc nfc = new Nfc(nfcAdapter, context, model);
				nfc.processIntent(getIntent());
				// Register callback
				nfcAdapter.setNdefPushMessageCallback(nfc, this);
			}
		}
		
		List<Group> groups = model.getGroups();
		if(!groups.isEmpty())
			Toast.makeText(context, "Groups ist nicht leer", Toast.LENGTH_LONG).show();
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
			aboutDialog.about(context);
			return true;
		case R.id.om_add_event:
			EventDialogInterface eventDialog = new EventDialog(context);
			eventDialog.addEvent(databaseHelper, model, eventSpinner, eventAdapter);
			refreshListViews();
			return true;
		case R.id.om_add_group:
			groupDialog.addGroup(databaseHelper, model, groupAdapter);
			return true;
		case R.id.om_add_person:
			personDialog.addPerson(databaseHelper, model, personAdapter);
			refreshListViews();
			return true;
		case R.id.om_nfc:
			NfcCheck nfcCheck = new NfcCheck(nfcAdapter, context);
			nfcCheck.check();
			return true;
		case R.id.om_repop:
			DatabasePopulator dp = new DatabasePopulator();
			dp.fillDatabase(databaseHelper, context, model);
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
		personAdapter.notifyDataSetChanged();
		groupAdapter.notifyDataSetChanged();
	}
}