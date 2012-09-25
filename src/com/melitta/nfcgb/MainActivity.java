package com.melitta.nfcgb;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.melitta.nfcgb.persistence.DatabaseHelper;
import com.melitta.nfcgb.persistence.DatabasePopulation;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	int request_Code = 1;
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";
	private ExpandableListAdapter expLVAdapter;
	private final String LOG_TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(LOG_TAG,
				"creating " + getClass() + " at " + System.currentTimeMillis());
		doEventDatabaseStuff("onCreate");

		createSpinner();
		createListView();
		createExpandableListView();
	}

	private void doEventDatabaseStuff(String string) {
		// delete all PersonData daos
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper()
				.getPersonDataDao();
		List<PersonData> personList = personDao.queryForAll();
		for (PersonData person : personList)
			personDao.delete(person);

		Log.i(LOG_TAG,
				"-------------------------------------------------------------------\n");
		Log.i(LOG_TAG, "Should be empty now\n");
		Log.i(LOG_TAG,
				"-------------------------------------------------------------------\n");

		// populate empty database
		DatabasePopulation.populatePersonDAO(personDao);

		// // TODO: why sleep?
		// try {
		// Thread.sleep(5);
		// } catch (InterruptedException e) {
		// // ignore
		// }
	}

	/**
	 * Fills spinner with events
	 */
	private void createSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.events_spinner);
		// TODO: load events from database
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.events_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	/**
	 * Create expandable list view with groups and their member of selected
	 * event.
	 */
	private void createExpandableListView() {
		ExpandableListView eventExpLV = (ExpandableListView) findViewById(R.id.eventExpLV);
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		// TODO: load groups
		for (int i = 0; i < 20; i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, "Group " + i);
			curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even"
					: "This group is odd");
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			// TODO: load member of groups
			for (int j = 0; j < 15; j++) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				curChildMap.put(NAME, "Child " + j);
				curChildMap.put(IS_EVEN, (j % 2 == 0) ? "This child is even"
						: "This child is odd");
			}
			childData.add(children);
		}
		// Set up our adapter
		expLVAdapter = new SimpleExpandableListAdapter(this, groupData,
				android.R.layout.simple_expandable_list_item_1, new String[] {
						NAME, IS_EVEN }, new int[] { android.R.id.text1,
						android.R.id.text2 }, childData,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						NAME, IS_EVEN }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		eventExpLV.setAdapter(expLVAdapter);

	}

	/**
	 * Create list with names and mail.
	 */
	private void createListView() {
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper()
				.getPersonDataDao();
		List<PersonData> personList = personDao.queryForAll();
		ArrayList<String> personsFullNames = new ArrayList<String>();
		for (PersonData pd : personList) {
			personsFullNames.add(String.format("%s %s\n%s", pd.first_name,
					pd.last_name, pd.email));
		}

		ListView personsLV = (ListView) findViewById(R.id.personsLV);
		// TODO: individual layout for individual design
		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, personsFullNames);
		personsLV.setAdapter(adapter);
		registerForContextMenu(personsLV);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.personsLV) {
			// AdapterView.AdapterContextMenuInfo info =
			// (AdapterView.AdapterContextMenuInfo)menuInfo;
			// menu.setHeaderTitle(Countries[info.position]);
			String[] menuItems = getResources().getStringArray(
					R.array.persons_context_menu);
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		CharSequence selectedItem = item.getTitle();
		Toast toast0 = Toast.makeText(getApplicationContext(), selectedItem,
				Toast.LENGTH_SHORT);
		toast0.show();
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(
				R.array.persons_context_menu);
		String menuItemName = menuItems[menuItemIndex];
		if (menuItemIndex == 0) {
			CharSequence text = menuItemName + " not yet implemented";
			Toast toast = Toast.makeText(getApplicationContext(), text,
					Toast.LENGTH_SHORT);
			toast.show();
		}
		if (menuItemIndex == 1) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle("remove " );
			adb.setMessage("Ya sure?");
			adb.setNegativeButton("Cancel", null);
	        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
//	                MyDataObject.remove(positionToRemove);
//	                adapter.notifyDataSetChanged();
	            	CharSequence text = "Not yet implemented";
	    			Toast toast = Toast.makeText(getApplicationContext(), text,
	    					Toast.LENGTH_SHORT);
	    			toast.show();
	            }});
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
		startActivityForResult(new Intent("com.melitta.EventActivity"),
				request_Code);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == request_Code) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, data.getData().toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
