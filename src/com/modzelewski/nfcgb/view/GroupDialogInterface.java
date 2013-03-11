package com.modzelewski.nfcgb.view;

import android.view.MenuItem;

import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface GroupDialogInterface {

	public abstract void addGroup(DatabaseHelper dbh, BackgroundModel model, GroupAdapter ga);

	public abstract void editGroup(DatabaseHelper dbh, BackgroundModel model, GroupAdapter ga, MenuItem item);

	public abstract void emailGroup(BackgroundModel model, MenuItem item);

	public abstract void removeGroup(DatabaseHelper dbh, BackgroundModel model, GroupAdapter ga, MenuItem item);

}