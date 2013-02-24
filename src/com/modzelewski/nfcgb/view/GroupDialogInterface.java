package com.modzelewski.nfcgb.view;

import android.view.MenuItem;
import android.widget.Spinner;

import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface GroupDialogInterface {

	public abstract void addGroup(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, GroupAdapter ga);

	public abstract void editGroup(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, GroupAdapter ga, MenuItem item);

	public abstract void emailGroup(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, GroupAdapter ga, MenuItem item);

	public abstract void removeGroup(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, GroupAdapter ga, MenuItem item);

}