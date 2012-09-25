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
		// our string builder for building the content-view
		StringBuilder sb = new StringBuilder();
		sb.append("got ").append(eventList.size()).append(" entries in ").append("EventData").append("\n");
		sb.append("got ").append(personList.size()).append(" entries in ").append("PersonData").append("\n");

		//--------------   Database foo to try if it works from Homepage  ----------------------------------------------------------------
		// if we already have items in the database
		int eventC = 0;
		for (EventData event : eventList) {
			sb.append("------------------------------------------\n");
			sb.append("[").append(eventC).append("] = ").append(event).append("\n");
			eventC++;
		}
		sb.append("------------------------------------------\n");
		for (EventData event : eventList) {
			eventDao.delete(event);
			sb.append("deleted id ").append(event.id).append("\n");
			Log.i(LOG_TAG, "deleting simple(" + event.id + ")");
			eventC++;
		}

		int createNum;
		do {
			createNum = new Random().nextInt(3) + 1;
		} while (createNum == eventList.size());
		for (int i = 0; i < createNum; i++) {
			// create a new simple object
			long millis = System.currentTimeMillis();
			EventData event = new EventData(millis);
			// store it in the database
			eventDao.create(event);
			Log.i(LOG_TAG, "created event(" + millis + ")");
			// output it
			sb.append("------------------------------------------\n");
			sb.append("created new entry #").append(i + 1).append(":\n");
			sb.append(event).append("\n");
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		Log.i(LOG_TAG, "Done with page at " + System.currentTimeMillis());
		//-------------- end of Database foo to try if it works. Now mine database foo   -----------------------------------------------------
		
		//Show me the persons Info
		int personC = 0;
		for (PersonData person : personList) {
			Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
			Log.i(LOG_TAG, "[" + personC + "] = " + person + "\n");
			eventC++;
		}
		Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
		PersonData aNewPerson = new PersonData("Meiser", new Date(System.currentTimeMillis()), "Hans", "hansi@gmail.com", "Medieninformatik2");
		personDao.create(aNewPerson);
		personDao.update(aNewPerson);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// ignore
		}
		personC = 0;
		for (PersonData personN : personList) {
			Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
			Log.i(LOG_TAG, "[" + personC + "] = " + personN + "\n");
			eventC++;
		}
		Log.i(LOG_TAG, "-------------------------------------------------------------------\n");
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
