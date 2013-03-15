package com.modzelewski.nfcgb.view;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;

import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.BackgroundModel;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.model.Event;

public class EventDialog implements EventDialogInterface {
	private final Context context;
	
	public EventDialog(Context c) {
        this.context = c;
	}
	
	/* (non-Javadoc)
	 * @see com.modzelewski.nfcgb.view.EventDialogInterface#addEvent(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.EventAdapter)
	 */
	@Override
	public void addEvent(final BackgroundModel model, final Spinner eventSpinner, final EventAdapter eventAdapter) {
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

				Event event = new Event();
				event.setEventname(eventname.getText().toString());
				event.setYear(year.getValue());
				event.setWintersemester(wintersemester.isChecked());
				event.setInfo(info.getText().toString());

				model.addEvent(event);
				eventAdapter.notifyDataSetChanged();
				eventSpinner.setSelection(eventAdapter.getPosition(event));
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
	 * @see com.modzelewski.nfcgb.view.EventDialogInterface#editEvent(com.modzelewski.nfcgb.persistence.DatabaseHelper, com.modzelewski.nfcgb.model.BackgroundModel, android.widget.Spinner, com.modzelewski.nfcgb.controller.EventAdapter, android.view.MenuItem)
	 */
	@Override
	public void editEvent(final BackgroundModel model, final EventAdapter eventAdapter) {
		final Event currentEvent = model.getCurrentEvent();
		LayoutInflater inflater = LayoutInflater.from(context);
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
				EditText name = (EditText) eventView.findViewById(R.id.ed_eventname);
				Switch ws = (Switch) eventView.findViewById(R.id.ed_wintersemester);
				NumberPicker y = (NumberPicker) eventView.findViewById(R.id.np_year);
				EditText i = (EditText) eventView.findViewById(R.id.ed_info);
				String eventName = null;
				if (name.toString().trim().isEmpty())
					eventName = context.getResources().getString(R.string.unknown_eventname);
				else
					eventName = name.getText().toString().trim();
				Boolean wintersemester = ws.isChecked();
				int year = y.getValue();
				String info = i.getText().toString().trim();
				model.editEvent(currentEvent, eventName, wintersemester, year, info);
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
	public void removeEvent(final BackgroundModel model, final EventAdapter eventAdapter) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(R.string.context_menu_remove_title);
		adb.setMessage(R.string.context_menu_remove_message);
		adb.setNegativeButton(R.string.cancel_button, null);
		adb.setPositiveButton(R.string.ok_button, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				model.removeEvent(model.getCurrentEvent());
				eventAdapter.notifyDataSetChanged();
			}
		});
		adb.show();
	}
}
