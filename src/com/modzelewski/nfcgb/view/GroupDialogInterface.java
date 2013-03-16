package com.modzelewski.nfcgb.view;

import android.view.MenuItem;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;

/**
 * Adapters for notifyDatasetChanges()
 * <p/>
 * add
 * groupAdapter
 * edit
 * groupAdapter
 * remove
 * groupAdapter
 */
public interface GroupDialogInterface {

    public abstract void addGroup(BackgroundModel model, GroupAdapter ga);

    public abstract void editGroup(BackgroundModel model, GroupAdapter ga, MenuItem item);

    public abstract void emailGroup(BackgroundModel model, MenuItem item);

    public abstract void removeGroup(BackgroundModel model, GroupAdapter ga, MenuItem item);

}