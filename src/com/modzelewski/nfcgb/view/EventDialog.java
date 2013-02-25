package com.modzelewski.nfcgb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.model.*;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class EventDialog implements EventDialogInterface {
	private final Context context;
	
	public EventDialog(Context c) {
        this.context = c;
	}
	
	/* (non-Javadoc)
	 * @see com.modzelewski.nfcgb.view.EventDialogInterface#addEvent(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.EventAdapter)
	 */
	@Override
	public void addEvent(final DatabaseHelper dbh, final BackgroundModel model, final Spinner spinner, final EventAdapter ea) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View eventView = inflater.inflate(R.layout.event_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(context.getResources().getString(R.string.add_event));
		adb.setView(eventView);
		NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
		year.setMinValue(1940);
		year.setMaxValue(2300);
		Calendar cal = Calendar.getInstance();
		year.setValue(cal.get(Calendar.YEAR));
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
				EditText info = (EditText) eventView.findViewById(R.id.ed_info);

				EventData ed = new EventData();

				ed.setEventname(eventname.getText().toString());
				ed.setWintersemester(wintersemester.isChecked());
				ed.setYear(year.getValue());
				ed.setInfo(info.getText().toString());
				
				RuntimeExceptionDao<EventData, Integer> eventDao = dbh.getEventDataDao();
				eventDao.create(ed);
				model.events.add(ed);
				model.setCurrentEvent(ed);
//				setCurrentEvent(ed);
				spinner.setSelection(ea.getPosition(ed));
				ea.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();
	}
	
	
	/* (non-Javadoc)
	 * @see com.modzelewski.nfcgb.view.EventDialogInterface#editEvent(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.EventAdapter, android.view.MenuItem)
	 */
	@Override
	public void editEvent(final DatabaseHelper dbh, final BackgroundModel model, final EventAdapter ea) {
		final EventData currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(context);
		final RuntimeExceptionDao<EventData, Integer> eventDao = dbh.getEventDataDao();
		final View eventView = inflater.inflate(R.layout.event_dialog, null);
		EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
		Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
		NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
		EditText info = (EditText) eventView.findViewById(R.id.ed_info);
		eventname.setText(currentEvent.getEventname());
		wintersemester.setChecked(currentEvent.isWintersemester());
		year.setMinValue(1940);
		year.setMaxValue(2300);
		year.setValue(currentEvent.getYear());
		info.setText(currentEvent.getInfo());

		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		String title = context.getResources().getString(R.string.edit_event);
		adb.setTitle(title);
		adb.setView(eventView);
		adb.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText eventname = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch wintersemester = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				NumberPicker year = (NumberPicker) eventView.findViewById(R.id.np_year);
				EditText info = (EditText) eventView.findViewById(R.id.ed_info);

				if (eventname.toString().trim().isEmpty())
					currentEvent.setEventname(context.getResources().getString(R.string.unknown_eventname));
				else
					currentEvent.setEventname(eventname.getText().toString().trim());
				currentEvent.setWintersemester(wintersemester.isChecked());
				currentEvent.setYear(year.getValue());
				currentEvent.setInfo(info.getText().toString());

				eventDao.update(currentEvent);
				eventDao.refresh(currentEvent);

				ea.notifyDataSetChanged();
			}
		}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// ignore, just dismiss
			}
		}).show();

	}
	
	/* (non-Javadoc)
	 * @see com.modzelewski.nfcgb.view.EventDialogInterface#removeEvent(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.EventAdapter, android.view.MenuItem)
	 */
	@Override
	public void removeEvent(final DatabaseHelper dbh, final BackgroundModel model, final EventAdapter ea) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EventData currentEvent = model.getCurrentEvent();
				RuntimeExceptionDao<EventData, Integer> eventDao = dbh.getEventDataDao();
				RuntimeExceptionDao<GroupData, Integer> groupDao = dbh.getGroupDataDao();
				RuntimeExceptionDao<EventMembershipData, Integer> eventMembershipDao = dbh.getEventMembershipDataDao();
				RuntimeExceptionDao<GroupMembershipData, Integer> groupMembershipDao = dbh.getGroupMembershipDataDao();
				List<EventData> ed = null;
				List<GroupData> gd = null;
				List<EventMembershipData> emd = null;
				List<GroupMembershipData> gmd = null;

				try {
					ed = eventDao.query(eventDao.queryBuilder().where().eq("id", model.getCurrentEvent().getId()).prepare());
					gd = groupDao.query(groupDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().getId()).prepare());
					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().getId()).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}

                assert gd != null;
                for (GroupData groupData : gd) {
					try {
						gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("group_id", groupData.id).prepare());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				eventDao.delete(ed);
				groupDao.delete(gd);
				eventMembershipDao.delete(emd);
				groupMembershipDao.delete(gmd);

				model.persons.clear();
				model.groups.clear();
				model.events.remove(currentEvent);
				if (model.events.contains(currentEvent))
					Toast.makeText(context, "ist noch drin", Toast.LENGTH_LONG).show();

				ea.notifyDataSetChanged();
				
				if (!model.getEvents().isEmpty())
					model.setCurrentEvent(model.getEvents().get(0));
			}
		});
		adb.show();
	}
}
