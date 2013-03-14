package com.modzelewski.nfcgb.view;

import java.sql.SQLException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.GroupAdapter;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.model.Person;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class GroupExpandableListView {
	private ExpandableListView groupExpLV;

	public GroupExpandableListView(ExpandableListView groupExpLV) {
		this.groupExpLV = groupExpLV;
	}

	public ExpandableListView create(final BackgroundModel model, Context context, final DatabaseHelper databaseHelper, final GroupAdapter ga) {
		groupExpLV.setAdapter(ga);
		// Menu on long Click


		// Removing of Child Items Menu on short Click
		groupExpLV.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				final Group group = (Group) ga.getGroup(groupPosition);
				final Person person = (Person) ga.getChild(groupPosition, childPosition);
				AlertDialog.Builder builder = new AlertDialog.Builder(groupExpLV.getContext());
				builder.setMessage(R.string.remove_person_from_group);
				builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						model.removeGroupMembership(group, person);
						ga.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();

				return true;
			}
		});
		return groupExpLV;
	}

}
