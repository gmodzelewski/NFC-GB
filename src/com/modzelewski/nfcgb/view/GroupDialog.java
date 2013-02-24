package com.modzelewski.nfcgb.view;

import java.util.List;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.model.EventData;
import com.modzelewski.nfcgb.model.GroupData;
import com.modzelewski.nfcgb.model.PersonData;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class GroupDialog {
	private Context context;

	public GroupDialog(Context c) {
		this.context = c;

	}

	public void addGroup(final DatabaseHelper dbh, final BackgroundModel model, final Spinner spinner, final GroupAdapter ga) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View groupView = inflater.inflate(R.layout.group_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
		groupNameET.requestFocus();

		String title = null;
			title = context.getResources().getString(R.string.add_group);

		adb.setTitle(title);
		adb.setView(groupView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
				String groupName = groupNameET.getText().toString().trim();
				GroupData group = new GroupData(groupName, model.getCurrentEvent().getId());
				RuntimeExceptionDao<GroupData, Integer> groupDao = dbh.getGroupDataDao();
					groupDao.create(group);
					model.groups.add(group);
					ga.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).show();
	
	}

	public void editGroup(final DatabaseHelper dbh, final BackgroundModel model, final Spinner spinner, final GroupAdapter ga, final MenuItem item) {
		final EventData currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(context);
		final View groupView = inflater.inflate(R.layout.group_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
		groupNameET.requestFocus();

			final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
			final GroupData gd = model.groups.get((int) pInfo.id);
			groupNameET.setText(gd.getGroupName());

		adb.setTitle(context.getResources().getString(R.string.edit_group));
		adb.setView(groupView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText groupNameET = (EditText) groupView.findViewById(R.id.gd_groupName);
				String groupName = groupNameET.getText().toString().trim();
				GroupData group = new GroupData(groupName, currentEvent.getId());

				// create Object
				RuntimeExceptionDao<GroupData, Integer> groupDao = dbh.getGroupDataDao();

					final ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
					final GroupData gd = model.groups.get((int) pInfo.id);
					gd.setGroupName(groupName);
					groupDao.update(gd);
					groupDao.refresh(gd);
				ga.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).show();
	}

	public void emailGroup(final DatabaseHelper dbh, final BackgroundModel model, final Spinner spinner, final GroupAdapter ga, final MenuItem item) {
		ExpandableListContextMenuInfo pInfo = (ExpandableListContextMenuInfo) item.getMenuInfo();
		GroupData gd = model.groups.get((int) pInfo.id);
		List<PersonData> persons = gd.getPerson();
		String emailAddresses = "";
		for (PersonData person : persons) {
			if (person.getEmail().contains("@")) {
				emailAddresses += ", " + person.getEmail();
			} else {
				Toast.makeText(context, context.getResources().getString(R.string.incorrect_mail) + person.getName(), Toast.LENGTH_LONG).show();
			}
		}

		final Intent i = new Intent(android.content.Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddresses });
		try {
			context.startActivity(Intent.createChooser(i, context.getResources().getString(R.string.about_email_chooser)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, context.getResources().getString(R.string.about_no_email_apps), Toast.LENGTH_SHORT).show();
		}
	}

	public void removeGroup(final DatabaseHelper dbh, final BackgroundModel model, final Spinner spinner, final GroupAdapter ga, final MenuItem item) {
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(R.string.remove_group);
		adb.setMessage(R.string.remove_group_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		final ExpandableListView.ExpandableListContextMenuInfo pInfo = info;
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GroupData gd = model.groups.get((int) pInfo.id);
				RuntimeExceptionDao<GroupData, Integer> groupDao = dbh.getGroupDataDao();
				groupDao.delete(gd);
				model.groups.remove(gd);
				ga.notifyDataSetChanged();
			}
		});
		adb.show();
	}
}
