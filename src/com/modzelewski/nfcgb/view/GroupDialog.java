package com.modzelewski.nfcgb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Toast;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.Person;

import java.util.List;

public class GroupDialog implements GroupDialogInterface {
    private final Context context;

    public GroupDialog(Context c) {
        this.context = c;

    }

    /* (non-Javadoc)
     * @see com.modzelewski.nfcgb.view.GroupDialogInterface#addGroup(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.GroupAdapter)
     */
    @Override
    public void addGroup(final BackgroundModel model, final GroupAdapter groupAdapter) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View groupView = inflater.inflate(R.layout.group_dialog, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
        groupNameET.requestFocus();

        adb.setTitle(context.getResources().getString(R.string.add_group));
        adb.setView(groupView);
        adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
                String groupName = groupNameET.getText().toString().trim();
                model.addGroup(groupName);
                groupAdapter.notifyDataSetChanged();
                model.reloadEverything();
            }
        }).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();

    }

    /* (non-Javadoc)
     * @see com.modzelewski.nfcgb.view.GroupDialogInterface#editGroup(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.GroupAdapter, android.view.MenuItem)
     */
    @Override
    public void editGroup(final BackgroundModel model, final GroupAdapter groupAdapter, final MenuItem item) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View groupView = inflater.inflate(R.layout.group_dialog, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
        groupNameET.requestFocus();

        final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
        final Group gd = model.groups.get((int) pInfo.id);
        groupNameET.setText(gd.getGroupName());

        adb.setTitle(context.getResources().getString(R.string.edit_group));
        adb.setView(groupView);
        adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
                String groupName = groupNameET.getText().toString().trim();
                final Group group = model.groups.get((int) pInfo.id);
                model.editGroup(group, groupName);
                groupAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    /* (non-Javadoc)
     * @see com.modzelewski.nfcgb.view.GroupDialogInterface#emailGroup(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.GroupAdapter, android.view.MenuItem)
     */
    @Override
    public void emailGroup(final BackgroundModel model, final MenuItem item) {
        ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
        Group gd = model.groups.get((int) pInfo.id);
        List<Person> persons = gd.getPerson();
        String emailAddresses = "";
        for (Person person : persons) {
            if (person.getEmail().contains("@")) {
                emailAddresses += ", " + person.getEmail();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.incorrect_mail) + person.getName(), Toast.LENGTH_LONG).show();
            }
        }

        final Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddresses});
        try {
            context.startActivity(Intent.createChooser(i, context.getResources().getString(R.string.about_email_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, context.getResources().getString(R.string.about_no_email_apps), Toast.LENGTH_SHORT).show();
        }
    }

    /* (non-Javadoc)
     * @see com.modzelewski.nfcgb.view.GroupDialogInterface#removeGroup(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.GroupAdapter, android.view.MenuItem)
     */
    @Override
    public void removeGroup(final BackgroundModel model, final GroupAdapter groupAdapter, final MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(R.string.remove_group);
        adb.setMessage(R.string.remove_group_message);
        adb.setNegativeButton(R.string.cancel_button, null);
        adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
                final ExpandableListView.ExpandableListContextMenuInfo pInfo = info;
                Group gd = model.groups.get((int) pInfo.id);
                model.removeGroup(gd);
                groupAdapter.notifyDataSetChanged();
            }
        });
        adb.show();
    }
}
