package com.modzelewski.nfcgb.view;

import android.widget.Spinner;

import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface EventDialogInterface {

	public abstract void addEvent(DatabaseHelper dbh, BackgroundModel model, Spinner spinner, EventAdapter ea);

	public abstract void editEvent(DatabaseHelper dbh, BackgroundModel model, EventAdapter ea);

	public abstract void removeEvent(DatabaseHelper dbh, BackgroundModel model, EventAdapter ea);

}