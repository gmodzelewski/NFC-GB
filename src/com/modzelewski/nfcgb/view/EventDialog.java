package com.modzelewski.nfcgb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
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
	public void addEvent(final DatabaseHelper dbh, final BackgroundModel model, final Spinner eventSpinner, final EventAdapter eventAdapter) {
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

				Event ed = new Event();

				ed.setEventname(eventname.getText().toString());
				ed.setWintersemester(wintersemester.isChecked());
				ed.setYear(year.getValue());
				ed.setInfo(info.getText().toString());
				
				RuntimeExceptionDao<Event, Integer> eventDao = dbh.getEventDataDao();
				eventDao.create(ed);
				model.events.add(ed);
				model.setCurrentEvent(ed);
				eventAdapter.notifyDataSetChanged();
				eventSpinner.setSelection(eventAdapter.getPosition(ed));
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
	public void editEvent(final DatabaseHelper dbh, final BackgroundModel model, final EventAdapter eventAdapter) {
		final Event currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(context);
		final RuntimeExceptionDao<Event, Integer> eventDao = dbh.getEventDataDao();
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

				eventAdapter.notifyDataSetChanged();
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
	public void removeEvent(final DatabaseHelper dbh, final BackgroundModel model, final EventAdapter eventAdapter) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Event currentEvent = model.getCurrentEvent();
				RuntimeExceptionDao<Event, Integer> eventDao = dbh.getEventDataDao();
				RuntimeExceptionDao<Group, Integer> groupDao = dbh.getGroupDataDao();
				RuntimeExceptionDao<EventMembership, Integer> eventMembershipDao = dbh.getEventMembershipDataDao();
				RuntimeExceptionDao<GroupMembership, Integer> groupMembershipDao = dbh.getGroupMembershipDataDao();
				List<Event> ed = null;
				List<Group> gd = null;
				List<EventMembership> emd = null;
				List<GroupMembership> gmd = null;

				try {
					ed = eventDao.query(eventDao.queryBuilder().where().eq("id", model.getCurrentEvent().getId()).prepare());
					gd = groupDao.query(groupDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().getId()).prepare());
					emd = eventMembershipDao.query(eventMembershipDao.queryBuilder().where().eq("event_id", model.getCurrentEvent().getId()).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}

                assert gd != null;
                for (Group group : gd) {
					try {
						gmd = groupMembershipDao.query(groupMembershipDao.queryBuilder().where().eq("group_id", group.id).prepare());
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

				eventAdapter.notifyDataSetChanged();
				
				if (!model.getEvents().isEmpty())
					model.setCurrentEvent(model.getEvents().get(0));
			}
		});
		adb.show();
	}
}
