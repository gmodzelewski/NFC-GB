package com.modzelewski.nfcgb;

import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

public class DragEventListener extends ListView implements OnDragListener {

	BackgroundModel model;
	private DatabaseHelper databaseHelper = null;
	final String LISTVIEW_TAG = "ListView";
	final String EXPLISTVIEW_TAG = "ExpandableListView";
	final String TARGETLAYOUT_TAG = "targetLayout";

	public DragEventListener(Context context) {
		super(context);
	}
	public DragEventListener(Context context, BackgroundModel model, DatabaseHelper databaseHelper) {
		super(context);
		this.model = model;
		this.databaseHelper = databaseHelper;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		final int action = event.getAction();
		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED accept");
				if (v.getTag() == EXPLISTVIEW_TAG) {
					ExpandableListView expLv = (ExpandableListView) v;
					int count = expLv.getCount();
					for (int i = 0; i < count; i++) {
						expLv.collapseGroup(i);
					}
				}
				v.invalidate();
				return true;
			} else {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED rejected");
				return false;
			}

		case MotionEvent.ACTION_DOWN:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED accept");
				return true;
			} else {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED rejected");
				return false;
			}

		case DragEvent.ACTION_DROP:
			List<PersonData> personList = model.persons;
			Log.i(getClass().getSimpleName(), personList.toString());
			ClipData.Item i = event.getClipData().getItemAt(0);
			Log.i(getClass().getSimpleName(), "i.getText(): " + i.getText().toString());

			int personId = Integer.parseInt((String) i.getText());
			RuntimeExceptionDao<PersonData, Integer> personDao = databaseHelper.getPersonDataDao();
			PersonData person = model.getPersonById(personId);
			PersonData personInDao = personDao.queryForSameId(person);

			Log.i(getClass().getSimpleName(), "person.toString(): " + person.toString());

			if (v.getTag() == EXPLISTVIEW_TAG) {
				ExpandableListView expLv = (ExpandableListView) v;
				int pos = expLv.pointToPosition((int) event.getX(), (int) event.getY());
				GroupData group = null;
				if (pos >= 0) {
					group = model.groups.get(pos);
				}
				if (!group.person.contains(person)) {
					RuntimeExceptionDao<GroupData, Integer> groupDao = databaseHelper.getGroupDataDao();
					group.person.add(person);
					GroupData groupInDao = groupDao.queryForSameId(group);
					groupInDao.person.add(personInDao);
					groupDao.update(groupInDao);
					groupDao.refresh(groupInDao);
				} else {
					Toast.makeText(getContext(), getResources().getString(R.string.person_already_in_group), Toast.LENGTH_LONG).show();
				}
				expLv.invalidate();
				expLv.invalidateViews();
			}
			v.invalidate();
			return true; // if drop accepted
		}
		return true;
	}
}
