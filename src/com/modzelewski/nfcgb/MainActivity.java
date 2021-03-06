package com.modzelewski.nfcgb;

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
 * @author Georg
 */
public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private final String LOG_TAG = getClass().getSimpleName();

	private DatabaseHelper databaseHelper = null;
	private NfcAdapter nfcAdapter;

	private static final int request_Code = 1;
	private final Context context = this;

	private final BackgroundModel model = new BackgroundModel(this);

	// some gui elements
	private Spinner eventSpinner;
	// gui adapter

	private AboutDialog aboutDialog;
	private EventDialogInterface eventDialog;
	private GroupDialogInterface groupDialog;
	private PersonDialogInterface personDialog;

	private GroupAdapter groupAdapter;
	private PersonAdapter personAdapter;
	private EventAdapter eventAdapter;

	private Nfc nfc;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == request_Code) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, data.getData().toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.cm_group_add:
			groupDialog.addGroup(model, groupAdapter);
			return true;
		case R.id.cm_group_edit:
			groupDialog.editGroup(model, groupAdapter, item);
			return true;
		case R.id.cm_group_remove:
			groupDialog.removeGroup(model, groupAdapter, item);
			return true;
		case R.id.cm_group_email:
			groupDialog.emailGroup(model, item);
			return true;
		case R.id.cm_person_add:
			personDialog.addPerson(model, personAdapter);
			refreshListViews();
			return true;
		case R.id.cm_person_edit:
			personDialog.editPerson(model, item, groupAdapter, personAdapter);
			return true;
		case R.id.cm_person_remove:
			personDialog.removePerson(model, item, groupAdapter, personAdapter);
			// Log.i("DEFAULT", "Bin drin, ItemID " + item.getItemId());
			return true;
		case R.id.cm_event_edit:
			eventDialog.editEvent(model, eventAdapter);
			return true;
		case R.id.cm_event_remove:
			eventDialog.removeEvent(model, eventAdapter, groupAdapter,
					personAdapter);
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG,
				"creating " + getClass() + " at " + System.currentTimeMillis());
		// set content and cache some important objects
		setContentView(R.layout.activity_main);

		// get DatabaseHelper
		databaseHelper = model.getHelper();
		// load events from database
		RuntimeExceptionDao<Event, Integer> eventDao = databaseHelper
				.getEventDao();
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
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		EventSpinner es = new EventSpinner(
				(Spinner) findViewById(R.id.events_spinner));
		PersonListView plv = new PersonListView(
				(ListView) findViewById(R.id.personsLV));
		GroupExpandableListView glv = new GroupExpandableListView(
				(ExpandableListView) findViewById(R.id.groupsExpLV));

		eventSpinner = es.create(model, context, databaseHelper, eventAdapter,
				groupAdapter, personAdapter);
		ListView personsLV = plv.create(model, context, databaseHelper,
				personAdapter);
		ExpandableListView groupsExpLV = glv.create(model, context,
				databaseHelper, groupAdapter);

		registerForContextMenu(eventSpinner);
		registerForContextMenu(groupsExpLV);
		// Workaround for short click function: Context Menu
		personsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				registerForContextMenu(l);
				openContextMenu(v);
				unregisterForContextMenu(l);
			}
		});

		// --- Drag and Drop init
		DragEventListener dragEL = new DragEventListener(getBaseContext(),
				model);
		groupsExpLV.setOnDragListener(dragEL);
		personsLV.setOnDragListener(dragEL);

		String LISTVIEW_TAG = "ListView";
		personsLV.setTag(LISTVIEW_TAG);
		String EXPLISTVIEW_TAG = "ELV";
		groupsExpLV.setTag(EXPLISTVIEW_TAG);

		// NFC Callback init
		if (nfcAdapter != null) {
			nfc = new Nfc(nfcAdapter, context, model);
			nfcAdapter.setNdefPushMessageCallback(nfc, this);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.personsLV) {
			getMenuInflater().inflate(R.menu.context_menu_person, menu);
		}

		if (v.getId() == R.id.events_spinner) {
			if (model.getEvents().isEmpty())
				Toast.makeText(this, getString(R.string.add_new_event),
						Toast.LENGTH_SHORT).show();
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
			eventDialog.addEvent(model, eventSpinner, eventAdapter,
					groupAdapter, personAdapter);
			return true;
		case R.id.om_add_group:
			if (model.getCurrentEvent() == null) {
				Toast.makeText(this,
						getString(R.string.please_add_a_new_event),
						Toast.LENGTH_LONG).show();
			} else {
				groupDialog.addGroup(model, groupAdapter);
			}
			return true;
		case R.id.om_add_person:
			if (model.getCurrentEvent() == null) {
				Toast.makeText(this,
						getString(R.string.please_add_a_new_event),
						Toast.LENGTH_LONG).show();
			} else {
				personDialog.addPerson(model, personAdapter);
				refreshListViews();
			}
			return true;
		case R.id.om_nfc:
			NfcCheck nfcCheck = new NfcCheck(nfcAdapter, context);
			nfcCheck.check(model);
			return true;
		case R.id.om_repop:
			DatabasePopulator dp = new DatabasePopulator();
			dp.fillDatabase(databaseHelper, context, model);
			eventAdapter.notifyDataSetChanged();
			refreshListViews();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            nfc.processIntent(getIntent());
        }
	}

	void refreshListViews() {
		personAdapter.notifyDataSetChanged();
		groupAdapter.notifyDataSetChanged();
	}

	public BackgroundModel getModel() {
		return model;
	}
}