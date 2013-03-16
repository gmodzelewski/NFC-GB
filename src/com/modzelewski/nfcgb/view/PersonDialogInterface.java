package com.modzelewski.nfcgb.view;

import android.view.MenuItem;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.controller.PersonAdapter;

/**
 * Adapters for notifyDatasetChanges()
 * <p/>
 * add
 * personAdapter
 * edit
 * personAdapter, groupAdapter
 * remove
 * personAdapter, groupAdapter
 */
public interface PersonDialogInterface {

    public abstract void addPerson(BackgroundModel model, PersonAdapter personAdapter);

    public abstract void removePerson(BackgroundModel model, MenuItem item, GroupAdapter groupAdapter, PersonAdapter personAdapter);

    public abstract void editPerson(BackgroundModel model, MenuItem item, GroupAdapter groupAdapter, PersonAdapter personAdapter);

}