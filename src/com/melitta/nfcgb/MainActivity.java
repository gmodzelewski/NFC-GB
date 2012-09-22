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
import android.widget.Toast;

public class MainActivity extends Activity {
	int request_Code = 1;
	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";
	private ExpandableListAdapter expLVAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String[] presidents = { "Dwight D. Eisenhower", "John F. Kennedy",
				"Lyndon B. Johnson", "Richard Nixon", "Gerald Ford",
				"Jimmy Carter", "Ronald Reagan", "George H. W. Bush",
				"Bill Clinton", "George W. Bush", "Barack Obama" };

		ListView eventLV = (ListView) findViewById(R.id.eventLV);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, presidents);
		eventLV.setAdapter(adapter);

		
		
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
