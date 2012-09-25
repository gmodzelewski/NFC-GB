package com.melitta.nfcgb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


//public class MainActivity  extends Activity  {
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

		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
		doEventDatabaseStuff("onCreate");

		createSpinner();
		createListView();
		createExpandableListView();

	}

	private void doEventDatabaseStuff(String string) {
		// get our dao
		RuntimeExceptionDao<EventData, Integer> eventDao = getHelper().getEventDataDao();
		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();
		// query for all of the data objects in the database
		List<EventData> eventList = eventDao.queryForAll();
		List<PersonData> personList = personDao.queryForAll();		
		
		for (PersonData person : personList) {
			personDao.delete(person);
		}
		Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
		Log.i(LOG_TAG, "Should be empty now\n");
		Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
		
		PersonData aNewPerson0 = new PersonData("Eisenhower", new Date(System.currentTimeMillis()), "Dwight D.", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson1 = new PersonData("Kennedy", new Date(System.currentTimeMillis()), "John F.", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson2 = new PersonData("Johnson", new Date(System.currentTimeMillis()), "Lyndon B.", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson3 = new PersonData("Nixon", new Date(System.currentTimeMillis()), "Richard", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson4 = new PersonData("Ford", new Date(System.currentTimeMillis()), "Gerald", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson5 = new PersonData("Carter", new Date(System.currentTimeMillis()), "Jimmy", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson6 = new PersonData("Reagan", new Date(System.currentTimeMillis()), "Ronald", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson7 = new PersonData("Bush", new Date(System.currentTimeMillis()), "George H. W.", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson8 = new PersonData("Clinton", new Date(System.currentTimeMillis()), "Bill", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson9 = new PersonData("Bush", new Date(System.currentTimeMillis()), "George W.", "hansi@gmail.com", "Medieninformatik2");
		PersonData aNewPerson10 = new PersonData("Obama", new Date(System.currentTimeMillis()), "Barack", "hansi@gmail.com", "Medieninformatik2");
		
		personDao.create(aNewPerson0);
		personDao.create(aNewPerson1);
		personDao.create(aNewPerson2);
		personDao.create(aNewPerson3);
		personDao.create(aNewPerson4);
		personDao.create(aNewPerson5);
		personDao.create(aNewPerson6);
		personDao.create(aNewPerson7);
		personDao.create(aNewPerson8);
		personDao.create(aNewPerson9);
		personDao.create(aNewPerson10);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// ignore
		}
		
	}

	private void createSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.events_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.events_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	private void createExpandableListView() {
		ExpandableListView eventExpLV = (ExpandableListView) findViewById(R.id.eventExpLV);
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		for (int i = 0; i < 20; i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, "Group " + i);
			curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even"
					: "This group is odd");
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
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

	private void createListView() {
//		String[] presidents = { "Dwight D. Eisenhower", "John F. Kennedy",
//				"Lyndon B. Johnson", "Richard Nixon", "Gerald Ford",
//				"Jimmy Carter", "Ronald Reagan", "George H. W. Bush",
//				"Bill Clinton", "George W. Bush", "Barack Obama" };

		RuntimeExceptionDao<PersonData, Integer> personDao = getHelper().getPersonDataDao();
		List<PersonData> personList = personDao.queryForAll();
		ArrayList<String> personsFullNames = new ArrayList<String>();
		for(PersonData pd : personList) {
			personsFullNames.add(pd.first_name + " " + pd.last_name);
		}
		
		ListView eventLV = (ListView) findViewById(R.id.eventLV);
		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, personsFullNames);
		eventLV.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
