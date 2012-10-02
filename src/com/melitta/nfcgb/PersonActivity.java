package com.melitta.nfcgb;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.melitta.nfcgb.persistence.DatabaseHelper;

public class PersonActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

	RuntimeExceptionDao<PersonData, Integer> personDao;
	PersonData person;
	List<EventData> events;
	EventData currentEvent;

	private EditText email;
	private EditText name;

	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setTitle(R.string.edit_person);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_dialog);
		
//		
//		// //TODO Bekomme richtiges PersonData Objekt, schreibe es in person
//		// personDao = getHelper().getPersonDataDao();
//		name = (EditText) findViewById(R.id.name);
//		email = (EditText) findViewById(R.id.email);
//		//
//		// //TODO Befuelle EditTexts mit Daten aus dem PersonData Objekt
//		// first_name.setText(person.first_name);
//		// last_name.setText(person.last_name);
//		// email.setText(person.email);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
