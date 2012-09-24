package com.melitta.nfcgb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity  extends Activity  {
	int request_Code = 1;
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";
	private ExpandableListAdapter expLVAdapter;
	private DatabaseHelper databaseHelper = null;
	private final String LOG_TAG = getClass().getSimpleName();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		Log.i(LOG_TAG, "creating " + getClass() + " at " + System.currentTimeMillis());
//		TextView tv = new TextView(this);
//		doSampleDatabaseStuff("onCreate", tv);
//		setContentView(tv);
		createSpinner();
		createListView();
		createExpandableListView();

	}


//	@Override
//	protected void onDestroy() {
//	    super.onDestroy();
//	    if (databaseHelper != null) {
//	        OpenHelperManager.releaseHelper();
//	        databaseHelper = null;
//	    }
//	}
//
//	private DatabaseHelper getHelper() {
//	    if (databaseHelper == null) {
//	        databaseHelper =
//	            OpenHelperManager.getHelper(this, DatabaseHelper.class);
//	    }
//	    return databaseHelper;
//	}
//	/**
//	 * Do our sample database stuff.
//	 */
//	private void doSampleDatabaseStuff(String action, TextView tv) {
//		// get our dao
//		RuntimeExceptionDao<SimpleData, Integer> simpleDao = getHelper().getSimpleDataDao();
//		// query for all of the data objects in the database
//		List<SimpleData> list = simpleDao.queryForAll();
//		// our string builder for building the content-view
//		StringBuilder sb = new StringBuilder();
//		sb.append("got ").append(list.size()).append(" entries in ").append(action).append("\n");
//
//		// if we already have items in the database
//		int simpleC = 0;
//		for (SimpleData simple : list) {
//			sb.append("------------------------------------------\n");
//			sb.append("[").append(simpleC).append("] = ").append(simple).append("\n");
//			simpleC++;
//		}
//		sb.append("------------------------------------------\n");
//		for (SimpleData simple : list) {
//			simpleDao.delete(simple);
//			sb.append("deleted id ").append(simple.id).append("\n");
//			Log.i(LOG_TAG, "deleting simple(" + simple.id + ")");
//			simpleC++;
//		}
//
//		int createNum;
//		do {
//			createNum = new Random().nextInt(3) + 1;
//		} while (createNum == list.size());
//		for (int i = 0; i < createNum; i++) {
//			// create a new simple object
//			long millis = System.currentTimeMillis();
//			SimpleData simple = new SimpleData(millis);
//			// store it in the database
//			simpleDao.create(simple);
//			Log.i(LOG_TAG, "created simple(" + millis + ")");
//			// output it
//			sb.append("------------------------------------------\n");
//			sb.append("created new entry #").append(i + 1).append(":\n");
//			sb.append(simple).append("\n");
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {
//				// ignore
//			}
//		}
//
//		tv.setText(sb.toString());
//		Log.i(LOG_TAG, "Done with page at " + System.currentTimeMillis());
//	}
	
	private void createSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.events_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.events_array, android.R.layout.simple_spinner_item);
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
		String[] presidents = { "Dwight D. Eisenhower", "John F. Kennedy",
				"Lyndon B. Johnson", "Richard Nixon", "Gerald Ford",
				"Jimmy Carter", "Ronald Reagan", "George H. W. Bush",
				"Bill Clinton", "George W. Bush", "Barack Obama" };

		ListView eventLV = (ListView) findViewById(R.id.eventLV);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, presidents);
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
