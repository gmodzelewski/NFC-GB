package com.modzelewski.nfcgb.view;

import android.view.MenuItem;
import android.widget.Spinner;

import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface EventDialogInterface {

	public abstract void addEvent(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, EventAdapter ea);

	public abstract void editEvent(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, EventAdapter ea, MenuItem item);

	public abstract void removeEvent(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, EventAdapter ea, MenuItem item);

}