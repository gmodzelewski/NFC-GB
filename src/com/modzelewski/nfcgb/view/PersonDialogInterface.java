package com.modzelewski.nfcgb.view;

import android.view.MenuItem;

import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface PersonDialogInterface {

	public abstract void addPerson(DatabaseHelper dbh, BackgroundModel model, PersonAdapter pa);

	public abstract void editPerson(DatabaseHelper dbh, BackgroundModel model, MenuItem item, PersonAdapter pa, final GroupAdapter ga);

	public abstract void removePerson(DatabaseHelper dbh, BackgroundModel model, MenuItem item, PersonAdapter pa, final GroupAdapter ga);

}