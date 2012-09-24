package com.melitta.nfcgb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class EventActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	
	EditText eventname;
	EditText semester; 
	EditText year;
	EditText tutor;
	Button applyButton;
	Button cancelButton;
	
//	MyDbHelper mHelper;
	SQLiteDatabase mDb;
	Cursor mCursor;
	SimpleCursorAdapter mAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);		
		
		eventname = (EditText)findViewById(R.id.eventname); 
		semester = (EditText)findViewById(R.id.semester);
		year = (EditText)findViewById(R.id.year);
		tutor = (EditText)findViewById(R.id.tutor);
		applyButton = (Button)findViewById(R.id.event_apply_button);
		applyButton.setOnClickListener(this);
		
		cancelButton = (Button)findViewById(R.id.event_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		      setResult(RESULT_CANCELED);
		    	finish();
		    }
		  });
		
//		mHelper = new MyDbHelper(this);
		
		
	}

	public void onClick(View v) {
//		Add a new value to the database 
//		ContentValues cv = new ContentValues(2); 
//		cv.put(MyDbHelper.COL_NAME, mText.getText().toString()); 
//		Create a formatter for SQL date format 
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//		cv.put(MyDbHelper.COL_DATE, dateFormat.format(new Date())); //Insert 'now' as the date 
//		mDb.insert(MyDbHelper.TABLE_NAME, null, cv); 
//		mAdapter.notifyDataSetChanged(); 
		
		Intent data = new Intent();
		data.setData(Uri.parse(eventname.getText().toString() + " changes confirmed"));
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		mDb.close();
//		mCursor.close();
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
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
         parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
