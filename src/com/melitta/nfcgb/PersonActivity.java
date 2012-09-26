package com.melitta.nfcgb;

import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.melitta.nfcgb.persistence.DatabaseHelper;

public class PersonActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	RuntimeExceptionDao<PersonData, Integer> personDao;
	PersonData person;

	// private EditText email;
	// private EditText last_name;
	// private EditText first_name;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setTitle(R.string.edit_person);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myinfo);

		// //TODO Bekomme richtiges PersonData Objekt, schreibe es in person
		// personDao = getHelper().getPersonDataDao();
		// first_name = (EditText)findViewById(R.id.first_name);
		// last_name = (EditText)findViewById(R.id.last_name);
		// email = (EditText)findViewById(R.id.email);
		//
		// //TODO Befuelle EditTexts mit Daten aus dem PersonData Objekt
		// first_name.setText(person.first_name);
		// last_name.setText(person.last_name);
		// email.setText(person.email);

	}
}
