package com.modzelewski.nfcgb.view;

import android.widget.Spinner;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;

/**
 * Adapters for notifyDatasetChanges:
 * add
 * all
 * edit
 * eventAdapter
 * remove
 * all
 */
public interface EventDialogInterface {

    public abstract void addEvent(BackgroundModel model, Spinner eventSpinner, EventAdapter eventAdapter, GroupAdapter groupAdapter, PersonAdapter personAdapter);

    public abstract void removeEvent(BackgroundModel model, EventAdapter eventAdapter, GroupAdapter groupAdapter, PersonAdapter personAdapter);

    public abstract void editEvent(BackgroundModel model, EventAdapter eventAdapter);

}