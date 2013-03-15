package com.modzelewski.nfcgb.view;

import android.widget.Spinner;

import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public interface EventDialogInterface {

	public abstract void addEvent(BackgroundModel model, Spinner eventSpinner, EventAdapter eventAdapter);

	public abstract void removeEvent(BackgroundModel model, EventAdapter eventAdapter);

	void editEvent(BackgroundModel model, EventAdapter eventAdapter);

}