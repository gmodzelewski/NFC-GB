package com.modzelewski.nfcgb.controller;

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
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.GroupMembership;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DragEventListener extends ListView implements OnDragListener {

	private BackgroundModel model;
    final String EXPLISTVIEW_TAG = "ELV";
    private final List<Integer> openedGroups = new LinkedList<Integer>();
	private int droppedInGroupPos;

	public DragEventListener(Context context) {
		super(context);
	}

	public DragEventListener(Context context, BackgroundModel model) {
		super(context);
		this.model = model;
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
						if (expLv.isGroupExpanded(i)) {
							openedGroups.add(i);
							expLv.collapseGroup(i);
						}
					}
				}
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
			if (v.getTag() == EXPLISTVIEW_TAG) {
				ClipData.Item i = event.getClipData().getItemAt(0);
				Log.i(getClass().getSimpleName(), "i.getText(): " + i.getText().toString());
				int personId = Integer.parseInt((String) i.getText());

				ExpandableListView expLv = (ExpandableListView) v;
				droppedInGroupPos = expLv.pointToPosition((int) event.getX(), (int) event.getY());

				model.addGroupMembership(personId, droppedInGroupPos);
			}
			return true; // if drop accepted

		case DragEvent.ACTION_DRAG_ENDED:
			Log.i(getClass().getSimpleName(), "ACTION DRAG ENDED called");
			
			if (v.getTag() == EXPLISTVIEW_TAG) {
				ExpandableListView expLv = (ExpandableListView) v;
				
				expLv.expandGroup(droppedInGroupPos);
				for (int opened : openedGroups) {
					expLv.expandGroup(opened);
				}
				openedGroups.clear();
			}
			return true;
		}
		return true;
	}
}
