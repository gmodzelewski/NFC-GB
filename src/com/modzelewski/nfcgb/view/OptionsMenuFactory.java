package com.modzelewski.nfcgb.view;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.controller.EventAdapter;
import com.modzelewski.nfcgb.model.BackgroundModel;
import com.modzelewski.nfcgb.model.EventData;
import com.modzelewski.nfcgb.persistence.DatabaseHelper;

public class OptionsMenuFactory extends MainActivity {
	private Context context;
//	private DatabaseHelper databasehelper;
	
	
	public void menuAbout(Context c) {
		context = c;
		
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		String title = getStringFrom(R.string.about_tv);
		String message = getStringFrom(R.string.about);
		final String emailSubject = getStringFrom(R.string.about_email_subject);
		final String emailBody = getStringFrom(R.string.about_email_body);
		final String emailAppChooseMessage = getStringFrom(R.string.about_email_chooser);
		final String emailAppFailedMessage = getStringFrom(R.string.about_no_email_apps);
		
		adb.setMessage(message).setTitle(title);
		adb.setNeutralButton(R.string.about_goto_github, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Uri uri = Uri.parse("https://github.com/melitta/NFC-GB");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(intent);
			}
		}).setPositiveButton(R.string.about_send_mail, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { "melitta@tzi.de" });
				i.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
				i.putExtra(Intent.EXTRA_TEXT, emailBody);
				try {
					context.startActivity(Intent.createChooser(i, emailAppChooseMessage));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(getBaseContext(), emailAppFailedMessage, Toast.LENGTH_SHORT).show();
				}
			}
		}).show();
	}
	

	
	public void menuAddEvent(Context c, final DatabaseHelper dbh, final BackgroundModel model, final Spinner spinner, final EventAdapter ea) {
		context = c;
		LayoutInflater inflater = LayoutInflater.from(context);
		final View eventView = inflater.inflate(R.layout.event_dialog, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(getStringFrom(R.string.add_event));
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

	private String getStringFrom(int s) {
		return context.getResources().getString(s);
	}

}
