package com.modzelewski.nfcgb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.modzelewski.nfcgb.MainActivity;
import com.modzelewski.nfcgb.R;

public class AboutDialog extends MainActivity {
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
	

	
	private String getStringFrom(int s) {
		return context.getResources().getString(s);
	}

}
