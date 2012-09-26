package com.melitta.nfcgb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.melitta.nfcgb.persistence.DatabaseHelper;

public class EventActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener, OnItemSelectedListener {
	
	RuntimeExceptionDao<EventData, Integer> eventDao;
	EventData event;
	EditText eventname;
	Switch wintersemester;
	EditText year;
	EditText tutor;
	Button applyButton;
	Button cancelButton;

	public void onCreate(Bundle savedInstanceState) {
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setTitle(R.string.edit_events);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);		
		
		//TODO: Bekomme EventData Objekt, schreibe in event
		eventDao = getHelper().getEventDataDao();
		
		eventname = (EditText)findViewById(R.id.eventname); 
		wintersemester = (Switch)findViewById(R.id.wintersemester);
		year = (EditText)findViewById(R.id.year);
		tutor = (EditText)findViewById(R.id.tutor);
		applyButton = (Button)findViewById(R.id.event_apply_button);
		applyButton.setOnClickListener(this);
		
//		//TODO Befülle EditTexts mit Daten aus dem EventData Objekt
//		eventname.setText(event.eventname);
//		wintersemester.setActivated(event.wintersemester);
//		year.setText(event.year);
//		tutor.setText(event.tutor);
		
		cancelButton = (Button) findViewById(R.id.event_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		// mHelper = new MyDbHelper(this);
	}

	public void onClick(View v) {
//		//TODO beschreibe richtig, wenn richtiges event da ist
//		event.eventname = eventname.getText().toString();
//		event.wintersemester = wintersemester.isChecked();
//		event.year = Integer.parseInt(year.getText().toString());
//		event.tutor = tutor.getText().toString();
//		eventDao.update(event);
		
		Intent data = new Intent();
		data.setData(Uri.parse(eventname.getText().toString() + " changes confirmed"));
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// mDb.close();
		// mCursor.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Open connections to the database 
//		mDb = mHelper.getWritableDatabase();
//		String[] columns = new String[] {"_id", MyDbHelper.COL_NAME, MyDbHelper.COL_DATE}; 
//		mCursor = mDb.query(MyDbHelper.TABLE_NAME, columns, null, null, null, null, null); 
//		//Refresh the list 
//		String[] headers = new String[] {MyDbHelper.COL_NAME, MyDbHelper.COL_DATE}; 
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// An item was selected. You can retrieve the selected item using
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}
}
