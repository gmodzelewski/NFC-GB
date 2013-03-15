package com.modzelewski.nfcgb.view;

import android.view.MenuItem;

import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface PersonDialogInterface {

	public abstract void addPerson(DatabaseHelper dbh, BackgroundModel model, PersonAdapter pa);

	public abstract void removePerson(BackgroundModel model, MenuItem item, GroupAdapter groupAdapter, PersonAdapter personAdapter);

	public abstract void editPerson(BackgroundModel model, MenuItem item, GroupAdapter groupAdapter, PersonAdapter personAdapter);

}